package edu.unc.mapseq.module.sequencing.staden;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.renci.common.exec.BashExecutor;
import org.renci.common.exec.CommandInput;
import org.renci.common.exec.CommandOutput;
import org.renci.common.exec.Executor;
import org.renci.common.exec.ExecutorException;

import edu.unc.mapseq.module.Module;
import edu.unc.mapseq.module.ModuleException;
import edu.unc.mapseq.module.ModuleOutput;
import edu.unc.mapseq.module.ShellModuleOutput;
import edu.unc.mapseq.module.annotations.Application;
import edu.unc.mapseq.module.annotations.InputArgument;
import edu.unc.mapseq.module.annotations.InputValidations;
import edu.unc.mapseq.module.annotations.OutputValidations;
import edu.unc.mapseq.module.constraints.Contains;
import edu.unc.mapseq.module.constraints.EndsWith;
import edu.unc.mapseq.module.constraints.Fastq;
import edu.unc.mapseq.module.constraints.FileListIsReadable;

/**
 * 
 * @author jdr0887
 * 
 */
@Fastq(aligner = "aligner", platform = "platform", ends = "ends", files = "outputFiles", groups = OutputValidations.class)
@Application(name = "SRF2Fastq", executable = "$%s_STADEN_IO_LIB_HOME/bin/srf2fastq")
public class SRF2Fastq extends Module {

    @NotNull
    @Contains(message = "Invalid aligner", values = { "bfast", "bwa" }, groups = InputValidations.class)
    @InputArgument(flag = "-a")
    private String aligner;

    @NotNull
    @Contains(message = "Invalid sequencer", values = { "Solexa 1G Genome Analyzer", "Illumina Genome Analyzer",
            "Illumina Genome Analyzer II", "Illumina Genome Analyzer IIx", "Illumina HiSeq 2000", "Illumina HiScan SQ",
            "Illumina HiSeq 1000", "AB SOLiD System", "AB SOLiD System 2.0", "AB SOLiD System 3.0",
            "AB SOLiD System 3 Plus", "AB SOLiD System 4", "AB SOLiD System PI", "AB SOLiD 5500xl",
            "AB SOLiD 5500" }, groups = InputValidations.class)
    @InputArgument(flag = "-p")
    private String platform;

    @NotNull
    @EndsWith(message = "input must have '.srf' extension", value = ".srf", groups = InputValidations.class)
    @InputArgument(flag = "-i")
    private String input;

    @InputArgument(flag = "-o")
    private String outputPrefix;

    @NotNull
    @Min(message = "ends is less than 1", value = 1, groups = InputValidations.class)
    @InputArgument(flag = "-e")
    private Integer ends;

    @NotNull
    @Min(message = "numberOfOutputFiles is less than 1", value = 1, groups = InputValidations.class)
    @InputArgument(flag = "-n")
    private Integer numberOfOutputFiles;

    @InputArgument(flag = "-f")
    private Boolean filter;

    @FileListIsReadable(message = "An outputFile is not readable", groups = OutputValidations.class)
    @InputArgument()
    private List<File> outputFiles = new ArrayList<File>();

    public SRF2Fastq() {
        super();
    }

    @Override
    public Class<?> getModuleClass() {
        return SRF2Fastq.class;
    }

    // @Override
    // public ModuleOutput init() throws ModuleException {

    // Set prefix if not defined
    // if (outputPrefix == null) {
    // // Strip off directories before
    // System.err.println("INPUT: " + input);
    // int start = input.lastIndexOf(System.getProperty("file.separator"));
    // if (start < 0)
    // start = 0;
    //
    // // Strip off .srf ending
    // int end = input.lastIndexOf(".srf");
    // if (end > 0)
    // outputPrefix = input.substring(start + 1, end);
    // }
    //
    // // for bwa output only make 1 output file (single end) or 2 output files (paired end)
    // if ("bwa".equals(aligner)) {
    // File file = new File(outputPrefix);
    // if (file.getParentFile() != null)
    // file.getParentFile().mkdirs();
    // if (ends > 1) {
    // outputFiles.add(file);
    // } else {
    // String outputFileName = new String(outputPrefix + ".fastq");
    // file = new File(outputFileName);
    // outputFiles.add(file);
    // }
    // } else if ("bfast".equals(aligner)) {
    // // Setup output files for BFAST, supports multiple output files for large inputs
    // for (int i = 0; i < numOut; i++) {
    //
    // String outputFileName = new String(outputPrefix + "." + i + ".fastq");
    // File file = new File(outputFileName);
    //
    // // Make directory if needed
    // if (file.getParentFile() != null)
    // file.getParentFile().mkdirs();
    //
    // // Make sure we can write to this file
    // try {
    // file.createNewFile();
    // } catch (IOException e) {
    // // FIXME: If file exists, continue anyway. Should we do this?
    // }
    //
    // if (!file.canWrite()) {
    // ret.setStderr("File " + outputFileName + " could not be created in a writable manner\n");
    // ret.setExitStatus(ReturnValue.FILENOTWRITABLE);
    // return ret;
    // }
    //
    // // Otherwise we are in the clear, so add to file array
    // outputFiles.add(file);
    // }
    // }
    // return null;
    // }

    @Override
    public String getExecutable() {
        return String.format(getModuleClass().getAnnotation(Application.class).executable(),
                getWorkflowName().toUpperCase());
    }

    @Override
    public ModuleOutput call() throws ModuleException {
        // Append initial command and options
        StringBuffer command = new StringBuffer();
        command.append(getExecutable());
        command.append(" -c ");

        // filter bad reads if requested
        if (filter) {
            command.append(" -C ");
        }

        // If multiple ends, they should be sequential if bfast
        if (ends > 1 && "bfast".equals(aligner)) {
            command.append(" -a -S ");
        } else if (ends > 1 && "bwa".equals(aligner)) {
            // for bwa should be two different files, give the base as the first outputFile
            // NOTE: this means BWA output is one or two files, there's not shreading implemented for large inputs
            command.append(" -n -a -s " + outputFiles.get(0) + " ");
        }

        // For paired end Illumina runs, add options to reverse compliments ends
        // great than 1
        /*
         * Brian 20101215: I don't think this is actually needed if (ends > 1 && sequencer.compareTo("illumina-ga2") ==
         * 0) { cmd.append(" -r 1");
         * 
         * for (int i = 2; i < ends; i++) cmd.append("," + i);
         * 
         * cmd.append(" "); }
         */

        // For Solid, have to add option to append Primer to Sequence
        if (platform.indexOf("AB SOLiD ") > -1) {
            command.append(" -e ");
        }

        // Add file name to the end
        command.append(" " + input);

        CommandInput commandInput = new CommandInput();
        commandInput.setCommand(command.toString());

        CommandOutput commandOutput;
        try {
            Executor executor = BashExecutor.getInstance();
            commandOutput = executor.execute(commandInput);
        } catch (ExecutorException e1) {
            throw new ModuleException(e1.getMessage());
        }

        // we're only parsing stdout and managing file creation for BFAST output (all one file)
        if ("bfast".equals(aligner) || ("bwa".equals(aligner) && ends == 1)) {
            // Read from stdout and write to output files. Each output file gets 4 *
            // ends before moving to next entry. This assures all ends for a given
            // read are together
            try {
                // FIXME: This first function call is just for now, srf2fastq strips
                // primer automatically

                long linesProcessed = 0; // Has to be a long, or else it will wrap on large files with more than 2.1
                // billion
                // lines
                int numFiles = outputFiles.size();
                BufferedWriter currentFile = null;
                BufferedReader stdout = new BufferedReader(new StringReader(commandOutput.getStdout().toString()));
                ArrayList<BufferedWriter> fileOutputs = new ArrayList<BufferedWriter>();

                // Open files
                for (int i = 0; i < numFiles; i++) {
                    fileOutputs.add(new BufferedWriter(new FileWriter(outputFiles.get(i))));
                }

                // Start with file zero, or return error if no files available
                if (numFiles <= 0)
                    throw new IOException("Must specify atleast one output file");

                // Read lines
                char primer = '\0';
                String line = null;
                while ((line = stdout.readLine()) != null) {

                    if (platform.indexOf("AB SOLiD ") > -1) {
                        // If this is the first read, let's use this primer forever. FIXME: This is a hack due to
                        // problems with
                        // srf2fastq
                        if (linesProcessed == 1) {
                            primer = line.charAt(0);
                        } else if (primer != '\0' && linesProcessed % 4 == 1) {
                            line = primer + line.substring(1);
                        }

                        // if we wrote a multiple of linesPerStripe lines, time to move
                        // onto next file
                        if (linesProcessed % (ends * 4) == 0) {
                            currentFile = fileOutputs.get((int) (linesProcessed / (ends * 4)) % numFiles);
                        }

                        // If it is the 3rd line (quality), strip off the primer
                        if (linesProcessed % 4 == 3) {
                            line = line.substring(1);
                        }

                        // Write to current file
                        currentFile.write(line + System.getProperty("line.separator"));
                        linesProcessed++;

                    } else {
                        // if we wrote a multiple of linesPerStripe lines, time to move
                        // onto next file
                        if (linesProcessed % (ends * 4) == 0) {
                            currentFile = fileOutputs.get((int) (linesProcessed / (ends * 4)) % numFiles);
                        }

                        // Write to current file
                        currentFile.write(line + System.getProperty("line.separator"));
                        linesProcessed++;
                    }

                }

            } catch (IOException e) {
                throw new ModuleException(e.getMessage());
            }
        }

        return new ShellModuleOutput(commandOutput);
    }

    public String getAligner() {
        return aligner;
    }

    public void setAligner(String aligner) {
        this.aligner = aligner;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getOutputPrefix() {
        return outputPrefix;
    }

    public void setOutputPrefix(String outputPrefix) {
        this.outputPrefix = outputPrefix;
    }

    public Integer getEnds() {
        return ends;
    }

    public void setEnds(Integer ends) {
        this.ends = ends;
    }

    public Integer getNumberOfOutputFiles() {
        return numberOfOutputFiles;
    }

    public void setNumberOfOutputFiles(Integer numberOfOutputFiles) {
        this.numberOfOutputFiles = numberOfOutputFiles;
    }

    public Boolean getFilter() {
        return filter;
    }

    public void setFilter(Boolean filter) {
        this.filter = filter;
    }

    public List<File> getOutputFiles() {
        return outputFiles;
    }

    public void setOutputFiles(List<File> outputFiles) {
        this.outputFiles = outputFiles;
    }

    @Override
    public String toString() {
        return String.format(
                "SRF2Fastq [aligner=%s, platform=%s, input=%s, outputPrefix=%s, ends=%s, numberOfOutputFiles=%s, filter=%s, outputFiles=%s, toString()=%s]",
                aligner, platform, input, outputPrefix, ends, numberOfOutputFiles, filter, outputFiles,
                super.toString());
    }

}
