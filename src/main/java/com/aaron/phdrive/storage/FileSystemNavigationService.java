package com.aaron.phdrive.storage;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aaron.phdrive.service.NavigationService;

@Service
public class FileSystemNavigationService implements NavigationService {

	private final Path rootLocation;
	private final Set<PosixFilePermission> defaultPerms = 
			PosixFilePermissions.fromString("rwxr-x---");
	private final FileAttribute<Set<PosixFilePermission>> defaultFolderAttr = 
			PosixFilePermissions.asFileAttribute(defaultPerms);
	
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
	
	@Override
	public void createFolder(String folderPath) {
		try {
			Path folderAbsPath = this.load(folderPath);
			Files.createDirectory(this.load(folderPath));
		} catch(FileAlreadyExistsException e) {
			throw new StorageException("Folder already exists.");
		} catch(IOException e) {
			throw new StorageException("Failed to create folder.");
		} 
	}
	
	@Override
	public File getFolder(String folderPath) {
		return null;
	}
	
	@Override
	public void getFolderContent(String folderPath) {}
	
	private void getFiles(String folderPath) {}
	
	private void getFolders(String folderPath) {}
	
	@Override
	public Path load(String folderPath) {
		return rootLocation.resolve(folderPath);
	}
	
	@Override
	public void deleteFolder(String folderPath) {}
}
