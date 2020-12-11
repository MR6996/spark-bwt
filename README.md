# SparkBWT

## About the project
SparkBWT is a tool for calculating the Burrows-Wheeler transform (BWT) on Apache Spark Framework.

### Build with
The application has been developed using **maven**, the main languages are **java** and **scala**. To improve the performance it has been used **C/C++** languages too, integrated through JNI.
In the development of the application was used the framework [Apache Spark](http://spark.apache.org/).

### Structure
The source code is the *src* folder. Inside there are the *main* folder that contains the code for the application and the *test* folder that contains the code for testing the classes in *main* folder. 

In *src/main* we can find:
* **java** contains the JNI glue code and the code for CLI.
* **native** contains the native code, that is the c++ procedure for sorting based on Radix-Sort algorithm
* **scala** contains the implementation of the algorithm in [Apache Spark](http://spark.apache.org/)..

## Getting started
### Prerequisites
The building of the project can be made automatically with maven, but this requires that the following tools are installed in the system:

*  `make`
*  `g++`

For building in Windows environment you have to use MinGW and [CMake](https://cmake.org/download/).

### Build
To build the project from command line:
	
    git clone https://github.com/MR6996/spark-bwt
    cd spark-bwt
    mvn package -P [profile]

The profiles are `window` and  `linux` depending on your operating system.

In the created `/target` folder, we can find the `jar` file needed to run the application (Should be named as `spark-bwt.jar`).

### Usage

The tool can be launched using the tool provided by default by Apache Spark `spark-submit`. Can be used a YARN cluster and can be used the option parameters for configuration.
A typical usage is:

    spark-submit [options] spark-bwt.jar <filename>

for help:

    spark-submit spark-bwt.jar -h

## License
  The project is distributed under GPL v.3 License [More info](LICENSE.md)
  
## References
  [1] **Mario Randazzo, Simona E. Rombo** [A Big Data Approach for Sequences Indexing on the Cloud via Burrows Wheeler Transform](https://arxiv.org/abs/2007.10095)
