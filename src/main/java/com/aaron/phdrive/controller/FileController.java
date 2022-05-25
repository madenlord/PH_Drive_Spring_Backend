package com.aaron.phdrive.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerMapping;
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
	public ResponseEntity<Resource> serveFile(@RequestParam("filename") String filename,
			@RequestParam(name="path",required=false) String path) {
		
		if(path != null) filename = path + "/" + filename;
		
		Resource file = storageService.loadAsResource(filename);
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
				"attachment; filename=\"" + file.getFilename() + 
				"\"").body(file);
	}
	
	@PostMapping("/upload")
	@ResponseBody
	public String handleFileUpload(@RequestParam("file") MultipartFile file,
			@RequestParam("path") String path, RedirectAttributes redirectAttributes) {

		if(path == null) path = "/";
		try {
			storageService.store(file, path);
			redirectAttributes.addFlashAttribute("message",
					file.getOriginalFilename() + " successfully uploaded!");
		} catch(StorageException e) {
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
