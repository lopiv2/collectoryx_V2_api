package com.collectoryx.collectoryxApi.images.repository;

import com.collectoryx.collectoryxApi.images.model.Images;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImagesRepository extends JpaRepository<Images, Long> {

}
