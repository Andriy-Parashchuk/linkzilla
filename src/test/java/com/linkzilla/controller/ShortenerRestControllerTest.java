package com.linkzilla.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linkzilla.dto.UrlDto;
import com.linkzilla.exceptions.LongUrlNotFoundException;
import com.linkzilla.service.ShortenerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(ShortenerRestController.class)
@ContextConfiguration(classes = ShortenerRestController.class)
public class ShortenerRestControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  ShortenerService service;

  @InjectMocks
  @Autowired
  ShortenerRestController controller;


  @Test
  void getUrl_shouldReturnLongUrl_whenGetShortcut() throws Exception {
    when(service.getLongUrl("shortcut")).thenReturn("longUrl");
    this.mockMvc.perform(get("/api/url/shortcut"))
            .andExpect(status().isOk())
            .andExpect(content().contentType("text/plain;charset=UTF-8"))
            .andExpect(content().string("longUrl"))
            .andDo(print());

    verify(service, times(1)).getLongUrl(anyString());
  }

  @Test
  void getUrl_shouldReturnException_whenGetShortcut() throws Exception {
    when(service.getLongUrl("shortcut")).thenThrow(LongUrlNotFoundException.class);
    this.mockMvc.perform(get("/api/url/shortcut"))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.error", is("This shortURL doesn't have long (actual) URL.")))
            .andDo(print());

    verify(service, times(1)).getLongUrl(anyString());
  }

  @Test
  void createUrl_shouldReturnShort_whenGetLongUrl() throws Exception {
    UrlDto urlDto = new UrlDto(null,null, "http://long");
    when(service.getShortUrl(urlDto)).thenReturn("http://short");
    when(service.getLongUrl("long")).thenThrow(LongUrlNotFoundException.class);
    this.mockMvc.perform(post("/api/url").contentType("application/json")
                    .content(objectMapper.writeValueAsString(urlDto)))
            .andExpect(status().isOk())
            .andExpect(content().contentType("text/plain;charset=UTF-8"))
            .andExpect(content().string("http://short"))
            .andDo(print());

    verify(service, times(1)).getShortUrl(any());
  }

  @Test
  void createUrl_shouldReturnLongUrl_whenGetShortUrl() throws Exception {
    UrlDto urlDto = new UrlDto(null,null, "http://long");
    when(service.getLongUrl("long")).thenReturn("http://short");
    this.mockMvc.perform(post("/api/url").contentType("application/json")
                    .content(objectMapper.writeValueAsString(urlDto)))
            .andExpect(status().isOk())
            .andExpect(content().contentType("text/plain;charset=UTF-8"))
            .andExpect(content().string("http://short"))
            .andDo(print());
    verify(service, times(1)).getLongUrl(anyString());
  }

  @Test
  void createUrl_shouldReturnExceptionText_whenGetNotValidUrl() throws Exception {
    UrlDto urlDto = new UrlDto(null,null, "long");
    when(service.getLongUrl("long")).thenReturn("http://short");
    this.mockMvc.perform(post("/api/url").contentType("application/json")
                    .content(objectMapper.writeValueAsString(urlDto)))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.error", is("This URL doesn't have shortcut or this URL is not valid.")))
            .andDo(print());
  }

}
