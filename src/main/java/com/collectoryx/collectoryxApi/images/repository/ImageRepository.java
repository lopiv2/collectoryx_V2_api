package com.collectoryx.collectoryxApi.images.repository;

import com.collectoryx.collectoryxApi.images.model.Image;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

  Optional<Image> findImageByName(String imageName);

}
