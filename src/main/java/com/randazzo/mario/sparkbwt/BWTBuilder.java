package com.randazzo.mario.sparkbwt;

import com.randazzo.mario.sparkbwt.util.Util;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;

import java.io.File;

/**
 *  Builder for class {@code BWT}.
 *
 * @author Mario Randazzo
 */
public class BWTBuilder {

    private final SparkContext sc;

    private String inputFilePath;
    private String outputFilePath;
    private int startIdx;
    private int endIdx;
    private int k;

    /**
     *  Build a Builder for {@code BWT} class from the input file specified.
     *
     * @param inputFilePath a path to the input file.
     * @throws IllegalArgumentException if the inputFilePath are not valid.
     */
    public BWTBuilder(String inputFilePath) throws IllegalArgumentException{
        if( !new File(inputFilePath).isFile())
            throw  new IllegalArgumentException("File " + inputFilePath + " is not valid file!");

        this.inputFilePath = inputFilePath;
        this.outputFilePath = inputFilePath + ".bwt";
        this.startIdx = 0;
        this.endIdx = -1;
        this.k = 3;

        SparkConf conf = new SparkConf()
                .setAppName("SparkBWT - " + Util.lastElement(inputFilePath.split("[\\\\/]")))
                .setMaster("local[*]");
        this.sc = new SparkContext(conf);
    }

    /**
     *  Set the output file path.
     *
     * @param outputFilePath a path to the outfile.
     * @return this builder.
     */
    public BWTBuilder setOutputFilePath(String outputFilePath) {
        this.outputFilePath = outputFilePath;
        return this;
    }

    /**
     *  Specify the index where the string in the input file begins.
     * Default value is 0.
     *
     * @param idx the beginning index, inclusive.
     * @return this builder.
     * @throws IndexOutOfBoundsException if {@code idx} is negative.
     */
    public BWTBuilder setStartIndex(int idx) throws IndexOutOfBoundsException {
        if(idx < 0) throw new IndexOutOfBoundsException("Start index is negative!");
        //TODO: Out of Uppper bound check
        this.startIdx = idx;
        return this;
    }

    /**
     *  Specify the index where the string in the input file ends.
     * Default value is -1 which represents the end.
     *
     * @param idx the ending index, exclusive.
     * @return this builder.
     * @throws IndexOutOfBoundsException if {@code idx} is negative.
     */
    public BWTBuilder setEndIndex(int idx) throws IndexOutOfBoundsException {
        if(idx < 0) throw new IndexOutOfBoundsException("End index is negative!");
        //TODO: Out of Uppper bound check
        this.endIdx = idx;
        return this;
    }

    /**
     *  Set the value of k in the algorithm.
     *
     * @param k a positive integer.
     * @return this builder.
     * @throws IllegalArgumentException if k are not valid value, must be positive.
     */
    public BWTBuilder setK(int k) throws IllegalArgumentException {
        if(k < 1)  throw new IllegalArgumentException("k must be positive.");
        this.k = k;
        return this;
    }

    /**
     *  Build the {@code BWT} object.
     *
     * @return a {@code BWT} built from the option specified.
     */
    public BWT build() {
        return new BWT(sc, 4, k, startIdx, endIdx, inputFilePath, outputFilePath);
    }
}
