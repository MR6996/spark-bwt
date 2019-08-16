package com.randazzo.mario.sparkbwt;

import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.ParseException;

import java.util.Objects;

/**
 *  Main class.
 *
 * @author Mario Randazzo
 */
public class SparkBWT {

    public static void main(String[] args) {
        SparkBWTCli cli = null;

        try {
            cli = new SparkBWTCli();
            cli.setup(args);

            if (cli.isHelp())
                cli.printHelpMessage();
            else {
                String inputPath = cli.getInputPath();

                BWT bwt = new BWT(4, cli.getK());
                bwt.run(inputPath);
            }
        } catch (MissingOptionException e) {
            cli.printHelpMessage("Missing parameter.");
        } catch (ParseException | IllegalArgumentException e) {
            Objects.requireNonNull(cli).printHelpMessage("Error in parsing command line arguments. " + e.getMessage());
        }
    }

}
