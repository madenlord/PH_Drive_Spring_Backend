package com.aaron.phdrive.storage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class FileSystemNavigationServiceTests {

	private StorageProperties properties = new StorageProperties();
	private FileSystemNavigationService service;
	
	private final String DIR_PATH = "randDir";
	
	@BeforeEach
	public void init() {
		properties.setLocation("target/dirs/" + Math.abs(new Random().nextLong()));
		service = new FileSystemNavigationService(properties);
		service.init();
	}
	
	@Test
	public void shouldCreateFolder() {
		service.createFolder(DIR_PATH);
		assertThat(service.load(DIR_PATH)).exists();
	}
	
	@Test
	@DisplayName("Should fail when trying to create already existing dir")
	public void shouldCreateFolderShouldFailFileAlreadyExisting() {
		service.createFolder(DIR_PATH);
		
		try {
			service.createFolder(DIR_PATH);
		} catch(Exception e) {
			assertEquals(e.getClass(), StorageException.class);
			assertEquals(e.getMessage(), "Folder already exists.");
		}
	}
}
