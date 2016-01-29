# Introduction #

This is a step to step tutorial with screenshots on how to perform an automatic query with a given annotated database.

In this example we use the Corel database and perform a query with random query images from the same database. The descriptor CEDD is used.


# Table of Contents #

**1. Before using simple-cbir**

_1.1. Download Corel database_

_1.2. Extract descriptors via img(Rummager)_

**2. Adjusting settings**

_2.1. Put the correct settings_

**3. Console input**

_3.1. Compiling with_ant_and executing_

_3.2. Output File_


# 1. Before using simple-cbir #

## 1.1. Download Corel database ##

Download the Corel database here: https://sites.google.com/site/dctresearch/Home/content-based-image-retrieval Download the 7 Parts and unpack them. Save the database in the folder of your choice.

## 1.2. Extract descriptors via img(Rummager) ##

Now it's time to extract the descriptors from the images of the database. For extracting CEDD we use the tool _img(rummager)_. Download it here: http://chatzichristofis.info/?page_id=213 and install it if you haven't already.

Run _img(rummager)_, and select "Manage Index Files > Create Index Files" in the menu bar.

![https://simple-cbir.googlecode.com/git/wiki/images/rummager1.png](https://simple-cbir.googlecode.com/git/wiki/images/rummager1.png)

Choose CEDD as descriptor, then specify the destination folder and name for the descriptor file, ideally this is the database folder (CorelDB). Also choose the folders from which you want to compute the descriptor. In this case, these are all the the subfolders of CorelDB. There should be 10800 Images in total. Finally hit the play button on the bottom and wait until the tool finishes the work.

https://simple-cbir.googlecode.com/git/wiki/images/rummager2.PNG


# 2. Adjusting settings #

Now that the preparation is done, it is time to execute _simple-cbir_. If you haven't downloaded our code yet, download it via git:
` git clone https://code.google.com/p/simple-cbir/ `

Before you can execute the program you have to specify the correct settings in _src/cbir/AutomatedRFTest.java_. So open _src/cbir/AutomatedRFTest.java_ with an (java) editor of your choice.

Specify following settings:

There are three settings which are important, otherwise the program may not execute.

  * _output_: you have to specify the html output file path. In our case this is "C:\\Users\\Stanic\\Desktop\\CorelDB\\automatic\_corel\_cedd.html", but you can take the path of your choice.

  * _type_: as we have extracted CEDD descriptors it's important to specify it as the descriptor type. So set type as ` DescriptorType.CEDD `

  * _xml path_: here you have to specify the path to the XML descriptor file created from _img(rummager)_. In our case this would be: "C:\\Users\\Stanic\\Desktop\\CorelDB\\cedd\_descriptors.xml"


https://simple-cbir.googlecode.com/git/wiki/images/simplecbir1.PNG

The other parameters can be left as they are. Of course you can play around with the settings.


# 3. Executing the program #

## 3.1. Compiling with _ant_ and executing ##

## 3.2. Output File ##