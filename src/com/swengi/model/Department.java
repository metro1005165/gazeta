package com.swengi.model;

public class Department {
	
	private String departmentId;
	private String title;
	
	public Department() {
		
	}

	public Department(String departmentId, String title) {
		this.departmentId = departmentId;
		this.title = title;
	}

	public String getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
