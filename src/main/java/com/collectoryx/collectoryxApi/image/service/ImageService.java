package com.collectoryx.collectoryxApi.image.service;

import com.collectoryx.collectoryxApi.image.model.Image;
import com.collectoryx.collectoryxApi.image.repository.ImageRepository;
import com.collectoryx.collectoryxApi.image.rest.response.ImageResponse;
import com.collectoryx.collectoryxApi.page.rest.request.PageFrontRequest;
import com.collectoryx.collectoryxApi.page.rest.response.PagingResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class ImageService {

  private final ImageRepository imageRepository;
  @Value("${collectoryx.upload-directory}")
  private String uploadDirectory;

  public ImageService(ImageRepository imageRepository) {
    this.imageRepository = imageRepository;
  }

  public void saveImage(MultipartFile file, String path) throws IOException {
    path = uploadDirectory + path;
    Path pathFinal = Paths.get(path);
    try {
      Files.copy(file.getInputStream(), pathFinal);
    } catch (Exception e) {
      throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
    }
  }

  /*public List<ImageResponse> getLocalImages() {
    List<Image> images = this.imageRepository.findAll();
    return StreamSupport.stream(images.spliterator(), false)
        .map(this::toImageResponse)
        .collect(Collectors.toList());
  }*/

  public PagingResponse<ImageResponse> getLocalImagesSearchQuery(
      PageFrontRequest request) {
    PageRequest pageRequest = PageRequest.of(request.getPage() != null ? request.getPage() : 0,
        request.getSize() != null ? request.getSize() : 500,
        Sort.by(request.getOrderDirection().contains("up") ? Order.asc(request.getOrderField())
            : Order.desc(request.getOrderField())));
    Page<Image> image = this.imageRepository
        .findByNameContaining(request.getSearch(),
            pageRequest);
    return getImagesResponsePagingResponse(image);
  }

  public PagingResponse<ImageResponse> getLocalImages(PageFrontRequest request) {
    PageRequest pageRequest = PageRequest.of(request.getPage() != null ? request.getPage() : 0,
        request.getSize() != null ? request.getSize() : 500,
        Sort.by(request.getOrderDirection().contains("up") ? Order.asc(request.getOrderField())
            : Order.desc(request.getOrderField())));
    Page<Image> image = this.imageRepository
        .findAll(pageRequest);
    return getImagesResponsePagingResponse(image);
  }

  public ImageResponse createImage(String name, MultipartFile fileName) {
    String path = name
        + "-" + RandomStringUtils.randomAlphanumeric(8)
        + "." + FilenameUtils.getExtension(fileName.getOriginalFilename());
    path = path.replaceAll(" ", "_");
    Image image = Image.builder()
        .name(name)
        .path(path)
        .created(new Date())
        .build();
    try {
      saveImage(fileName, path);
    } catch (IOException e) {
      throw new RuntimeException(e);
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

  private PagingResponse<ImageResponse> getImagesResponsePagingResponse(
      Page<Image> imagePage) {
    List<ImageResponse> imageResponseList = toImageListResponse(
        imagePage.getContent());
    return new PagingResponse<>(
        imageResponseList,
        imagePage.getNumber(),
        imagePage.getSize(),
        imagePage.getTotalPages(),
        imagePage.getTotalElements(), imagePage.isLast());
  }

  private List<ImageResponse> toImageListResponse(
      Iterable<Image> Image) {
    return StreamSupport.stream(Image.spliterator(), false)
        .map(p -> this.toImageResponse(p))
        .collect(Collectors.toList());
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
