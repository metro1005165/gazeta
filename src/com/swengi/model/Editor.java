package com.swengi.model;

public class Editor {
	
	private String name;
    private String photoUrl;
    
    public Editor() {
    	
    }

	public Editor(String name, String photoUrl) {
		this.name = name;
		this.photoUrl = photoUrl;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}  
}
