package com.collectoryx.collectoryxApi.collections.service;

import com.collectoryx.collectoryxApi.collections.model.CollectionItem;
import com.collectoryx.collectoryxApi.collections.model.CollectionItemsMetadata;
import com.collectoryx.collectoryxApi.collections.model.CollectionList;
import com.collectoryx.collectoryxApi.collections.model.CollectionMetadata;
import com.collectoryx.collectoryxApi.collections.model.CollectionSeriesList;
import com.collectoryx.collectoryxApi.collections.repository.CollectionItemRepository;
import com.collectoryx.collectoryxApi.collections.repository.CollectionItemsMetadataRepository;
import com.collectoryx.collectoryxApi.collections.repository.CollectionListRepository;
import com.collectoryx.collectoryxApi.collections.repository.CollectionMetadataRepository;
import com.collectoryx.collectoryxApi.collections.repository.CollectionSeriesListRepository;
import com.collectoryx.collectoryxApi.collections.rest.request.CollectionCreateItemImportApiRequest;
import com.collectoryx.collectoryxApi.collections.rest.request.CollectionCreateItemRequest;
import com.collectoryx.collectoryxApi.collections.rest.request.CollectionItemMetadataRequest;
import com.collectoryx.collectoryxApi.collections.rest.request.CollectionItemRequest;
import com.collectoryx.collectoryxApi.collections.rest.request.CollectionRequest;
import com.collectoryx.collectoryxApi.collections.rest.request.CollectionSerieListRequest;
import com.collectoryx.collectoryxApi.collections.rest.response.CSVHeadersResponse;
import com.collectoryx.collectoryxApi.collections.rest.response.CollectionItemMetadataResponse;
import com.collectoryx.collectoryxApi.collections.rest.response.CollectionItemsResponse;
import com.collectoryx.collectoryxApi.collections.rest.response.CollectionListResponse;
import com.collectoryx.collectoryxApi.collections.rest.response.CollectionMetadataResponse;
import com.collectoryx.collectoryxApi.collections.rest.response.CollectionResponse;
import com.collectoryx.collectoryxApi.collections.rest.response.CollectionSeriesListResponse;
import com.collectoryx.collectoryxApi.image.model.Image;
import com.collectoryx.collectoryxApi.image.repository.ImageRepository;
import com.collectoryx.collectoryxApi.image.rest.response.ImageResponse;
import com.collectoryx.collectoryxApi.user.model.User;
import com.collectoryx.collectoryxApi.user.repository.UserRepository;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.transaction.Transactional;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FilenameUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Transactional
public class CollectionService {

  private final CollectionItemRepository collectionItemRepository;
  private final CollectionListRepository collectionListRepository;
  private final ImageRepository imagesRepository;
  private final CollectionMetadataRepository collectionMetadataRepository;
  private final CollectionSeriesListRepository collectionSeriesListRepository;
  private final UserRepository userRepository;
  private final CollectionItemsMetadataRepository collectionItemsMetadataRepository;
  public WebClient webClient = WebClient.builder()
      .baseUrl("http://localhost:8083")
      .build();

  public CollectionService(CollectionItemRepository collectionItemRepository,
      CollectionListRepository collectionListRepository,
      ImageRepository imagesRepository, CollectionMetadataRepository collectionMetadataRepository,
      CollectionSeriesListRepository collectionSeriesListRepository,
      CollectionItemsMetadataRepository collectionItemsMetadataRepository,
      UserRepository userRepository) {
    this.collectionItemRepository = collectionItemRepository;
    this.collectionListRepository = collectionListRepository;
    this.imagesRepository = imagesRepository;
    this.collectionMetadataRepository = collectionMetadataRepository;
    this.collectionSeriesListRepository = collectionSeriesListRepository;
    this.userRepository = userRepository;
    this.collectionItemsMetadataRepository = collectionItemsMetadataRepository;
  }

  public long getCountOfCollections(Long id) {
    long count = this.collectionListRepository.countByUserId_Id(id);
    return count;
  }

  public long getCountOfCollectionItems(Long id) {
    long count = this.collectionItemRepository.countByCollection_UserId_Id(id);
    return count;
  }

  public CollectionItemsResponse getMostValuableItem(Long id) {
    CollectionItem item = this.collectionItemRepository.getMostValuableItem(id);
    return toCollectionItemsResponse(item);
  }

  public CollectionResponse createCollection(CollectionRequest request) throws NotFoundException {
    Image image = null;
    CollectionList collectionList = null;
    User user = this.userRepository.findById(request.getUserId())
        .orElseThrow(NotFoundException::new);
    if (request.getFile() != null) {
      image = this.imagesRepository.findImageByPath(request.getFile()).orElseThrow(
          NotFoundException::new);
      collectionList = CollectionList.builder()
          .name(request.getName())
          .logo(image)
          .template(request.getTemplate())
          .user(user)
          .build();
    } else {
      collectionList = CollectionList.builder()
          .name(request.getName())
          .template(request.getTemplate())
          .user(user)
          .build();
    }
    this.collectionListRepository.save(collectionList);
    ImageResponse imageResponse = toImageResponse(image);
    CollectionResponse collectionResponse = CollectionResponse.builder()
        .collection(request.getName())
        .template(request.getTemplate())
        .logo(imageResponse)
        .build();
    return collectionResponse;
  }

  public CollectionResponse createCollectionNew(CollectionRequest request)
      throws NotFoundException {
    Image image = null;
    CollectionList collectionList = null;
    User user = this.userRepository.findById(request.getUserId())
        .orElseThrow(NotFoundException::new);
    if (request.getFile() != null) {
      image = this.imagesRepository.findImageByPath(request.getFile()).orElseThrow(
          NotFoundException::new);
      collectionList = CollectionList.builder()
          .name(request.getName())
          .template(request.getTemplate())
          .logo(image)
          .user(user)
          .build();
    } else {
      collectionList = CollectionList.builder()
          .name(request.getName())
          .template(request.getTemplate())
          .user(user)
          .build();
    }
    this.collectionListRepository.save(collectionList);
    if (request.getMetadata().size() > 0) {
      List<CollectionMetadata> collectionMetadataList = new ArrayList<>();
      for (CollectionMetadata m : request.getMetadata()) {
        CollectionMetadata collectionMetadata = CollectionMetadata.builder()
            .id(m.getId())
            .name(m.getName())
            .type(m.getType())
            .collection(collectionList)
            .build();
        collectionMetadataList.add(collectionMetadata);
      }
      this.collectionMetadataRepository.saveAll(collectionMetadataList);
    }
    ImageResponse imageResponse = null;
    CollectionResponse collectionResponse = null;
    if (image == null) {
      collectionResponse = CollectionResponse.builder()
          .collection(request.getName())
          .template(request.getTemplate())
          .logo(null)
          .build();
    } else {
      imageResponse= toImageResponse(image);
      collectionResponse = CollectionResponse.builder()
          .collection(request.getName())
          .template(request.getTemplate())
          .logo(imageResponse)
          .build();
    }

    return collectionResponse;
  }

  public CollectionItemsResponse createItem(CollectionCreateItemRequest request)
      throws NotFoundException {
    Image image = null;
    ImageResponse imageResponse = null;
    if (request.getImage() != null) {
      image = this.imagesRepository.findImageByPath(request.getImage()).orElseThrow(
          NotFoundException::new);
      imageResponse = toImageResponse(image);
    }
    CollectionList collectionList = null;
    CollectionListResponse collectionListResponse = null;
    collectionList = this.collectionListRepository.findById(request.getCollection())
        .orElseThrow(NotFoundException::new);
    collectionListResponse = toCollectionListResponse(collectionList);

    CollectionSeriesList collectionSeriesList = null;
    CollectionSeriesListResponse collectionSeriesListResponse = null;
    collectionSeriesList = this.collectionSeriesListRepository.findById(request.getSerie())
        .orElseThrow(NotFoundException::new);
    collectionSeriesListResponse = toCollectionSerieListResponse(collectionSeriesList);
    CollectionItem collectionItem = null;
    CollectionItemsResponse collectionItemsResponse = null;
    if (imageResponse != null) {
      collectionItem = CollectionItem.builder()
          .name(request.getName())
          .serie(collectionSeriesList)
          .price(request.getPrice())
          .year(request.getYear())
          .adquiringDate(request.getAdquiringDate())
          .own(request.isOwn())
          .notes(request.getNotes())
          .image(image)
          .collection(collectionList)
          .build();
      this.collectionItemRepository.save(collectionItem);
      List<CollectionItemMetadataResponse> collectionItemMetadataResponseList = new ArrayList<>();
      for (CollectionItemMetadataRequest c : request.getMetadata()) {
        CollectionMetadata collectionMetadata = this.collectionMetadataRepository.findById(
            c.getId());
        CollectionItemsMetadata collectionItemsMetadata = CollectionItemsMetadata.builder()
            .item(collectionItem)
            .metadata(collectionMetadata)
            .value(c.getValue())
            .build();
        this.collectionItemsMetadataRepository.save(collectionItemsMetadata);
        collectionItemMetadataResponseList.add(
            toCollectionItemMetadataResponse(collectionItemsMetadata));
      }
      collectionItemsResponse = CollectionItemsResponse.builder()
          .name(request.getName())
          .serie(collectionSeriesListResponse)
          .price(request.getPrice())
          .year(request.getYear())
          .adquiringDate(request.getAdquiringDate())
          .own(request.isOwn())
          .notes(request.getNotes())
          .image(imageResponse)
          .metadata(collectionItemMetadataResponseList)
          .collection(collectionListResponse)
          .build();

    } else {
      collectionItem = CollectionItem.builder()
          .name(request.getName())
          .serie(collectionSeriesList)
          .price(request.getPrice())
          .year(request.getYear())
          .adquiringDate(request.getAdquiringDate())
          .own(request.isOwn())
          .notes(request.getNotes())
          .collection(collectionList)
          .build();
      this.collectionItemRepository.save(collectionItem);
      List<CollectionItemMetadataResponse> collectionItemMetadataResponseList = new ArrayList<>();
      for (CollectionItemMetadataRequest c : request.getMetadata()) {
        CollectionMetadata collectionMetadata = this.collectionMetadataRepository.findById(
            c.getId());
        CollectionItemsMetadata collectionItemsMetadata = CollectionItemsMetadata.builder()
            .item(collectionItem)
            .metadata(collectionMetadata)
            .value(c.getValue())
            .build();
        this.collectionItemsMetadataRepository.save(collectionItemsMetadata);
        collectionItemMetadataResponseList.add(
            toCollectionItemMetadataResponse(collectionItemsMetadata));
      }
      collectionItemsResponse = CollectionItemsResponse.builder()
          .name(request.getName())
          .serie(collectionSeriesListResponse)
          .price(request.getPrice())
          .year(request.getYear())
          .adquiringDate(request.getAdquiringDate())
          .own(request.isOwn())
          .notes(request.getNotes())
          .metadata(collectionItemMetadataResponseList)
          .collection(collectionListResponse)
          .build();
    }
    return collectionItemsResponse;
  }

  public void createItemNewSerie(CollectionCreateItemImportApiRequest request)
      throws NotFoundException {
    Image image = null;
    if (request.getImage() != null) {
      image = Image.builder()
          .path(request.getImage())
          .name(request.getName())
          .build();
    }
    CollectionList collectionList = null;
    collectionList = this.collectionListRepository.findById(request.getCollection())
        .orElseThrow(NotFoundException::new);
    CollectionSeriesList collectionSeriesList = null;
    collectionSeriesList = this.collectionSeriesListRepository.findByName(request.getSerie());
    if (collectionSeriesList == null) {
      collectionSeriesList = CollectionSeriesList.builder()
          .name(request.getSerie())
          .collection(collectionList)
          .build();
    }
    this.imagesRepository.save((image));
    this.collectionSeriesListRepository.save(collectionSeriesList);
    CollectionItem collectionItem = null;
    collectionItem = CollectionItem.builder()
        .name(request.getName())
        .serie(collectionSeriesList)
        .price(request.getPrice())
        .year(request.getYear())
        .adquiringDate(request.getAdquiringDate())
        .own(request.isOwn())
        .notes(request.getNotes())
        .image(image)
        .collection(collectionList)
        .build();
    this.collectionItemRepository.save(collectionItem);
  }

  public CollectionSeriesListResponse createSerie(CollectionSerieListRequest request)
      throws NotFoundException {
    Image image = null;
    ImageResponse imageResponse = null;
    if (request.getFile() != null) {
      image = this.imagesRepository.findImageByPath(request.getFile()).orElseThrow(
          NotFoundException::new);
      imageResponse = toImageResponse(image);
    }
    CollectionList collectionList = this.collectionListRepository.findById(
        request.getCollection()).orElseThrow(
        NotFoundException::new);
    CollectionResponse collectionResponse = toCollectionResponse(collectionList);
    CollectionSeriesList collectionSeriesList = CollectionSeriesList.builder()
        .name(request.getName())
        .logo(image)
        .collection(collectionList)
        .build();
    this.collectionSeriesListRepository.save(collectionSeriesList);
    CollectionSeriesListResponse collectionSeriesListResponse = null;
    if (imageResponse != null) {
      collectionSeriesListResponse = CollectionSeriesListResponse
          .builder()
          .name(request.getName())
          .collection(collectionResponse)
          .logo(imageResponse)
          .build();
    } else {
      collectionSeriesListResponse = CollectionSeriesListResponse
          .builder()
          .name(request.getName())
          .collection(collectionResponse)
          .build();
    }

    return collectionSeriesListResponse;
  }

  public boolean deleteCollectionItem(Long id) throws NotFoundException {
    CollectionItem col = this.collectionItemRepository.findById(id)
        .orElseThrow(NotFoundException::new);
    this.collectionItemRepository.deleteById(col.getId());
    return true;
  }

  public boolean deleteCollection(Long id) throws NotFoundException {
    CollectionList col = this.collectionListRepository.findById(id)
        .orElseThrow(NotFoundException::new);
    this.collectionListRepository.deleteById(col.getId());
    return true;
  }

  public boolean deleteCollectionCascade(Long id) throws NotFoundException {
    List<CollectionMetadata> collectionMetadata = this.collectionMetadataRepository
        .findByCollection_Id(id);
    this.collectionMetadataRepository.deleteAll(collectionMetadata);
    CollectionList col = this.collectionListRepository.findById(id)
        .orElseThrow(NotFoundException::new);
    this.collectionListRepository.deleteById(col.getId());
    return true;
  }

  public boolean deleteSerie(Long id) throws NotFoundException {
    CollectionSeriesList col = this.collectionSeriesListRepository.findById(id)
        .orElseThrow(NotFoundException::new);
    this.collectionSeriesListRepository.deleteById(col.getId());
    return true;
  }

  public CollectionListResponse getCollectionById(Long id) {
    CollectionList collection = null;
    try {
      collection = this.collectionListRepository
          .findById(id).orElseThrow(NotFoundException::new);
    } catch (NotFoundException e) {
      e.printStackTrace();
    }

    return toCollectionListResponse(collection);
  }

  public CollectionItemsResponse getCollectionItem(Long id) throws NotFoundException {
    CollectionItem col = this.collectionItemRepository.findById(id)
        .orElseThrow(NotFoundException::new);
    CollectionItemsResponse collectionItemsResponse = toCollectionItemResponse(col);
    return collectionItemsResponse;
  }

  public List<CollectionItemsResponse> getCollectionItemsById(Long id) {
    //final List<CollectionItemsResponse> collectionResponseList = new LinkedList<>();
    List<CollectionItem> collections = this.collectionItemRepository
        .findByCollection_Id(id);
    return StreamSupport.stream(collections.spliterator(), false)
        .map(this::toCollectionItemsResponse)
        .collect(Collectors.toList());
  }

  public List<CollectionItemsResponse> getItemsYear(Long id) {
    LocalDate firstDayOfThisYear = Year.now(ZoneId.systemDefault()).atDay(1);
    LocalDate lastDayOfThisYear = Year.now(ZoneId.systemDefault()).atDay(365);
    List<CollectionItem> collections = this.collectionItemRepository
        .getItemsPerYear(id, firstDayOfThisYear, lastDayOfThisYear);
    return StreamSupport.stream(collections.spliterator(), false)
        .map(this::toCollectionItemResponse)
        .collect(Collectors.toList());
  }

  public List<CollectionMetadataResponse> getMetadataFields(Long id) {
    List<CollectionMetadata> collectionMetadata = this.collectionMetadataRepository
        .findByCollection_Id(id);
    return StreamSupport.stream(collectionMetadata.spliterator(), false)
        .map(this::toCollectionMetadataResponse)
        .collect(Collectors.toList());
  }

  public List<CollectionItemsResponse> getMoneyFromAllItems(Long id) {
    //final List<CollectionItemsResponse> collectionResponseList = new LinkedList<>();
    List<CollectionItem> collections = this.collectionItemRepository
        .findByCollection_UserId_Id(id);
    return StreamSupport.stream(collections.spliterator(), false)
        .map(this::toCollectionItemsResponse)
        .collect(Collectors.toList());
  }

  public List<CollectionSeriesListResponse> listAllSeriesCollections(Long id) {
    List<CollectionSeriesList> collections = this.collectionSeriesListRepository
        .findAllByCollection_UserId(id);
    return StreamSupport.stream(collections.spliterator(), false)
        .map(this::toCollectionSerieListResponse)
        .collect(Collectors.toList());
  }

  public List<CollectionListResponse> listCollections(Long id) {
    List<CollectionList> collections = this.collectionListRepository
        .findAllByUser_Id(id);
    return StreamSupport.stream(collections.spliterator(), false)
        .map(this::toCollectionListResponse)
        .collect(Collectors.toList());
  }

  public List<CollectionMetadataResponse> listMetadataByCollection(Long id) {
    List<CollectionMetadata> collections = this.collectionMetadataRepository
        .findByCollection_Id(id);
    return StreamSupport.stream(collections.spliterator(), false)
        .map(this::toCollectionMetadataResponse)
        .collect(Collectors.toList());
  }

  public List<CollectionSeriesListResponse> listSeriesByCollection(Long id) {
    final List<CollectionSeriesListResponse> collectionSerieListResponseList = new LinkedList<>();
    List<CollectionSeriesList> collections = this.collectionSeriesListRepository
        .findAllByCollection_Id(id);
    return StreamSupport.stream(collections.spliterator(), false)
        .map(this::toCollectionSerieListResponse)
        .collect(Collectors.toList());
  }

  public CollectionResponse toggleAmbit(CollectionRequest item) throws NotFoundException {
    CollectionList collectionList = this.collectionListRepository.findById(item.getId())
        .orElseThrow(NotFoundException::new);
    collectionList.setAmbit(!item.getAmbit());
    this.collectionListRepository.save(collectionList);
    CollectionResponse collectionResponse = toCollectionResponse(collectionList);
    return collectionResponse;
  }

  public CollectionItemsResponse toggleOwn(CollectionItemRequest item) throws NotFoundException {
    CollectionItem collection = this.collectionItemRepository.findById(item.getId())
        .orElseThrow(NotFoundException::new);
    collection.setOwn(!item.getOwn());
    LocalDateTime today = LocalDateTime.now();
    if (item.getOwn()) {
      collection.setAdquiringDate(null);
    } else {
      collection.setAdquiringDate(java.sql.Timestamp.valueOf(today));
    }
    this.collectionItemRepository.save(collection);
    CollectionItemsResponse collectionItemsResponse = toCollectionItemResponse(collection);
    return collectionItemsResponse;
  }

  public CollectionItemsResponse toggleWish(CollectionItemRequest item) throws NotFoundException {
    CollectionItem collection = this.collectionItemRepository.findById(item.getId())
        .orElseThrow(NotFoundException::new);
    collection.setWanted(!item.getWanted());
    this.collectionItemRepository.save(collection);
    CollectionItemsResponse collectionItemsResponse = toCollectionItemResponse(collection);
    return collectionItemsResponse;
  }

  public String saveFile(MultipartFile file, String path) throws IOException {
    File files = new File(System.getProperty("user.dir")).getCanonicalFile();
    //path = files + "/src/main/resources/" + file;
    Path pathFinal = Paths.get(files + "/src/main/resources/" + path);
    try {
      Files.copy(file.getInputStream(), pathFinal, StandardCopyOption.REPLACE_EXISTING);
    } catch (Exception e) {
      throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
    }

    return pathFinal.toString();
  }

  public List<CSVHeadersResponse> getCSVHeaders(MultipartFile fileName) {
    long cont = 0;
    String path = fileName.getName()
        + "." + FilenameUtils.getExtension(fileName.getOriginalFilename());
    String finalFile = null;
    try {
      finalFile = saveFile(fileName, path);
    } catch (IOException e) {
      e.printStackTrace();
    }
    File file = new File(finalFile);
    try {
      fileName.transferTo(file);
    } catch (IOException e) {
      e.printStackTrace();
    }
    Reader in = null;
    try {
      in = new FileReader(file);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    String[] HEADERS = {"Image", "Name", "Collection", "Serie", "Year", "Price", "Own", "Notes"};

    Iterable<CSVRecord> records = null;
    CSVParser csvParser = null;
    List<Map<String, Integer>> list = new ArrayList<>();
    List<String> result2 = new ArrayList<>();
    List<CSVHeadersResponse> csvHeadersResponseList = new ArrayList<>();
    try {
      csvParser = new CSVParser(in, CSVFormat.DEFAULT.withHeader());
      Map<String, Integer> header = csvParser.getHeaderMap();
      list.add(header);
      //list.forEach(System.out::println);
      for (int x = 0; x < list.size(); x++) {
        result2 = list.get(x).keySet().stream().collect(Collectors.toList());
      }
      for (int x = 0; x < result2.size(); x++) {
        csvHeadersResponseList.add(CSVHeadersResponse.builder().name(result2.get(x)).build());
      }
      //System.out.println(result2);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return StreamSupport.stream(csvHeadersResponseList.spliterator(), false)
        .map(this::toCSVHeadersResponse)
        .collect(Collectors.toList());
  }

  public int parseCSV(String HEADERS) {
    //Convierte el string Headers en un Array de JSON
    JSONArray jsonArr = new JSONArray(HEADERS);
    File files = null;
    try {
      files = new File(System.getProperty("user.dir")).getCanonicalFile();
    } catch (IOException e) {
      e.printStackTrace();
    }
    Path pathFinal = Paths.get(files + "/src/main/resources/file.csv");
    File file = new File(pathFinal.toString());
    Reader in = null;
    try {
      in = new FileReader(file);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    Iterable<CSVRecord> records = null;
    try {
      records = CSVFormat.DEFAULT
          .withHeader(HEADERS)
          .withFirstRecordAsHeader()
          .parse(in);
    } catch (IOException e) {
      e.printStackTrace();
    }
    int cont = 0;
    CollectionList collectionList = null;
    CollectionSeriesList collectionSeriesList = null;
    String name = "";
    String serie = "";
    String collection = "";
    String own = "";
    String price = "";
    String year = "";
    String notes = "";
    String image = "";
    //Leo los mapeos de los headers
    for (int v = 0; v < jsonArr.length(); v++) {
      JSONObject jsonObj = jsonArr.getJSONObject(v);
      if (!jsonObj.has("collection")) {
        switch (jsonObj.getString("original")) {
          case "name":
            name = jsonObj.getString("new");
            break;
          case "notes":
            notes = jsonObj.getString("new");
            break;
          case "serie":
            serie = jsonObj.getString("new");
            break;
          case "collection":
            collection = jsonObj.getString("new");
            break;
          case "image":
            image = jsonObj.getString("new");
            break;
          case "own":
            own = jsonObj.getString("new");
            break;
          case "price":
            price = jsonObj.getString("new");
            break;
          case "year":
            year = jsonObj.getString("new");
            break;
        }
      } else {
        collectionList = checkCollection(jsonObj.getLong("collection"));
      }
    }
    //Leo registro a registro del documento csv
    for (CSVRecord record : records) {
      collectionSeriesList = checkSerie(record.get(serie), collectionList);
      float pric;
      boolean ow = false;
      int ye;
      try {
        pric = Float.valueOf(record.get(price));
      } catch (NumberFormatException e) {
        pric = 0;
      }
      try {
        ye = Integer.valueOf(record.get(year));
      } catch (NumberFormatException e) {
        ye = 2022;
      }
      try {
        ow = Boolean.parseBoolean(record.get(own));
      } catch (IllegalArgumentException e) {
        ow = false;
      }

      CollectionItem collectionItem = CollectionItem.builder()
          .name(record.get(name))
          .own(ow)
          .price(pric)
          .notes(record.get(notes))
          .year(ye)
          .wanted(false)
          .serie(collectionSeriesList)
          .collection(collectionList)
          .build();
      this.collectionItemRepository.save(collectionItem);
      //System.out.println(collectionItem);
      cont++;
    }
    return cont;
  }

  public CollectionList checkCollection(Long id) {
    CollectionList collectionList = null;
    try {
      collectionList = this.collectionListRepository.findById(id)
          .orElseThrow(NotFoundException::new);
    } catch (NotFoundException e) {
      e.printStackTrace();
    }
    return collectionList;
  }

  public CollectionSeriesList checkSerie(String name,
      CollectionList collectionList) {
    CollectionSeriesList collectionSeriesList = null;
    if (name.isBlank()) {
      name = "Null";
    }
    collectionSeriesList = this.collectionSeriesListRepository.findByName(name);
    if (collectionSeriesList == null) {
      collectionSeriesList = CollectionSeriesList.builder()
          .name(name)
          .collection(collectionList)
          .build();
    }
    this.collectionSeriesListRepository.save(collectionSeriesList);
    return collectionSeriesList;
  }

  public CollectionItemsResponse updateItem(CollectionCreateItemRequest request)
      throws NotFoundException {
    Image image = null;
    ImageResponse imageResponse = null;
    if (request.getImage() != null) {
      image = this.imagesRepository.findImageByPath(request.getImage()).orElseThrow(
          NotFoundException::new);
      imageResponse = toImageResponse(image);
    }
    final Image imageRight = image;
    CollectionList collectionList = null;
    CollectionListResponse collectionListResponse = null;
    collectionList = this.collectionListRepository.findById(request.getCollection())
        .orElseThrow(NotFoundException::new);
    collectionListResponse = toCollectionListResponse(collectionList);

    CollectionSeriesListResponse collectionSeriesListResponse = null;
    final CollectionSeriesList collectionSeriesList = this.collectionSeriesListRepository.findById(
            request.getSerie())
        .orElseThrow(NotFoundException::new);
    collectionSeriesListResponse = toCollectionSerieListResponse(collectionSeriesList);
    CollectionItem collectionItem = this.collectionItemRepository.findById(request.getId())
        .map(item -> {
          item.setName(request.getName());
          item.setSerie(collectionSeriesList);
          item.setPrice(request.getPrice());
          item.setYear(request.getYear());
          item.setAdquiringDate(request.getAdquiringDate());
          item.setOwn(request.isOwn());
          item.setNotes(request.getNotes());
          item.setImage(imageRight);
          return this.collectionItemRepository.save(item);
        }).orElseThrow(NotFoundException::new);
    List<CollectionItemMetadataResponse> collectionItemMetadataResponseList = new ArrayList<>();
    for (CollectionItemMetadataRequest c : request.getMetadata()) {
      //CollectionMetadata collectionMetadata = this.collectionMetadataRepository.findById(c.getId());
      CollectionItemsMetadata collectionItemsMetadata = this.collectionItemsMetadataRepository.
          findById(Long.parseLong(c.getId())).map(item -> {
            item.setValue(c.getValue());
            return this.collectionItemsMetadataRepository.save(item);
          }).orElseThrow(NotFoundException::new);
      collectionItemMetadataResponseList.add(
          toCollectionItemMetadataResponse(collectionItemsMetadata));
    }
    CollectionItemsResponse collectionItemsResponse = null;
    collectionItemsResponse = CollectionItemsResponse.builder()
        .name(request.getName())
        .serie(collectionSeriesListResponse)
        .price(request.getPrice())
        .year(request.getYear())
        .adquiringDate(request.getAdquiringDate())
        .own(request.isOwn())
        .notes(request.getNotes())
        .image(imageResponse)
        .collection(collectionListResponse)
        .metadata(collectionItemMetadataResponseList)
        .build();
    return collectionItemsResponse;
  }

  private CSVHeadersResponse toCSVHeadersResponse(CSVHeadersResponse request) {
    return CSVHeadersResponse.builder()
        .name(request.getName())
        .build();
  }

  private CollectionItemsResponse toCollectionItemsResponse(CollectionItem collection) {
    ImageResponse image = null;
    if (collection.getImage() != null) {
      try {
        image = toImageResponse(
            this.imagesRepository.findById(collection.getImage().getId())
                .orElseThrow(NotFoundException::new));
      } catch (NotFoundException e) {
        e.printStackTrace();
      }
    }

    CollectionSeriesListResponse collectionSeriesListResponse = null;
    try {
      collectionSeriesListResponse = toCollectionSerieListResponse(
          this.collectionSeriesListRepository.findById(collection.getSerie().getId())
              .orElseThrow(NotFoundException::new));
    } catch (NotFoundException e) {
      e.printStackTrace();
    }

    CollectionListResponse collectionListResponse = null;
    try {
      collectionListResponse = toCollectionListResponse(
          this.collectionListRepository.findById(collection.getCollection().getId())
              .orElseThrow(NotFoundException::new));
    } catch (NotFoundException e) {
      e.printStackTrace();
    }

    List<CollectionItemMetadataResponse> collectionItemMetadataResponseList = StreamSupport
        .stream(this.collectionItemsMetadataRepository
            .findByItem_Id(collection.getId()).spliterator(), false)
        .map(this::toCollectionItemMetadataResponse).collect(
            Collectors.toList());
    return CollectionItemsResponse.builder()
        .id(collection.getId())
        .name(collection.getName())
        .image(image)
        .serie(collectionSeriesListResponse)
        .collection(collectionListResponse)
        .year(collection.getYear())
        .price(collection.getPrice())
        .own(collection.isOwn())
        .wanted(collection.isWanted())
        .notes(collection.getNotes())
        .adquiringDate(collection.getAdquiringDate())
        .metadata(collectionItemMetadataResponseList)
        .build();
  }

  private CollectionSeriesListResponse toCollectionSerieListResponse(
      CollectionSeriesList collection) {
    ImageResponse image = null;
    if (collection.getLogo() != null) {
      try {
        image = toImageResponse(
            this.imagesRepository.findById(collection.getLogo().getId())
                .orElseThrow(NotFoundException::new));
      } catch (NotFoundException e) {
        e.printStackTrace();
      }
    }
    CollectionList collectionList = null;
    try {
      collectionList = this.collectionListRepository.findById(
          collection.getCollection().getId()).orElseThrow(
          NotFoundException::new);
    } catch (NotFoundException e) {
      e.printStackTrace();
    }
    CollectionResponse collectionResponse = toCollectionResponse(collectionList);
    if (image != null) {
      return CollectionSeriesListResponse.builder()
          .id(collection.getId())
          .name(collection.getName())
          .collection(collectionResponse)
          .logo(image)
          .build();
    } else {
      return CollectionSeriesListResponse.builder()
          .id(collection.getId())
          .name(collection.getName())
          .collection(collectionResponse)
          .build();
    }
  }

  private CollectionItemsResponse toCollectionItemResponse(CollectionItem collection) {
    ImageResponse image = null;
    if (collection.getImage() != null) {
      try {
        image = toImageResponse(
            this.imagesRepository.findById(collection.getImage().getId())
                .orElseThrow(NotFoundException::new));
      } catch (NotFoundException e) {
        e.printStackTrace();
      }
    }
    CollectionSeriesListResponse collectionSeriesListResponse = null;
    try {
      collectionSeriesListResponse = toCollectionSerieListResponse(
          this.collectionSeriesListRepository.findById(collection.getSerie().getId())
              .orElseThrow(NotFoundException::new));
    } catch (NotFoundException e) {
      e.printStackTrace();
    }
    CollectionListResponse collectionListResponse = null;
    try {
      collectionListResponse = toCollectionListResponse(
          this.collectionListRepository.findById(collection.getCollection().getId())
              .orElseThrow(NotFoundException::new));
    } catch (NotFoundException e) {
      e.printStackTrace();
    }

    if (image != null) {
      return CollectionItemsResponse.builder()
          .id(collection.getId())
          .name(collection.getName())
          .image(image)
          .adquiringDate(collection.getAdquiringDate())
          .collection(collectionListResponse)
          .notes(collection.getNotes())
          .price(collection.getPrice())
          .year(collection.getYear())
          .serie(collectionSeriesListResponse)
          .own(collection.isOwn())
          .wanted(collection.isWanted())
          .build();
    } else {
      return CollectionItemsResponse.builder()
          .id(collection.getId())
          .name(collection.getName())
          .adquiringDate(collection.getAdquiringDate())
          .collection(collectionListResponse)
          .notes(collection.getNotes())
          .price(collection.getPrice())
          .year(collection.getYear())
          .serie(collectionSeriesListResponse)
          .own(collection.isOwn())
          .wanted(collection.isWanted())
          .build();
    }

  }

  private CollectionListResponse toCollectionListResponse(CollectionList request) {
    ImageResponse image = null;
    List<CollectionMetadataResponse> collectionMetadata = null;
    collectionMetadata = StreamSupport.stream(
            this.collectionMetadataRepository.findByCollection_Id(
                request.getId()).spliterator(), false).map(this::toCollectionMetadataResponse)
        .collect(Collectors.toList());
    if (request.getLogo() != null) {
      try {
        image = toImageResponse(
            this.imagesRepository.findById(request.getLogo().getId())
                .orElseThrow(NotFoundException::new));
      } catch (NotFoundException e) {
        e.printStackTrace();
      }
      return CollectionListResponse.builder()
          .id(request.getId())
          .name(request.getName())
          .ambit(request.getAmbit())
          .logo(image)
          .template(request.getTemplate())
          .metadata(collectionMetadata)
          .build();
    } else {
      return CollectionListResponse.builder()
          .id(request.getId())
          .name(request.getName())
          .ambit(request.getAmbit())
          .template(request.getTemplate())
          .metadata(collectionMetadata)
          .build();
    }
  }

  private CollectionSeriesListResponse toCollectionSerieListResponse(
      CollectionList collection) {
    ImageResponse image = null;
    if (collection.getLogo() != null) {
      try {
        image = toImageResponse(
            this.imagesRepository.findById(collection.getLogo().getId())
                .orElseThrow(NotFoundException::new));
      } catch (NotFoundException e) {
        e.printStackTrace();
      }
      return CollectionSeriesListResponse.builder()
          .id(collection.getId())
          .name(collection.getName())
          .logo(image)
          .build();
    } else {
      return CollectionSeriesListResponse.builder()
          .id(collection.getId())
          .name(collection.getName())
          .build();
    }
  }

  private CollectionResponse toCollectionResponse(CollectionList request) {
    ImageResponse image = null;
    if (request.getLogo() != null) {
      try {
        image = toImageResponse(
            this.imagesRepository.findById(request.getLogo().getId())
                .orElseThrow(NotFoundException::new));
      } catch (NotFoundException e) {
        e.printStackTrace();
      }
      return CollectionResponse.builder()
          .id(request.getId())
          .collection(request.getName())
          .ambit(request.getAmbit())
          .logo(image)
          .build();
    } else {
      return CollectionResponse.builder()
          .id(request.getId())
          .collection(request.getName())
          .ambit(request.getAmbit())
          .build();
    }
  }

  private CollectionMetadataResponse toCollectionMetadataResponse(CollectionMetadata request) {
    if (request != null) {
      return CollectionMetadataResponse.builder()
          .id(request.getId())
          .name(request.getName())
          .type(request.getType())
          .build();
    }
    return null;
  }

  private CollectionItemMetadataResponse toCollectionItemMetadataResponse(
      CollectionItemsMetadata request) {
    CollectionMetadata collectionMetadata = null;
    collectionMetadata = this.collectionMetadataRepository.findById(
        request.getMetadata().getId());
    return CollectionItemMetadataResponse.builder()
        .id(request.getId())
        .value(request.getValue())
        .type(collectionMetadata.getType())
        .name(collectionMetadata.getName())
        .build();
  }

  private ImageResponse toImageResponse(Image request) {
    return ImageResponse.builder()
        .id(request.getId())
        .name(request.getName())
        .path(request.getPath())
        .created(request.getCreated())
        .build();
  }

}
