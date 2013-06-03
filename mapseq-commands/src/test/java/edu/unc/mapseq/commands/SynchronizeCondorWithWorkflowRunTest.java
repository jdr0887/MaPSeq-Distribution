package edu.unc.mapseq.commands;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;

public class SynchronizeCondorWithWorkflowRunTest {

    public SynchronizeCondorWithWorkflowRunTest() {
        super();
    }

    @Test
    public void test() {

        File submitDirectory = new File("/home/jdr0887/mapseq", "submit");

        Collection<File> fileCollection = FileUtils.listFiles(
                submitDirectory,
                FileFilterUtils.or(FileFilterUtils.suffixFileFilter("_1.sub"),
                        FileFilterUtils.suffixFileFilter("dag.dagman.log")), DirectoryFileFilter.DIRECTORY);
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

            // File subFile = fileList.get(0);
            // System.out.println(subFile.getName());
            try {
                File dagFile = fileList.get(1);
                System.out.println(dagFile.getName());
                List<String> dagFileLines = FileUtils.readLines(dagFile);
                for (String line : dagFileLines) {
                    if (line.contains("Job terminated.")) {
                        String[] lineSplit = line.split(" ");
                        try {
                            Date d = DateUtils.parseDate(String.format("%s %s", lineSplit[2], lineSplit[3]),
                                    new String[] { "MM/dd HH:mm:ss" });
                            System.out.println(d);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
}
