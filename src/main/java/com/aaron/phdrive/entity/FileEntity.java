package com.aaron.phdrive.entity;

import java.io.Serializable;

public class FileEntity implements Serializable {
	
	private String filename;
	private String path;

	public FileEntity() {}
	
	public FileEntity(String filename, String path) {
		this.filename = filename;
		this.path = path;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}
