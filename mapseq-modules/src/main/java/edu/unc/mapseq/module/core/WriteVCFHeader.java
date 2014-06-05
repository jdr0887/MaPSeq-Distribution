package edu.unc.mapseq.module.core;

import java.io.File;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import edu.unc.mapseq.module.DefaultModuleOutput;
import edu.unc.mapseq.module.Module;
import edu.unc.mapseq.module.ModuleOutput;
import edu.unc.mapseq.module.annotations.Application;
import edu.unc.mapseq.module.annotations.InputArgument;
import edu.unc.mapseq.module.annotations.InputValidations;
import edu.unc.mapseq.module.annotations.OutputValidations;
import edu.unc.mapseq.module.constraints.FileIsReadable;

@Application(name = "WriteVCFHeader")
public class WriteVCFHeader extends Module {

    @NotNull(message = "output is required", groups = InputValidations.class)
    @FileIsReadable(message = "invalid output", groups = OutputValidations.class)
    @InputArgument
    private File output;

    @InputArgument
    private String labName;

    @InputArgument
    private String studyName;

    @InputArgument
    private Long studyId;

    @InputArgument
    private String experimentName;

    @InputArgument
    private Long experimentId;

    @InputArgument
    private String libraryName;

    @InputArgument
    private String run;

    @InputArgument
    private Integer lane;

    @InputArgument
    private String sampleName;

    @InputArgument
    private String participantId;

    @InputArgument
    private String barcode;

    @InputArgument
    private String flowcell;

    @InputArgument
    private File filter;

    @InputArgument
    private List<File> mergedBAMFile;

    @Override
    public Class<?> getModuleClass() {
        return WriteVCFHeader.class;
    }

    @Override
    public ModuleOutput call() throws Exception {
        DefaultModuleOutput moduleOutput = new DefaultModuleOutput();
        int exitCode = 0;
        try {

            StringBuilder sb = new StringBuilder();
            if (StringUtils.isNotEmpty(this.studyName)) {
                sb.append(String.format("## %s = %s%n", "StudyName", this.studyName));
            }
            if (this.studyId != null) {
                sb.append(String.format("## %s = %s%n", "StudyId", this.studyId.toString()));
            }
            if (StringUtils.isNotEmpty(this.experimentName)) {
                sb.append(String.format("## %s = %s%n", "ExperimentName", this.experimentName));
            }
            if (this.experimentId != null) {
                sb.append(String.format("## %s = %s%n", "ExperimentId", this.experimentId.toString()));
            }
            if (StringUtils.isNotEmpty(this.labName)) {
                sb.append(String.format("## %s = %s%n", "LabName", this.labName));
            }
            if (StringUtils.isNotEmpty(this.libraryName)) {
                sb.append(String.format("## %s = %s%n", "LibraryName", this.libraryName));
            }
            if (StringUtils.isNotEmpty(this.sampleName)) {
                sb.append(String.format("## %s = %s%n", "ParticipantID", this.sampleName));
            }
            if (StringUtils.isNotEmpty(this.participantId)) {
                sb.append(String.format("## %s = %s%n", "ParticipantID", this.participantId));
            }
            if (StringUtils.isNotEmpty(this.run)) {
                sb.append(String.format("## %s = %s%n", "run", this.run));
            }
            if (this.lane != null) {
                sb.append(String.format("## %s = %s%n", "lane", this.lane.toString()));
            }
            if (StringUtils.isNotEmpty(this.barcode)) {
                sb.append(String.format("## %s = %s%n", "barcode", this.barcode));
            }
            if (StringUtils.isNotEmpty(this.flowcell)) {
                sb.append(String.format("## %s = %s%n", "flowcell", this.flowcell));
            }
            if (this.filter != null) {
                sb.append(String.format("## %s = %s%n", "filter", this.filter.getAbsolutePath()));
            }

            if (this.mergedBAMFile != null) {
                for (File mergedBAM : this.mergedBAMFile) {
                    sb.append(String.format("## %s = %s%n", "bam", mergedBAM.getAbsolutePath()));
                }
            }

            FileUtils.writeStringToFile(output, sb.toString());

        } catch (Exception e) {
            e.printStackTrace();
            moduleOutput.setError(new StringBuilder(e.getMessage()));
            moduleOutput.setExitCode(exitCode);
        }
        moduleOutput.setExitCode(exitCode);
        return moduleOutput;
    }

    public File getOutput() {
        return output;
    }

    public void setOutput(File output) {
        this.output = output;
    }

    public String getLabName() {
        return labName;
    }

    public void setLabName(String labName) {
        this.labName = labName;
    }

    public String getStudyName() {
        return studyName;
    }

    public void setStudyName(String studyName) {
        this.studyName = studyName;
    }

    public Long getStudyId() {
        return studyId;
    }

    public void setStudyId(Long studyId) {
        this.studyId = studyId;
    }

    public String getParticipantId() {
        return participantId;
    }

    public void setParticipantId(String participantId) {
        this.participantId = participantId;
    }

    public String getExperimentName() {
        return experimentName;
    }

    public void setExperimentName(String experimentName) {
        this.experimentName = experimentName;
    }

    public Long getExperimentId() {
        return experimentId;
    }

    public void setExperimentId(Long experimentId) {
        this.experimentId = experimentId;
    }

    public String getLibraryName() {
        return libraryName;
    }

    public void setLibraryName(String libraryName) {
        this.libraryName = libraryName;
    }

    public String getRun() {
        return run;
    }

    public void setRun(String run) {
        this.run = run;
    }

    public Integer getLane() {
        return lane;
    }

    public void setLane(Integer lane) {
        this.lane = lane;
    }

    public String getSampleName() {
        return sampleName;
    }

    public void setSampleName(String sampleName) {
        this.sampleName = sampleName;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getFlowcell() {
        return flowcell;
    }

    public void setFlowcell(String flowcell) {
        this.flowcell = flowcell;
    }

    public File getFilter() {
        return filter;
    }

    public void setFilter(File filter) {
        this.filter = filter;
    }

    public List<File> getMergedBAMFile() {
        return mergedBAMFile;
    }

    public void setMergedBAMFile(List<File> mergedBAMFile) {
        this.mergedBAMFile = mergedBAMFile;
    }

}
