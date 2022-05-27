package com.aaron.phdrive.entity;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;

@Entity
public class FolderEntity implements Serializable {

	private List<Path> dirs = new ArrayList<>();
	private List<Path> files = new ArrayList<>();
	
	public FolderEntity() {}
	
	public FolderEntity(List<Path> dirs, List<Path> files) {
		this.dirs  = dirs;
		this.files = files;
	}
	
	public List<Path> getDirs() {
		return this.dirs;
	}
	
	public void setDirs(List<Path> dirs) {
		this.dirs = dirs;
	}
	
	public void addDir(Path dir) {
		this.dirs.add(dir);
	}
	
	public List<Path> getFiles() {
		return this.files;
	}
	
	public void setFiles(List<Path> files) {
		this.files = files;
	}
	
	public void addFile(Path file) {
		this.files.add(file);
	}
}
