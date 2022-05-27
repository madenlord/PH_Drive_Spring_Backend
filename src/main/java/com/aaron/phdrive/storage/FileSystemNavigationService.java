package com.aaron.phdrive.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aaron.phdrive.service.NavigationService;

@Service
public class FileSystemNavigationService implements NavigationService {

	private final Path rootLocation;
	
	@Autowired
	public FileSystemNavigationService(StorageProperties properties) {
		this.rootLocation = Paths.get(properties.getLocation());
	}
	
	@Override
	public void init() {
		if(!Files.exists(rootLocation, LinkOption.NOFOLLOW_LINKS)) {
			try {
				Files.createDirectories(rootLocation);
			}
			catch (IOException e) {
				throw new StorageException("Could not initialize storage", e);
			}
		}
	}
	
	public void getFolderContent(String folderPath) {}
	
	private void getFiles(String folderPath) {}
	
	private void getFolders(String folderPath) {}
	
	public void createFolder(String folderPath) {}
	
	public void deleteFolder(String folderPath) {}
}
