package edu.unc.mapseq.module.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import javax.validation.constraints.NotNull;

import org.biojava3.sequencing.io.fastq.Fastq;
import org.biojava3.sequencing.io.fastq.SangerFastqReader;
import org.biojava3.sequencing.io.fastq.StreamListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;
import com.google.common.io.InputSupplier;

import edu.unc.mapseq.dao.model.FileData;
import edu.unc.mapseq.dao.model.MimeType;
import edu.unc.mapseq.module.DefaultModuleOutput;
import edu.unc.mapseq.module.Module;
import edu.unc.mapseq.module.ModuleOutput;
import edu.unc.mapseq.module.annotations.Application;
import edu.unc.mapseq.module.annotations.InputArgument;
import edu.unc.mapseq.module.annotations.InputValidations;
import edu.unc.mapseq.module.annotations.OutputValidations;
import edu.unc.mapseq.module.constraints.FileIsReadable;
import edu.unc.mapseq.module.constraints.FileListIsReadable;

@Application(name = "DetermineMaxReadLength")
public class DetermineReadLength extends Module {

    private final Logger logger = LoggerFactory.getLogger(DetermineReadLength.class);

    @NotNull(message = "input is required", groups = InputValidations.class)
    @FileListIsReadable(message = "input does not exist or is not readable", groups = InputValidations.class)
    @InputArgument
    private List<File> input;

    @NotNull(message = "output is required", groups = InputValidations.class)
    @FileIsReadable(message = "output does not exist or is not readable", groups = OutputValidations.class)
    @InputArgument(order = 9)
    private File output;

    @Override
    public Class<?> getModuleClass() {
        return DetermineReadLength.class;
    }

    @Override
    public ModuleOutput call() throws Exception {
        logger.debug("ENTERING call()");
        DefaultModuleOutput moduleOutput = new DefaultModuleOutput();
        moduleOutput.setExitCode(0);

        try {
            SangerFastqReader fastqReader = new SangerFastqReader();
            final AtomicInteger maxLength = new AtomicInteger(0);
            final AtomicInteger minLength = new AtomicInteger(0);

            for (File f : input) {
                InputSupplier<InputStreamReader> inputSupplier = Files
                        .newReaderSupplier(f, Charset.forName("US-ASCII"));

                fastqReader.stream(inputSupplier, new StreamListener() {
                    @Override
                    public void fastq(final Fastq fastq) {
                        if (fastq.getSequence().length() > maxLength.get()) {
                            maxLength.getAndSet(fastq.getSequence().length());
                        }
                        if (fastq.getSequence().length() < minLength.get()) {
                            minLength.getAndSet(fastq.getSequence().length());
                        }
                    }
                });
            }
            Properties props = new Properties();
            props.setProperty("maxLength", String.format("%d", maxLength.get()));
            props.setProperty("minLength", String.format("%d", minLength.get()));
            FileOutputStream fos = new FileOutputStream(output);
            props.storeToXML(fos, "Read Lengths");
            fos.flush();
            fos.close();
        } catch (Exception e) {
            logger.error("Problem running DetermineMaxReadLength", e);
            moduleOutput.setExitCode(-1);
            moduleOutput.setError(new StringBuilder(e.getMessage()));
            return moduleOutput;
        }

        FileData fm = new FileData();
        fm.setMimeType(MimeType.TEXT_PLAIN);
        fm.setName(output.getName());
        getFileDatas().add(fm);

        return moduleOutput;
    }

    public List<File> getInput() {
        return input;
    }

    public void setInput(List<File> input) {
        this.input = input;
    }

    public File getOutput() {
        return output;
    }

    public void setOutput(File output) {
        this.output = output;
    }

    public static void main(String[] args) {
        DetermineReadLength module = new DetermineReadLength();
        module.setWorkflowName("TEST");
        List<File> input = new ArrayList<File>();
        input.add(new File("/home/jdr0887/data/mapsplice",
                "130913_UNC12-SN629_0330_AD2E14ACXX_AGTTCC_L006_R1.filtered.fastq"));
        module.setInput(input);
        module.setOutput(new File("/home/jdr0887/data/mapsplice", "readLength.txt"));
        try {
            module.call();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
