package com.aaron.phdrive.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
import org.springframework.test.web.servlet.MvcResult;

import com.aaron.phdrive.service.StorageFileNotFoundException;
import com.aaron.phdrive.service.StorageService;
import com.aaron.phdrive.storage.StorageException;
import com.aaron.phdrive.storage.StorageProperties;

@AutoConfigureMockMvc
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FileOperationTests {

	private final String MULTIPART_NAME = "file";
	private final String FILENAME 		= "test.txt";
	private final String ROOT_PATH		= "/";
	private final String PATH			= "/rand/dir";
	private final String GET_URL	    = "/download";
	private final String DELETE_URL     = "/delete";
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
		
		this.mvc.perform(get(GET_URL).param("file", FILENAME))
				.andExpect(status().isOk());
	}
	
	@Test
	@DisplayName("Should download file " + PATH + "/" + FILENAME)
	public void shouldDownloadFileFromSpecificPath() throws Exception {
		given(this.storageService.loadAsResource(PATH + "/" + FILENAME))
				.willReturn(multipartFile.getResource());
		
		this.mvc.perform(get(GET_URL).param("file", PATH + "/" + FILENAME))
				.andExpect(status().isOk());	
	}
	
	@Test()
	public void shouldFailAtDownload() {
		given(this.storageService.loadAsResource(FILENAME + "t"))
				.willThrow(StorageFileNotFoundException.class);
		
		try {
			this.mvc.perform(get(GET_URL).param("file", FILENAME + "t"))
					.andExpect(status().isNotFound());
		} catch(Exception e) {
			assertEquals(e.getCause().getClass(), StorageFileNotFoundException.class);
		}
	}
	
	@Test
	public void shouldSaveUploadedFile() throws Exception {
		this.mvc.perform(multipart(POST_URL).file(multipartFile).param("path", ROOT_PATH))
				.andExpect(status().is2xxSuccessful())
				.andExpect(content().json(
						"{'filename':'" + FILENAME + "'," + 
						"'path': '" + ROOT_PATH + "'}"
						));
		
		then(this.storageService).should().store(multipartFile, ROOT_PATH);
	}
	
	@Test
	@DisplayName("Should upload file in path " + PATH)
	public void shouldSaveUploadedFileInSpecificPath() throws Exception {
		this.mvc.perform(multipart(POST_URL).file(multipartFile).param("path", PATH))
				.andExpect(status().is2xxSuccessful());
		
		then(this.storageService).should().store(multipartFile, PATH);
	}
	
	@Test
	@DisplayName("Should fail due to server file storage service error")
	public void shouldFailUploadOperation() throws Exception {
		doThrow(StorageException.class).when(this.storageService)
									   .store(multipartFile, PATH);
		MvcResult result = null;
		try {
			result = this.mvc.perform(multipart(POST_URL).file(multipartFile).param("path", PATH))
							 .andExpect(status().is2xxSuccessful())
							 .andReturn();
		} catch(Exception e) {
			assertEquals(StorageException.class, e.getCause().getClass());
			assertEquals("application/json", result.getResponse().getContentType());
			assertEquals("{'filename':'','path':''}", result.getResponse().getContentAsString());
		}
	}
	
	@Test
	public void shouldDeleteFile() throws Exception {
		doNothing().when(this.storageService).delete(FILENAME);
		
		this.mvc.perform(delete(DELETE_URL).param("file", FILENAME))
			.andExpect(status().is2xxSuccessful())
			.andExpect(content().json(
					"{'response':'" + FILENAME + " successfully deleted!'}"));
	}
	
	@Test
	@DisplayName("Should throw StorageFileNotFoundException when deleting non-existing file")
	public void shouldFailAtDeletingNonExistingFile() {
		doThrow(StorageFileNotFoundException.class).when(this.storageService).delete(FILENAME);
		
		try  {
			this.mvc.perform(delete(DELETE_URL).param("file", FILENAME))
			.andExpect(status().is2xxSuccessful())
			.andExpect(content().json(
					"{'response':'" + FILENAME + " could not be deleted.'}"));
		} catch(Exception e) {
			assertEquals(e.getCause().getClass(), StorageFileNotFoundException.class);
		}
		
	}
	
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
