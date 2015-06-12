package com.codingforcookies.mayabackbone.src.module;

import java.util.regex.Pattern;

public interface ModuleLoader {
    public Pattern[] getPluginFileFilters();
    public Module loadModule(Module module);
    public void unloadModule(Module module);
}