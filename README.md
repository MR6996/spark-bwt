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
* **java** contains the JNI glue code and the code for CLI.
* **native** contains the native code (included the [sais](https://sites.google.com/site/yuta256/sais) libray)
* **scala** contains the implementation of the algorithm.

## Getting started
### Prerequisites
The building of the project can be made automatically with maven, but this requires that this tool are installed in the 
system: 
* cmake
* make

*(this are used for build **sais** library)*

* g++

For building in windows environment you have to use MinGW and [CMake](https://cmake.org/download/).

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
