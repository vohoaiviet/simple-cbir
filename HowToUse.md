# Table of contents - How to use simple-cbir #

**1) Resizing images**

**2) Extracting Descriptors**

2.1) img(rummager)

2.2) FIRE

**3) Running simple-cbir**

3.3) Queries with manual Relevance Feedback

3.4) Queries with automated Relevance Feedback


# Details #

**1) Resizing images**

> Before extracting descriptors it's recommended to resize the images, so that all 	images have the same size. This guarantees that the descriptors all have the same 	value base.

> For resizing images we recommend irfanview (http://irfanview.tuwien.ac.at/)
> With "File > Batch Conversion/Rename" its easy to perform reszing. Select the 	database folder and choose the resizing parameters in "Batch conversion settings - 	Advanced". Select the output directory for the result files and start the batch mode 	which does the resizing.



**2) Extracting Descriptors**

> Before our program can run descriptors have to be extracted. This is done by the 	tools img(rummager) and FIRE.


**2.1) img(rummager)**

> The extraction of MPEG7-EHD and CEDD is done by the tool img(rummager).
> Open "Manage Index Files > Create Index Files" to open a new window. Now you can 	tick "CEDD" or "MPEG-7 EHD". Specify a destination folder and a filename for the 	new XML file and choose your database folder. By pressing the play button the XML 	files will be genetated.

> IMPORTANT: CEDD and MPEG-7 EHD should be saved into two different XML files, 	so make one CEDD XML file and one MPEG-7 EHD XML file.


**2.2) FIRE**

> The extraction of Color Histogram is done with FIRE (http://thomas.deselaers.de/fire/)

> IMPORTANT: The extracted descriptors have to lie in the same folder as the 	database images.



**3) Running simple-cbir**

> The program has two classes which are used for different purposes:
> With "AutomatedRFTest.java" it's possible to perform automatic queries with 	annotated images, while "RelevanceFeedbackTest.java" is used for queries with 	manual annotation of positive and negative results during relevance feedback.

> IMPORTANT: to run the program the XML parser "dom4j" 	(http://dom4j.sourceforge.net/) is needed (in the classpath).


**3.3) Queries with manual Relevance Feedback**

> "RelevanceFeedbackTest.java" performs queries with random pictures of the 	specified database. The result is stored into a HTML file.

> Before running a query there are several things that have to be specified (attributes of 	the class "RelevanceFeedbackTest.java"):

  * resultFolder and outputfile: here the absolute path to the wanted result folder of the 	  	output file has to be specified

  * metric: here the wanted metric has to be specified: choose between the classes of 		cbir.metric (Cosine, Euclidian, Manhattan, WeightedCosine. 				WeightedEuclidian)

  * type: choose the descriptor type you want to perform the query with: 				DescriptorType.CEDD, DescriptorType.COLOR\_HISTO, 					DescriptorType.MPEG\_EHD or DescriptorType.MERGED

  * normalization: choose the normalization method (important for the merged 			descriptor): e.g: Normalization.GAUSSIAN or Normalization.

  * xml\_path: specify the absolute path of the img(rummager) XML file from the 			descriptor that you want (either CEDD or MPEG-7 EHD). IMPORTANT: The 		selected XML file will also be used for the merged descriptor.

  * use\_indexing: true if you want to use the k-d-tree as index structure and false if not

  * indexingFor: Alternatively to use\_indexing you can add all the types that you want to 		index into this array manually

  * rf: here you can specify the wanted relevance feedback. Choose from the classes of 		rf, rf.bayesian, rf-mars and rf.nn

  * queryAmount: change the amount of random queries.

> When you run the program, it perfoms a query, which is saved to the specified output 	file. Then the system waits for Relevance Feedback:

> usage: for each rf iteration you need to specify the indices of the relevant and
> irrelevant images. There are several commands that the demo user
> interface accepts:

> "others": this command has to be entered when the program asks for irrelevant 	images, by entering "others" all images that are not marked as relevant are 	considered as irrelevant.

> "all": this command has to be entered when the program asks for relevant images. 	This means that all images are considered as positive.

> "assist": this command has to be entered when the program asks for relevant images, 	after assist you have to specify the file name of the file that contains the already 	marked images. IMPORTANT: you still have to mark the positive images in the same 	line.

> "quit": this command has to be entered when the program asks for relevant images, 	and causes the rf demo to stop, so the next query can be performed. After stop you 	can specify a filename where the marked images should be saved, this is needed 	when you want to use the assistant function in the future.


**3.4) Queries with automated Relevance Feedback**

> "AutomatedRFTest.java" is performed in the same fasion as above. But in this case 	annotated databases are used. Subfolders in the database folder denote the label of a 	picture. A good examle for such a database is the Corel-database (https://sites.google.com/site/dctresearch/Home/content-based-image-retrieval)

> Now that Relevance Feedback is done automatically by comparing the query image 	label with the other labels, you have to specify additional attributes:

  * numOfQueries - the number of queries that should be performed

  * numOfRfIterations - the number of automatic RF-Iterations performed

  * numOfResults - the size of the result set

  * newRandoms - denotes if you want to generate new random queries

  * randomIndicesFile - if newRandoms is false, you can specify a txt file where you get 			the query image indices from

> All the other attributes are defined as in 3.3)


