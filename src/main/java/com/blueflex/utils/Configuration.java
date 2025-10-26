package com.blueflex.utils;

import java.util.Map;

public class Configuration {
    public String className;
    public boolean enabled = true;
    public Map<String, Object> config;

    public Configuration() {} // Jackson needs this no-arg constructor

    public Configuration(String className, boolean enabled, Map<String, Object> config) {
        this.className = className;
        this.enabled = enabled;
        this.config = config;
    }
}
