package org.sankalptaru.www.framework.model;

public class Census {

	private int id;
	private String treename;
	private String latitude;
	private String longitude;
	private String height;
	private String girth;
	private String canopy_size;
	private String remarks;
	private int forest_id;
	private String created_at;
	private String imageUrl;
	
	
	public Census() {
		super();
	}
	public Census(String treename, String latitude, String longitude,
			String height, String girth, String canopy_size, String remarks,
			int forest_id,String imageUrl) {
		super();
		this.treename = treename;
		this.latitude = latitude;
		this.longitude = longitude;
		this.height = height;
		this.girth = girth;
		this.canopy_size = canopy_size;
		this.remarks = remarks;
		this.forest_id = forest_id;
		this.imageUrl = imageUrl;
	}
	
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getCreated_at() {
		return created_at;
	}
	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTreename() {
		return treename;
	}
	public void setTreename(String treename) {
		this.treename = treename;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public int getForest_id() {
		return forest_id;
	}
	public void setForest_id(int forest_id) {
		this.forest_id = forest_id;
	}
	public String getHeight() {
		return height;
	}
	public void setHeight(String height) {
		this.height = height;
	}
	public String getGirth() {
		return girth;
	}
	public void setGirth(String girth) {
		this.girth = girth;
	}
	public String getCanopy_size() {
		return canopy_size;
	}
	public void setCanopy_size(String canopy_size) {
		this.canopy_size = canopy_size;
	}
}
