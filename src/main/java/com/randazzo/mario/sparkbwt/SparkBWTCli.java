package com.randazzo.mario.sparkbwt;

import org.apache.commons.cli.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * This class provides a Command Line Interface to the application.
 * Parse and set up the option parameters from the input command line arguments.
 *
 * @author Mario Randazzo
 */
public class SparkBWTCli {

    private static final String HELP_OPT = "h";
    private static final String OUTPUT_OPT = "o";
    private static final String DIRECTORY_OPT = "d";
    //private static final String START_INDEX_OPT = "s";
    //private static final String END_INDEX_OPT = "e";
    private static final String VERBOSE_OPT = "v";

    private boolean help;

    private BWTBuilder bwtBuilder;
    private CommandLine cmd;
    private Options opts;

    public SparkBWTCli() {
    }

    /**
     * Parse and set up the option parameters from the args passed by parameter.
     * Must be called after the constructor!
     *
     * @param args command line arguments array.
     * @throws ParseException            if there is an error with the arguments passed by parameter.
     * @throws IllegalArgumentException  if there is an error with the option arguments.
     * @throws IndexOutOfBoundsException if there is an error with the option arguments.
     */
    public void setup(String[] args) throws ParseException,
            IllegalArgumentException, IndexOutOfBoundsException {
        opts = new Options();

        opts.addOption(HELP_OPT, "help", false, "Show this help message");

        Option outputOpt = Option.builder(OUTPUT_OPT)
                .longOpt("output")
                .hasArg(true)
                .required(false)
                .desc("Specify the output filename. Default is <input_filename>.bwt")
                .argName("filename")
                .build();
        opts.addOption(outputOpt);

        /*Option startIndexOpt = Option.builder(START_INDEX_OPT)
                .longOpt("start")
                .hasArg(true)
                .required(false)
                .desc("Specify the index where the string in the input file begins. " +
                        "Default value is 0.")
                .argName("index")
                .build();
        opts.addOption(startIndexOpt);*/

        Option directoryOpt = Option.builder(DIRECTORY_OPT)
                .longOpt("directory")
                .hasArg(true)
                .required(false)
                .desc("Specify the working directory. Default is \"./\".")
                .argName("path")
                .build();
        opts.addOption(directoryOpt);

        /*Option endIndexOpt = Option.builder(END_INDEX_OPT)
                .longOpt("end")
                .hasArg(true)
                .required(false)
                .desc("Specify the index where the string in the input file ends, " +
                        "Default value is -1 indicates the end.")
                .argName("index")
                .build();
        opts.addOption(endIndexOpt);*/

        Option verboseOpt = Option.builder(VERBOSE_OPT)
                .longOpt("verbose")
                .hasArg(false)
                .required(false)
                .desc("Set the log level in verbose mode.")
                .argName("verbose")
                .build();
        opts.addOption(verboseOpt);

        CommandLineParser parser = new DefaultParser();
        this.cmd = parser.parse(opts, args);

        setupOption();
    }

    /**
     * Set bwtBuilder from parsed options.
     *
     * @throws IllegalArgumentException  if some option have a not valid argument.
     * @throws IndexOutOfBoundsException if some option have a not valid argument.
     * @throws MissingOptionException    if the inputFilePath is no specified.
     */
    private void setupOption() throws IllegalArgumentException,
            MissingOptionException, IndexOutOfBoundsException {

        if (cmd.hasOption(DIRECTORY_OPT))
            bwtBuilder = new BWTBuilder(cmd.getOptionValue(DIRECTORY_OPT), getInputPath());
        else
            bwtBuilder = new BWTBuilder(getInputPath());


        if (cmd.hasOption(HELP_OPT))
            help = true;

        if (cmd.hasOption(OUTPUT_OPT))
            bwtBuilder.setOutputFilePath(cmd.getOptionValue(OUTPUT_OPT));

        /*if (cmd.hasOption(START_INDEX_OPT))
            bwtBuilder.setStartIndex(Integer.parseInt(cmd.getOptionValue(START_INDEX_OPT)));

        if (cmd.hasOption(END_INDEX_OPT))
            bwtBuilder.setEndIndex(Integer.parseInt(cmd.getOptionValue(END_INDEX_OPT)));*/

        if (cmd.hasOption(VERBOSE_OPT))
            bwtBuilder.setVerbose(true);

    }

    /**
     * Get the input filename from command line arguments.
     *
     * @return the input filename.
     * @throws MissingOptionException if there isn't any arguments.
     */
    private String getInputPath() throws MissingOptionException {
        List<String> argList = cmd.getArgList();
        if (argList.size() == 0) throw new MissingOptionException("");
        return argList.get(0);
    }

    /**
     * Print a message of help: usage and help.
     * Also print a custom message.
     *
     * @param message message to print.
     */
    public void printHelpMessage(String message) {
        PrintWriter writer = new PrintWriter(System.out);

        HelpFormatter formatter = new HelpFormatter();

        BufferedReader reader = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(SparkBWTCli.class.getClassLoader().getResourceAsStream("logo.txt"))));

        String logo = reader.lines().collect(Collectors.joining("\n"));
        formatter.printWrapped(writer, 80, logo + (message != null ? "\n\n" + message + "\n\n" : "\n\n"));

        formatter.printUsage(writer, 80, "spark-submit spark-bwt.jar [Options] [File]");
        formatter.printOptions(writer, 80, this.opts, 2, 12);

        writer.print("\n\n");
        writer.flush();
    }

    /**
     * Print a message of help: usage and help.
     */
    public void printHelpMessage() {
        printHelpMessage(null);
    }

    /**
     * Check if there is the help option.
     *
     * @return true if there is the help option.
     */
    public boolean isHelp() {
        return help;
    }


    /**
     * Build a {@code BWT} with the option parsed from command line.
     *
     * @return a {@code BWT}
     */
    public BWT getBuiltBWT() {
        return bwtBuilder.build();
    }

}
