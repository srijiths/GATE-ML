package com.sree.textbytes.gateml.training;

import gate.util.ExtensionFileFilter;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

import org.apache.log4j.Logger;

import com.sree.textbytes.StringHelpers.PropertyFileLoader;
import com.sree.textbytes.gateml.annie.GateAnnie;
import com.sree.textbytes.gateml.loaders.GappLoader;


/**
 * ML Training class which takes the xmlDir and apply it
 * on the Training GAPP and create a model in the sources/Training folder
 * 
 * @author Sree
 *
 */

public class MLTrainer {
	public static final Logger logger = Logger.getLogger(MLTrainer.class.getName());
	public PropertyFileLoader trainingProperty = new PropertyFileLoader();
	
	public void initializeTraining(String trainingPropertyFile,GappLoader gappLoader) {
		trainingProperty.inilializeProperty(trainingPropertyFile);
		String xmlInputDirString = trainingProperty.properties.getProperty("xmlCorpus");
		logger.debug("XMl Input Folder : "+xmlInputDirString);
		File xmlInputDir = new File(xmlInputDirString);
		if(!xmlInputDir.isDirectory()) {
    		logger.error("INPUT FOLDER should be a Directory , exiting..",new Exception());
    		return;
		}else {
			ExtensionFileFilter fileFilter = new ExtensionFileFilter();
		    fileFilter.addExtension("xml");
		    File[] xmlFiles = xmlInputDir.listFiles(fileFilter);
		    Arrays.sort(xmlFiles, new Comparator<File>() {
		        public int compare(File a, File b) {
		          return a.getName().compareTo(b.getName());
		        }
		    });

		    GateAnnie gateAnnie = new GateAnnie();
		    gateAnnie.performTraining(xmlFiles,gappLoader);
			
		}
	}


}
