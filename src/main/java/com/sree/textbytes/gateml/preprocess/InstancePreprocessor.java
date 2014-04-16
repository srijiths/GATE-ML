package com.sree.textbytes.gateml.preprocess;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.log4j.Logger;

import com.sree.textbytes.StringHelpers.PropertyFileLoader;
import com.sree.textbytes.StringHelpers.string;
import com.sree.textbytes.gateml.annie.GateAnnie;
import com.sree.textbytes.gateml.loaders.GappLoader;

/**
 * Run the GATE-ML in Data pre-processing mode.
 * Iterate over the input folder which contains sample instances to process.
 * Get the annotations from the annotationRequired.txt file and apply the class label as feature "type"
 * Write the output as a file in output folder as GATE XML's.
 * 
 * @author Sree
 *
 */
public class InstancePreprocessor {
	public static final Logger logger = Logger.getLogger(InstancePreprocessor.class.getName());
	Map<String,String> filesLabels = new HashMap<String, String>();
	PropertyFileLoader preprocessProperty = new PropertyFileLoader();
	
	public void initializePreprocessing(String preprocessPropertyFile,GappLoader gappLoader) {
		preprocessProperty.inilializeProperty(preprocessPropertyFile);
    	String inputFolderString = preprocessProperty.properties.getProperty("inputDir");
		String stopWordRemove = preprocessProperty.properties.getProperty("removeStopWords");
		String punctRemove = preprocessProperty.properties.getProperty("removePunctuation");
    	
    	File inputFolder = new File(inputFolderString);
    	if(!inputFolder.isDirectory()) {
    		logger.error("INPUT FOLDER should be a Directory , exiting..",new Exception());
    		return;
    	}else {
    		recursiveParsing(inputFolder, null);
    		Iterator filesIterator = filesLabels.keySet().iterator();
    		GateAnnie gateAnnie = new GateAnnie();
    		TextPruning textPruning = new TextPruning();
    		while(filesIterator.hasNext()) {
    			try {
    				String file = filesIterator.next().toString();
    				String classLabel = filesLabels.get(file);
    				String inputText = textPruning.readTextFile(file);
    				if(stopWordRemove.equalsIgnoreCase("True")) {
    					inputText = textPruning.cleanStopWords(inputText);
    				}
					if(punctRemove.equalsIgnoreCase("True")) {
    					inputText = textPruning.cleanPunctuations(inputText);
    				}
    				if(!string.isNullOrEmpty(inputText.trim())) {
    					String xmlString = gateAnnie.performDataPreprocess(inputText, classLabel, gappLoader);
    					writeXMLToFile(xmlString, file);
    				}else {
    					logger.debug("Nothing to write..Input text null : "+file);
    				}
    			}catch(Exception e) {
    				logger.error(e.toString(), e);
    			}
    		}
    	}
	}

	
	/**
	 * Recursive directory iteration to get all the files from input Folder
	 * @param inputFolder
	 */
	private void recursiveParsing(File inputFile,String classLabel) {
		if(inputFile.isDirectory()) {
			File[] files = inputFile.listFiles();
			for(File file : files) {
				recursiveParsing(file, inputFile.getName());
			}
			
		}else {
			logger.debug("Inner file : "+inputFile.getAbsolutePath() + " class label : "+classLabel);
			filesLabels.put(inputFile.getAbsolutePath(), classLabel);
		}
	}
	
	/**
	 * Write GATE XML to a file in output folder
	 * 
	 * @param xmlString
	 * @param file
	 * @throws Exception
	 */
	private void writeXMLToFile(String xmlString,String file) throws Exception {
		String outputFileName = new File(file).getName() + ".out.xml";
		File outputFolder = new File(preprocessProperty.properties.getProperty("outputDir"));
		if(!outputFolder.exists()) {
			outputFolder.mkdir();
		}
		File outputFile = new File(outputFolder.toString(), outputFileName);
		BufferedWriter bufferedWriter = null;
		try {
			bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile)));
			bufferedWriter.write(xmlString);
			bufferedWriter.close();
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			bufferedWriter.close();
		}
	}
}
