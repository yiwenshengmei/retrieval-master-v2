package com.zj.retrieval.master.entity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;


public class NodeFeature {

	public static final int YES    = 2;
	public static final int NO     = 1;
	public static final int UNKNOW = 3;
	
	public static String textValue(int value) {
		switch(value) {
			case YES : return "Yes";
			case NO : return "No";
			case UNKNOW : return "Unknow";
			default : return "Unknow Feature Answer Value";
		}
	}
	
	public static String shotTextValue(int value) {
		switch(value) {
			case YES : return "¡Ì";
			case NO : return "¡Á";
			case UNKNOW : return "-";
			default : return "Unknow Feature Answer Value";
		}
	}
	
	private String desc = StringUtils.EMPTY;
	private String name = StringUtils.EMPTY;
	private String englishName = StringUtils.EMPTY;
	private List<FeatureImage> images;
	private File[] imageFiles;
	private String[] imageFilesContentType;
	private String[] imageFilesFileName;
	private RetrievalDataSource retrievalDataSource;
	private String id;
	private int index = -1;
	
	public NodeFeature() { 
		images = new ArrayList<FeatureImage>();
	}
	
	public NodeFeature(String name) {
		this();
		this.name = name;
	}
	
	public NodeFeature(String name, RetrievalDataSource retrievalDataSource) {
		this(name);
		this.retrievalDataSource = retrievalDataSource;
	}
	
	public NodeFeature(String name, String id) {
		this(name);
		this.id = id;
	}
	
	public NodeFeature(String name, String enName, String desc) {
		this.desc = desc;
		this.name = name;
		this.englishName = enName;
	}
	
	public NodeFeature withRetrievalDataSource(RetrievalDataSource rds) {
		this.retrievalDataSource = rds;
		return this;
	}
	
	public NodeFeature withImages(List<FeatureImage> images) {
		this.images = images;
		return this;
	}
	
	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEnglishName() {
		return englishName;
	}

	public void setEnglishName(String englishName) {
		this.englishName = englishName;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public List<FeatureImage> getImages() {
		return images;
	}

	public void setImages(List<FeatureImage> images) {
		this.images = images;
	}

	public String getEnName() {
		return englishName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public RetrievalDataSource getRetrievalDataSource() {
		return retrievalDataSource;
	}

	public void setRetrievalDataSource(RetrievalDataSource retrievalDataSource) {
		this.retrievalDataSource = retrievalDataSource;
	}

	public String[] getImageFilesContentType() {
		return imageFilesContentType;
	}

	public void setImageFilesContentType(String[] imageFilesContentType) {
		this.imageFilesContentType = imageFilesContentType;
	}

	public String[] getImageFilesFileName() {
		return imageFilesFileName;
	}

	public void setImageFilesFileName(String[] imageFilesFileName) {
		this.imageFilesFileName = imageFilesFileName;
	}

	public void setImageFiles(File[] imageFiles) {
		this.imageFiles = imageFiles;
	}

	public File[] getImageFiles() {
		return imageFiles;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((englishName == null) ? 0 : englishName.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + index;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NodeFeature other = (NodeFeature) obj;
		if (englishName == null) {
			if (other.englishName != null)
				return false;
		} else if (!englishName.equals(other.englishName))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (index != other.index)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
