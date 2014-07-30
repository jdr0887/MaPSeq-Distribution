package edu.unc.mapseq.commands;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.AbstractAction;

import edu.unc.mapseq.dao.MaPSeqDAOBean;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.SampleDAO;
import edu.unc.mapseq.dao.StudyDAO;
import edu.unc.mapseq.dao.WorkflowRunDAO;
import edu.unc.mapseq.dao.model.Sample;
import edu.unc.mapseq.dao.model.Study;
import edu.unc.mapseq.dao.model.WorkflowRun;

@Command(scope = "mapseq", name = "check-workflow-status", description = "Check workflow status by Study name")
public class CheckWorkflowRunAttemptStatusAction extends AbstractAction {

    @Argument(index = 0, name = "studyName", description = "Study Name", required = true, multiValued = false)
    private String studyName;

    private MaPSeqDAOBean maPSeqDAOBean;

    public CheckWorkflowRunAttemptStatusAction() {
        super();
    }

    @Override
    public Object doExecute() {

        List<WorkflowRun> wrList = new ArrayList<WorkflowRun>();
        StudyDAO studyDAO = maPSeqDAOBean.getStudyDAO();
        WorkflowRunDAO workflowRunDAO = maPSeqDAOBean.getWorkflowRunDAO();
        try {
            List<Study> studyList = studyDAO.findByName(this.studyName);
            if (studyList == null) {
                System.out.printf("Study '%s' not found%n", this.studyName);
                return null;
            }
            List<WorkflowRun> wfRunList = workflowRunDAO.findByStudyId(studyList.get(0).getId());
            if (wfRunList != null) {
                wrList.addAll(wfRunList);
            }
        } catch (MaPSeqDAOException e) {
        }
        SampleDAO sampleDAO = maPSeqDAOBean.getSampleDAO();

        try {

            if (wrList.size() > 0) {
                StringBuilder sb = new StringBuilder();
                Formatter formatter = new Formatter(sb, Locale.US);
                formatter.format("%1s%2$-18s %3$-18s %4$-30s %5$-18s %6$-20s %7$-24s %8$s%n", "", "Flowcell ID",
                        "Sample ID", "Sample Name", "Workflow Name", "Started", "Finished", "Status");

                for (WorkflowRun workflowRun : wrList) {

                    List<Sample> sampleList = sampleDAO.findByFlowcellId(workflowRun.getSequencerRun().getId());
                    
                    
                    WorkflowRun wr = wp.getWorkflowRun();
                    if (sampleList != null && !sampleList.isEmpty()) {
                        for (Sample sample : sampleList) {
                            Date startDate = wr.getStartDate();
                            String formattedStartDate = "";
                            if (startDate != null) {
                                formattedStartDate = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                                        .format(wr.getStartDate());
                            }
                            Date endDate = wr.getEndDate();
                            String formattedEndDate = "";
                            if (endDate != null) {
                                formattedEndDate = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                                        .format(wr.getEndDate());
                            }
                            formatter.format("%s%1$-18s %2$-18s %3$-30s %4$-18s %5$-20s %6$-24s %7$s%n", wp
                                    .getSequencerRun().getId(), sample.getId(), sample.getName(), wr.getWorkflow()
                                    .getName(), formattedStartDate, formattedEndDate, wr.getStatus().getState());
                        }
                    }
                    formatter.flush();
                }
                System.out.println(formatter.toString());
                formatter.close();
            }
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getStudyName() {
        return studyName;
    }

    public void setStudyName(String studyName) {
        this.studyName = studyName;
    }

    public MaPSeqDAOBean getMaPSeqDAOBean() {
        return maPSeqDAOBean;
    }

    public void setMaPSeqDAOBean(MaPSeqDAOBean maPSeqDAOBean) {
        this.maPSeqDAOBean = maPSeqDAOBean;
    }

}
