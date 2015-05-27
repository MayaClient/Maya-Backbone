package com.codingforcookies.mayabackbone.src.module;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.codingforcookies.mayabackbone.src.MayaClient;
import com.codingforcookies.mayaclientapi.src.RenderLoading;

public class ModuleLoader {
	public File moduleFolder = new File(MayaClient.mayaDirectory, "modules");
	public HashMap<String, Module> modules = new HashMap<String, Module>();
	
	public ModuleLoader() {
		if(!moduleFolder.exists() && !moduleFolder.mkdirs())
			System.err.println("Failed to create " + moduleFolder.getAbsolutePath());
	}
	
	/**
	 * Loads the moduals with first loading set. Then returns the remaining modules.
	 * @return
	 */
	public List<Module> loadFirst() {
		RenderLoading.complete = false;
		RenderLoading.process = "Loading Modules";
		
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
			
			System.out.println("  Found " + possibleFiles.size() + " possible module" + (possibleFiles.size() != 1 ? "s" : "") + ".");
			
			List<Module> modulesFirstLoad = new ArrayList<Module>();
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
							Module module = new Module(possibleFile, main);
							if(module.firstLoad)
								modulesFirstLoad.add(module);
							else
								modules.add(module);
							break;
						}
						
						if(failed)
							System.out.println("  Invalid module. Offending file is " + file.getName() + ".");
					}
				}

				file.close();
			}
			
			System.out.println("  Preparing to load " + modules.size() + " module" + (modules.size() != 1 ? "s" : "") + (modulesFirstLoad.size() != 0 ? " and " + modulesFirstLoad.size() + " required modules" : "") + "...");
			
			for(Module module : modulesFirstLoad) {
				module.load();
				this.modules.put(module.name, module);
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
				module.load();
				this.modules.put(module.name, module);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		RenderLoading.complete = true;
	}

	public void unload() {
		for(Entry<String, Module> module : modules.entrySet()) {
			modules.remove(module.getKey());
			module.getValue().unload();
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