package tech.nilu.bots.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tech.nilu.bots.ActionCodes;
import tech.nilu.bots.dao.mysql.BountyReceiverRepository;
import tech.nilu.bots.dao.mysql.BountyRepository;
import tech.nilu.bots.dao.mysql.UserInfoRepository;
import tech.nilu.bots.dto.*;
import tech.nilu.bots.dto.request.BountyCreateRequest;
import tech.nilu.bots.dto.request.ThirdpartyBotExecuteRequest;
import tech.nilu.bots.dto.request.ThirdpartyBotExecuteResponse;
import tech.nilu.bots.dto.response.BountyCreateResponse;
import tech.nilu.bots.exception.ApplicationException;
import tech.nilu.bots.model.mysql.Bounty;
import tech.nilu.bots.model.mysql.BountyReceiver;
import tech.nilu.bots.model.mysql.UserInfo;
import tech.nilu.bots.proxy.bots.instagram.InstagramBot;
import tech.nilu.bots.proxy.web3.NiluWallet;
import tech.nilu.bots.util.MiscUtil;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Date;

@Service
public class BountyService {

    @Autowired
    private BountyRepository bountyRepository;

    @Autowired
    private BountyReceiverRepository bountyReceiverRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private InstagramBot instagramBot;

    @Autowired
    private GenericTransactionManager transactionManager;

    @Autowired
    private NiluWallet wallet;

    @Scheduled(fixedDelay = 60000)
    @Transactional(propagation = Propagation.REQUIRED)
    public void processBounties() {
        bountyRepository.findByCheckDateBeforeAndStatusIn(new Date()
                , Arrays.asList(BountyStatus.CREATED, BountyStatus.READY_TO_PROCESS))
                .forEach(this::process);
    }

    @Scheduled(fixedDelay = 60000)
    @Transactional(propagation = Propagation.REQUIRED)
    public void processBountyReceivers() {
        bountyReceiverRepository.findByStatusIn(Arrays.asList(BountyReceiverStatus.CREATED, BountyReceiverStatus.READY_TO_PROCESS))
                .forEach(this::payBountyToReceiver);
    }

    public void process(Bounty inputBounty) {
        try {
            if (inputBounty.getStatus().equals(BountyStatus.CREATED)
                    || inputBounty.getStatus().equals(BountyStatus.READY_TO_PROCESS)) {
                inputBounty.setLastCheckDate(new Date());
                inputBounty.setRetryCount(inputBounty.getRetryCount() + 1);
                inputBounty.setStatus(BountyStatus.PROCESSING);
                BigInteger tb = (BigDecimal.valueOf(wallet.getBalance(inputBounty.getBountyNiluAddress()))
                        .subtract(BigDecimal.valueOf(inputBounty.getFeeInNilu())))
                        .multiply(BigDecimal.valueOf(10).pow(18))
                        .toBigInteger();
                if (tb.compareTo(BigInteger.ZERO) < 0) {
                    throw new ApplicationException(ActionCodes.TYPE_FORBIDDEN, ActionCodes.FEE_NOT_PAID);
                }
                transactionManager.doInSeparateTx(() -> bountyRepository.save(inputBounty));

                if (inputBounty.getTokenAddress() != null) {
                    tb = wallet.getTokenBalance(inputBounty.getTokenAddress(), inputBounty.getBountyNiluAddress());
                }
                BigInteger tokenBalance = tb;
                int possibleWinnerCount = Math.min(tokenBalance.divide(inputBounty.getBountyAmount()).intValue()
                        , inputBounty.getWinnersCount());
                BotType botType = inputBounty.getBotType();
                if (botType.equals(BotType.INSTAGRAM)) {
                    Bounty bounty = bountyRepository.findOne(inputBounty.getId());
                    transactionManager.doInSeparateTx(() -> {
                        int valids = 0;
                        int all = 0;
                        int totalCount = Integer.MAX_VALUE;
                        while (all < totalCount && valids < possibleWinnerCount) {
                            Comments comments = instagramBot.getComments(bounty.getPostAddress()
                                    , bounty.getBotMetaData()
                                    , possibleWinnerCount);
                            all += comments.getComments().size();
                            bounty.setBotMetaData(comments.getLastIndex());
                            totalCount = comments.getTotal();
                            for (CommentInfo c : comments.getComments()) {
                                String niluId = MiscUtil.lookupNiluId(c.getText());
                                UserInfo userInfo = userInfoRepository.findByBotTypeAndUser(botType, c.getUserId())
                                        .orElse(new UserInfo());
                                if (niluId != null) {
                                    if (userInfo.getNiluId() == null) {
                                        userInfo.setBotType(botType);
                                        userInfo.setUserId(c.getUserId());
                                        userInfo.setUserName(c.getUsername());
                                        userInfo.setNiluId(niluId);
                                        userInfo.setCreateDate(new Date());
                                    }
                                } else {
                                    if (userInfo.getNiluId() != null) {
                                        niluId = userInfo.getNiluId();
                                    }
                                }
                                if (niluId != null) {
                                    userInfo.setLastBounty(bounty);
                                    userInfo.setLastBountyDate(new Date());
                                    userInfoRepository.save(userInfo);
                                    BountyReceiver receiver = bountyReceiverRepository.findByBountyAndNiluId(bounty, niluId).orElse(null);
                                    if (receiver == null) {
                                        BountyReceiver userPreviousReceive = bountyReceiverRepository.findByBountyAndUser(bounty, userInfo).orElse(null);
                                        if (userPreviousReceive == null) {
                                            receiver = new BountyReceiver();
                                            receiver.setBounty(bounty);
                                            receiver.setCreateDate(new Date());
                                            receiver.setStatus(BountyReceiverStatus.CREATED);
                                            receiver.setUser(userInfo);
                                            receiver.setNiluId(niluId);
                                            receiver.setValue(bounty.getBountyAmount());
                                            bountyReceiverRepository.save(receiver);
                                            valids++;
                                        }
                                    }
                                }
                            }
                        }
                        bounty.setTotalAmount(/*bounty.getBountyAmount().multiply(BigInteger.valueOf(valids))*/tokenBalance);
                        bounty.setStatus(BountyStatus.READY_FOR_SETTLEMENT);
                        bounty.setFinalWinnerCount(valids);
                        if (valids == 0) {
                            if (tokenBalance.compareTo(BigInteger.ZERO) > 0) {
                                createRefundReceiver(bounty, tokenBalance);
                            } else
                                bounty.setStatus(BountyStatus.COMPLETE);
                        }
                        return bountyRepository.save(bounty);
                    });
                } else
                    throw new ApplicationException(ActionCodes.TYPE_BAD_REQUEST, ActionCodes.INVALID_BOT_TYPE_ERROR);

            }
        } catch (ApplicationException e) {
            e.printStackTrace();
            Bounty bounty = bountyRepository.findOne(inputBounty.getId());
            if (e.getError().equals(ActionCodes.INVALID_BOT_TYPE_ERROR)
                    || bounty.getRetryCount() > bounty.getMaxRetryCount())
                bounty.setStatus(BountyStatus.FAILED);
            else if (bounty.getRetryCount() <= bounty.getMaxRetryCount()) {
                bounty.setCheckDate(new Date(bounty.getCheckDate().getTime() + 2 * 3600 * 1000));
                bounty.setStatus(BountyStatus.READY_TO_PROCESS);
            }
            bounty.setLastError(e.getError());
            transactionManager.doInSeparateTx(() -> bountyRepository.save(bounty));
        }

    }

    private void createRefundReceiver(Bounty bounty, BigInteger tokenBalance) {
        BountyReceiver receiver = new BountyReceiver();
        receiver.setBounty(bounty);
        receiver.setCreateDate(new Date());
        receiver.setStatus(BountyReceiverStatus.CREATED);
        receiver.setNiluId(bounty.getRefundAddress());
        receiver.setValue(tokenBalance);
        receiver.setRefund(true);
        bounty.setRefundRequired(true);
        bountyReceiverRepository.save(receiver);
    }

    public void payBountyToReceiver(BountyReceiver inputBountyReceiver) {
        if (inputBountyReceiver.getBounty().getStatus().ordinal() > BountyStatus.PROCESSING.ordinal()
                && (inputBountyReceiver.getStatus().equals(BountyReceiverStatus.CREATED)
                || inputBountyReceiver.getStatus().equals(BountyReceiverStatus.READY_TO_PROCESS))) {
            inputBountyReceiver.setStatus(BountyReceiverStatus.INPROGRESS);
            inputBountyReceiver.setExecuteCount(inputBountyReceiver.getExecuteCount() + 1);
            inputBountyReceiver.setLastUpdateDate(new Date());
            transactionManager.doInSeparateTx(() -> bountyReceiverRepository.save(inputBountyReceiver));
            BountyReceiver bountyReceiver = bountyReceiverRepository.findOne(inputBountyReceiver.getId());
            Bounty bounty = bountyReceiver.getBounty();
            transactionManager.doInSeparateTx(() -> {
                try {
                    String txHash = null;
                    if (bounty.getTokenAddress() == null) {
                        txHash = wallet.giveNilu(bounty.getBountyMnemonic(), bountyReceiver.getNiluId(), bountyReceiver.getValue());
                    } else {
                        txHash = wallet.giveToken(bounty.getBountyMnemonic(), bounty.getTokenAddress(), bountyReceiver.getNiluId(), bountyReceiver.getValue());
                    }
                    if (txHash == null)
                        throw new ApplicationException(500, "null.tx");
                    bountyReceiver.setTxHash(txHash);
                    bountyReceiver.setStatus(BountyReceiverStatus.PAID);
                    if (bountyReceiver.isRefund()) {
                        bounty.setRefundTxHash(txHash);
                        bounty.setRefundAmount(bountyReceiver.getValue());
                    } else {
                        bounty.setSpentAmount(bounty.getSpentAmount().add(bountyReceiver.getValue()));
                    }
                    bounty.setSuccessCount(bounty.getSuccessCount() + 1);
                } catch (Exception e) {
                    e.printStackTrace();
                    if (bountyReceiver.getExecuteCount() < bounty.getMaxRetryCount())
                        bountyReceiver.setStatus(BountyReceiverStatus.READY_TO_PROCESS);
                    else {
                        bountyReceiver.setStatus(BountyReceiverStatus.FAILED);
                        bounty.setFailedCount(bounty.getFailedCount() + 1);
                    }
                    bountyReceiver.setError(e instanceof ApplicationException ? ((ApplicationException) e).getError() : e.getMessage());
                    bounty.setLastError(bountyReceiver.getError());
                }
                if (bounty.getSuccessCount() + bounty.getFailedCount() == bounty.getFinalWinnerCount() + (bounty.isRefundRequired() ? 1 : 0)) {
                    BigInteger refundAmount = bounty.getTotalAmount().subtract(bounty.getSpentAmount().add(bounty.getRefundAmount()));
                    if (!bountyReceiver.isRefund() && refundAmount.compareTo(BigInteger.ZERO) > 0) {
                        createRefundReceiver(bounty, refundAmount);
                    } else {
                        bounty.setStatus(bounty.getFailedCount() == 0 ? BountyStatus.SUCCEED :
                                bounty.getSuccessCount() == 0 ? BountyStatus.FAILED :
                                        BountyStatus.COMPLETE);
                    }
                }
                bountyReceiverRepository.save(bountyReceiver);
                bountyRepository.save(bounty);
                return bountyReceiver;
            });

        }

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public BountyCreateResponse createBounty(BountyCreateRequest request) {
        BountyCreateResponse ret = new BountyCreateResponse();
        Bounty bounty = new Bounty();
        bounty.setStatus(BountyStatus.CREATED);
        bounty.setBotType(request.getBotType());
        bounty.setActionType(request.getActionType());
        bounty.setRegisterDate(new Date());
        bounty.setCheckDate(request.getBotType().equals(BotType.THIRDPARTY) ? null : request.getCheckDate());
        bounty.setWinnersCount(request.getWinnersCount());
        bounty.setPostAddress(request.getPostAddress());
        bounty.setBountyAmount(request.getBountyAmount());
        bounty.setTokenAddress(request.getTokenAddress());
        bounty.setRefundAddress(request.getRefundAddress());
        String[] newWallet = wallet.generateWallet();
        bounty.setBountyNiluAddress(newWallet[0]);
        bounty.setBountyMnemonic(newWallet[1]);
        bounty.setFeeInNilu(request.getWinnersCount() * (request.getBotType().equals(BotType.THIRDPARTY) ? 0.005 : 0.01));
        bountyRepository.save(bounty);
        ret.setFeeInNilu(bounty.getFeeInNilu());
        ret.setBountyNiluAddress(bounty.getBountyNiluAddress());
        return ret;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public ThirdpartyBotExecuteResponse executeRequest(ThirdpartyBotExecuteRequest request) {
        Bounty bounty = bountyRepository.findByBountyNiluAddress(request.getBountyNiluAddress()).orElseThrow(() -> new ApplicationException(404, "bounty.not.found"));
        bounty.setCheckDate(new Date());
        bounty.setFinalWinnerCount(request.getReceivers().size());
        bounty.setWinnersCount(bounty.getFinalWinnerCount());
        BigInteger tb = (BigDecimal.valueOf(wallet.getBalance(bounty.getBountyNiluAddress()))
                .subtract(BigDecimal.valueOf(bounty.getFeeInNilu())))
                .multiply(BigDecimal.valueOf(10).pow(18))
                .toBigInteger();
        if (tb.compareTo(BigInteger.ZERO) < 0) {
            throw new ApplicationException(ActionCodes.TYPE_FORBIDDEN, ActionCodes.FEE_NOT_PAID);
        }

        if (bounty.getTokenAddress() != null) {
            tb = wallet.getTokenBalance(bounty.getTokenAddress(), bounty.getBountyNiluAddress());
        }
        BigInteger tokenBalance = tb;
        int possibleWinnerCount = 0;
        BigInteger total = BigInteger.valueOf(0);
        for (BountyReceiver br : request.getReceivers()) {
            total = total.add(br.getValue());
            tokenBalance = tokenBalance.subtract(br.getValue());
            if (tokenBalance.compareTo(BigInteger.ZERO) < 0) {
                tokenBalance = tokenBalance.add(br.getValue());
                break;
            }
            possibleWinnerCount++;
        }
        bounty.setTotalAmount(total);
        bounty.setStatus(BountyStatus.READY_FOR_SETTLEMENT);
        if (Math.min(possibleWinnerCount, request.getReceivers().size()) == 0) {
            if (tokenBalance.compareTo(BigInteger.ZERO) > 0) {
                createRefundReceiver(bounty, tokenBalance);
            } else
                bounty.setStatus(BountyStatus.COMPLETE);
        } else {
            request.getReceivers().stream().limit(possibleWinnerCount).forEach(receiver -> {
                receiver.setBounty(bounty);
                receiver.setStatus(BountyReceiverStatus.CREATED);
                receiver.setRefund(false);
                bountyReceiverRepository.save(receiver);
            });
            if (tokenBalance.subtract(total).compareTo(BigInteger.ZERO) > 0) {
                createRefundReceiver(bounty, tokenBalance.subtract(total));
            }
        }
        bountyRepository.save(bounty);
        return new ThirdpartyBotExecuteResponse();
    }


}
