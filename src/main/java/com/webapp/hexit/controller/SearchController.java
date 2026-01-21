package com.webapp.hexit.controller;

import com.webapp.hexit.model.Event;
import com.webapp.hexit.service.SearchService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SearchController {

  private final SearchService searchService;

  public SearchController(SearchService searchService) {
    this.searchService = searchService;
  }

  @GetMapping("/search/title")
  public List<Event> searchByTitle(@RequestParam String title) {
    return searchService.searchByTitle(title);
  }

  @GetMapping("/search/location")
  public List<Event> searchByLocation(
    @RequestParam double latStart,
    @RequestParam double latEnd,
    @RequestParam double lngStart,
    @RequestParam double lngEnd
  ) {
    return searchService.searchByLocation(latStart, latEnd, lngStart, lngEnd);
  }
}
