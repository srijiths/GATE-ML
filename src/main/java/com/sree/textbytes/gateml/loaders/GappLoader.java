package com.sree.textbytes.gateml.loaders;

import gate.Corpus;
import gate.CorpusController;
import gate.Factory;
import gate.Gate;
import gate.creole.ResourceInstantiationException;
import gate.util.GateException;
import gate.util.persistence.PersistenceManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.sree.textbytes.StringHelpers.PropertyFileLoader;
import com.sree.textbytes.StringHelpers.ReadFile;
import com.sree.textbytes.gateml.GateLearning;
/**
 * Class which initializes the previously saved .gapp file with all the PR's
 * and create an object corresponding to the .gapp file for {@link GateAnnie}
 * 
 * @author Sree
 *  
 */
public class GappLoader {
private static final Logger logger = Logger.getLogger(GappLoader.class.getName());
	
	// Holds the corpus controller information
	public CorpusController corpusController = null;
	
	//Holds the corpus reference
	public Corpus corpus;
	public PropertyFileLoader sourceProperty = new PropertyFileLoader();
	
	/**
	 * List of annotation types to write out.  If null, write everything as GateXML
	 * 
	 */
	public List<String> annotTypesToWrite = null;
	
	static {
		initGateConf();
		try {
			Gate.init();
		} catch (GateException e) {
			logger.error(e.toString());
		}
	}
	
	/**
	 * Initialize GATE with PR's
	 * The PR's comes from a previously stored .GAPP file
	 * 
	 * @param propertyFileName
	 */
	public void initGATE(String propertyFileName) {
		logger.debug("GATE Initialization started...");
		sourceProperty.inilializeProperty(propertyFileName);
		
		// Path to the saved application file.
		File gappFile = null;
		String gappFileName = sourceProperty.properties.getProperty("GAPPFile");
		
		logger.debug("GAPP file name : "+gappFileName);
		gappFile = new File(gappFileName);
		if(gappFile.exists()) {
			logger.debug("Got GAPP file  "+gappFile +" from "+sourceProperty.getPropertyFileName());
			try { 
				if(GateLearning.globalProeprty.properties.getProperty("learningMode").equalsIgnoreCase("Preprocess")) {
					annotTypesToWrite = readAnnotationsFromFile("AnnotationTypesRequired");
					logger.debug("Annotation Required "+annotTypesToWrite.toString());
				}
				// load the saved application gapp file
				corpusController = (CorpusController) PersistenceManager.loadObjectFromFile(gappFile);
				logger.debug("Loaded GAPP file  "+gappFile +" from "+sourceProperty.getPropertyFileName());
				setCorpus();
			} catch (GateException e) {
				logger.error("GATE Initialization Exception : "+e.toString());
			} catch (IOException e) {
				logger.error("IO Exception : "+e.toString());
			}
			logger.debug("GATE Initialization completed...");
		}else {
			logger.error("GAPP file "+gappFile.getAbsolutePath()+" from property does not exist, exiting...");
			return;
		}
	}
	
	
	/**
	 * Create a Corpus to use. We recycle the same Corpus object for each
	 * iteration. The string parameter to newCorpus() is simply the
	 * GATE-internal name to use for the corpus. It has no particular significance.
	 */
	private void setCorpus() {
		logger.debug("Setting up the corpus started");
		String corpusName = sourceProperty.properties.getProperty("CorpusName");
		try {
			corpus = Factory.newCorpus(corpusName);
			corpusController.setCorpus(corpus);
		} catch (ResourceInstantiationException e) {
			logger.error("Resource Instantiation  : "+e.toString());
		}
		logger.debug("New Corpus created with name : "+corpusName);
	}
	
	
	/**
	 * Initialize GATE_HOME and some other configuration properties here.
	 */
	private static void initGateConf() {
		String gateHome = GateLearning.globalProeprty.properties.getProperty("GATE_HOME");
		logger.debug("Got the GATE_HOME from property file "+gateHome);
		
		String fileSeparator = System.getProperty("file.separator");
		System.setProperty("gate.home", gateHome);
        System.setProperty("gate.site.config", System.getProperty("gate.home")+ fileSeparator + "gate.xml");
        System.setProperty("gate.user.config", System.getProperty("gate.site.config"));
        
        logger.debug("Initalization of GATEConf Parameters completed with "+gateHome);
	}
	
	/**
	 * We need to inject class labels for the needed annotations from AnnotationsRequired.txt file 
	 * under sources/preprocess dir.
	 * This file specifies on which all annotations we need to apply the class label
	 * 
	 * @return
	 */
	
	private List<String> readAnnotationsFromFile(String txtFile) {
		String annotationRequired = sourceProperty.properties.getProperty(txtFile);
		File annotationRequiredFile = new File(annotationRequired);
		if(!annotationRequiredFile.exists()) {
			logger.error("AnnotationTypeRequired file does not exists , exiting ");
			return null;
		}
		logger.debug("AnnotationTypeRequired file exists "+annotationRequiredFile.getAbsolutePath());
		if(!annotationRequiredFile.getName().endsWith(".txt")) {
			logger.debug("AnnotationTypeRequired is not a text file, exiting "+annotationRequiredFile.getName());
			return null;
		}
			
		logger.debug("AnnotationTypeRequired is a text file,procees "+annotationRequiredFile.getName());
		List<String> annotations = new ArrayList<String>();
		annotations = ReadFile.read(annotationRequiredFile);
		return annotations;
	}
}
