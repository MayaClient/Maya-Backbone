package com.codingforcookies.mayabackbone.src.module;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.codingforcookies.mayabackbone.src.MayaClient;

public class ModuleLoadHandler {
	public File moduleFolder = new File(MayaClient.mayaDirectory, "modules");
	
	private JavaModuleLoader javaModuleLoader;
	
	public ModuleLoadHandler() {
		if(!moduleFolder.exists() && !moduleFolder.mkdirs())
			System.err.println("Failed to create " + moduleFolder.getAbsolutePath());
		
		javaModuleLoader = new JavaModuleLoader();
	}
	
	/**
	 * Loads the moduals with first loading set. Then returns the remaining modules.
	 * @return
	 */
	public List<Module> loadFirst() {
		System.out.println("Loading modules...");
		try {
			List<File> possibleFiles = new ArrayList<File>();
			for(File file : moduleFolder.listFiles()) {
				if(file.isDirectory() || (!file.getName().endsWith(".jar") && !file.getName().endsWith(".mcm"))) {
					System.out.println("Invalid file in modules folder! Offending file is " + file.getName() + ".");
					continue;
				}
				possibleFiles.add(file);
			}
			
			System.out.println("Found " + possibleFiles.size() + " possible module" + (possibleFiles.size() != 1 ? "s" : "") + ".");
			
			Module[] modulesFirstLoad = new Module[100];
			List<Module> modules = new ArrayList<Module>();
			
			for(File possibleFile : possibleFiles) {
				ZipFile file = new ZipFile(possibleFile);

				if(file != null) {
					Enumeration<? extends ZipEntry> entries = file.entries();

					if(entries != null) {
						boolean failed = true;
						while(entries.hasMoreElements()) {
							ZipEntry entry = entries.nextElement();
							if(!entry.getName().equals("module.main"))
								continue;
							failed = false;
							
							String main = readStream(file.getInputStream(entry));
							Module module = new Module();
							module.moduleFile = possibleFile;
							module.moduleClass = main.trim();
							
							if(main.contains(":")) {
								module.moduleClass = main.split(":")[1].trim();
								modulesFirstLoad[Integer.parseInt(main.split(":")[0])] = module;
							}else
								modules.add(module);
							break;
						}
						
						if(failed)
							System.out.println("Invalid module. Offending file is " + file.getName() + ".");
					}
				}

				file.close();
			}
			
			List<Module> firstLoadModules = new ArrayList<Module>();
			for(Module module : modulesFirstLoad)
				if(module != null)
					firstLoadModules.add(module);
			
			System.out.println("Preparing to load " + modules.size() + " module" + (modules.size() != 1 ? "s" : "") + (firstLoadModules.size() != 0 ? " and " + firstLoadModules.size()+ " required modules" : "") + "...");
			
			for(Module module : firstLoadModules) {
				Module mod = javaModuleLoader.loadModule(module);
				MayaClient.modules.put(mod.ID, mod);
				System.out.println("Loaded " + mod.name);
			}
			
			return modules;
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public void load(List<Module> modules) {
		try {
			for(Module module : modules) {
				Module mod = javaModuleLoader.loadModule(module);
				MayaClient.modules.put(mod.ID, mod);
				System.out.println("Loaded " + mod.name);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void unload() {
		for(Entry<String, Module> module : MayaClient.modules.entrySet()) {
			module.getValue().onUnload();
			MayaClient.modules.remove(module.getKey());
		}
	}

	private static String readStream(java.io.InputStream inputStream) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
		try {
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while(line != null) {
				sb.append(line);
				sb.append("\n");
				line = br.readLine();
			}

			return sb.toString();
		} finally {
			br.close();
			inputStream.close();
		}
	}
}