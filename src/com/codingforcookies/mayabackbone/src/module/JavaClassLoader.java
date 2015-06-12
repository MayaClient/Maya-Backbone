package com.codingforcookies.mayabackbone.src.module;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.codingforcookies.mayaclientapi.src.MayaModule;

public class JavaClassLoader extends URLClassLoader {
    private final JavaModuleLoader loader;
    private final Map<String, Class<?>> classes = new HashMap<String, Class<?>>();
    public final Module module;

    JavaClassLoader(final JavaModuleLoader loader, final ClassLoader parent, final File file, String mainClass) throws MalformedURLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        super(new URL[] { file.toURI().toURL() }, parent);
        this.loader = loader;
        
        Class<?> jarClass = Class.forName(mainClass, true, this);
        Class<? extends Module> moduleClass = jarClass.asSubclass(Module.class);
        module = moduleClass.newInstance();
        
		for(Annotation annotation : moduleClass.getDeclaredAnnotations()) {
			if(annotation instanceof MayaModule) {
				MayaModule mAnnotation = (MayaModule)annotation;
				module.ID = mAnnotation.ID();
				module.name = mAnnotation.name();
				module.version = mAnnotation.version();
				module.creator = mAnnotation.creator();
				module.homepage = mAnnotation.homepage();
			}
		}
		
		System.out.println("Initializing " + file.getName() + ": " + module.name + "(" + module.ID + ")");
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return findClass(name, true);
    }

    Class<?> findClass(String name, boolean checkGlobal) throws ClassNotFoundException {
        Class<?> result = classes.get(name);

        if(result == null) {
            if(checkGlobal)
                result = loader.getClassByName(name);

            if(result == null) {
                result = super.findClass(name);

                if(result != null)
                    loader.setClass(name, result);
            }

            classes.put(name, result);
        }

        return result;
    }

    Set<String> getClasses() {
        return classes.keySet();
    }
}