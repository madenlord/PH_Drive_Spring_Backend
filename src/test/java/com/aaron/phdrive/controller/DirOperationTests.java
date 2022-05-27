package com.aaron.phdrive.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.aaron.phdrive.entity.FolderEntity;
import com.aaron.phdrive.service.NavigationService;
import com.aaron.phdrive.service.StorageService;
import com.aaron.phdrive.storage.StorageProperties;

@AutoConfigureMockMvc
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DirOperationTests {
	
	/*
	 * REST Operations
	 */
	private final String GET_URL    = "/dir";
	private final String POST_URL   = "/mkdir";
	private final String DELETE_URL = "/rm";
	
	/*
	 *  Test folder and file names
	 */
	private final String DIR_PATH    = "randDir/";
	private final String SUBDIR_PATH = "randDir/dir"; 
	private final String FILE_PATH   = "randDir/test.txt";
	private final String FILE_NAME   = "test.txt";
	
	/*
	 * Util objs
	 */
	private String STORAGE_LOCATION;
	private FolderEntity TEST_FOLDER;
	
	@Autowired
	private MockMvc mvc;
	
	@Autowired
	private StorageProperties storageProperties;
	
	@MockBean
	private NavigationService navigationService;
	
	@MockBean
	private StorageService storageService;
	
	@BeforeAll
	public void init() {
		List<Path> subFolders = new ArrayList<>();
		List<Path> subFiles   = new ArrayList<>();
		STORAGE_LOCATION      = this.storageProperties.getLocation();
		TEST_FOLDER 		  = new FolderEntity();
		
		subFolders.add(Paths.get(STORAGE_LOCATION).resolve(SUBDIR_PATH));
		subFiles.add(Paths.get(STORAGE_LOCATION).resolve(FILE_PATH));
		
		TEST_FOLDER.setDirs(subFolders);
		TEST_FOLDER.setFiles(subFiles);
	}
	
	@Test
	public void shouldGetFolderContent() throws Exception {
		given(this.navigationService.getFolderContent(eq(DIR_PATH), ArgumentMatchers.any(FolderEntity.class)))
			.willReturn(TEST_FOLDER);
		
		MvcResult result = this.mvc.perform(get(GET_URL).param("path", DIR_PATH))
				.andExpect(status().isOk())
				.andReturn();
		
		assertEquals("application/json", result.getResponse().getContentType());
	}
}
