package com.collectoryx.collectoryxApi.user.repository;

import com.collectoryx.collectoryxApi.user.model.UserEvents;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserEventsRepository extends JpaRepository<UserEvents, Long> {

  @Query(value = "select * from users_events where user_id = :userId "
      + "AND YEAR(start)=:year AND MONTH(start)=:month", nativeQuery = true)
  List<UserEvents> findByPeriod(@Param("userId") Long id, @Param("month") Long month,
      @Param("year") Long year);

}
