package com.sree.textbytes.gateml.loaders;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.sree.textbytes.gateml.GateLearning;

/**
 * This class used to invoke all the sources and its property files.
 * we have a sources folder in the current working dir , which has all
 * the property files and information of Preprocess,Training and Application modes
 * 
 * @author Sree
 *
 */

public class SourceLoader
{
	public static final Logger logger = Logger.getLogger(SourceLoader.class.getName());
	
	/** Holds all the source property files */
	public static Map<String,String> sourcesPropertyList = new LinkedHashMap<String,String>();
	
	/**
	 * Load the sources property files from Global property file.
	 */
	public static void loadSources() {
		String sources = GateLearning.globalProeprty.properties.getProperty("sourceDirectory");
		logger.debug("Sources Name : "+sources);
		File sourceDirectory = new File(sources);
		if(!sourceDirectory.exists()) {
			logger.error("Source directory does not exists...exiting..:(");
			return;
		}
		logger.debug("Source directory exists : "+sourceDirectory);
		File[] innerDirs = sourceDirectory.listFiles();
		for(File file : innerDirs) {
			logger.debug("Inner Directroies  : "+file.getName());
			File[] innerFiles = file.listFiles();
			for(File innerFile : innerFiles) {
				if(innerFile.getName().endsWith(".properties")) {
					sourcesPropertyList.put(file.getName(), innerFile.toString());
					logger.debug("Added "+ innerFile.toString() + " to sources property list");
				}
			}
		}
	}
}
