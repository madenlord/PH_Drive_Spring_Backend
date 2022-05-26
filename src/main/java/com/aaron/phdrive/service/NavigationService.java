package com.aaron.phdrive.service;

public interface NavigationService {
	
	void init();
	
	void getFolderContent(String folderPath);
	
	void createFolder(String folderPath);
	
	void deleteFolder(String folderPath);
}
