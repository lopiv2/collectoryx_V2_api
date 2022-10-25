package com.collectoryx.collectoryxApi.image.repository;

import com.collectoryx.collectoryxApi.image.model.Image;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

  Optional<Image> findImageByName(String imageName);

  Optional<Image> findImageByPath(String imageName);

  Page<Image> findAllByPathNotContaining(String http, Pageable pageable);

  Page<Image> findByNameContainingAndPathNotContaining(String search, String http,
      Pageable pageable);

  boolean existsByPath(String image);
}
