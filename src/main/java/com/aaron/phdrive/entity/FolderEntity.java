package com.aaron.phdrive.entity;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FolderEntity implements Serializable {

	private List<String> dirs = new ArrayList<>();
	private List<String> files = new ArrayList<>();
	
	public FolderEntity() {}
	
	public FolderEntity(List<Path> dirs, List<Path> files) {
		this.setDirs(dirs);
		this.setFiles(files);
	}
	
	public List<String> getDirs() {
		return this.dirs;
	}
	
	public void setDirs(List<Path> dirs) {
		dirs.forEach(dir -> this.dirs.add(this.getLastChild(dir)));
	}
	
	public void addDir(Path dir) {
		this.dirs.add(this.getLastChild(dir));
	}
	
	public List<String> getFiles() {
		return this.files;
	}
	
	public void setFiles(List<Path> files) {
		files.forEach(file -> this.files.add(this.getLastChild(file)));
	}
	
	public void addFile(Path file) {
		this.files.add(this.getLastChild(file));
	}
	
	private String getLastChild(Path path) {
		return path.getFileName().toString();
	}
}