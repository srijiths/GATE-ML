package com.sree.textbytes.gateml.preprocess;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sree.textbytes.StringHelpers.StopWords;

public class TextPruning {
	
	/**
	 * Read content of the Text files line by line
	 * 
	 * @param inputFile
	 * @return
	 */
	public String readTextFile(String inputFile) {
		StringBuilder fileText = new StringBuilder();
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(
					inputFile));
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				fileText.append(line);
				fileText.append(System.lineSeparator());
			}
			bufferedReader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileText.toString();
	}
	
	/**
	 * Clean Stopwords in text
	 * 
	 * @param text
	 * @return
	 */
	public String cleanStopWords(String text) {
		StringBuilder sb = new StringBuilder();
		Iterator iter = StopWords.STOP_WORDS.iterator();
		while (iter.hasNext()) {
			sb.append(iter.next().toString() + "|");
		}
		String subString = sb.toString().substring(0, sb.lastIndexOf("|"));

		Pattern pattern = Pattern.compile("\\b(" + subString + ")\\b",Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		text = matcher.replaceAll("");

		return text;
	}
	
		/**
	 * Clean all punctuations 
	 * 
	 * @param text
	 * @return
	 */
	public String cleanPunctuations(String text) {
		String replacedString = text.replaceAll("\\p{Punct}", "");
		return replacedString;
	}

}
