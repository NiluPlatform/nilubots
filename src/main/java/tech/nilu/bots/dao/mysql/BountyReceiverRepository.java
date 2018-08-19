package tech.nilu.bots.dao.mysql;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.nilu.bots.dto.BountyReceiverStatus;
import tech.nilu.bots.model.mysql.Bounty;
import tech.nilu.bots.model.mysql.BountyReceiver;
import tech.nilu.bots.model.mysql.UserInfo;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface BountyReceiverRepository extends JpaRepository<BountyReceiver,Long> {
    Optional<BountyReceiver> findByBountyAndNiluId(Bounty bounty, String nilu);

    Optional<BountyReceiver> findByBountyAndUser(Bounty bounty, UserInfo userInfo);

    Stream<BountyReceiver> findByStatusIn(List<BountyReceiverStatus> statuses);
}
