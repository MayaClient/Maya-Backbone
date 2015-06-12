package com.codingforcookies.mayabackbone.src.module;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class JavaModuleLoader implements ModuleLoader {
	private final Pattern[] fileFilters = new Pattern[] { Pattern.compile("\\.jar$"), };
	private final Map<String, Class<?>> classes = new HashMap<String, Class<?>>();
	private final Map<String, JavaClassLoader> loaders = new LinkedHashMap<String, JavaClassLoader>();

	public Pattern[] getPluginFileFilters() {
		return fileFilters.clone();
	}

	Class<?> getClassByName(final String name) {
		Class<?> cachedClass = classes.get(name);

		if(cachedClass != null) {
			return cachedClass;
		}else{
			for(String current : loaders.keySet()) {
				JavaClassLoader loader = loaders.get(current);

				try {
					cachedClass = loader.findClass(name, false);
				} catch (ClassNotFoundException cnfe) {}
				if (cachedClass != null) {
					return cachedClass;
				}
			}
		}
		return null;
	}

	void setClass(final String name, final Class<?> clazz) {
		if(!classes.containsKey(name))
			classes.put(name, clazz);
	}

	private void removeClass(String name) {
		classes.remove(name);
	}

	public Module loadModule(final Module module) {
		JavaClassLoader loader = null;
		try {
			loader = new JavaClassLoader(this, getClass().getClassLoader(), module.moduleFile, module.moduleClass);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		loader.module.onLoad();
		
		loaders.put(loader.module.ID, loader);
		
		return loader.module;
	}

	public void unloadModule(final Module module) {
		System.out.println("Unloading " + module.name + "(" + module.ID + ")");
		
		ClassLoader cloader = module.classLoader;
		module.onUnload();

		loaders.remove(module.ID);

		if(cloader instanceof JavaClassLoader) {
			@SuppressWarnings("resource")
			JavaClassLoader loader = (JavaClassLoader)cloader;
			Set<String> names = loader.getClasses();
			
			for(String name : names)
				removeClass(name);
		}
	}
}