package com.codingforcookies.mayabackbone.src;

import java.io.File;
import java.util.HashMap;

import com.codingforcookies.mayabackbone.src.module.Module;
import com.codingforcookies.mayabackbone.src.module.ModuleLoadHandler;
import com.codingforcookies.mayaui.test.Test;

public class MayaClient {
	public static void main(String[] args) {
		new MayaClient();
	}
	
	public static final String version = "1.0.0";
	public static String OS = "";
	
	public static File mayaDirectory;
	private static ModuleLoadHandler moduleLoader;
	public static ModuleLoadHandler getModuleLoader() {
		return moduleLoader;
	}
	
	public static HashMap<String, Module> modules = new HashMap<String, Module>();
	public static Module getInstance(String moduleName) {
		if(modules.containsKey(moduleName))
			return modules.get(moduleName);
		return null;
	}
	
	public MayaClient() {
		System.out.println("Maya Client Backbone v." + version);
		
		String OS = (System.getProperty("os.name")).toUpperCase();
		String mayaDirectory = "";
		if(OS.contains("WIN")) {
			mayaDirectory = System.getenv("AppData");
			OS = "windows";
		}else{
			mayaDirectory = System.getProperty("user.home");
			OS = "linux";
		    if(new File(mayaDirectory + "/Library/Application Support").exists()) {
		    	mayaDirectory += "/Library/Application Support";
		    	OS = "macosx";
		    }
		}
		if(!mayaDirectory.endsWith("/") && !mayaDirectory.endsWith("\\"))
			mayaDirectory += "/";
		MayaClient.mayaDirectory = new File(mayaDirectory, "MayaClient");
		
	    System.setProperty("org.lwjgl.librarypath", MayaClient.mayaDirectory + File.separator + "libs" + File.separator + "natives" + File.separator + OS);
	    
		moduleLoader = new ModuleLoadHandler();
		moduleLoader.load(moduleLoader.loadFirst());
		
		new Test();
	}
}