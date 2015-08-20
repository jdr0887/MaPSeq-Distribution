package edu.unc.mapseq.module.fastx;

import java.io.File;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.StringUtils;
import org.renci.common.exec.BashExecutor;
import org.renci.common.exec.CommandInput;
import org.renci.common.exec.CommandOutput;
import org.renci.common.exec.Executor;

import edu.unc.mapseq.dao.model.FileData;
import edu.unc.mapseq.dao.model.MimeType;
import edu.unc.mapseq.module.Module;
import edu.unc.mapseq.module.ModuleException;
import edu.unc.mapseq.module.ModuleOutput;
import edu.unc.mapseq.module.ShellModuleOutput;
import edu.unc.mapseq.module.annotations.Application;
import edu.unc.mapseq.module.annotations.InputArgument;
import edu.unc.mapseq.module.annotations.InputValidations;
import edu.unc.mapseq.module.annotations.OutputArgument;
import edu.unc.mapseq.module.annotations.OutputValidations;
import edu.unc.mapseq.module.constraints.FileIsNotEmpty;
import edu.unc.mapseq.module.constraints.FileIsReadable;

@Application(name = "FastqQualityBoxplotGraph", executable = "$%s_FASTX_TOOLKIT_HOME/bin/fastq_quality_boxplot_graph.sh")
public class FastqQualityBoxplotGraph extends Module {

    @FileIsReadable(message = "input is not readable", groups = InputValidations.class)
    @FileIsNotEmpty(message = "input is empty", groups = InputValidations.class)
    @NotNull(message = "input is required", groups = InputValidations.class)
    @InputArgument
    private File input;

    @FileIsNotEmpty(message = "output is empty", groups = OutputValidations.class)
    @NotNull(message = "output is required", groups = InputValidations.class)
    @OutputArgument
    private File output;

    @InputArgument
    private Boolean generatePostcript;

    @InputArgument
    private String title;

    public FastqQualityBoxplotGraph() {
        super();
    }

    @Override
    public Class<?> getModuleClass() {
        return FastqQualityBoxplotGraph.class;
    }

    @Override
    public String getExecutable() {
        return String.format(getModuleClass().getAnnotation(Application.class).executable(), getWorkflowName()
                .toUpperCase());
    }

    @Override
    public ModuleOutput call() throws ModuleException {

        CommandOutput commandOutput = null;

        try {
            CommandInput commandInput = new CommandInput();
            StringBuilder command = new StringBuilder();
            command.append(String.format(getModuleClass().getAnnotation(Application.class).executable(),
                    getWorkflowName().toUpperCase()));

            if (generatePostcript != null && generatePostcript) {
                command.append(" -p");
            }
            if (StringUtils.isNotEmpty(title)) {
                command.append(" -t ").append(title);
            }
            command.append(" -i ").append(input.getAbsolutePath());
            command.append(" -o ").append(output.getAbsolutePath());

            System.out.println(command.toString());
            commandInput.setCommand(command.toString());
            Executor executor = BashExecutor.getInstance();
            commandOutput = executor.execute(commandInput);

        } catch (Exception e) {
            e.printStackTrace();
            throw new ModuleException(e);
        }

        FileData fm = new FileData();
        if (generatePostcript != null && generatePostcript) {
            fm.setMimeType(MimeType.APPLICATION_POSTSCRIPT);
        } else {
            fm.setMimeType(MimeType.IMAGE_PNG);
        }
        fm.setPath(output.getParentFile().getAbsolutePath());
        fm.setName(output.getName());
        getFileDatas().add(fm);

        return new ShellModuleOutput(commandOutput);
    }

    public File getInput() {
        return input;
    }

    public void setInput(File input) {
        this.input = input;
    }

    public File getOutput() {
        return output;
    }

    public void setOutput(File output) {
        this.output = output;
    }

    public Boolean getGeneratePostcript() {
        return generatePostcript;
    }

    public void setGeneratePostcript(Boolean generatePostcript) {
        this.generatePostcript = generatePostcript;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public static void main(String[] args) {
        FastqQualityBoxplotGraph module = new FastqQualityBoxplotGraph();
        module.setWorkflowName("TEST");
        module.setInput(new File(
                "/proj/seq/mapseq/HTSF/130513_UNC13-SN749_0260_AD1WMHACXX/ChIPSeq/L008_GTCCGC/130513_UNC13-SN749_0260_AD1WMHACXX_GTCCGC_L008_R1.stat.txt"));
        module.setGeneratePostcript(Boolean.TRUE);
        module.setOutput(new File(
                "/proj/seq/mapseq/HTSF/130513_UNC13-SN749_0260_AD1WMHACXX/ChIPSeq/L008_GTCCGC/130513_UNC13-SN749_0260_AD1WMHACXX_GTCCGC_L008_R1.boxplot.ps"));
        try {
            module.call();
        } catch (ModuleException e) {
            e.printStackTrace();
        }
    }

}
