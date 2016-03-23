package edu.unc.mapseq.module.sequencing.macs;

import java.io.File;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.StringUtils;
import org.renci.common.exec.BashExecutor;
import org.renci.common.exec.CommandInput;
import org.renci.common.exec.CommandOutput;
import org.renci.common.exec.Executor;
import org.renci.common.exec.ExecutorException;

import edu.unc.mapseq.dao.model.FileData;
import edu.unc.mapseq.dao.model.MimeType;
import edu.unc.mapseq.module.Module;
import edu.unc.mapseq.module.ModuleException;
import edu.unc.mapseq.module.ModuleOutput;
import edu.unc.mapseq.module.ShellModuleOutput;
import edu.unc.mapseq.module.annotations.Application;
import edu.unc.mapseq.module.annotations.InputArgument;
import edu.unc.mapseq.module.annotations.InputValidations;
import edu.unc.mapseq.module.constraints.FileIsReadable;

@Application(name = "MACS2", executable = "$%s_MACS2_HOME/bin/macs2")
public class MACS2 extends Module {

    @NotNull(message = "treatment is required", groups = InputValidations.class)
    @FileIsReadable(message = "treatment does not exist or is not readable", groups = InputValidations.class)
    @InputArgument
    private File treatment;

    @InputArgument
    private File control;

    @InputArgument
    private String name;

    @InputArgument
    private String format;

    @InputArgument
    private Long genomeSize;

    @InputArgument
    private Long tagSize;

    @InputArgument
    private Integer bandWidth;

    @InputArgument
    private Double qValue;

    @InputArgument
    private Double pValue;

    @InputArgument
    private Integer mFold;

    @InputArgument
    private Boolean noLambda;

    @InputArgument
    private Integer smallLocal;

    @InputArgument
    private Integer largeLocal;

    @InputArgument
    private Boolean autoBiModal;

    @InputArgument
    private Boolean noModal;

    @InputArgument
    private Integer shiftSize;

    @InputArgument
    private String keepDuplicates;

    @InputArgument
    private Boolean toLarge;

    @InputArgument
    private Boolean downSample;

    @InputArgument
    private Boolean shiftControl;

    @InputArgument
    private Boolean halfExtends;

    @InputArgument
    private Boolean bedGraph;

    @InputArgument
    private Boolean broad;

    @InputArgument
    private Double broadCutoff;

    @InputArgument
    private File workDir;

    @Override
    public Class<?> getModuleClass() {
        return MACS2.class;
    }

    @Override
    public String getExecutable() {
        return String.format(getModuleClass().getAnnotation(Application.class).executable(),
                getWorkflowName().toUpperCase());
    }

    @Override
    public ModuleOutput call() throws Exception {

        CommandInput commandInput = new CommandInput();

        StringBuilder command = new StringBuilder();
        command.append(getExecutable());

        command.append(" --treatment=").append(this.treatment.getAbsolutePath());

        if (this.control != null) {
            command.append(" --control=").append(this.control.getAbsolutePath());
        }

        if (StringUtils.isNotEmpty(this.name)) {
            command.append(" --name=").append(this.name);
        }

        if (StringUtils.isNotEmpty(this.format)) {
            command.append(" --format=").append(this.format);
        }

        if (this.genomeSize != null) {
            command.append(" --gsize=").append(this.genomeSize.toString());
        }

        if (this.tagSize != null) {
            command.append(" --tsize=").append(this.tagSize.toString());
        }

        if (this.bandWidth != null) {
            command.append(" --bw=").append(this.bandWidth.toString());
        }

        if (this.qValue != null) {
            command.append(" --qvalue=").append(this.qValue.toString());
        }

        if (this.pValue != null) {
            command.append(" --pvalue=").append(this.pValue.toString());
        }

        if (this.mFold != null) {
            command.append(" --mfold=").append(this.mFold.toString());
        }

        if (this.noLambda != null && this.noLambda) {
            command.append(" --nolambda");
        }

        if (this.smallLocal != null) {
            command.append(" --slocal=").append(this.smallLocal);
        }

        if (this.largeLocal != null) {
            command.append(" --llocal=").append(this.largeLocal);
        }

        if (this.autoBiModal != null && this.autoBiModal) {
            command.append(" --auto-bimodal");
        }

        if (this.noModal != null && this.noModal) {
            command.append(" --nomodal");
        }

        if (this.shiftSize != null) {
            command.append(" --shiftsize=").append(this.shiftSize);
        }

        if (StringUtils.isNotEmpty(this.keepDuplicates)) {
            command.append(" --keep-dup=").append(this.keepDuplicates);
        }

        if (this.toLarge != null && this.toLarge) {
            command.append(" --to-large");
        }

        if (this.downSample != null && this.downSample) {
            command.append(" --down-sample");
        }

        if (this.shiftControl != null && this.shiftControl) {
            command.append(" --shift-control");
        }

        if (this.halfExtends != null && this.halfExtends) {
            command.append(" --half-ext");
        }

        if (this.bedGraph != null && this.bedGraph) {
            command.append(" --bdg");
        }

        if (this.broad != null && this.broad) {
            command.append(" --broad");
        }

        if (this.broadCutoff != null) {
            command.append(" --broad-cutoff=").append(this.broadCutoff.toString());
        }

        commandInput.setCommand(command.toString());
        commandInput.setWorkDir(workDir);
        CommandOutput commandOutput;
        Executor executor = BashExecutor.getInstance();
        try {
            commandOutput = executor.execute(commandInput);
        } catch (ExecutorException e) {
            throw new ModuleException(e);
        }

        if (commandOutput != null && commandOutput.getExitCode() == 0) {

            File peaksXLSFile = new File(this.treatment.getParentFile(), this.name + "_peaks.xls");
            if (peaksXLSFile.exists()) {
                FileData fileData = new FileData();
                fileData.setMimeType(MimeType.APPLICATION_XLS);
                fileData.setName(peaksXLSFile.getName());
                getFileDatas().add(fileData);
            }

            File peaksBedFile = new File(this.treatment.getParentFile(), this.name + "_peaks.bed");
            if (peaksBedFile.exists()) {
                FileData fileData = new FileData();
                fileData.setMimeType(MimeType.TEXT_BED);
                fileData.setName(peaksBedFile.getName());
                getFileDatas().add(fileData);
            }

            File encodePeaksFile = new File(this.treatment.getParentFile(), this.name + "_peaks.encodePeak");
            if (peaksBedFile.exists()) {
                FileData fileData = new FileData();
                fileData.setMimeType(MimeType.TEXT_BED);
                fileData.setName(encodePeaksFile.getName());
                getFileDatas().add(fileData);
            }

        }

        return new ShellModuleOutput(commandOutput);

    }

    public File getTreatment() {
        return treatment;
    }

    public void setTreatment(File treatment) {
        this.treatment = treatment;
    }

    public File getControl() {
        return control;
    }

    public void setControl(File control) {
        this.control = control;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Long getGenomeSize() {
        return genomeSize;
    }

    public void setGenomeSize(Long genomeSize) {
        this.genomeSize = genomeSize;
    }

    public Long getTagSize() {
        return tagSize;
    }

    public void setTagSize(Long tagSize) {
        this.tagSize = tagSize;
    }

    public Integer getBandWidth() {
        return bandWidth;
    }

    public void setBandWidth(Integer bandWidth) {
        this.bandWidth = bandWidth;
    }

    public Double getQValue() {
        return qValue;
    }

    public void setQValue(Double qValue) {
        this.qValue = qValue;
    }

    public Double getPValue() {
        return pValue;
    }

    public void setPValue(Double pValue) {
        this.pValue = pValue;
    }

    public Integer getMFold() {
        return mFold;
    }

    public void setMFold(Integer mFold) {
        this.mFold = mFold;
    }

    public Boolean getNoLambda() {
        return noLambda;
    }

    public void setNoLambda(Boolean noLambda) {
        this.noLambda = noLambda;
    }

    public Integer getSmallLocal() {
        return smallLocal;
    }

    public void setSmallLocal(Integer smallLocal) {
        this.smallLocal = smallLocal;
    }

    public Integer getLargeLocal() {
        return largeLocal;
    }

    public void setLargeLocal(Integer largeLocal) {
        this.largeLocal = largeLocal;
    }

    public Boolean getAutoBiModal() {
        return autoBiModal;
    }

    public void setAutoBiModal(Boolean autoBiModal) {
        this.autoBiModal = autoBiModal;
    }

    public Boolean getNoModal() {
        return noModal;
    }

    public void setNoModal(Boolean noModal) {
        this.noModal = noModal;
    }

    public Integer getShiftSize() {
        return shiftSize;
    }

    public void setShiftSize(Integer shiftSize) {
        this.shiftSize = shiftSize;
    }

    public String getKeepDuplicates() {
        return keepDuplicates;
    }

    public void setKeepDuplicates(String keepDuplicates) {
        this.keepDuplicates = keepDuplicates;
    }

    public Boolean getToLarge() {
        return toLarge;
    }

    public void setToLarge(Boolean toLarge) {
        this.toLarge = toLarge;
    }

    public Boolean getDownSample() {
        return downSample;
    }

    public void setDownSample(Boolean downSample) {
        this.downSample = downSample;
    }

    public Boolean getShiftControl() {
        return shiftControl;
    }

    public void setShiftControl(Boolean shiftControl) {
        this.shiftControl = shiftControl;
    }

    public Boolean getHalfExtends() {
        return halfExtends;
    }

    public void setHalfExtends(Boolean halfExtends) {
        this.halfExtends = halfExtends;
    }

    public Boolean getBedGraph() {
        return bedGraph;
    }

    public void setBedGraph(Boolean bedGraph) {
        this.bedGraph = bedGraph;
    }

    public Boolean getBroad() {
        return broad;
    }

    public void setBroad(Boolean broad) {
        this.broad = broad;
    }

    public Double getBroadCutoff() {
        return broadCutoff;
    }

    public void setBroadCutoff(Double broadCutoff) {
        this.broadCutoff = broadCutoff;
    }

    public File getWorkDir() {
        return workDir;
    }

    public void setWorkDir(File workDir) {
        this.workDir = workDir;
    }

    @Override
    public String toString() {
        return String.format(
                "MACS2 [treatment=%s, control=%s, name=%s, format=%s, genomeSize=%s, tagSize=%s, bandWidth=%s, qValue=%s, pValue=%s, mFold=%s, noLambda=%s, smallLocal=%s, largeLocal=%s, autoBiModal=%s, noModal=%s, shiftSize=%s, keepDuplicates=%s, toLarge=%s, downSample=%s, shiftControl=%s, halfExtends=%s, bedGraph=%s, broad=%s, broadCutoff=%s, workDir=%s, toString()=%s]",
                treatment, control, name, format, genomeSize, tagSize, bandWidth, qValue, pValue, mFold, noLambda,
                smallLocal, largeLocal, autoBiModal, noModal, shiftSize, keepDuplicates, toLarge, downSample,
                shiftControl, halfExtends, bedGraph, broad, broadCutoff, workDir, super.toString());
    }

}
