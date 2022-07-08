package com.collectoryx.collectoryxApi.image.controller;

import com.collectoryx.collectoryxApi.image.rest.response.ImageResponse;
import com.collectoryx.collectoryxApi.image.service.ImageService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import javax.validation.constraints.NotEmpty;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/images")
@CrossOrigin
public class ImageController {

  private final ImageService imageService;

  public ImageController(ImageService imageService) {
    this.imageService = imageService;
  }

  @PutMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  public Mono<ImageResponse> putImage(
      @Parameter(description = "Name of the image") @RequestPart("name") @NotEmpty String name,
      @Parameter(description = "Content of the image",
          content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE))
      @RequestPart("image") MultipartFile image) {
    ImageResponse imageResponse = this.imageService.createImage(name,image);
    //ImageResponse imageResponse=ImageResponse.builder().name("hola").path("hola").build();
    return Mono.just(imageResponse);

  }
}
