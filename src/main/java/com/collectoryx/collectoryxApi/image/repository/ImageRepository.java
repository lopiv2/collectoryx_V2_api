package com.collectoryx.collectoryxApi.image.repository;

import com.collectoryx.collectoryxApi.image.model.Image;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

  Optional<Image> findImageByName(String imageName);

  Optional<Image> findImageByPath(String imageName);

}
