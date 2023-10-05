package com.linkzilla.service;

import com.google.common.hash.Hashing;
import com.google.common.hash.Hashing;
import com.linkzilla.dto.UrlDto;
import com.linkzilla.exceptions.LongUrlNotFoundException;
import com.linkzilla.models.Url;
import com.linkzilla.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class ShortenerServiceTest {
  @InjectMocks
  private ShortenerService service;

  @Mock
  UrlRepository repository;

  @Spy
  ModelMapper mapper;

  @Test
  void getLongUrl_shouldReturnLongUrl_whenGetShortcut() throws LongUrlNotFoundException {
    String shortUrl = "http://short";
    Url url = new Url("123", "http://short", "http://long");
    when(repository.findUrlByShortcut(shortUrl)).thenReturn(Optional.of(url));
    assertEquals(url.getLongUrl(), service.getLongUrl(shortUrl));
  }

  @Test
  void getLongUrl_shouldThrowException_whenGetShortcutWhichNotFoundInDb() throws LongUrlNotFoundException {
    String shortUrl = "http://short";
    when(repository.findUrlByShortcut(shortUrl)).thenReturn(Optional.empty());
    assertThrows(LongUrlNotFoundException.class, () -> service.getLongUrl(shortUrl));
  }

}
