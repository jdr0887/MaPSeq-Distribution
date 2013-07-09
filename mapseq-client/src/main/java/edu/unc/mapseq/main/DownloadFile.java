package edu.unc.mapseq.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.IOUtils;

import edu.unc.mapseq.config.MaPSeqConfigurationServiceImpl;
import edu.unc.mapseq.dao.model.FileData;
import edu.unc.mapseq.ws.FileDataService;

public class DownloadFile implements Runnable {

    private final static HelpFormatter helpFormatter = new HelpFormatter();

    private final static Options cliOptions = new Options();

    private Long fileDataId;

    private File destinationDirectory;

    public DownloadFile() {
        super();
    }   

    @Override
    public void run() {
        QName serviceQName = new QName("http://ws.mapseq.unc.edu", "FileDataService");
        Service service = Service.create(serviceQName);
        QName portQName = new QName("http://ws.mapseq.unc.edu", "FileDataPort");
        String host = new MaPSeqConfigurationServiceImpl().getWebServiceHost("localhost");
        service.addPort(portQName, SOAPBinding.SOAP11HTTP_BINDING,
                String.format("http://%s:%d/cxf/FileDataService", host, 8181));
        FileDataService fileDataService = service.getPort(FileDataService.class);
        Binding binding = ((BindingProvider) service.getPort(portQName, FileDataService.class)).getBinding();
        ((SOAPBinding) binding).setMTOMEnabled(true);
        FileData fileData = fileDataService.findById(fileDataId);
        DataHandler handler = fileDataService.download(fileDataId);
        try {
            IOUtils.copyLarge(handler.getInputStream(),
                    new FileOutputStream(new File(destinationDirectory, fileData.getName())));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Long getFileDataId() {
        return fileDataId;
    }

    public void setFileDataId(Long fileDataId) {
        this.fileDataId = fileDataId;
    }

    public File getDestinationDirectory() {
        return destinationDirectory;
    }

    public void setDestinationDirectory(File destinationDirectory) {
        this.destinationDirectory = destinationDirectory;
    }

    @SuppressWarnings("static-access")
    public static void main(String[] args) {

        cliOptions.addOption(OptionBuilder.withLongOpt("fileDataId").withArgName("fileDataId").isRequired().hasArgs()
                .create());
        cliOptions.addOption(OptionBuilder.withLongOpt("destinationDirectory").withArgName("destinationDirectory")
                .isRequired().hasArgs().create());
        cliOptions.addOption(OptionBuilder.withLongOpt("help").withDescription("print this help message")
                .withLongOpt("help").create("?"));

        CommandLineParser commandLineParser = new GnuParser();
        DownloadFile main = new DownloadFile();
        try {
            CommandLine commandLine = commandLineParser.parse(cliOptions, args);
            if (commandLine.hasOption("?")) {
                helpFormatter.printHelp(main.getClass().getSimpleName(), cliOptions);
                return;
            }

            if (commandLine.hasOption("fileDataId")) {
                main.setFileDataId(Long.valueOf(commandLine.getOptionValue("fileDataId")));
            }

            if (commandLine.hasOption("destinationDirectory")) {
                File destDir = new File(commandLine.getOptionValue("destinationDirectory"));
                if (!destDir.exists()) {
                    System.err.println("Destination Directory does not exist");
                    helpFormatter.printHelp(main.getClass().getSimpleName(), cliOptions);
                    return;
                }
                main.setDestinationDirectory(destDir);
            }

            main.run();
        } catch (ParseException e) {
            System.err.println(("Parsing Failed: " + e.getMessage()));
            helpFormatter.printHelp(main.getClass().getSimpleName(), cliOptions);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
