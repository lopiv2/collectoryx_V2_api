package com.collectoryx.collectoryxApi.image.service;

import com.collectoryx.collectoryxApi.image.rest.response.ImageResponse;
import com.collectoryx.collectoryxApi.image.model.Image;
import com.collectoryx.collectoryxApi.image.repository.ImageRepository;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.UUID;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class ImageService {

  private final ImageRepository imageRepository;

  public ImageService(ImageRepository imageRepository) {
    this.imageRepository = imageRepository;
  }

  public void save(MultipartFile file, String path) throws IOException {
    File files = new File(System.getProperty("user.dir")).getCanonicalFile();
    System.out.println("Parent directory : " + files.getParent() + "\\images");
    Path pathFinal = Paths.get(path);
    try {
      Files.copy(file.getInputStream(), pathFinal);
    } catch (Exception e) {
      throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
    }
  }

  public ImageResponse createImage(String name, MultipartFile fileName) {
    String path = FilenameUtils.getBaseName(fileName.getName())
        + "-" + UUID.randomUUID()
        + "." + FilenameUtils.getExtension(fileName.getOriginalFilename());
    Image image = Image.builder()
        .name(name)
        .path(path)
        .created(new Date())
        .build();
    try {
      save(fileName, path);
    } catch (IOException e) {
      e.printStackTrace();
    }
    image = this.imageRepository.save(image);
    ImageResponse imageResponse = toImageResponse(image);
    return imageResponse;
  }

  public ImageResponse getImage(Long imageId) {
    return this.imageRepository.findById(imageId)
        .map(this::toImageResponse)
        .orElseThrow(EntityNotFoundException::new);
  }

  public ImageResponse toImageResponse(Image image) {
    return ImageResponse.builder()
        .id(image.getId())
        .name(image.getName())
        .url(image.getPath())
        .created(image.getCreated())
        .build();
  }

}
