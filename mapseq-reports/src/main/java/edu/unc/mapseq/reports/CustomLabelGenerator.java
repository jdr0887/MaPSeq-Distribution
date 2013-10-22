package edu.unc.mapseq.reports;

import java.io.Serializable;
import java.text.AttributedString;
import java.text.NumberFormat;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.general.PieDataset;

public class CustomLabelGenerator implements PieSectionLabelGenerator, Serializable {

    private static final long serialVersionUID = -430538477746403768L;

    private final NumberFormat percentFormat = NumberFormat.getPercentInstance();

    public CustomLabelGenerator() {
        super();
    }

    public String generateSectionLabel(PieDataset dataset, Comparable key) {
        Number value = dataset.getValue(key);
        double total = DatasetUtilities.calculatePieDatasetTotal(dataset);
        double percent = 0.0;
        if (value != null) {
            double v = value.doubleValue();
            if (v > 0.0) {
                percent = v / total;
            }
        }
        return String.format("%s %s (%s)", key, DurationFormatUtils.formatDuration(value.longValue(), "d+HH:mm"),
                percentFormat.format(percent));
    }

    public AttributedString generateAttributedSectionLabel(PieDataset dataset, Comparable key) {
        return null;
    }
}
