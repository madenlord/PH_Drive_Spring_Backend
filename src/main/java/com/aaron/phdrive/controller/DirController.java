package com.aaron.phdrive.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.aaron.phdrive.entity.FolderEntity;
import com.aaron.phdrive.service.NavigationService;
import com.aaron.phdrive.service.StorageFileNotFoundException;
import com.aaron.phdrive.storage.StorageException;

@CrossOrigin(origins="http://localhost:4200")
@RestController
public class DirController {
	
	private final NavigationService navigationService;
	
	@Autowired
	public DirController(NavigationService navigationService) {
		this.navigationService = navigationService;
	}
	
	@GetMapping("/dir")
	@ResponseBody
	public ResponseEntity<FolderEntity> getFolderContent(@RequestParam("path") String folderPath) {
		
		FolderEntity folderInfo = new FolderEntity();
		
		if(folderPath == null || folderPath.isEmpty()) folderPath = "/";
		try {
			folderInfo = this.navigationService.getFolderContent(folderPath, folderInfo);
			return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json")
									  .body(folderInfo);
		} catch(StorageFileNotFoundException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	@PostMapping(value="/mkdir", produces="application/json")
	@ResponseBody
	public ResponseEntity<FolderEntity> createFolder(@RequestParam("path") String folderPath) {
		
		if(folderPath == null || folderPath.isEmpty()) 
			return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
		else {
			try {
				this.navigationService.createFolder(folderPath);
				return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json")
										  .body(new FolderEntity());
			} catch(StorageException e) {
				if(e.getMessage().equals("Folder " + folderPath + " already exists."))
					return new ResponseEntity<>(HttpStatus.CONFLICT);
				else
					return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}
	
	@DeleteMapping(value="/rm", produces="application/json")
	@ResponseBody
	public ResponseEntity<FolderEntity> deleteFolder(@RequestParam("path") String folderPath) {
		
		if(folderPath == null || folderPath.isEmpty())
			return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
		else {
			try {
				this.navigationService.deleteFolder(folderPath);
				return new ResponseEntity<>(HttpStatus.OK);
			} catch(StorageFileNotFoundException e) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			} catch(StorageException e) {
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}
}
