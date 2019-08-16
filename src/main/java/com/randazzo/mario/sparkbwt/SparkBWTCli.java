package com.randazzo.mario.sparkbwt;

import org.apache.commons.cli.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 *  This class provides a Command Line Interface to the application.
 * Parse and set up the option parameters from the input command line arguments.
 *
 * @author Mario Randazzo
 *
 */
public class SparkBWTCli {

    private static final String HELP_OPT = "h";
    private static final String OUTPUT_OPT = "o";
    private static final String K_LENGHT_OPT = "k";
    private static final String K_LENGHT_OPT_DEFAULT = "3";

    private boolean help = false;
    private String outputFilename;
    private int k;

    private CommandLine cmd;
    private Options opts;

    public SparkBWTCli() {}

    /**
     *  Parse and set up the option parameters from the args passed by parameter.
     * Must be called after the constructor!
     *
     * @param args command line arguments array
     * @throws ParseException if there is an error with the arguments passed by parameter
     * @throws IllegalArgumentException if there is an error with the option arguments
     */
    public void setup(String[] args) throws ParseException, IllegalArgumentException {
        opts = new Options();

        opts.addOption(HELP_OPT, "help", false, "Show this help message");

        Option outputOpt = Option.builder(OUTPUT_OPT)
                .longOpt("output")
                .hasArg(true)
                .required(false)
                .desc("Specify the output filename")
                .argName("filename")
                .build();
        opts.addOption(outputOpt);

        Option kLengthOpt = Option.builder(K_LENGHT_OPT)
                .longOpt("kLength")
                .hasArg(true)
                .required(false)
                .desc("Specify the the length of k-mers in the algorithm. Default value is 3, the value must be positive.")
                .argName("lenght")
                .build();
        opts.addOption(kLengthOpt);

        CommandLineParser parser = new DefaultParser();
        this.cmd = parser.parse(opts, args);

        setupOption();
    }

    private void setupOption() throws IllegalArgumentException {
        if(cmd.hasOption(HELP_OPT))
            help = true;
        else if(cmd.hasOption(OUTPUT_OPT))
            outputFilename = cmd.getOptionValue(OUTPUT_OPT, null);
        else if(cmd.hasOption(K_LENGHT_OPT)) {
            k = Integer.parseInt(cmd.getOptionValue(K_LENGHT_OPT, K_LENGHT_OPT_DEFAULT));
            if(k < 1) throw new IllegalArgumentException("k must be positive.");
        }
    }

    /**
     *  Print a message of help: usage and help.
     * Also print a custom message.
     *
     * @param message message to print.
     */
    public void printHelpMessage(String message) {
        PrintWriter writer = new PrintWriter(System.out);

        HelpFormatter formatter = new HelpFormatter();
        if(message != null)
            formatter.printWrapped(writer, 80, "SPARK BWT Version 0.1.0\n" + message + "\n");
        else {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(
                        Objects.requireNonNull(
                                SparkBWTCli.class.getClassLoader().getResource("logo.txt")).getFile()));

                String logo = reader.lines().collect(Collectors.joining("\n"));
                formatter.printWrapped(writer, 80, logo);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        formatter.printUsage(writer, 80, "spark-bwt [Options] [File]");
        formatter.printOptions(writer, 80, this.opts, 2, 12);

        writer.flush();
    }

    /**
     *  Print a message of help: usage and help.
     */
    public void printHelpMessage() {
        printHelpMessage(null);
    }

    /**
     *  Check if there is the help option.
     *
     * @return true if there is the help option.
     */
    public boolean isHelp() {
        return help;
    }

    /**
     *  Get the output filename, if specified.
     *
     * @return the output filename, or null if the option is unused.
     */
    public String getOutputFilename() {
        return outputFilename;
    }

    /**
     *  Get the k length options.
     *
     * @return k length
     */
    public int getK() {
        return k;
    }

    /**
     *  Get the input filename from command line arguments.
     *
     * @return the input filename
     * @throws MissingOptionException if there isn't any arguments.
     */
    public String getInputPath() throws MissingOptionException {
        List<String> argList = cmd.getArgList();
        if(argList.size() == 0) throw new MissingOptionException("");
        return argList.get(0);
    }

}
