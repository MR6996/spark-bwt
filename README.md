# SparkBWT

## About the project
SparkBWT is a tool for calculating the Burrows-Wheeler transform (BWT) on Apache Spark Framework.

### Build with
The application has been developed using **maven**, the main languages are **java** and **scala**. To improve the performance it has been used **C/C++** languages too, integrated through JNI.

The framworks and libraries used in the development of the application are the following:
* [sais](https://sites.google.com/site/yuta256/sais) - this library privide an implementation of
the IS based linear suffix array construction algorithm, [more info](https://ieeexplore.ieee.org/document/5582081).
* [Apache Spark](http://spark.apache.org/)

### Structure
The source code is the *src* folder. Inside there are the *main* folder that contains the code for the application and the *test* folder that contains the code for testing the classes in *main* folder. 

In *src/main* we can find:
* **java** contains ...
* **native** contains the native code (included the [sais](https://sites.google.com/site/yuta256/sais) libray)
* **scala** contains ...

## Getting started
### Prerequisites

### Build
To build the project from command line:
	
    git clone https://github.com/MR6996/spark-bwt
    cd spark-bwt
    mvn package -P [profile]

The profiles are: *window*, *linux*

In the created *target* folder, we can find the *jar* file needed to run the application.

### Usage

## License
  TODO license info
  
## References
  TODO references info
