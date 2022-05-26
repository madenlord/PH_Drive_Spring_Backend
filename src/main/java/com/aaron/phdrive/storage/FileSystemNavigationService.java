package com.aaron.phdrive.storage;

import org.springframework.stereotype.Service;

import com.aaron.phdrive.service.NavigationService;

@Service
public class FileSystemNavigationService implements NavigationService {

	public void init() {}
	
	public void getFolderContent(String folderPath) {}
	
	private void getFiles(String folderPath) {}
	
	private void getFolders(String folderPath) {}
	
	public void createFolder(String folderPath) {}
	
	public void deleteFolder(String folderPath) {}
}
