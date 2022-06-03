package com.collectoryx.collectoryxApi.collections.rest.request;

import com.collectoryx.collectoryxApi.collections.model.CollectionTypes;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CollectionRequest {

  @NotEmpty
  protected String name;

  @NotEmpty
  protected CollectionTypes template;

  @NotEmpty
  protected MultipartFile file;

}
