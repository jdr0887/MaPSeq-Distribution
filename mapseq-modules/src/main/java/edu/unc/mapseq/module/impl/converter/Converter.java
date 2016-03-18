package edu.unc.mapseq.module.impl.converter;

import java.io.File;
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
import edu.unc.mapseq.module.annotations.OutputValidations;
import edu.unc.mapseq.module.constraints.FileIsNotEmpty;
import edu.unc.mapseq.module.constraints.FileIsReadable;

/**
 * 
 * @author jdr0887
 */
@Application(name = "Converter")
public class Converter extends Module {

    @NotNull(message = "bamFile is required", groups = InputValidations.class)
    @FileIsNotEmpty(message = "bamFile is empty", groups = InputValidations.class)
    @FileIsReadable(message = "bamFile is not readable", groups = InputValidations.class)
    @InputArgument
    private File bamFile;

    @NotNull(message = "vcfFile is required", groups = InputValidations.class)
    @FileIsNotEmpty(message = "vcfFile is empty", groups = InputValidations.class)
    @FileIsReadable(message = "vcfFile is not readable", groups = InputValidations.class)
    @InputArgument
    private File vcfFile;

    @NotNull(message = "outputDir is required", groups = InputValidations.class)
    @FileIsNotEmpty(message = "invalid output file", groups = OutputValidations.class)
    @InputArgument
    private File outputDir;

    @NotNull(message = "convertGenome is required", groups = InputValidations.class)
    @InputArgument
    private Boolean convertGenome = Boolean.FALSE;

    @NotNull(message = "convertExome is required", groups = InputValidations.class)
    @InputArgument
    private Boolean convertExome = Boolean.FALSE;

    @NotNull(message = "generateMetrics is required", groups = InputValidations.class)
    @InputArgument
    private Boolean generateMetrics = Boolean.FALSE;

    public Converter() {
        super();
    }

    @Override
    public Class<?> getModuleClass() {
        return Converter.class;
    }

    @SuppressWarnings("static-access")
    @Override
    public ModuleOutput call() throws ModuleException {
        DefaultModuleOutput moduleOutput = new DefaultModuleOutput();
        int exitCode = 0;
        try {

            List<String> args = new ArrayList<String>();

            args.add("-v " + vcfFile.getAbsolutePath());
            args.add("-b " + bamFile.getAbsolutePath());
            args.add("-o " + outputDir.getAbsolutePath());

            if (convertGenome) {
                args.add("-g");
            }

            if (convertExome) {
                args.add("-x");
            }

            if (generateMetrics) {
                args.add("-m");
            }

            org.renci.sequencing.converter.Converter converter = new org.renci.sequencing.converter.Converter();
            converter.main(args.toArray(new String[args.size()]));

        } catch (Exception e) {
            e.printStackTrace();
            moduleOutput.setExitCode(-1);
            moduleOutput.setError(new StringBuilder(e.getMessage()));
            return moduleOutput;
        }
        moduleOutput.setExitCode(exitCode);

        return moduleOutput;
    }

    public File getBamFile() {
        return bamFile;
    }

    public void setBamFile(File bamFile) {
        this.bamFile = bamFile;
    }

    public File getVcfFile() {
        return vcfFile;
    }

    public void setVcfFile(File vcfFile) {
        this.vcfFile = vcfFile;
    }

    public File getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(File outputDir) {
        this.outputDir = outputDir;
    }

    public Boolean getConvertGenome() {
        return convertGenome;
    }

    public void setConvertGenome(Boolean convertGenome) {
        this.convertGenome = convertGenome;
    }

    public Boolean getConvertExome() {
        return convertExome;
    }

    public void setConvertExome(Boolean convertExome) {
        this.convertExome = convertExome;
    }

    public Boolean getGenerateMetrics() {
        return generateMetrics;
    }

    public void setGenerateMetrics(Boolean generateMetrics) {
        this.generateMetrics = generateMetrics;
    }

}
