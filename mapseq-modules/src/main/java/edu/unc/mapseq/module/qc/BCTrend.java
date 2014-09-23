package edu.unc.mapseq.module.qc;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
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
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
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
import edu.unc.mapseq.module.constraints.FileIsReadable;

@Application(name = "BCTrend")
public class BCTrend extends Module {

    @NotNull(message = "inFile is required", groups = InputValidations.class)
    @FileIsReadable(message = "Input file is not readable", groups = InputValidations.class)
    @InputArgument
    private File inFile;

    @NotNull(message = "outFile is required", groups = InputValidations.class)
    @InputArgument
    private File outFile;

    @NotNull(message = "trendPlot is required", groups = InputValidations.class)
    @InputArgument
    private File trendPlot;

    @NotNull(message = "flagFile is required", groups = InputValidations.class)
    @InputArgument
    private File flagFile;

    public BCTrend() {
        super();
    }

    @Override
    public Class<?> getModuleClass() {
        return BCTrend.class;
    }

    @Override
    public ModuleOutput call() throws ModuleException {

        DefaultModuleOutput moduleOutput = new DefaultModuleOutput();

        int exitCode = 0;
        try {

            StringBuilder outFileSB = new StringBuilder();

            XYSeries series = new XYSeries("Median");
            List<Integer> bcHash = new ArrayList<Integer>();
            List<String> lines = FileUtils.readLines(inFile);
            for (int i = 0; i < lines.size(); ++i) {
                if (i == 0) {
                    continue;
                }

                StringTokenizer st = new StringTokenizer(lines.get(i), "\t");
                String token = st.nextToken();
                token = st.nextToken();
                token = st.nextToken();
                token = st.nextToken();
                token = st.nextToken();
                token = st.nextToken();
                token = st.nextToken();
                token = st.nextToken();
                Integer median = Integer.valueOf(token);
                bcHash.add(median);

                if (i == 1) {
                    continue;
                }

                Integer drop = bcHash.get(i - 2) - median;
                series.add(i - 2, drop < 0 ? 0 : drop);
                outFileSB.append(i - 2).append("\t").append(drop);
            }

            FileUtils.writeStringToFile(outFile, outFileSB.toString());

            XYSeriesCollection dataset = new XYSeriesCollection();
            dataset.addSeries(series);

            JFreeChart chart = ChartFactory.createScatterPlot("Base Calling score dropping trend", null,
                    "BC Score difference across cycle", dataset, PlotOrientation.VERTICAL, true, false, false);

            XYPlot plot = (XYPlot) chart.getPlot();
            plot.setNoDataMessage("NO DATA");
            plot.setDomainZeroBaselineVisible(true);
            plot.setRangeZeroBaselineVisible(true);

            XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
            renderer.setSeriesOutlinePaint(0, Color.black);
            renderer.setUseOutlinePaint(true);

            NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
            domainAxis.setAutoRangeIncludesZero(false);
            domainAxis.setTickMarkInsideLength(2.0f);
            domainAxis.setTickMarkOutsideLength(0.0f);

            NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
            rangeAxis.setTickMarkInsideLength(2.0f);
            rangeAxis.setTickMarkOutsideLength(0.0f);

            ImageIO.write(chart.createBufferedImage(600, 480), "png", trendPlot);

        } catch (Exception e) {
            e.printStackTrace();
            moduleOutput.setError(new StringBuilder(e.getMessage()));
            moduleOutput.setExitCode(exitCode);
        }
        moduleOutput.setExitCode(exitCode);

        FileData fm = new FileData();
        fm.setName(trendPlot.getName());
        fm.setMimeType(MimeType.PNG_BCTREND);
        getFileDatas().add(fm);

        fm = new FileData();
        fm.setName(outFile.getName());
        fm.setMimeType(MimeType.TEXT_BC_TREND);
        getFileDatas().add(fm);

        fm = new FileData();
        fm.setName(flagFile.getName());
        fm.setMimeType(MimeType.TEXT_BC_FLAG);
        getFileDatas().add(fm);

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

    public File getTrendPlot() {
        return trendPlot;
    }

    public void setTrendPlot(File trendPlot) {
        this.trendPlot = trendPlot;
    }

    public File getFlagFile() {
        return flagFile;
    }

    public void setFlagFile(File flagFile) {
        this.flagFile = flagFile;
    }

}
