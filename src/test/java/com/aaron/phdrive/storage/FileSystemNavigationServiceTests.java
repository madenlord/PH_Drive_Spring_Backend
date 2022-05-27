package com.aaron.phdrive.storage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import com.aaron.phdrive.entity.FolderEntity;

public class FileSystemNavigationServiceTests {

	private StorageProperties properties = new StorageProperties();
	private FileSystemNavigationService service;
	private FileSystemStorageService storageService;
	
	private final String DIR_PATH    = "randDir/";
	private final String SUBDIR_PATH = "randDir/dir";
	private final String FILE_PATH   = "randDir/test.txt";
	private final String FILE_NAME   = "test.txt";
	
	@BeforeEach
	public void init() {
		this.properties.setLocation("target/dirs/" + Math.abs(new Random().nextLong()));
		this.service        = new FileSystemNavigationService(properties);
		this.storageService = new FileSystemStorageService(properties);
		this.service.init();
	}
	
	@Test
	public void shouldCreateFolder() {
		this.service.createFolder(DIR_PATH);
		assertThat(this.service.load(DIR_PATH)).exists();
	}
	
	@Test
	@DisplayName("Should fail when trying to create already existing dir")
	public void shouldCreateFolderShouldFailFileAlreadyExisting() {
		this.service.createFolder(DIR_PATH);
		
		try {
			this.service.createFolder(DIR_PATH);
		} catch(Exception e) {
			assertEquals(StorageException.class, e.getClass());
			assertEquals("Folder already exists.", e.getMessage());
		}
	}
	
	@Test
	public void shouldGetFolderContent() {
		FolderEntity folderContent = new FolderEntity();
		List<Path> dirs  = new ArrayList<>();
		List<Path> files = new ArrayList<>();
		
		this.service.createFolder(DIR_PATH);
		this.service.createFolder(SUBDIR_PATH);
		this.storageService.store(new MockMultipartFile("file", FILE_NAME,
				MediaType.TEXT_PLAIN_VALUE, "Hello, World!".getBytes()), DIR_PATH);

		dirs.add(this.service.load(SUBDIR_PATH));
		files.add(this.service.load(FILE_PATH));
		
		folderContent = this.service.getFolderContent(DIR_PATH, folderContent);
		
		assertEquals(dirs, folderContent.getDirs());
		assertEquals(files, folderContent.getFiles());
	}
	
	@Test
	public void shouldGetEmptyFolderContent() {
		FolderEntity folderContent = new FolderEntity();
		List<Path> dirs  = new ArrayList<>();
		List<Path> files = new ArrayList<>();
		
		this.service.createFolder(DIR_PATH);
		this.service.createFolder(SUBDIR_PATH);
		
		folderContent = this.service.getFolderContent(SUBDIR_PATH, folderContent);
		
		assertEquals(dirs, folderContent.getDirs());
		assertEquals(files, folderContent.getFiles());
	}
	
	@Test
	@DisplayName("Fail at getting content from non-existing folder.")
	public void shouldFailtAtGetFolderContentFormNonExistingFolder() {
		try {
			FolderEntity folderContent = this.service.getFolderContent(DIR_PATH + "/error", new FolderEntity());	
		} catch(Exception e) {
			assertEquals(StorageException.class, e.getClass());
			assertEquals("Folder " + DIR_PATH + "/error" + "doesn't exist.", e.getMessage());
		}
	}
}
