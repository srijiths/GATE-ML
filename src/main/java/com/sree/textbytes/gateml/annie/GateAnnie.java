package com.sree.textbytes.gateml.annie;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Iterator;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Corpus;
import gate.Document;
import gate.Factory;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;

import org.apache.log4j.Logger;

import com.sree.textbytes.StringHelpers.string;
import com.sree.textbytes.gateml.loaders.GappLoader;

/**
 * Class which invokes the previously saved .gapp file instances and execute GATE pipeline
 * 
 * @author 		: Sree
 *  
 *
 */
public class GateAnnie {
	
	public static final Logger logger = Logger.getLogger(GateAnnie.class.getName());
	public GappLoader gappLoader = null;
	public Document annieDocument = null;
	
	/**
	 * Perform ANNIE process on the input text passed and return a GATE XML file.
	 * It execute GATE over an input text and returns an XML string which comprises all
	 * matched annotations in input text.
	 * If the annotation is Sentence then we are injecting the Sentence class value as
	 *  "type" for Machine Learning
	 * 
	 * @param inputText
	 * @param classLabel
	 * @param gappLoader
	 * @return
	 */
	public String performDataPreprocess(String inputFile,String classLabel,GappLoader gappLoader) {
		this.gappLoader = gappLoader;
		annieDocument = null;
		String xmlString = "";
		
		if(!string.isNullOrEmpty(inputFile)) {
			try {
				annieDocument = createGateDocument(inputFile);
				setCorpus(annieDocument);
				executeAnnie();
				clearCorpus();
				xmlString = writeGateXML(classLabel);
				releaseFactoryResource(annieDocument);
			}catch(Exception e) {
				logger.error(e.toString(),e);
			}finally {
				releaseFactoryResource(annieDocument);
			}
			return xmlString;
		}else 
			return null;
	}

	/**
	 * Perform Training over a set of GATE XML files.
	 * 
	 * @param xmlFiles
	 * @param gappLoader
	 */
	
	public void performTraining(File[] xmlFiles,GappLoader gappLoader) {
		annieDocument = null;
		this.gappLoader = gappLoader;
		for(File xmlFile : xmlFiles) {
			if(!xmlFile.isDirectory()) {
				try {
					annieDocument = createGateDocument(xmlFile);
					setCorpus(annieDocument);
				} catch (Exception e) {
					logger.error(e.toString(),e);
			   }
		   	}
		}
		executeAnnie();
		clearCorpus();
		releaseFactoryResource(annieDocument);
		
	}
	
	/**
	 * Perform classification on a text input
	 * 
	 * The ml_application.gapp file already loaded in GAPP Loader.
	 * The pipeline contains all the needed ANNIE PR's and a Batch Learning PR 
	 * at the end of the pipeline.
	 * The Batch Learning PR's outputAS name set as "Classified" in gapp.
	 * 
	 * @param inputText
	 * @param gappLoader
	 * @return
	 */
	
	public Document performClassification(String inputText,GappLoader gappLoader) {
		annieDocument = null;
		this.gappLoader = gappLoader;
		if(!string.isNullOrEmpty(inputText)) {
			try {
				annieDocument = createGateDocument(inputText);
				setCorpus(annieDocument);
				executeAnnie();
				clearCorpus();
//				releaseFactoryResource(annieDocument);
			}catch(Exception e) {
				logger.error(e.toString(),e);
			}
			
		}
		return annieDocument;
	}
	
	/**
	 * Get the default annotation set from ANNIE Document
	 * @return
	 */
	private AnnotationSet getDefaultAnnotationSet() {
		AnnotationSet defaultAnnotationSet = annieDocument.getAnnotations();
		return defaultAnnotationSet;
	}
	
	/**
	 * Get only needed Annotations from Property file.
	 * We dont need all the default annotations here. 
	 * If Sentence annotations then inject the class label as feature key "type"
	 * 
	 * @param classLabel
	 * @return
	 */
	private void getNeededAnnotations(String classLabel) {
		if(gappLoader.annotTypesToWrite != null) {
			Iterator<String> annotationTypesIterator = gappLoader.annotTypesToWrite.iterator();
			AnnotationSet defaultAnnotationSet = getDefaultAnnotationSet();
			while(annotationTypesIterator.hasNext()) {
				String neededAnnotation = annotationTypesIterator.next().toString();
				AnnotationSet neededAnnotationSet = defaultAnnotationSet.get(neededAnnotation);
				if(neededAnnotationSet != null) {
					Iterator<Annotation> sentenceIterator = neededAnnotationSet.iterator();
					Annotation currentAnnotation;
					while(sentenceIterator.hasNext()) {
						currentAnnotation = sentenceIterator.next();
						currentAnnotation = injectClassLabelInSentence(classLabel, currentAnnotation);
					}
				}
			}
		}
	}
	
	/**
	 * If its a sentence then inject class label in Features
	 * @param classLabel
	 * @param currentAnnotation
	 * @return
	 */
	
	private Annotation injectClassLabelInSentence(String classLabel,Annotation currentAnnotation) {
		currentAnnotation.getFeatures().put("type", classLabel);
		return currentAnnotation;
	}
	
	/**
	 * Writes GATE XML from ANNIE Document
	 * 
	 * @param classLabel
	 * @return
	 */
	private String writeGateXML(String classLabel) {
		String docXMLString = null;
		if(classLabel != null) {
			getNeededAnnotations(classLabel);
		}
		
		docXMLString = annieDocument.toXml();
		return docXMLString;
	}
	
	
	/**
	 * Set the Gate document to the Corpus for processing
	 * @param document
	 */
	private void setCorpus(Document document) {
		Corpus corpus = gappLoader.corpusController.getCorpus();
		corpus.add(document);
		logger.debug("New Gate document added to Corpus "+corpus.getName());
	}
	
	/**
	 * create Gate representation document from File
	 * @param inputText
	 * @return
	 * @throws MalformedURLException 
	 */

	private Document createGateDocument(File inputFile) throws MalformedURLException {
		Document document = null;
		try {
			document = Factory.newDocument(inputFile.toURI().toURL(),"UTF-8");
			document.setName(inputFile.getName());
		} catch (ResourceInstantiationException e) {
			logger.error("Gate document can not created "+e.toString());
		}
		
		logger.debug("Gate document successfully created ");
		return document;
	}
	
	/**
	 * create Gate representation document from input text
	 * @param inputText
	 * @return
	 * @throws MalformedURLException 
	 */
	
	private Document createGateDocument(String inputText) throws MalformedURLException {
		Document document = null;
		try {
			document = Factory.newDocument(inputText);
		} catch (ResourceInstantiationException e) {
			logger.error("Gate document can not created "+e.toString());
		}
		
		logger.debug("Gate document successfully created ");
		return document;
	}
	
	/**
	 * Execute GATE ANNIE with a corpus
	 */
	private void executeAnnie() {
		try {
			gappLoader.corpusController.execute();
		} catch (ExecutionException e) {
			logger.error("GATE ANNIE Execution Exception "+e.toString());
		}
		logger.debug("GATE ANNIE Execution completed");
	}
	
	/**
	 * Clears the corpus
	 */
	private void clearCorpus() {
		gappLoader.corpus.clear();
		gappLoader.corpusController.cleanup();
		logger.debug("GATE Corpus cleared ");
	}
	
	/**
	 * Release the occupied document factory resources..
	 * @param gateDoc
	 */
	public void releaseFactoryResource(Document gateDoc) {
		gateDoc.cleanup();
		if(gateDoc != null) {
			Factory.deleteResource(gateDoc);
		}
		logger.debug("Gate document factory deleted :)");
	}

}
