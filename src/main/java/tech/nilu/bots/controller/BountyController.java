package tech.nilu.bots.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.nilu.bots.dto.request.BountyCreateRequest;
import tech.nilu.bots.dto.request.ThirdpartyBotExecuteRequest;
import tech.nilu.bots.dto.request.ThirdpartyBotExecuteResponse;
import tech.nilu.bots.dto.response.BountyCreateResponse;
import tech.nilu.bots.service.BountyService;

@RestController
public class BountyController {

    @Autowired
    BountyService bountyService;


    @PostMapping("/bounty")
    public BountyCreateResponse createBounty(BountyCreateRequest request) {
        return bountyService.createBounty(request);
    }

    @PostMapping("/bounty/execute")
    public ThirdpartyBotExecuteResponse executeRequest(ThirdpartyBotExecuteRequest request) {
        return bountyService.executeRequest(request);
    }

}
