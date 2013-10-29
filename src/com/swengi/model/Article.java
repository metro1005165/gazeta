package com.swengi.model;

public class Article {

	private String articleId;
	private String mainHeader;
	private String lead;
	private String date;
	private String bodyText;

	public Article() {

	}

	public Article(String articleId, String mainHeader, String lead,
			String date, String bodyText) {
		this.articleId = articleId;
		this.mainHeader = mainHeader;
		this.lead = lead;
		this.date = date;
		this.bodyText = bodyText;
	}

	public String getArticleId() {
		return articleId;
	}

	public void setArticleId(String articleId) {
		this.articleId = articleId;
	}

	public String getMainHeader() {
		return mainHeader;
	}

	public void setMainHeader(String mainHeader) {
		this.mainHeader = mainHeader;
	}

	public String getLead() {
		return lead;
	}

	public void setLead(String lead) {
		this.lead = lead;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getBodyText() {
		return bodyText;
	}

	public void setBodyText(String bodyText) {
		this.bodyText = bodyText;
	}

}
