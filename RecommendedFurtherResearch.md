#This is a brief overview of cbir related technologies that should be examined.

# Introduction #

Due to the fact that our application was meant to be a proof of concept and research application we examined several technologies, unfortunately not all of them. There remain methods of possible interest in different sections of our thesis.

  1. Image representation & distance metric
  1. Indexing
  1. Relevance Feedback


# Details #

## 1. Image representation & distance metric ##

### Descriptors ###

The most obvious thing that could be investigated is the usage of new descriptors in the system. This also implicates that there are new combinations of descriptors to be considered. In addition the choice of descriptors could be made use-case dependend. In the example of the museum database where similar images are taken in front of a uniform background, this fact can be exploited by using a color based descriptor for example. In other databases color is less descriptive and the edge based component could be enhanced.

### Earth Mover Distance ###

Another approach to improve the overall performance could be to utilize the famous "earth mover distance" (EMD) by changing the image representation from histograms to signatures and then compute the EMD on those signatures. Doing this should not affect the compatibility with RF methods like BQS, Rocchio or the NN-RF methods.

## 2. Indexing ##

### Locality-Based-Hashing ###

In the paper: http://ieeexplore.ieee.org/xpl/abstractAuthors.jsp?arnumber=6144153
locality-based-hashing gets utilized for a 2.5D descriptor, and shows great performance. This hashing strategy could be also made available for our image descriptors like in: http://ieeexplore.ieee.org/xpls/abs_all.jsp?arnumber=5192277.

## 3. Relevance Feedback ##

### RF as classification problem ###

The current state-of-the-art methods consider the RF problem as a classification problem and solve it by using existing classifiers like the "Support Vector Machine" (SVM) and "Discriminant Analysis" (DA). We recommend to implement these approaches and evaluate them using our AutomatedRFTest class. The papers give instructions how to use those RF-methods: http://ieeexplore.ieee.org/xpls/abs_all.jsp?arnumber=1658034 (DA) and http://ieeexplore.ieee.org/xpls/abs_all.jsp?arnumber=1394177, http://ieeexplore.ieee.org/stamp/stamp.jsp?arnumber=01634340 (SVM)

### Speedup the NN methods ###

We could show that the Nearest Neighbor based RF methods provide very good results and can be used to explore several regions of the search space. The only problem with these approaches was the bad performance. The score calculation could be performed in parallel using Java-Threads or FutureTasks (we already tried this in a sloppy way and achieved a speedup of factor 2 on a 2-core machine). In addition the utilization of a proper index structure would reduce the computational costs of this method as well. Maybe these optimizations would be sufficient to make NN-RF feasible for real world CBIR applications.