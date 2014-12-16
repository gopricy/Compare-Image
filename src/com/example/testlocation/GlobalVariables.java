package com.example.testlocation;

import android.app.Application;

public class GlobalVariables extends Application {
	private String path = null;
	private String name = null;
	private String namesmall = null;
	private int MaxSize = 512;

	public String getPath(){
		return this.path;
	}

	public void setPath(String newPath){
		
		this.path = newPath;
	}

	public String getName(){
		return this.name;
	}

	public void setName(String newName){
		this.name = newName;
	}

	public String getNameSmall(){
		return this.namesmall;
	}

	public void setNameSmall(String newNameSmall){
		this.namesmall = newNameSmall;
	}

	public int getMaxSize(){
		return this.MaxSize;
	}
}
