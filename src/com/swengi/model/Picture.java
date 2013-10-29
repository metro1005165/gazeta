package com.swengi.model;

public class Picture {
	
	private String pictureID;
	private String url;
	private String caption;
	private String photographer;
	private int width;
	private int height;
	
	public Picture() {
		
	}

	public Picture(String pictureID, String url, String caption,
			String photographer, int width, int height) {
		this.pictureID = pictureID;
		this.url = url;
		this.caption = caption;
		this.photographer = photographer;
		this.width = width;
		this.height = height;
	}

	public String getPictureID() {
		return pictureID;
	}

	public void setPictureID(String pictureID) {
		this.pictureID = pictureID;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getPhotographer() {
		return photographer;
	}

	public void setPhotographer(String photographer) {
		this.photographer = photographer;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
}
