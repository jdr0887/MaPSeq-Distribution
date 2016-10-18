package edu.unc.mapseq.module.core;

import java.io.File;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.model.FileData;
import edu.unc.mapseq.dao.model.MimeType;
import edu.unc.mapseq.module.DefaultModuleOutput;
import edu.unc.mapseq.module.Module;
import edu.unc.mapseq.module.ModuleOutput;
import edu.unc.mapseq.module.annotations.Application;
import edu.unc.mapseq.module.annotations.InputArgument;
import edu.unc.mapseq.module.annotations.InputValidations;
import edu.unc.mapseq.module.annotations.OutputArgument;
import edu.unc.mapseq.module.annotations.OutputValidations;
import edu.unc.mapseq.module.constraints.FileIsReadable;
import edu.unc.mapseq.module.constraints.FileListIsReadable;

/**
 * 
 * @author jdr0887
 * 
 */
@Application(name = "Zip", isWorkflowRunIdOptional = true)
public class Zip extends Module {

    private static final Logger logger = LoggerFactory.getLogger(Zip.class);

    @NotNull(message = "Zip is required", groups = InputValidations.class)
    @FileIsReadable(message = "Zip file does is not readable", groups = OutputValidations.class)
    @OutputArgument(persistFileData = true, mimeType = MimeType.APPLICATION_ZIP)
    private File output;

    @NotNull(message = "Entry is required", groups = InputValidations.class)
    @FileListIsReadable(message = "One or more entries is not readable", groups = InputValidations.class)
    @InputArgument(description = "Files to zip")
    private List<File> entry;

    public Zip() {
        super();
    }

    @Override
    public String getExecutable() {
        return getModuleClass().getAnnotation(Application.class).executable();
    }

    @Override
    public Class<?> getModuleClass() {
        return Zip.class;
    }

    @Override
    public ModuleOutput call() throws Exception {
        logger.debug("ENTERING call()");
        DefaultModuleOutput moduleOutput = new DefaultModuleOutput();
        moduleOutput.setExitCode(0);
        try {
            Map<String, String> env = new HashMap<>();
            env.put("create", "true");
            URI uri = URI.create(String.format("jar:file:%s", output.getAbsolutePath()));
            try (FileSystem zipfs = FileSystems.newFileSystem(uri, env)) {
                for (File f : entry) {
                    Path externalPath = f.toPath();
                    Path internalPath = zipfs.getPath(String.format("/%s", f.getName()));
                    String message = String.format("adding %s to %s in %s", externalPath.toString(), internalPath.toString(),
                            output.getAbsolutePath());
                    logger.info(message);
                    moduleOutput.getOutput().append(message);
                    Files.copy(externalPath, internalPath, StandardCopyOption.REPLACE_EXISTING);
                }
            }

            FileData fileData = new FileData(output.getName(), output.getParentFile().getAbsolutePath(), MimeType.APPLICATION_ZIP);
            getFileDatas().add(fileData);

        } catch (Exception e) {
            moduleOutput.setExitCode(-1);
            moduleOutput.getError().append(e.getMessage());
        }
        return moduleOutput;
    }

    public File getOutput() {
        return output;
    }

    public void setOutput(File output) {
        this.output = output;
    }

    public List<File> getEntry() {
        return entry;
    }

    public void setEntry(List<File> entry) {
        this.entry = entry;
    }

    @Override
    public String toString() {
        return String.format("Zip [output=%s, entry=%s, toString()=%s]", output, entry, super.toString());
    }

    public static void main(String[] args) {
        try {
            Zip module = new Zip();
            module.setWorkflowName("TEST");
            module.setEntry(Arrays.asList(new File("/tmp", "asdf.txt"), new File("/tmp", "zxcv.txt")));
            module.setOutput(new File("/tmp", "asdf.zip"));
            module.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
