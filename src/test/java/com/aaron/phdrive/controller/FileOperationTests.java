package com.aaron.phdrive.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import com.aaron.phdrive.service.StorageFileNotFoundException;
import com.aaron.phdrive.service.StorageService;

@AutoConfigureMockMvc
@SpringBootTest
public class FileOperationTests {

	private final String MULTIPART_NAME = "file";
	private final String FILENAME 		= "test.txt";
	private final String PATH			= "/";
	private final String POST_URL       = "/upload";
	
	@Autowired
	private MockMvc mvc;
	
	@MockBean
	private StorageService storageService;
	
	@Test
	public void shouldSaveUploadedFile() throws Exception {
		MockMultipartFile multipartFile = new MockMultipartFile(MULTIPART_NAME, FILENAME,
				"text/plain", "Spring Framework".getBytes());
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
