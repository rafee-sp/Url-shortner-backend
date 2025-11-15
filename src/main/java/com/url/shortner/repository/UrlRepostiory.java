package com.url.shortner.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.url.shortner.entity.UrlEntity;

public interface UrlRepostiory extends JpaRepository<UrlEntity, Long> {

	Optional<UrlEntity> findByShortUrl(String shortUrl);

}
