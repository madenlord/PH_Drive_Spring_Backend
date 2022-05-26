package com.aaron.phdrive.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.aaron.phdrive.service.StorageService;
import com.aaron.phdrive.storage.StorageException;

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
		Resource file = storageService.loadAsResource(filepath);
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
				"attachment; filename=\"" + file.getFilename() + 
				"\"").body(file);
	}
	
	@PostMapping("/upload")
	@ResponseBody
	public String handleFileUpload(@RequestParam("file") MultipartFile file,
			@RequestParam("path") String path) {

		String response = "{'response':'";
		
		if(path == null) path = "/";
		try {
			storageService.store(file, path);
			response += file.getOriginalFilename() + " successfully uploaded!'}";
		} catch(StorageException e) {
			System.out.println("The introduced path doesn't exist!");
			response += file.getOriginalFilename() + " could not be uploaded'}";
		}
		
		return response;
	}
	
	@DeleteMapping("/delete")
	@ResponseBody
	public String deleteFile(@RequestParam("file") String filepath) {
		
		String response = "{'response':'";
		
		try {
			storageService.delete(filepath);
			response += filepath + " successfully deleted!'}";
		} catch (Exception e) {
			System.out.println("The file " + filepath + "couldn't be deleted");
			response += filepath + " could not be deleted.'}";
		}
		
		return response;
	}
}
