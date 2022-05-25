package com.aaron.phdrive.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import com.aaron.phdrive.service.StorageService;
import com.aaron.phdrive.storage.StorageProperties;

@AutoConfigureMockMvc
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FileOperationTests {

	private final String MULTIPART_NAME = "file";
	private final String FILENAME 		= "test.txt";
	private final String ROOT_PATH		= "/";
	private final String PATH			= "/rand/dir";
	private final String POST_URL       = "/upload";
	private String STORAGE_LOCATION;
	private MockMultipartFile multipartFile;
	
	@Autowired
	private MockMvc mvc;
	
	@Autowired
	private StorageProperties storageProperties;
	
	@MockBean
	private StorageService storageService;
	
	@BeforeAll
	public void init() {
		STORAGE_LOCATION = this.storageProperties.getLocation();
	}
	
	@BeforeEach
	public void initTest() {
		multipartFile = new MockMultipartFile(MULTIPART_NAME, FILENAME,
				"text/plain", "Spring Framework".getBytes());
	}
	
	@Test
	public void shouldDownloadFile() throws Exception {
		given(this.storageService.loadAsResource(FILENAME))
				.willReturn(multipartFile.getResource());
		
		this.mvc.perform(get("/" + FILENAME))
				.andExpect(status().isOk());
		
		then(this.storageService).should().loadAsResource(FILENAME);
	}
	
	@Test
	public void shouldSaveUploadedFile() throws Exception {
		this.mvc.perform(multipart(POST_URL).file(multipartFile).param("path", ROOT_PATH))
				.andExpect(status().is2xxSuccessful());
		
		then(this.storageService).should().store(multipartFile, ROOT_PATH);
	}
	
	@Test
	@DisplayName("Should upload file in path " + PATH)
	public void shouldSaveUploadedFileInSpecificPath() throws Exception {
		this.mvc.perform(multipart(POST_URL).file(multipartFile).param("path", PATH))
				.andExpect(status().is2xxSuccessful());
		
		then(this.storageService).should().store(multipartFile, PATH);
	}
	
//	@Test
//	public void shouldDeleteFile() throws Exception {
//		given(this.storageService.load(TEST_FILENAME))
//			.willReturn(Paths.get(TEST_PATH + TEST_FILENAME));
//		
//		this.mvc.perform(delete(TEST_FILEPATH))
//			.andExpect(status().is2xxSuccessful());
//	}
//	
//	@SuppressWarnings("unchecked")
//	@Test
//	public void should404WhenMissingFile() throws Exception {
//		given(this.storageService.loadAsResource(TEST_FILENAME))
//			.willThrow(StorageFileNotFoundException.class);
//		
//		this.mvc.perform(get(TEST_FILEPATH)).andExpect(status().isNotFound());
//		
//		doThrow(StorageFileNotFoundException.class).when(this.storageService)
//			.delete(TEST_FILEPATH);
//		
//		this.mvc.perform(delete(TEST_FILEPATH)).andExpect(status().isNotFound());
//	}
}
