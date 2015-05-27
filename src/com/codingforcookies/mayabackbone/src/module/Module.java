package com.codingforcookies.mayabackbone.src.module;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import com.codingforcookies.mayaclientapi.src.MayaModule;

public class Module {
	private File moduleFile;
	private String main;
	private Class<?> mainClass;
	private Object instance;

	public boolean firstLoad = false;
	public String ID = "";
	public String name = "";
	public String description = "";
	public String version = "";
	public String creator = "";
	public String homepage = "";

	public Module(File moduleFile, String main) {
		this.moduleFile = moduleFile;
		this.main = main.trim();
		
		try {
			URLClassLoader child = new URLClassLoader(new URL[] { moduleFile.toURI().toURL() }, this.getClass().getClassLoader());

			mainClass = Class.forName(this.main, true, child);
			instance = mainClass.newInstance();
			for(Annotation annotation : mainClass.getDeclaredAnnotations()) {
				if(annotation instanceof MayaModule) {
					MayaModule mAnnotation = (MayaModule)annotation;
					firstLoad = mAnnotation.firstLoad();
					ID = mAnnotation.ID();
					name = mAnnotation.name();
					description = mAnnotation.description();
					version = mAnnotation.version();
					creator = mAnnotation.creator();
					homepage = mAnnotation.homepage();
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void load() throws MalformedURLException, ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		System.out.println("Loading " + name + " v." + version + "...");
		System.out.println("------------------------------");
		URLClassLoader child = new URLClassLoader(new URL[] { moduleFile.toURI().toURL() }, this.getClass().getClassLoader());
		mainClass = Class.forName(main, true, child);
		instance = mainClass.newInstance();
		try {
			for(Method method : mainClass.getMethods()) {
				if(method.getName().toLowerCase().equals("onload")) {
					method.invoke(instance);
					break;
				}
			}
		} catch(SecurityException e) {
			e.printStackTrace();
		}
		System.out.println("------------------------------");
	}

	public void unload() {
		try {
			for(Method method : mainClass.getMethods()) {
				if(method.getName().toLowerCase().equals("onunload")) {
					method.invoke(instance);
					break;
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}