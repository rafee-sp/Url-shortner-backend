package com.url.shortner.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.url.shortner.dto.ShortenUrlRequest;
import com.url.shortner.service.QRcodeService;
import com.url.shortner.service.UrlShortnerService;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api")
public class UrlShortnerController {
	
	private final UrlShortnerService urlShortnerService;
	private final QRcodeService qrService;
	
	public UrlShortnerController(UrlShortnerService urlShortnerService, QRcodeService qrService) {  // TODO : Use lombok
		this.urlShortnerService = urlShortnerService;
		this.qrService = qrService;
	}

	@PostMapping("/shorten")
	public ResponseEntity<Map<String, String>> shorternUrl(@RequestBody ShortenUrlRequest request){
		
		try {
		
		System.out.println("longUrl : "+request.getLongUrl());	
		
		String shortUrl = urlShortnerService.shortenUrl(request.getLongUrl());
		
		String encodedQr = qrService.generateQr(shortUrl);
		
		Map<String, String> resultMap = new HashMap<>();
		resultMap.put("shortUrl", shortUrl);
		resultMap.put("qrCode", encodedQr);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(resultMap);
	
		} catch (Exception e) {
			System.out.println("An error occured at shorternUrl method : "+e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	
	}
	
	@GetMapping("/{shortUrl}")
	public ResponseEntity<Map<String, String>> getLongurl(@PathVariable String shortUrl, HttpServletResponse response){
		
		try {
			
			String longUrl = urlShortnerService.getLongUrl(shortUrl);
			
			if(longUrl != null && !longUrl.isBlank()) {
				
				Map<String, String> resultMap = new HashMap<>();
				resultMap.put("longUrl", longUrl);
				
				return ResponseEntity.status(HttpStatus.OK).body(resultMap);
				
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			}
			
		} catch (Exception e) {
			
			System.out.println("An error occured at getLongurl method : "+e.getMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
	}
	
}
