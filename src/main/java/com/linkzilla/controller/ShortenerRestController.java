package com.linkzilla.controller;

import com.linkzilla.dto.UrlDto;
import com.linkzilla.exceptions.LongUrlNotFoundException;
import com.linkzilla.models.Url;
import com.linkzilla.service.ShortenerService;
import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**Class connecting URL object with UI*/
@RequestMapping("/api/url")
@RestController
public class ShortenerRestController {
  @Autowired
  ShortenerService service;

  private static final Logger log = LoggerFactory.getLogger(ShortenerRestController.class);

  /**Return long (actual) URL from database to UI using service*/
  @Cacheable(value = "urls", key = "#shortUrl")
  @GetMapping("/{shortUrl}")
  public String getUrl(@PathVariable String shortUrl) throws LongUrlNotFoundException {
    log.info("Actual URL was transfer to web-page.");
    return service.getLongUrl(shortUrl);
  }

  /**Return required URL (if get short return long, if get long - return short) from database to UI using service*/
  @PostMapping
  public String createUrl(@RequestBody UrlDto urlDto){
    UrlValidator urlValidator = new UrlValidator(UrlValidator.ALLOW_LOCAL_URLS);
    if (urlValidator.isValid(urlDto.getLongUrl())){
      log.info("Required URL was transfer to web-page.");
      String shortUrl = urlDto.getLongUrl().split("/")[urlDto.getLongUrl().split("/").length - 1];
      log.info("Short form from this URL is: {}", shortUrl);
      try {
        String fullUrl = service.getLongUrl(shortUrl);
        log.info("Full URL for this shortcut is: {}", fullUrl);
        return fullUrl;
      } catch (LongUrlNotFoundException exception) {
        return service.getShortUrl(urlDto);
      }
    }
    throw new NullPointerException();

  }


  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler({NoSuchElementException.class, NullPointerException.class})
  public Map<String, String> handleNoElementException() {
    Map<String, String> errors = new HashMap<>();
    errors.put("error", "This URL doesn't have shortcut or this URL is not valid.");
    log.error("Something gone wrong {}", errors);
    return errors;
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler({LongUrlNotFoundException.class})
  public Map<String, String> handleLongUrlNotFoundException() {
    Map<String, String> errors = new HashMap<>();
    errors.put("error", "This shortURL doesn't have long (actual) URL.");
    log.error("Something gone wrong {}", errors);
    return errors;
  }

}
