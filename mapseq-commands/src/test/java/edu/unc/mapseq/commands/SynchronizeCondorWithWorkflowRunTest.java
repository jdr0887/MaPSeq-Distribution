package edu.unc.mapseq.commands;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;

public class SynchronizeCondorWithWorkflowRunTest {

    public SynchronizeCondorWithWorkflowRunTest() {
        super();
    }

    @Test
    public void test() {

        File submitDirectory = new File("/home/jdr0887/mapseq", "submit");

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2013);
        calendar.set(Calendar.MONTH, 6);
        calendar.set(Calendar.DAY_OF_MONTH, 18);
        final String formattedDate = DateFormatUtils.format(calendar, "yyyy-MM-dd");

        IOFileFilter shFF = FileFilterUtils.suffixFileFilter("_1.sh");
        IOFileFilter dagLogFF = FileFilterUtils.suffixFileFilter("dag.dagman.log");
        Collection<File> fileCollection = FileUtils.listFiles(submitDirectory, FileFilterUtils.or(shFF, dagLogFF),
                new IOFileFilter() {

                    @Override
                    public boolean accept(File arg0, String arg1) {
                        return arg0.getAbsolutePath().contains("NCGenesDX")
                                && arg0.getAbsolutePath().contains(formattedDate);
                    }

                    @Override
                    public boolean accept(File arg0) {
                        return true;
                    }
                });

        Map<Path, List<File>> fileMap = new HashMap<Path, List<File>>();
        for (File f : fileCollection) {
            if (!fileMap.containsKey(f.getParentFile().toPath())) {
                fileMap.put(f.getParentFile().toPath(), new ArrayList<File>());
            }
        }

        for (File f : fileCollection) {
            fileMap.get(f.getParentFile().toPath()).add(f);
        }

        for (Path key : fileMap.keySet()) {
            List<File> fileList = fileMap.get(key);
            Collections.sort(fileList, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    if (o1.getName().contains("dagman")) {
                        return 1;
                    }
                    return 0;
                }
            });

            File subFile = fileList.get(1);

            try {

                String subFileContents = FileUtils.readFileToString(subFile);

                if (subFileContents.contains(String.format("--sequencerRunId %d", 544290))
                        && subFileContents.contains(String.format("--htsfSampleId %d", 544293))
                        && subFileContents.contains(String.format("--workflowRunId %d", 592892))) {

                    File dagFile = fileList.get(0);
                    System.out.printf("Reading %s%n", dagFile.getAbsolutePath());
                    List<String> dagFileLines = FileUtils.readLines(dagFile);
                    for (String line : dagFileLines) {
                        if (line.contains("Job terminated.")) {
                            String[] lineSplit = line.split(" ");
                            try {
                                Date endDate = DateUtils.parseDate(String.format("%s %s", lineSplit[2], lineSplit[3]),
                                        new String[] { "MM/dd HH:mm:ss" });
                                Calendar c = Calendar.getInstance();
                                c.setTime(endDate);
                                c.set(Calendar.YEAR, 2013);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
}
