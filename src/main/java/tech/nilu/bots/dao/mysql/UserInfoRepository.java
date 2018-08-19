package tech.nilu.bots.dao.mysql;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tech.nilu.bots.dto.BotType;
import tech.nilu.bots.model.mysql.UserInfo;

import java.util.Optional;

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo,Long> {
    @Query("select u from UserInfo u where u.botType = :botType and u.userId = :id")
    Optional<UserInfo> findByBotTypeAndUser(@Param("botType") BotType botType, @Param("id") String id);
}
