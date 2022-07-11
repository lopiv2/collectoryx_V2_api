package com.collectoryx.collectoryxApi.image.service;

import com.collectoryx.collectoryxApi.image.model.Image;
import com.collectoryx.collectoryxApi.image.repository.ImageRepository;
import com.collectoryx.collectoryxApi.image.rest.response.ImageResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class ImageService {

  private final ImageRepository imageRepository;

  public ImageService(ImageRepository imageRepository) {
    this.imageRepository = imageRepository;
  }

  public void saveImage(MultipartFile file, String path) throws IOException {
    File files = new File(System.getProperty("user.dir")).getCanonicalFile();
    path=files.getParent() + "\\images\\"+path;
    Path pathFinal = Paths.get(path);
    try {
      Files.copy(file.getInputStream(), pathFinal);
    } catch (Exception e) {
      throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
    }
  }

  public ImageResponse createImage(String name, MultipartFile fileName) {
    String path = name
        + "-" + RandomStringUtils.randomAlphanumeric(8)
        + "." + FilenameUtils.getExtension(fileName.getOriginalFilename());
    path=path.replaceAll(" ","_");
    Image image = Image.builder()
        .name(name)
        .path(path)
        .created(new Date())
        .build();
    try {
      saveImage(fileName, path);
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
        .path(image.getPath())
        .created(image.getCreated())
        .build();
  }

}
