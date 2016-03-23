package edu.unc.mapseq.module.sequencing.fastqc;

import java.io.File;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.babraham.FastQC.Analysis.AnalysisListener;
import uk.ac.babraham.FastQC.Modules.BasicStats;
import uk.ac.babraham.FastQC.Modules.KmerContent;
import uk.ac.babraham.FastQC.Modules.NContent;
import uk.ac.babraham.FastQC.Modules.OverRepresentedSeqs;
import uk.ac.babraham.FastQC.Modules.PerBaseGCContent;
import uk.ac.babraham.FastQC.Modules.PerBaseQualityScores;
import uk.ac.babraham.FastQC.Modules.PerBaseSequenceContent;
import uk.ac.babraham.FastQC.Modules.PerSequenceGCContent;
import uk.ac.babraham.FastQC.Modules.PerSequenceQualityScores;
import uk.ac.babraham.FastQC.Modules.QCModule;
import uk.ac.babraham.FastQC.Modules.SequenceLengthDistribution;
import uk.ac.babraham.FastQC.Report.HTMLReportArchive;
import uk.ac.babraham.FastQC.Sequence.SequenceFactory;
import uk.ac.babraham.FastQC.Sequence.SequenceFile;
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

@Application(name = "FastQC")
public class FastQC extends Module {

    private final Logger logger = LoggerFactory.getLogger(FastQC.class);

    @NotNull(message = "input is required", groups = InputValidations.class)
    @FileIsNotEmpty(message = "input is empty", groups = InputValidations.class)
    @FileIsReadable(message = "input does not exist", groups = InputValidations.class)
    @InputArgument
    private File input;

    @NotNull(message = "ignore is required", groups = InputValidations.class)
    @InputArgument
    private IgnoreLevelType ignore;

    @NotNull(message = "output is required", groups = InputValidations.class)
    @FileIsNotEmpty(message = "output is empty", groups = OutputValidations.class)
    @InputArgument
    private File output;

    public FastQC() {
        super();
    }

    @Override
    public Class<?> getModuleClass() {
        return FastQC.class;
    }

    @Override
    public ModuleOutput call() throws ModuleException {
        final DefaultModuleOutput moduleOutput = new DefaultModuleOutput();
        try {
            FastQCAnalysisRunner runner = new FastQCAnalysisRunner(SequenceFactory.getSequenceFile(input));
            runner.addAnalysisListener(new AnalysisListener() {

                @Override
                public void analysisUpdated(SequenceFile file, int sequencesProcessed, int percentComplete) {
                    if (percentComplete % 5 == 0) {
                        if (percentComplete == 105) {
                            moduleOutput.getError().append(
                                    "It seems our guess for the total number of records wasn't very good.  Sorry about that.\n");
                        }
                        if (percentComplete > 100) {
                            moduleOutput.getError().append("Still going at ").append(percentComplete)
                                    .append("% complete for ").append(file.name()).append("\n");
                        } else {
                            moduleOutput.getOutput().append("Approx ").append(percentComplete).append("% complete for ")
                                    .append(file.name()).append("\n");
                        }
                    }
                }

                @Override
                public void analysisStarted(SequenceFile file) {
                    moduleOutput.getOutput().append("Started analysis of ").append(file.name()).append("\n");
                }

                @Override
                public void analysisExceptionReceived(SequenceFile file, Exception e) {
                    moduleOutput.getError().append("Failed to process file ").append(file.name()).append("\n");
                    e.printStackTrace();
                }

                @Override
                public void analysisComplete(SequenceFile file, QCModule[] results) {
                    moduleOutput.getOutput().append("Analysis complete for ").append(file.name()).append("\n");
                    try {
                        new HTMLReportArchive(file, results, output);
                    } catch (Exception e) {
                        analysisExceptionReceived(file, e);
                        return;
                    }
                }
            });

            OverRepresentedSeqs os = new OverRepresentedSeqs();

            QCModule[] module_list = new QCModule[] { new BasicStats(), new PerBaseQualityScores(),
                    new PerSequenceQualityScores(), new PerBaseSequenceContent(), new PerBaseGCContent(),
                    new PerSequenceGCContent(), new NContent(), new SequenceLengthDistribution(),
                    os.duplicationLevelModule(), os, new KmerContent() };

            runner.startAnalysis(module_list);
            runner.run();
        } catch (Exception e) {
            logger.error("Exception", e);
            moduleOutput.getError().append(new StringBuilder(e.getMessage()));
            moduleOutput.setExitCode(-1);
            return moduleOutput;
        }
        return moduleOutput;
    }

    public File getInput() {
        return input;
    }

    public void setInput(File input) {
        this.input = input;
    }

    public IgnoreLevelType getIgnore() {
        return ignore;
    }

    public void setIgnore(IgnoreLevelType ignore) {
        this.ignore = ignore;
    }

    public File getOutput() {
        return output;
    }

    public void setOutput(File output) {
        this.output = output;
    }

    @Override
    public String toString() {
        return String.format("FastQC [logger=%s, input=%s, ignore=%s, output=%s, toString()=%s]", logger, input, ignore,
                output, super.toString());
    }

}
