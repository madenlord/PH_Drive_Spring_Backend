package com.aaron.phdrive.storage;

import java.util.Random;

import org.junit.jupiter.api.BeforeEach;

public class FileSystemNavigationServiceTests {

	private StorageProperties properties = new StorageProperties();
	private FileSystemNavigationService service;
	
	@BeforeEach
	public void init() {
		properties.setLocation("target/files" + Math.abs(new Random().nextLong()));
		service = new FileSystemNavigationService(properties);
		service.init();
	}
}
