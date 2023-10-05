package com.linkzilla.service;
import com.google.common.hash.Hashing;
import com.linkzilla.dto.UrlDto;
import com.linkzilla.exceptions.LongUrlNotFoundException;
import com.linkzilla.models.Url;
import com.linkzilla.repository.UrlRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;

/**Class with logic for searching long or short URLs or creating shortcuts if needed*/
@Service
public class ShortenerService {

  private final UrlRepository repository;
  private static final Logger log = LoggerFactory.getLogger(ShortenerService.class);

  private final ModelMapper modelMapper;

  @Autowired
  public ShortenerService(UrlRepository repository, ModelMapper modelMapper) {
    this.repository = repository;
    this.modelMapper = modelMapper;
  }


  /**Method for create shortcut id for long version of URL, save it to db via repository and return short URL.*/
  public String getShortUrl(UrlDto urlDto){
    String shortcut = Hashing.murmur3_32_fixed().hashString(urlDto.getLongUrl(), StandardCharsets.UTF_8).toString();
    if (repository.findUrlByShortcut(shortcut).isEmpty()) {
      log.info("Shortcut for this URL generated is: {}", shortcut);
      urlDto.setShortcut(shortcut);
      Url url = modelMapper.map(urlDto, Url.class);
      repository.save(url);
      log.info("Shortcut was saved to db");
    }
    HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
    log.info("Short URL was created");
    return baseUrl + "/api/url/" + shortcut;
  }

  /**Method find long url for received shortcut.*/
  public String getLongUrl(String shortUrl) throws LongUrlNotFoundException {
    if (repository.findUrlByShortcut(shortUrl).isPresent()){
      log.info("Get long url from repository");
      return repository.findUrlByShortcut(shortUrl).get().getLongUrl();
    }
    log.error("Long URL for this shortcut does not exists.");
    throw new LongUrlNotFoundException("No actual URL for this short form.");
  }

}
