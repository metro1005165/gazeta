package com.swengi.model;

public class Collaborator {
	
	private int editorRowId;
	private int articleRowId;
	
	public Collaborator() {
		
	}

	public Collaborator(int editorRowId, int articleRowId) {
		this.editorRowId = editorRowId;
		this.articleRowId = articleRowId;
	}

	public int getEditorRowId() {
		return editorRowId;
	}

	public void setEditorRowId(int editorRowId) {
		this.editorRowId = editorRowId;
	}

	public int getArticleRowId() {
		return articleRowId;
	}

	public void setArticleRowId(int articleRowId) {
		this.articleRowId = articleRowId;
	}
}
