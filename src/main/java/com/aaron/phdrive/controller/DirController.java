package com.aaron.phdrive.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.aaron.phdrive.entity.FolderEntity;
import com.aaron.phdrive.service.NavigationService;

@RestController
public class DirController {
	
	private final NavigationService navigationService;
	
	@Autowired
	public DirController(NavigationService navigationService) {
		this.navigationService = navigationService;
	}
	
	@GetMapping("/dir")
	@ResponseBody
	public FolderEntity getFolderContent(@RequestParam("path") String folderPath) {
		if(folderPath == null || folderPath.isEmpty()) folderPath = "/";
		return this.navigationService.getFolderContent(folderPath, new FolderEntity());
	}
}
