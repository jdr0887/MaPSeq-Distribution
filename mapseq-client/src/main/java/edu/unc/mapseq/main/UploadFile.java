package edu.unc.mapseq.main;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

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

import edu.unc.mapseq.config.MaPSeqConfigurationServiceImpl;
import edu.unc.mapseq.dao.model.MimeType;
import edu.unc.mapseq.ws.FileDataService;

public class UploadFile implements Runnable {

    private final static HelpFormatter helpFormatter = new HelpFormatter();

    private final static Options cliOptions = new Options();

    private List<File> fileList = new ArrayList<File>();

    private MimeType mimeType;

    private String flowcell;

    private String workflow;

    public UploadFile() {
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
        try {
            for (File f : fileList) {
                DataHandler handler = new DataHandler(f.toURI().toURL());
                fileDataService.upload(handler, this.flowcell, this.workflow, f.getName(), mimeType.toString());
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public List<File> getFileList() {
        return fileList;
    }

    public void setFileList(List<File> fileList) {
        this.fileList = fileList;
    }

    public MimeType getMimeType() {
        return mimeType;
    }

    public void setMimeType(MimeType mimeType) {
        this.mimeType = mimeType;
    }

    public String getFlowcell() {
        return flowcell;
    }

    public void setFlowcell(String flowcell) {
        this.flowcell = flowcell;
    }

    public String getWorkflow() {
        return workflow;
    }

    public void setWorkflow(String workflow) {
        this.workflow = workflow;
    }

    @SuppressWarnings("static-access")
    public static void main(String[] args) {

        cliOptions.addOption(OptionBuilder.withLongOpt("file").withArgName("file").isRequired().hasArgs().create());
        cliOptions.addOption(OptionBuilder.withLongOpt("mimeType").withArgName("mimeType").isRequired().hasArg()
                .create());
        cliOptions.addOption(OptionBuilder.withLongOpt("flowcell").withArgName("flowcell").isRequired().hasArg()
                .create());
        cliOptions.addOption(OptionBuilder.withLongOpt("workflow").withArgName("workflow").isRequired().hasArg()
                .create());
        cliOptions.addOption(OptionBuilder.withLongOpt("help").withDescription("print this help message")
                .withLongOpt("help").create("?"));

        CommandLineParser commandLineParser = new GnuParser();
        UploadFile main = new UploadFile();
        try {
            CommandLine commandLine = commandLineParser.parse(cliOptions, args);
            if (commandLine.hasOption("?")) {
                helpFormatter.printHelp(main.getClass().getSimpleName(), cliOptions);
                return;
            }

            if (commandLine.hasOption("file")) {
                List<File> fList = new ArrayList<File>();
                String[] filePathArray = commandLine.getOptionValues("file");
                for (String filePath : filePathArray) {
                    File f = new File(filePath);
                    if (!f.exists()) {

                    }
                    fList.add(f);
                }
                main.setFileList(fList);
            }

            if (commandLine.hasOption("mimeType")) {
                main.setMimeType(MimeType.valueOf(commandLine.getOptionValue("mimeType")));
            }

            if (commandLine.hasOption("flowcell")) {
                main.setFlowcell(commandLine.getOptionValue("flowcell"));
            }

            if (commandLine.hasOption("workflow")) {
                main.setWorkflow(commandLine.getOptionValue("workflow"));
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
