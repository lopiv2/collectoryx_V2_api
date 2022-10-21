package com.collectoryx.collectoryxApi.image.service;

import com.collectoryx.collectoryxApi.collections.model.CollectionItem;
import com.collectoryx.collectoryxApi.collections.model.CollectionList;
import com.collectoryx.collectoryxApi.collections.model.CollectionSeriesList;
import com.collectoryx.collectoryxApi.collections.repository.CollectionItemRepository;
import com.collectoryx.collectoryxApi.collections.repository.CollectionListRepository;
import com.collectoryx.collectoryxApi.collections.repository.CollectionSeriesListRepository;
import com.collectoryx.collectoryxApi.image.model.Image;
import com.collectoryx.collectoryxApi.image.repository.ImageRepository;
import com.collectoryx.collectoryxApi.image.rest.request.ImageRequest;
import com.collectoryx.collectoryxApi.image.rest.response.ImageResponse;
import com.collectoryx.collectoryxApi.page.rest.request.PageFrontRequest;
import com.collectoryx.collectoryxApi.page.rest.response.PagingResponse;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.imageio.ImageIO;
import javax.transaction.Transactional;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
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
  private final CollectionListRepository collectionListRepository;
  private final CollectionItemRepository collectionItemRepository;
  private CollectionSeriesListRepository collectionSeriesListRepository;
  @Value("${collectoryx.upload-directory}")
  private String uploadDirectory;

  public ImageService(ImageRepository imageRepository,
      CollectionSeriesListRepository collectionSeriesListRepository,
      CollectionItemRepository collectionItemRepository,
      CollectionListRepository collectionListRepository) {
    this.imageRepository = imageRepository;
    this.collectionListRepository = collectionListRepository;
    this.collectionItemRepository = collectionItemRepository;
    this.collectionSeriesListRepository = collectionSeriesListRepository;
  }

  public boolean deleteImage(Long id) throws NotFoundException {
    Image image = this.imageRepository.findById(id)
        .orElseThrow(NotFoundException::new);
    File imgFile = new File(uploadDirectory + image.getPath());
    if (imgFile.delete()) {
      System.out.println("Image file deleted");
    } else {
      System.out.println("Failed to delete the file");
    }
    List<CollectionList> collectionLists = this.collectionListRepository
        .findByLogo_Id(id);
    for (CollectionList item : collectionLists) {
      item.setLogo(null);
      this.collectionListRepository.save(item);
    }
    List<CollectionSeriesList> collectionSeriesLists = this.collectionSeriesListRepository
        .findByLogo_Id(id);
    for (CollectionSeriesList item : collectionSeriesLists) {
      item.setLogo(null);
      this.collectionSeriesListRepository.save(item);
    }
    List<CollectionItem> collectionItemList = this.collectionItemRepository
        .findByImage_Id(id);
    for (CollectionItem item : collectionItemList) {
      item.setImage(null);
      this.collectionItemRepository.save(item);
    }
    this.imageRepository.deleteById(image.getId());
    return true;
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

  public ImageResponse updateImage(ImageRequest request)
      throws NotFoundException {
    Image image = null;

    if (request.getPath() != null) {
      image = this.imageRepository.findById(request.getId()).map(item -> {
        item.setName(request.getName());
        int iend = item.getPath().indexOf("-");
        String subString = "";
        if (iend != -1) {
          //Get the name of the file from "-" until the end
          subString = item.getPath().substring(iend, item.getPath().length());
        }
        File source = new File(uploadDirectory + item.getPath());
        File target = new File(uploadDirectory + request.getName() + subString);
        try {
          FileUtils.copyFile(source, target);
        } catch (IOException e) {
          e.printStackTrace();
        }

        Path p = Paths.get(uploadDirectory + item.getPath());
        try {
          Files.delete(p);
        } catch (IOException e) {
          e.printStackTrace();
        }
        item.setPath(request.getName() + subString);
        return this.imageRepository.save(item);
      }).orElseThrow(NotFoundException::new);
    }
    try {
      return toImageResponse(image);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public PagingResponse<ImageResponse> getLocalImagesSearchQuery(
      PageFrontRequest request) {
    PageRequest pageRequest = PageRequest.of(request.getPage() != null ? request.getPage() : 0,
        request.getSize() != null ? request.getSize() : 500,
        Sort.by(request.getOrderDirection().contains("up") ? Order.asc(request.getOrderField())
            : Order.desc(request.getOrderField())));
    Page<Image> image = this.imageRepository
        .findByNameContainingAndPathNotContaining(request.getSearch(), "http",
            pageRequest);
    return getImagesResponsePagingResponse(image);
  }

  public PagingResponse<ImageResponse> getLocalImages(PageFrontRequest request) {
    PageRequest pageRequest = PageRequest.of(request.getPage() != null ? request.getPage() : 0,
        request.getSize() != null ? request.getSize() : 500,
        Sort.by(request.getOrderDirection().contains("up") ? Order.asc(request.getOrderField())
            : Order.desc(request.getOrderField())));
    Page<Image> image = this.imageRepository
        .findAllByPathNotContaining("http", pageRequest);
    return getImagesResponsePagingResponse(image);
  }

  /*public ImageResponse getImage(Long imageId) {
    return this.imageRepository.findById(imageId)
        .map(this::toImageResponse)
        .orElseThrow(EntityNotFoundException::new);
  }*/

  public ImageResponse createImage(String name, MultipartFile fileName) throws IOException {
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
        .map(p -> {
          try {
            return this.toImageResponse(p);
          } catch (IOException e) {
            e.printStackTrace();
          }
          return null;
        })
        .collect(Collectors.toList());
  }

  public String getImageDimensions(String path) {
    File imageFile = new File(uploadDirectory + path);
    if (imageFile.exists() && !imageFile.isDirectory()) {
      FileInputStream fisLocal = null;
      try {
        fisLocal = new FileInputStream(imageFile);
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }
      try {
        byte[] imageByteLocal = IOUtils.toByteArray(fisLocal);
      } catch (IOException e) {
        e.printStackTrace();
      }

//converting file format
      FileInputStream fis = null;
      try {
        fis = new FileInputStream(imageFile);
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }
//Get Image height and width
      BufferedImage bimg = null;
      try {
        bimg = ImageIO.read(fis);
      } catch (IOException e) {
        e.printStackTrace();
      }
      int width = bimg.getWidth();
      int height = bimg.getHeight();
      return (width + " x " + height);
    }
    return null;
  }

  public String imageFileSize(String fileName) {
    Path path = Paths.get(uploadDirectory + fileName);
    File imageFile = new File(String.valueOf(path));
    if (imageFile.exists() && !imageFile.isDirectory()) {
      long bytes = 0;
      try {

        // size of a file (in bytes)
        bytes = Files.size(path);
        //System.out.println(String.format("%,d bytes", bytes));
        //System.out.println(String.format("%,d kilobytes", bytes / 1024));

      } catch (IOException e) {
        e.printStackTrace();
      }
      return String.format("%,d kB", bytes / 1024);
    }
    return null;
  }

  public ImageResponse toImageResponse(Image image) throws IOException {
    String dimensions = getImageDimensions(image.getPath());
    String size = imageFileSize(image.getPath());
    return ImageResponse.builder()
        .id(image.getId())
        .name(image.getName())
        .dimensions(dimensions)
        .size(size)
        .path(image.getPath())
        .created(image.getCreated())
        .build();
  }

}
