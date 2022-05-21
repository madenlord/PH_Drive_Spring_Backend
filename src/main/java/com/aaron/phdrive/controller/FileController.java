package com.aaron.phdrive.controller;

import java.io.IOException;
import java.nio.file.Paths;

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

@RestController
public class FileController {

	private final StorageService storageService;
	
	@Autowired
	public FileController(StorageService storageService) {
		this.storageService = storageService;
	}
	
	@GetMapping("/{filename:.+}")
	@ResponseBody
	public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
		Resource file = storageService.loadAsResource(filename);
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
				"attachment; filename=\"" + file.getFilename() + 
				"\"").body(file);
	}
	
	@PostMapping("/{path}")
	@ResponseBody
	public String handleFileUpload(@RequestParam("file") MultipartFile file,
			@PathVariable String path, RedirectAttributes redirectAttributes) {
		try {
			file.transferTo(Paths.get(path));
			storageService.store(file);
			redirectAttributes.addFlashAttribute("message",
					file.getOriginalFilename() + " successfully uploaded!");
		} catch(IOException e) {
			System.out.println("The introduced path doesn't exist!");
			redirectAttributes.addFlashAttribute("message", 
					file.getOriginalFilename() + " could not be uploaded");
		}
		
		return "redirect:/" + path;
	}
	
//	@DeleteMapping("/{filename:.+}")
//	@ResponseBody
//	public String deleteFile(@PathVariable String filename, 
//			RedirectAttributes redirectAttributes) {
//		
//		try {
//			storageService.delete(filename.toString());
//			redirectAttributes.addFlashAttribute("message",
//					filename + " successfully deleted!");
//		} catch (Exception e) {
//			System.out.println("The file " + filename + "couldn't be deleted");
//			redirectAttributes.addFlashAttribute("message",
//					filename + " could not be deleted");
//		}
//		
//		return "redirect:/" + filename;
//	}
}
