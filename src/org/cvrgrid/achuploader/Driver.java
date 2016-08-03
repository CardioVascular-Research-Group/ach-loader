package org.cvrgrid.achuploader;

import java.io.IOException;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Driver {

    static Options options = new Options();

    /**
     * Registers command line interface options.
     */
    private static void registerOptions() {
        Option version = new Option("v", "version", false, "Displays version information");

        Option limit = new Option("l", "limit", true, "Number of folders to script up");
        Option processedFile = new Option("p", "processed-file", true, "File to track what's already been done");
        Option batchFile = new Option("b", "batch-file", true, "File to output the batch script in");
        Option achRoot = new Option("r", "root-dir", true, "Directory to generate the script for");

        options.addOption(version);
        options.addOption(limit);
        options.addOption(processedFile);
        options.addOption(batchFile);
        options.addOption(achRoot);
    }

    private static void printHelp() {
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp("java -jar achuploader.jar", options);
    }

	public static void main(String... aArgs) {

		registerOptions();

        CommandLineParser parser = new BasicParser();

        try {
            CommandLine cmd = parser.parse(options, aArgs);
            ApplicationContext context =  new AnnotationConfigApplicationContext(ApplicationConfigs.class);
            String achRoot = "", limit = "", processedFile = "", batchFile = "";
            
            if (cmd.hasOption("version")) {
                String versionInfo = (String)context.getBean("version");
                System.out.println(versionInfo);

            } else if (cmd.hasOption("achRoot") || cmd.hasOption("limit") || cmd.hasOption("processedFile") || cmd.hasOption("batchFile")) {
                if (cmd.hasOption("achRoot")) achRoot = cmd.getOptionValue("achRoot");
            	if (cmd.hasOption("limit")) limit = cmd.getOptionValue("limit");
                if (cmd.hasOption("processedFile")) processedFile = cmd.getOptionValue("processedFile");
                if (cmd.hasOption("batchFile")) batchFile = cmd.getOptionValue("batchFile");

                AchUploaderFacade facade = (AchUploaderFacade)context.getBean("achUploaderFacade");
                facade.generateScript(achRoot, limit, processedFile, batchFile);

            } else {
                printHelp();
            }

        } catch (ParseException | IOException | java.text.ParseException e) {
            System.err.println(e.getMessage());
        }

	}

}
