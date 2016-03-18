package edu.unc.mapseq.module.impl.qc;

import java.io.File;
import java.util.List;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;
import javax.validation.constraints.NotNull;

import org.apache.commons.io.FileUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import edu.unc.mapseq.dao.model.FileData;
import edu.unc.mapseq.dao.model.MimeType;
import edu.unc.mapseq.module.DefaultModuleOutput;
import edu.unc.mapseq.module.Module;
import edu.unc.mapseq.module.ModuleException;
import edu.unc.mapseq.module.ModuleOutput;
import edu.unc.mapseq.module.annotations.Application;
import edu.unc.mapseq.module.annotations.InputArgument;
import edu.unc.mapseq.module.annotations.InputValidations;
import edu.unc.mapseq.module.annotations.OutputValidations;
import edu.unc.mapseq.module.constraints.FileIsReadable;

@Application(name = "TranscriptCoverageTrend")
public class TranscriptCoverageTrend extends Module {

    @NotNull(message = "inFile is required", groups = InputValidations.class)
    @FileIsReadable(message = "File is not readable: inFile", groups = InputValidations.class)
    @InputArgument
    private File inFile;

    @NotNull(message = "outFile is required", groups = InputValidations.class)
    @FileIsReadable(message = "File is not readable: outFile", groups = OutputValidations.class)
    @InputArgument
    private File outFile;

    public TranscriptCoverageTrend() {
        super();
    }

    @Override
    public Class<?> getModuleClass() {
        return TranscriptCoverageTrend.class;
    }

    @Override
    public ModuleOutput call() throws ModuleException {

        DefaultModuleOutput moduleOutput = new DefaultModuleOutput();

        int exitCode = 0;
        try {

            XYSeries series = new XYSeries("Position");
            List<String> lines = FileUtils.readLines(inFile);
            for (String line : lines) {
                StringTokenizer st = new StringTokenizer(line, "\t");
                String x = st.nextToken();
                String y = st.nextToken();
                series.add(Double.valueOf(x.trim()), Double.valueOf(y.trim()));
            }

            XYSeriesCollection dataset = new XYSeriesCollection();
            dataset.addSeries(series);

            JFreeChart chart = ChartFactory.createXYLineChart("Base Calling score dropping trend",
                    "Relative Position in Transcript", "# of short reads", dataset, PlotOrientation.VERTICAL, true,
                    false, false);

            XYPlot plot = (XYPlot) chart.getPlot();
            plot.setNoDataMessage("NO DATA");

            NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
            domainAxis.setAutoRangeIncludesZero(false);

            NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
            rangeAxis.setAutoRangeIncludesZero(false);

            ImageIO.write(chart.createBufferedImage(600, 480), "png", outFile);

            FileData fm = new FileData();
            fm.setName(outFile.getName());
            fm.setMimeType(MimeType.PNG_COVERAGE_X_TRANSCRIPT_PLOT);
            getFileDatas().add(fm);

        } catch (Exception e) {
            e.printStackTrace();
            moduleOutput.setError(new StringBuilder(e.getMessage()));
            moduleOutput.setExitCode(exitCode);
            return moduleOutput;
        }
        moduleOutput.setExitCode(exitCode);

        return moduleOutput;
    }

    public File getInFile() {
        return inFile;
    }

    public void setInFile(File inFile) {
        this.inFile = inFile;
    }

    public File getOutFile() {
        return outFile;
    }

    public void setOutFile(File outFile) {
        this.outFile = outFile;
    }

    @Override
    public String toString() {
        return String.format("TranscriptCoverageTrend [inFile=%s, outFile=%s, toString()=%s]", inFile, outFile,
                super.toString());
    }

}
