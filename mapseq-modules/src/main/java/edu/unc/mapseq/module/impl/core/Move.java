package edu.unc.mapseq.module.impl.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.commons.io.FileUtils;

import edu.unc.mapseq.dao.model.FileData;
import edu.unc.mapseq.dao.model.MimeType;
import edu.unc.mapseq.module.DefaultModuleOutput;
import edu.unc.mapseq.module.Module;
import edu.unc.mapseq.module.ModuleException;
import edu.unc.mapseq.module.ModuleOutput;
import edu.unc.mapseq.module.annotations.Application;
import edu.unc.mapseq.module.annotations.InputArgument;
import edu.unc.mapseq.module.annotations.InputValidations;
import edu.unc.mapseq.module.constraints.FileListIsReadable;

/**
 * 
 * @author jdr0887
 */
@Application(name = "Move", isWorkflowRunIdOptional = true)
public class Move extends Module {

    @NotNull(message = "source is required", groups = InputValidations.class)
    @FileListIsReadable(message = "source does not exist", groups = InputValidations.class)
    @InputArgument
    private List<File> source;

    @NotNull(message = "destination is required", groups = InputValidations.class)
    @InputArgument
    private File destination;

    @InputArgument
    private MimeType mimeType;

    public Move() {
        super();
    }

    @Override
    public Class<?> getModuleClass() {
        return Move.class;
    }

    @Override
    public ModuleOutput call() throws ModuleException {
        DefaultModuleOutput moduleOutput = new DefaultModuleOutput();
        int exitCode = 0;
        try {

            if (destination.exists() && !destination.isDirectory()) {
                FileUtils.forceDelete(destination);
            }

            if (source.size() > 1) {

                if (!destination.exists()) {
                    FileUtils.forceMkdir(destination);
                }

                for (File srcFile : source) {
                    FileUtils.moveFileToDirectory(srcFile, destination, true);
                }

            } else {
                FileUtils.moveFile(source.get(0), destination);
            }

            if (mimeType != null) {
                FileData fileData = new FileData();
                fileData.setName(destination.getName());
                fileData.setMimeType(mimeType);
                getFileDatas().add(fileData);
            }

        } catch (Exception e) {
            e.printStackTrace();
            moduleOutput.setError(new StringBuilder(e.getMessage()));
            moduleOutput.setExitCode(exitCode);
        }
        moduleOutput.setExitCode(exitCode);
        return moduleOutput;
    }

    public List<File> getSource() {
        return source;
    }

    public void setSource(List<File> source) {
        this.source = source;
    }

    public File getDestination() {
        return destination;
    }

    public void setDestination(File destination) {
        this.destination = destination;
    }

    public MimeType getMimeType() {
        return mimeType;
    }

    public void setMimeType(MimeType mimeType) {
        this.mimeType = mimeType;
    }

    @Override
    public String toString() {
        return String.format("Move [source=%s, destination=%s, mimeType=%s, toString()=%s]", source, destination,
                mimeType, super.toString());
    }

    public static void main(String[] args) {
        Move move = new Move();
        List<File> fileList = new ArrayList<File>();
        fileList.add(new File("/tmp/asdf", "asdf.txt"));
        // fileList.add(new File("/tmp/asdf", "qwer.txt"));
        move.setSource(fileList);
        move.setDestination(new File("/tmp/qwer/asdf.txt"));
        // move.setDestination(new File("/tmp/qwer"));
        try {
            move.call();
        } catch (ModuleException e) {
            e.printStackTrace();
        }
    }

}
