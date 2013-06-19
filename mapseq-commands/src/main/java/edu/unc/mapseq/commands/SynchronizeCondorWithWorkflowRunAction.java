package edu.unc.mapseq.commands;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.AbstractAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.MaPSeqDAOBean;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.WorkflowRunDAO;
import edu.unc.mapseq.dao.model.Account;
import edu.unc.mapseq.dao.model.HTSFSample;
import edu.unc.mapseq.dao.model.SequencerRun;
import edu.unc.mapseq.dao.model.WorkflowPlan;
import edu.unc.mapseq.dao.model.WorkflowRun;
import edu.unc.mapseq.dao.model.WorkflowRunStatusType;

@Command(scope = "mapseq", name = "synchronize-condor-with-workflow-run", description = "Synchronize Condor with WorkflowRun")
public class SynchronizeCondorWithWorkflowRunAction extends AbstractAction {

    private final Logger logger = LoggerFactory.getLogger(SynchronizeCondorWithWorkflowRunAction.class);

    @Argument(index = 0, name = "workflowRunId", description = "WorkflowRun identifier", required = true, multiValued = true)
    private List<Long> workflowRunIdList;

    private MaPSeqDAOBean maPSeqDAOBean;

    public SynchronizeCondorWithWorkflowRunAction() {
        super();
    }

    @Override
    public Object doExecute() {
        logger.info("ENTERING doExecute()");
        Account account = null;
        try {
            account = maPSeqDAOBean.getAccountDAO().findByName(System.getProperty("user.name"));
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }

        if (account == null) {
            logger.error("No account found");
            return null;
        }

        List<WorkflowRun> workflowRunList = new ArrayList<WorkflowRun>();
        WorkflowRunDAO workflowRunDAO = maPSeqDAOBean.getWorkflowRunDAO();
        try {
            if (workflowRunIdList != null) {
                for (Long id : workflowRunIdList) {
                    workflowRunList.add(workflowRunDAO.findById(id));
                }
            }
        } catch (MaPSeqDAOException e) {
        }

        try {

            if (workflowRunList != null && workflowRunList.size() > 0) {

                Collections.sort(workflowRunList, new Comparator<WorkflowRun>() {
                    @Override
                    public int compare(WorkflowRun wr1, WorkflowRun wr2) {
                        return wr1.getId().compareTo(wr2.getId());
                    }
                });

                File submitDirectory = new File(System.getenv("MAPSEQ_HOME"), "submit");

                for (final WorkflowRun workflowRun : workflowRunList) {

                    if (workflowRun.getStartDate() == null) {
                        continue;
                    }
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(workflowRun.getStartDate());
                    final String formattedDate = DateFormatUtils.format(calendar, "yyyy-MM-dd");

                    long startTime = new Date().getTime();
                    IOFileFilter shFF = FileFilterUtils.suffixFileFilter("_1.sh");
                    IOFileFilter dagLogFF = FileFilterUtils.suffixFileFilter("dag.dagman.log");
                    Collection<File> fileCollection = FileUtils.listFiles(submitDirectory,
                            FileFilterUtils.or(shFF, dagLogFF), new IOFileFilter() {

                                @Override
                                public boolean accept(File dir, String name) {
                                    return dir.getAbsolutePath().contains(workflowRun.getWorkflow().getName())
                                            && dir.getAbsolutePath().contains(formattedDate);
                                }

                                @Override
                                public boolean accept(File file) {
                                    return true;
                                }
                            });
                    long endTime = new Date().getTime();
                    System.out.printf("Duration to find files...%s%n", ((endTime - startTime) / 1000) / 60);

                    List<File> fileList = new ArrayList<File>(fileCollection);
                    Collections.sort(fileList, new Comparator<File>() {
                        @Override
                        public int compare(File o1, File o2) {
                            if (o1.getName().contains("dagman")) {
                                return 1;
                            }
                            return 0;
                        }
                    });

                    logger.debug(workflowRun.toString());
                    startTime = new Date().getTime();

                    List<WorkflowPlan> workflowPlanList = maPSeqDAOBean.getWorkflowPlanDAO().findByWorkflowRunId(
                            workflowRun.getId());

                    if (workflowPlanList != null && workflowPlanList.size() > 0) {

                        for (WorkflowPlan wp : workflowPlanList) {

                            logger.debug(wp.toString());

                            if (wp.getSequencerRun() != null) {

                                List<HTSFSample> sampleList = maPSeqDAOBean.getHTSFSampleDAO().findBySequencerRunId(
                                        wp.getSequencerRun().getId());

                                sampleLoop: for (HTSFSample sample : sampleList) {
                                    logger.debug(sample.toString());

                                    SequencerRun sequencerRun = sample.getSequencerRun();

                                    File subFile = fileList.get(1);
                                    String subFileContents = FileUtils.readFileToString(subFile);
                                    if (subFileContents.contains(String.format("--sequencerRunId %d",
                                            sequencerRun.getId()))
                                            && subFileContents.contains(String.format("--workflowRunId %d",
                                                    workflowRun.getId()))) {

                                        File dagFile = fileList.get(0);
                                        System.out.printf("Reading %s%n", dagFile.getAbsolutePath());
                                        List<String> dagFileLines = FileUtils.readLines(dagFile);
                                        for (String line : dagFileLines) {
                                            if (line.contains("Job terminated.")) {
                                                String[] lineSplit = line.split(" ");
                                                try {
                                                    if (workflowRun.getStartDate() == null) {
                                                        Calendar c = Calendar.getInstance();
                                                        c.setTime(workflowRun.getCreationDate());
                                                        c.add(Calendar.MINUTE, 2);
                                                        workflowRun.setStartDate(c.getTime());
                                                    }
                                                    Date endDate = DateUtils.parseDate(
                                                            String.format("%s %s", lineSplit[2], lineSplit[3]),
                                                            new String[] { "MM/dd HH:mm:ss" });
                                                    Calendar c = Calendar.getInstance();
                                                    c.setTime(endDate);
                                                    c.set(Calendar.YEAR, 2013);
                                                    workflowRun.setEndDate(c.getTime());
                                                    workflowRun.setStatus(WorkflowRunStatusType.DONE);
                                                    workflowRunDAO.save(workflowRun);
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }
                                                break sampleLoop;
                                            }
                                        }

                                    }

                                }

                            }

                            if (wp.getHTSFSamples() != null) {

                                Set<HTSFSample> sampleSet = wp.getHTSFSamples();

                                sampleLoop: for (HTSFSample sample : sampleSet) {
                                    logger.debug(sample.toString());

                                    SequencerRun sequencerRun = sample.getSequencerRun();

                                    File subFile = fileList.get(1);
                                    String subFileContents = FileUtils.readFileToString(subFile);
                                    if (subFileContents.contains(String.format("--sequencerRunId %d",
                                            sequencerRun.getId()))
                                            && subFileContents.contains(String.format("--htsfSampleId %d",
                                                    sample.getId()))
                                            && subFileContents.contains(String.format("--workflowRunId %d",
                                                    workflowRun.getId()))) {

                                        File dagFile = fileList.get(0);
                                        System.out.printf("Reading %s%n", dagFile.getAbsolutePath());
                                        List<String> dagFileLines = FileUtils.readLines(dagFile);
                                        for (String line : dagFileLines) {
                                            if (line.contains("Job terminated.")) {
                                                String[] lineSplit = line.split(" ");
                                                try {
                                                    if (workflowRun.getStartDate() == null) {
                                                        Calendar c = Calendar.getInstance();
                                                        c.setTime(workflowRun.getCreationDate());
                                                        c.add(Calendar.MINUTE, 2);
                                                        workflowRun.setStartDate(c.getTime());
                                                    }
                                                    Date endDate = DateUtils.parseDate(
                                                            String.format("%s %s", lineSplit[2], lineSplit[3]),
                                                            new String[] { "MM/dd HH:mm:ss" });
                                                    Calendar c = Calendar.getInstance();
                                                    c.setTime(endDate);
                                                    c.set(Calendar.YEAR, 2013);
                                                    workflowRun.setEndDate(c.getTime());
                                                    workflowRun.setStatus(WorkflowRunStatusType.DONE);
                                                    workflowRunDAO.save(workflowRun);
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }
                                                break sampleLoop;
                                            }
                                        }

                                    }

                                }

                            }

                        }
                    }
                    endTime = new Date().getTime();
                    System.out.printf("Duration to process WorkflowRun...%s%n", ((endTime - startTime) / 1000) / 60);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    public List<Long> getWorkflowRunIdList() {
        return workflowRunIdList;
    }

    public void setWorkflowRunIdList(List<Long> workflowRunIdList) {
        this.workflowRunIdList = workflowRunIdList;
    }

    public MaPSeqDAOBean getMaPSeqDAOBean() {
        return maPSeqDAOBean;
    }

    public void setMaPSeqDAOBean(MaPSeqDAOBean maPSeqDAOBean) {
        this.maPSeqDAOBean = maPSeqDAOBean;
    }

}
