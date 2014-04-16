package com.sree.textbytes.gateml;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.log4j.Logger;

import com.sree.textbytes.StringHelpers.PropertyFileLoader;
import com.sree.textbytes.gateml.classifier.MLClassifier;
import com.sree.textbytes.gateml.loaders.GappLoader;
import com.sree.textbytes.gateml.loaders.SourceLoader;
import com.sree.textbytes.gateml.preprocess.InstancePreprocessor;
import com.sree.textbytes.gateml.training.MLTrainer;

/**
 * Main Class for running GATE-ML
 * It accepts a property file named "GATE-ML.properties"
 * The three learningMode values are "Preprocess","Training" and "Application"
 * 
 * @author Sree
 *
 */
public class GateLearning 
{
	private static final Logger logger = Logger.getLogger(GateLearning.class.getName());
	public static PropertyFileLoader globalProeprty = new PropertyFileLoader();
	public GappLoader gappLoader = new GappLoader();
	Map<String,String> sourceProperties = new LinkedHashMap<String,String>();
	
	public GateLearning() {
		if(!isGlobalPropertyNull()) {
			SourceLoader.loadSources();
        	sourceProperties = SourceLoader.sourcesPropertyList;

        	String learningMode = globalProeprty.properties.getProperty("learningMode");
        	if(learningMode.equalsIgnoreCase("Preprocess")) {
        		executeDataPreprocessing(sourceProperties.get("preprocess"));
        	}else if(learningMode.equalsIgnoreCase("Training")) {
        		executeTraining(sourceProperties.get("training"));
        	}else if(learningMode.equalsIgnoreCase("Application")) {
        		executeClassifier(sourceProperties.get("application"));
        		
        	}else 
        		logger.error("UNKNOWN learning mode", new Exception());
        	}
	}
	
	/**
	 * Run the GATE-ML in Data pre-processing mode.
	 * Iterate over the input folder which contains sample instances to process.
	 * Get the "Sentence" annotation and apply the class label as feature "type"
	 * Write the output as a file in output folder as GATE XML's.
	 *
	 * Stop Word Removal and Punctuation removal can be activated using the 
	 * removeStopWords = True,removePunctuation = True in sources/preprocess.properties
	 * 
	 * @param learningModePropertyName
	 */
	private void executeDataPreprocessing(String learningModePropertyName) {
		gappLoader.initGATE(learningModePropertyName);
		InstancePreprocessor preprocess = new InstancePreprocessor();
   		preprocess.initializePreprocessing(learningModePropertyName,gappLoader);
	}
	
	/**
	 * Execute Batch Learning PR on top of the training instances
	 * 
	 * @param learningModePropertyName
	 */
	
	private void executeTraining(String learningModePropertyName) {
		gappLoader.initGATE(learningModePropertyName);
		MLTrainer trainer = new MLTrainer();
		trainer.initializeTraining(learningModePropertyName,gappLoader);
	}
	
	
	/**
	 * Classify an input text
	 * Just a sample input to show how GATE classification happens
	 *  
	 * @param learningModePropertyName
	 */
	private void executeClassifier(String learningModePropertyName) {
		
		String inputText = "Your sample Text goes here..";
		
		gappLoader.initGATE(learningModePropertyName);
		MLClassifier classifier = new MLClassifier();
		classifier.initializeClassification(inputText, learningModePropertyName, gappLoader);
		
	}
	
	/**
	 * Is the global property file {learning.properties} is null ?
	 * 
	 * @return
	 */
	private boolean isGlobalPropertyNull() {
		if(globalProeprty.properties == null)
			return true;
		else
			return false;
	}

	
    public static void main( String[] args ) {
		logger.debug("Poperty file loader started ");
		globalProeprty.inilializeProperty("GATE_ML.properties");
		new GateLearning();
    }
}
