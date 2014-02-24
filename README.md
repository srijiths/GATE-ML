GATE-ML
=======

Machine Learning in GATE as JAVA Embedded. This package contains 3 phases

* Preprocessing
* Training
* Application

GATE_Learning.java is the main program which takes arguments from the global property file GATE_ML.properties.

* GATE_ML.properties
====================
* GATE_HOME 		: GATE HOME in your system
* learningMode		: Three modes are : Preprocessing,Training and Application
* sourceDirectory 	: contains all the property files for the above 3 learning modes

Sources Directory
================
It contains three sub directories:

* preprocess : it contains preprocess.properties and annotationsRequired.txt
preprocess.properties has below properties
GAPPFile 				: GAPP file to load for Data Preprocessing. 
A Sample gapp file can be found at gappFiles/ml_data_preprocessing.gapp
AnnotationTypesRequired 



Brief Description about the work flow
=====================================
If the learningMode is Preprocess , then system takes the sources/preprocess directory and initialize the inne



Whats extra in readabilityBUNDLE
================================

* Preserve the html tags in the extracted content.
* Keep all the possible images in the content instead of finding best image.
* Keep all the available videos.
* Better extraction of li,ul,ol tags
* Content normalization of extracted content.
* Incorporated 3 best popular extraction algorithm , you can choose based on your requirement.
* Provision to append next pages extracted content and create a consolidated output
* Many cleaner / formatter measures added.
* Some core changes in algorithms.

The main challenge which i was facing to extract the main content by keeping all the images / videos / html tags / and some realated div tags which are used as content / non content identification by most of the algorithms.

readabilityBUNDLE borrows much code and concepts from [Project Goose](https://github.com/GravityLabs/goose) , [Snacktory](https://github.com/karussell/snacktory) and [Java-Readability](https://github.com/basis-technology-corp/Java-readability). My intension was just fine tune / modify the algorithm to work with my requirements.

Some html pages works very well in a particular algorithm and some not. This is the main reason i put all the available algorithm under a roof . You can choose an algorithm which best suits you.

You can see all author citations in each java file itself.

Dependency Projects
===================
* [StringHelpers](https://github.com/srijiths/StringHelpers)
* [Network](https://github.com/srijiths/Network)
* [NextPageFinder] (https://github.com/srijiths/NextPageFinder)

Usage
=====
You need to say which extraction algorithm to use. The 3 extraction algorithms are ReadabilitySnack,ReadabilityCore and ReadabilityGoose. By default its ReadabilitySnack.

* With out next page finding

Sample Usage

	Article article = new Article();
	ContentExtractor ce = new ContentExtractor();
	HtmlFetcher htmlFetcher = new HtmlFetcher();
	String html = htmlFetcher.getHtml("http://blogmaverick.com/2012/11/19/what-i-really-think-about-facebook/?utm_source=feedburner&utm_medium=feed&utm_campaign=Feed%3A+Counterparties+%28Counterparties%29", 0);

	article = ce.extractContent(html, "ReadabilitySnack");

	System.out.println("Content : "+article.getCleanedArticleText());

* With next page html sources

If you need to extract and append content from next pages also then,

* You can use [NextPageFinder] (https://github.com/srijiths/NextPageFinder) to find out all the next pages links.
* Get the html of each next pages as a List of String using [Network](https://github.com/srijiths/Network)
* Pass it to the content extractor like

	article = ce.extractContent(firstPageHtml,extractionAlgorithm,nextPagesHtmlSources)

Build
=====

Using Maven , mvn clean package