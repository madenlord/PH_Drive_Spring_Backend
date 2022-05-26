/*
 * Copyright 2016-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *	  https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.aaron.phdrive.storage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import com.aaron.phdrive.service.StorageFileNotFoundException;

/**
 * @author Dave Syer
 *
 */
public class FileSystemStorageServiceTests {

	private StorageProperties properties = new StorageProperties();
	private FileSystemStorageService service;
	
	private final String MULTIPARTFILE_NAME = "foo";
	private final String FILE_PATH = "";

	@BeforeEach
	public void init() {
		properties.setLocation("target/files/" + Math.abs(new Random().nextLong()));
		service = new FileSystemStorageService(properties);
		service.init();
	}

	@Test
	public void loadNonExistent() {
		assertThat(service.load("foo.txt")).doesNotExist();
	}

	@Test
	public void saveAndLoad() {
		service.store(new MockMultipartFile(MULTIPARTFILE_NAME, "foo.txt", MediaType.TEXT_PLAIN_VALUE,
				"Hello, World".getBytes()), FILE_PATH);
		assertThat(service.load("foo.txt")).exists();
	}

	@Test
	public void saveRelativePathNotPermitted() {
		assertThrows(StorageException.class, () -> {
			service.store(new MockMultipartFile(MULTIPARTFILE_NAME, "../foo.txt",
					MediaType.TEXT_PLAIN_VALUE, "Hello, World".getBytes()), FILE_PATH);
		});
	}

	@Test
	public void saveAbsolutePathNotPermitted() {
		assertThrows(StorageException.class, () -> {
			service.store(new MockMultipartFile(MULTIPARTFILE_NAME, "/etc/passwd",
					MediaType.TEXT_PLAIN_VALUE, "Hello, World".getBytes()), FILE_PATH);
		});
	}

	@Test
	@EnabledOnOs({OS.LINUX})
	public void saveAbsolutePathInFilenamePermitted() {
		//Unix file systems (e.g. ext4) allows backslash '\' in file names.
		String fileName="\\etc\\passwd";
		service.store(new MockMultipartFile(fileName, fileName,
				MediaType.TEXT_PLAIN_VALUE, "Hello, World".getBytes()), FILE_PATH);
		assertTrue(Files.exists(
				Paths.get(properties.getLocation()).resolve(Paths.get(fileName))));
	}

	@Test
	public void savePermitted() {
		service.store(new MockMultipartFile(MULTIPARTFILE_NAME, "bar/../foo.txt",
				MediaType.TEXT_PLAIN_VALUE, "Hello, World".getBytes()), FILE_PATH);
	}
	
	@Test
	@DisplayName("Saving empty file should throw StorageException")
	public void saveEmptyFile() {
		assertThrows(StorageException.class, () -> service.store(
				new MockMultipartFile(MULTIPARTFILE_NAME, "foo.txt",
				MediaType.TEXT_PLAIN_VALUE, "".getBytes()), FILE_PATH));
	}
	
	@Test
	public void deleteExistent() {
		String filename = "foo.txt";
		service.store(new MockMultipartFile(filename, filename, MediaType.TEXT_PLAIN_VALUE,
				"Hello, World!".getBytes()), FILE_PATH);
		service.delete(filename);
		assertThat(service.load(filename)).doesNotExist();
	}
	
	@Test
	public void deleteNonExistent() {
		assertThrows(StorageFileNotFoundException.class, () -> {
			service.delete("foo.txt");
		});
	}

}
