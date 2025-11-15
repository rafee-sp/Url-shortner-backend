package com.url.shortner.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.url.shortner.entity.UrlEntity;
import com.url.shortner.repository.UrlRepostiory;

@Service
public class UrlShortnerService {
	
	private final UrlRepostiory urlRepostiory;
	private final RedisTemplate<String, String> redisTemplate;
	
	@Value("${app.server.url}")
	private String serverUrl;
	
	public UrlShortnerService(UrlRepostiory urlRepostiory, RedisTemplate<String, String> redisTemplate) {
		this.urlRepostiory = urlRepostiory;
		this.redisTemplate = redisTemplate;
	}

	public String shortenUrl(String longUrl) {
		
		String shortUrl = UUID.randomUUID().toString().substring(0, 8);
	
		UrlEntity urlEntity = new UrlEntity();
		
		urlEntity.setLongUrl(longUrl);
		urlEntity.setShortUrl(shortUrl);
		urlRepostiory.save(urlEntity);
		
		return serverUrl+"/"+shortUrl;
	}
	
	public String getLongUrl(String shortUrl) {
		
		String cachedUrl = redisTemplate.opsForValue().get(shortUrl);
		
		if(cachedUrl != null && !cachedUrl.isBlank()) {
			return cachedUrl;
		}
		
		Optional<UrlEntity> urlEntity = urlRepostiory.findByShortUrl(shortUrl);
		
		if(urlEntity.isPresent()) {
			
			String longUrl = urlEntity.get().getLongUrl();
			
			redisTemplate.opsForValue().set(shortUrl, longUrl);  // add ttl
			
			return longUrl;
		}
		
		return null;
			
	}
	
}
