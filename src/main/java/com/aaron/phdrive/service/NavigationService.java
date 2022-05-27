package com.aaron.phdrive.service;

import java.io.File;
import java.nio.file.Path;

public interface NavigationService {
	
	void init();
	
	void createFolder(String folderPath);
	
	File getFolder(String folderPath);
	
	void getFolderContent(String folderPath);
	
	Path load(String folderPath);
	
	void deleteFolder(String folderPath);
}
