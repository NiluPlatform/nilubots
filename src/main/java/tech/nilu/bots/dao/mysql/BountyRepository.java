package tech.nilu.bots.dao.mysql;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.nilu.bots.dto.BountyStatus;
import tech.nilu.bots.model.mysql.Bounty;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface BountyRepository extends JpaRepository<Bounty,Long> {
    Stream<Bounty> findByCheckDateBeforeAndStatusIn(Date d, List<BountyStatus> statuses);
    Optional<Bounty> findByBountyNiluAddress(String address);
}
