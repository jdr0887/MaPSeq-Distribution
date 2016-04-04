package edu.unc.mapseq.workflow.sequencing;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.MaPSeqDAOBeanService;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.model.FileData;
import edu.unc.mapseq.dao.model.Job;
import edu.unc.mapseq.dao.model.MimeType;
import edu.unc.mapseq.dao.model.Sample;

public class SequencingWorkflowUtil {

    private static final Logger logger = LoggerFactory.getLogger(SequencingWorkflowUtil.class);

    public static final Pattern read1Pattern = Pattern.compile("^.+_R1\\.fastq\\.gz$");

    public static final Pattern read2Pattern = Pattern.compile("^.+_R2\\.fastq\\.gz$");

    public static String getRootFastqName(String name) {
        logger.debug("ENTERING getRootFastqName(String)");
        if (name.endsWith(".fastq.gz")) {
            return StringUtils.removeEnd(name, ".fastq.gz");
        }
        if (name.endsWith(".fastq")) {
            return StringUtils.removeEnd(name, ".fastq");
        }
        return name;
    }

    public static List<File> getReadPairList(Sample sample) {
        logger.debug("ENTERING getReadPairList(Set<FileData>, String, Integer)");

        List<File> readPairList = new ArrayList<File>();

        Set<FileData> fileDataSet = sample.getFileDatas();
        if (fileDataSet == null) {
            logger.warn("fileDataSet was null...returning empty readPairList");
            return readPairList;
        }

        logger.info("fileDataSet.size() = {}", fileDataSet.size());

        for (FileData fileData : fileDataSet) {
            MimeType mimeType = fileData.getMimeType();
            if (mimeType != null && mimeType.equals(MimeType.FASTQ)) {
                Matcher matcherR1 = read1Pattern.matcher(fileData.getName());
                if (matcherR1.matches()) {
                    readPairList.add(new File(fileData.getPath(), fileData.getName()));
                }

                Matcher matcherR2 = read2Pattern.matcher(fileData.getName());
                if (matcherR2.matches()) {
                    readPairList.add(new File(fileData.getPath(), fileData.getName()));
                }
            }
        }

        readPairList.sort((a, b) -> a.getName().compareTo(b.getName()));
        readPairList.forEach(a -> logger.info(a.getAbsolutePath()));
        return readPairList;
    }


}
