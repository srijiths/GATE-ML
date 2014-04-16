package com.sree.textbytes.gateml.classifier;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;

import com.sree.textbytes.StringHelpers.PropertyFileLoader;
import com.sree.textbytes.gateml.annie.GateAnnie;
import com.sree.textbytes.gateml.loaders.GappLoader;
import com.sree.textbytes.gateml.preprocess.TextPruning;

/**
 * CLassifier class which accepts an input file and gapp loder,
 * execute it and return the GATE Document , which contains all the annotations.
 *
 * Batch Learning PR's outputAS name set to "Classified"
 * So you can get the classified results by iterating over "Classified" annotation set
 * 
 * @author Sree
 *
 */

public class MLClassifier {
	public static Logger logger = Logger.getLogger(MLClassifier.class.getName());
	public void initializeClassification(String inputText,String learningPropertyFile,GappLoader gappLoader) {
		
		PropertyFileLoader classifierProperty = new PropertyFileLoader();
		classifierProperty.inilializeProperty(learningPropertyFile);
		String stopWordRemove = classifierProperty.properties.getProperty("removeStopWords");
		String punctRemove = classifierProperty.properties.getProperty("removePunctuation");
		TextPruning textPruning = new TextPruning();
		if(stopWordRemove.equalsIgnoreCase("True")) {
			inputText = textPruning.cleanStopWords(inputText);
		}
		
		if(punctRemove.equalsIgnoreCase("True")) {
			inputText = textPruning.cleanPunctuations(inputText);
		}
		
		GateAnnie gateAnnie = new GateAnnie();
		Document document = gateAnnie.performClassification(inputText, gappLoader);
		
		Map<String,AnnotationSet> classifiedMap = new HashMap<String, AnnotationSet>(document.getNamedAnnotationSets());
		
		Iterator keyIterator = classifiedMap.keySet().iterator();
		while(keyIterator.hasNext()) {
			String key = keyIterator.next().toString();
			AnnotationSet classifiedSet = classifiedMap.get(key);
			Iterator<Annotation> annotationIterator = classifiedSet.iterator();
			while(annotationIterator.hasNext()) {
				Annotation predictedAnnotation = annotationIterator.next();
				String annotationString = getAnnotationString(predictedAnnotation, document);
				String predictedClass = predictedAnnotation.getFeatures().get("type").toString();
				String predictedProb = predictedAnnotation.getFeatures().get("prob").toString();
				
				logger.debug("====================================");
				logger.debug("Annotation String : "+annotationString);
				logger.debug("Predicted Class : "+predictedClass);
				logger.debug("Confidence Score : "+predictedProb);
				logger.debug("====================================");
			}
			
		}
		
		gateAnnie.releaseFactoryResource(document);
	}
	
	public static String getAnnotationString(Annotation annot , Document doc) {
		String annotation = gate.Utils.stringFor(doc, annot);
		annotation = StringEscapeUtils.unescapeJava(annotation);
		return annotation;
	}

}
