package com.aaron.phdrive.service;

import java.io.File;
import java.nio.file.Path;

import com.aaron.phdrive.entity.FolderEntity;

public interface NavigationService {
	
	void init();
	
	void createFolder(String folderPath);
	
	File getFolder(String folderPath);
	
	FolderEntity getFolderContent(String folderPath, FolderEntity folder);
	
	Path load(String folderPath);
	
	void deleteFolder(String folderPath);
}
