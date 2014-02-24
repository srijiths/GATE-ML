GATE-ML
=======
Machine Learning in GATE as Embedded. This package contains 3 phases.

* Preprocessing : Read input text files and create GATE XML files
* Training		: Train GATE XML files and create a model
* Application	: Classify a input text using the trained model

Property files
====================
GATE_ML.properties
------------------
Inital property needed for the system to run

* GATE_HOME 		: GATE HOME in your system
* learningMode		: Three modes are : Preprocessing,Training and Application
* sourceDirectory 	: contains all the property files for the above 3 learning modes

Sources Directory
================
Source directory contains three sub directories. Each points to one of the three learning modes.

preprocess
---------

* **GAPPFile :** GAPP file for Preprocessing . A sample gapp file can be found at gappFile/ml_data_preprocessing.gapp
* **AnnotationTypesRequired :** Annotation name which you want to inject the class label.By default its Sentence.You can add your own custom
annotations here. 
If you are using a annotation other than GATE default annotations , make sure to build the gapp files using that PR's
* **CorpusName :** Name of the corpus
* **inputDir :** Contains training files as .txt files. At the time of preprocessing , the directory name is treated as
the class label for all the txt files in it. Expects simple directory hierarchy like [20news-group-data](http://qwone.com/~jason/20Newsgroups/)
* **outputDir :** Output GATE XML's are stored here
* **removeStopWords :** Removing stopwords or not ( true / false )

training
--------

* **GAPPFile :** GAPP file for Training . A sample gapp file can be found at gappFile/ml_training.gapp
* **CorpusName :** Name of the corpus
* **xmlCorpus :** outputDir of Preprocess mode

The ml-config.xml is under this folder , so default location of trained model is here.

application
----------

* **GAPPFile :** GAPP file for Preprocessing . A sample gapp file can be found at gappFile/ml_application.gapp
* **CorpusName :** Name of the corpus
* **removeStopWords :** Removing stopwords or not ( true / false )

GAPP Files
==========
Sample gapp files can be found here.

* **ml_data_preprocessing.gapp : ** ANNIE with defaults ( with out NE Transducer and Ortho Matcher) 
* **ml_training.gapp :** Batch Learning PR with ml-config.xml from sources/training
* **ml_application.gapp:** ANNIE with defaults ( with out NE Transducer and Ortho Matcher) and Batch Learning PR

GATE-ML Work Flow
=========

Execution starts from GATE_Learning.java which takes GATE_ML.properties and proceed further according to the learningMode.

If the learningMode is Preprocess then the system takes sources/preprocess folder as configuration directory.

If the learningMode is Training then the system takes sources/training folder as configuration directory.

If the learningMode is Application then the system takes sources/application folder as configuration directory. 

Dependency Project
===================
* [StringHelpers](https://github.com/srijiths/StringHelpers)

Build
=====

Using Maven , mvn clean install assembly:single or mvn clean package