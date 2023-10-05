package com.linkzilla.repository;

import com.linkzilla.models.Url;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlRepository extends MongoRepository<Url, String> {
  Optional<Url> findUrlByShortcut(String shortcut);

}
