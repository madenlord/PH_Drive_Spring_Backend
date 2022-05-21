package com.aaron.phdrive.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.aaron.phdrive.service.StorageService;

@RestController
public class DirController {
	
	private final StorageService storageService;
	
	@Autowired
	public DirController(StorageService storageService) {
		this.storageService = storageService;
	}
}
