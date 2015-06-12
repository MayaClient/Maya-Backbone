package com.codingforcookies.mayabackbone.src.module;

import java.io.File;

public class Module {
	public ClassLoader classLoader = null;
	public ModuleLoader loader;
	
	public File moduleFile;
	public String moduleClass;
	
	public String ID = "";
	public String name = "";
	public String version = "";
	public String creator = "";
	public String homepage = "";
	
	public void onLoad() {
		
	}
	
	public void onUnload() {
		
	}
}