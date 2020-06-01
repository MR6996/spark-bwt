package com.randazzo.mario.sparkbwt;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.spark.SparkContext;
import org.apache.spark.sql.SparkSession;

import java.io.File;
import java.nio.file.Paths;

/**
 * Builder for class {@code BWT}.
 *
 * @author Mario Randazzo
 */
public class BWTBuilder {

    private static final Log LOG = LogFactory.getLog(BWTBuilder.class);

    private File inputFile;

    private String inputFilePath;
    private String outputFilePath;
    private String workingDirectory;
    //private int startIdx;
    //private int endIdx;
    private boolean verbose;

    /**
     * Build a Builder for {@code BWT} class from the input file specified.
     *
     * @param inputFilePath a path to the input file.
     * @throws IllegalArgumentException if the inputFilePath are not valid.
     */
    public BWTBuilder(String inputFilePath) throws IllegalArgumentException {
        inputFile = new File(inputFilePath);

        /*if (!inputFile.isFile())
            throw new IllegalArgumentException("File " + inputFilePath + " is not valid file!");
*/
        this.inputFilePath = inputFilePath;
        this.outputFilePath = inputFilePath + ".bwt";
        this.workingDirectory = "";
        //this.startIdx = 0;
        //this.endIdx = -1;
        this.verbose = false;

        LOG.info("Initialized builder for: " + inputFilePath);
    }

    /**
     * Build a Builder for {@code BWT} class from the input file specified and the working directory specified.
     *
     * @param workingDirectory a path to a directory.
     * @param inputPath        a path to the input file.
     * @throws IllegalArgumentException if {@code workingDirectory} is not a directory.
     */
    public BWTBuilder(String workingDirectory, String inputPath) throws IllegalArgumentException {
        this(Paths.get(workingDirectory, inputPath).toString());

        if (!new File(workingDirectory).isDirectory())
            throw new IllegalArgumentException(workingDirectory + " is not a valid directory.");
        this.workingDirectory = workingDirectory;

        LOG.info("Setting working directory: " + workingDirectory);
    }

    /**
     * Set the output file path.
     *
     * @param outputFilePath a path to the outfile.
     * @return this builder.
     */
    public BWTBuilder setOutputFilePath(String outputFilePath) {
        LOG.info("Setting output file path: " + outputFilePath);

        this.outputFilePath = outputFilePath;
        return this;
    }

    /*
     * Specify the index where the string in the input file begins.
     * Default value is 0.
     *
     * @param idx the beginning index, inclusive.
     * @return this builder.
     * @throws IndexOutOfBoundsException if {@code idx} is negative.
     */
    /*public BWTBuilder setStartIndex(int idx) throws IndexOutOfBoundsException {
        LOG.info("Setting start index: " + idx);

        if (idx < 0) throw new IndexOutOfBoundsException("Start index is negative!");
        if (idx > inputFile.length())
            throw new IndexOutOfBoundsException("Start index is not valid! It is greater than file size!");
        this.startIdx = idx;
        return this;
    }*/

    /*
     * Specify the index where the string in the input file ends.
     * Default value is -1 which represents the end.
     *
     * @param idx the ending index, exclusive.
     * @return this builder.
     * @throws IndexOutOfBoundsException if {@code idx} is negative.
     */
    /*public BWTBuilder setEndIndex(int idx) throws IndexOutOfBoundsException {
        LOG.info("Setting end index: " + idx);

        if (idx < 0) throw new IndexOutOfBoundsException("End index is negative!");
        if (idx > inputFile.length())
            throw new IndexOutOfBoundsException("End index is not valid! It is greater than file size!");
        this.endIdx = idx;
        return this;
    }*/


    public BWTBuilder setVerbose(boolean verbose) {
        LOG.info("Setting verbose mode to: " + verbose);

        this.verbose = verbose;
        return this;
    }

    /**
     * Build the {@code BWT} object.
     *
     * @return a {@code BWT} built from the option specified.
     */
    public BWT build() {
        SparkSession session = new SparkSession.Builder()
                .appName(String.format(
                        "SparkBWT - %s",
                        lastOf(inputFilePath.split("[\\\\/]"))
                ))
                .config("spark.hadoop.validateOutputSpecs", false)
                //.master("local[*]")
                .getOrCreate();

        SparkContext sc = session.sparkContext();

        if (!verbose) sc.setLogLevel("ERROR");

        return new BWT(session, verbose, inputFilePath,
                Paths.get(workingDirectory, outputFilePath).toString());
    }

    private static <T> T lastOf(T[] v) {
        return v[v.length - 1];
    }
}
