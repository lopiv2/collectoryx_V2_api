package com.collectoryx.collectoryxApi.user.repository;

import com.collectoryx.collectoryxApi.user.model.UserFeeds;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserFeedsRepository extends JpaRepository<UserFeeds, Long> {

  List<UserFeeds> findAllByUserId(Long id);

  UserFeeds findByUserId(Long id);

  UserFeeds findByUserIdAndName(Long id, String title);
}
