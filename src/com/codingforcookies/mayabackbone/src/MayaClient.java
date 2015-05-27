package com.codingforcookies.mayabackbone.src;

import java.io.File;
import java.util.List;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import com.codingforcookies.mayabackbone.src.module.Module;
import com.codingforcookies.mayabackbone.src.module.ModuleLoader;

public class MayaClient {
	public static void main(String[] args) {
		new MayaClient();
	}
	
	public static final String version = "1.0.0";
	public static String OS = "";
	
	public static File mayaDirectory;
	private static ModuleLoader moduleLoader;
	public static ModuleLoader getModuleLoader() {
		return moduleLoader;
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
		
		initOpenGL();
		
		moduleLoader = new ModuleLoader();
		final List<Module> nextModules = moduleLoader.loadFirst();
		
		new Thread() {
			public void run() {
				try {
					Thread.sleep(10000L);
				} catch(InterruptedException e) {
					e.printStackTrace();
				}
				moduleLoader.load(nextModules);
			}
		}.start();
		
		new Test();
	}

	private void initOpenGL() {
		try {
		    Display.setResizable(true);
		    Display.setDisplayMode(new DisplayMode(800, 500));
		    Display.create();
		} catch (LWJGLException e) {
		    e.printStackTrace();
		    System.exit(0);
		}
	}
}