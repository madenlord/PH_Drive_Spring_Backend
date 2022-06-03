package com.aaron.phdrive.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
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
import org.springframework.web.multipart.MultipartFile;

import com.aaron.phdrive.entity.FileEntity;
import com.aaron.phdrive.service.StorageFileNotFoundException;
import com.aaron.phdrive.service.StorageService;
import com.aaron.phdrive.storage.StorageException;

@CrossOrigin(origins="http://localhost:4200")
@RestController
public class FileController {

	private final StorageService storageService;
	
	@Autowired
	public FileController(StorageService storageService) {
		this.storageService = storageService;
	}
	
	@GetMapping("/download")
	@ResponseBody
	public ResponseEntity<Resource> serveFile(@RequestParam("file") String filepath) {
		try {
			Resource file = storageService.loadAsResource(filepath);
			return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
					"attachment; filename=\"" + file.getFilename() + 
					"\"").body(file);
		} catch(StorageFileNotFoundException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
	}
	
	@PostMapping(value="/upload", produces="application/json")
	@ResponseBody
	public ResponseEntity<FileEntity> handleFileUpload(@RequestParam("file") MultipartFile file,
			@RequestParam("path") String path) {
		
		if(path == null || path.isEmpty()) path = "/";
		try {
			storageService.store(file, path);
			return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json")
					.body(new FileEntity(file.getOriginalFilename(), path));
		} catch(StorageException e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@DeleteMapping("/delete")
	@ResponseBody
	public ResponseEntity<String> deleteFile(@RequestParam("file") String filepath) {
	
		try {
			storageService.delete(filepath);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception e) {
			System.out.println("The file " + filepath + "couldn't be deleted");
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
}
