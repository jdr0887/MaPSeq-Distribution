package edu.unc.mapseq.module.core;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import edu.unc.mapseq.module.DefaultModuleOutput;
import edu.unc.mapseq.module.Module;
import edu.unc.mapseq.module.ModuleException;
import edu.unc.mapseq.module.ModuleOutput;
import edu.unc.mapseq.module.annotations.Application;
import edu.unc.mapseq.module.annotations.InputArgument;
import edu.unc.mapseq.module.annotations.InputValidations;

/**
 * 
 * @author jdr0887
 */
@Application(name = "Symlink")
public class BatchSymlink extends Module {

    @NotNull(message = "link is required", groups = InputValidations.class)
    @InputArgument(description = "comma delimited pair (ie, --targetLinkPair=link1,target1 --targetLinkPair=link2,target2)")
    private List<String> targetLinkPair;

    public BatchSymlink() {
        super();
    }

    @Override
    public Class<?> getModuleClass() {
        return BatchSymlink.class;
    }

    @Override
    public ModuleOutput call() throws ModuleException {
        DefaultModuleOutput moduleOutput = new DefaultModuleOutput();
        int exitCode = 0;
        try {

            if (targetLinkPair != null) {
                for (String targetLink : targetLinkPair) {

                    String[] split = targetLink.split(",");

                    File targetFile = new File(split[0]);

                    if (!targetFile.exists()) {
                        throw new ModuleException("target file does not exit: " + targetFile.getAbsolutePath());
                    }

                    File linkFile = new File(split[1]);

                    if (!linkFile.exists()) {
                        Files.createSymbolicLink(linkFile.toPath(), targetFile.toPath());
                    }

                }
            }

        } catch (Exception e) {
            moduleOutput.setError(new StringBuilder(e.getMessage()));
            moduleOutput.setExitCode(-1);
        }
        moduleOutput.setExitCode(exitCode);
        return moduleOutput;
    }

    public List<String> getTargetLinkPair() {
        return targetLinkPair;
    }

    public void setTargetLinkPair(List<String> targetLinkPair) {
        this.targetLinkPair = targetLinkPair;
    }

    @Override
    public String toString() {
        return String.format("BatchSymlink [targetLinkPair=%s, toString()=%s]", targetLinkPair, super.toString());
    }

    public static void main(String[] args) {
        BatchSymlink module = new BatchSymlink();
        List<String> linkTargetPair = new ArrayList<String>();
        linkTargetPair.add("/home/jdr0887/.bashrc,/home/jdr0887/tmp/.bashrc");
        linkTargetPair.add("/home/jdr0887/.bash_history,/home/jdr0887/tmp/.bash_history");
        linkTargetPair.add("/home/jdr0887/.emacs,/home/jdr0887/tmp/.emacs");
        linkTargetPair.add("/home/jdr0887,/home/jdr0887/tmp");
        module.setTargetLinkPair(linkTargetPair);
        System.out.println(module.toString());
        try {
            module.call();
        } catch (ModuleException e) {
            e.printStackTrace();
        }
    }

}
