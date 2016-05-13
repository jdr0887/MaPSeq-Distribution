package edu.unc.mapseq.module.sequencing.converter;

import java.io.File;

import javax.validation.constraints.NotNull;

import edu.unc.mapseq.module.Module;
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
@Application(name = "Converter", executable = "$JAVA7_HOME/bin/java -Xmx4g -jar $%s_SEQUENCING_TOOLS/converter.jar")
public class Converter extends Module {

    @NotNull(message = "bamFile is required", groups = InputValidations.class)
    @FileIsNotEmpty(message = "bamFile is empty", groups = InputValidations.class)
    @FileIsReadable(message = "bamFile is not readable", groups = InputValidations.class)
    @InputArgument(flag = "-b")
    private File bamFile;

    @NotNull(message = "vcfFile is required", groups = InputValidations.class)
    @FileIsNotEmpty(message = "vcfFile is empty", groups = InputValidations.class)
    @FileIsReadable(message = "vcfFile is not readable", groups = InputValidations.class)
    @InputArgument(flag = "-v")
    private File vcfFile;

    @NotNull(message = "outputDir is required", groups = InputValidations.class)
    @FileIsNotEmpty(message = "invalid output file", groups = OutputValidations.class)
    @InputArgument(flag = "-o")
    private File outputDir;

    @NotNull(message = "convertGenome is required", groups = InputValidations.class)
    @InputArgument(flag = "-g")
    private Boolean convertGenome = Boolean.FALSE;

    @NotNull(message = "convertExome is required", groups = InputValidations.class)
    @InputArgument(flag = "-x")
    private Boolean convertExome = Boolean.FALSE;

    @NotNull(message = "generateMetrics is required", groups = InputValidations.class)
    @InputArgument(flag = "-m")
    private Boolean generateMetrics = Boolean.FALSE;

    public Converter() {
        super();
    }

    @Override
    public Class<?> getModuleClass() {
        return Converter.class;
    }

    @Override
    public String getExecutable() {
        return String.format(getModuleClass().getAnnotation(Application.class).executable(), getWorkflowName().toUpperCase());
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

    @Override
    public String toString() {
        return String.format("Converter [bamFile=%s, vcfFile=%s, outputDir=%s, convertGenome=%s, convertExome=%s, generateMetrics=%s]",
                bamFile, vcfFile, outputDir, convertGenome, convertExome, generateMetrics);
    }

    public static void main(String[] args) {
        Converter module = new Converter();
        module.setWorkflowName("TEST");
        module.setBamFile(new File("/tmp", "input.sam"));
        module.setOutputDir(new File("/tmp"));
        module.setVcfFile(new File("/tmp", "some.vcf"));
        module.setConvertExome(Boolean.TRUE);
        try {
            module.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
