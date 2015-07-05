package org.sankalptaru.www.framework.model;

public class Forest_Census {
	private int id;
	private String created_at;
	private String forest_name;
	
	public Forest_Census() {
		super();
	}
	public Forest_Census(String forest_name) {
		super();
		this.forest_name = forest_name;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCreated_at() {
		return created_at;
	}
	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}
	public String getForest_name() {
		return forest_name;
	}
	public void setForest_name(String forest_name) {
		this.forest_name = forest_name;
	}
	
	
}
