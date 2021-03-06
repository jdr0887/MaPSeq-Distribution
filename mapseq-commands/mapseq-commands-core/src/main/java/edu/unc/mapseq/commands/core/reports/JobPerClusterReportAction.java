package edu.unc.mapseq.commands.core.reports;

import java.io.File;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.JobDAO;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.model.Job;
import edu.unc.mapseq.reports.ReportFactory;

@Command(scope = "mapseq", name = "generate-weekly-jobs-per-cluster-report", description = "")
@Service
public class JobPerClusterReportAction implements Action {

    private static final Logger logger = LoggerFactory.getLogger(JobPerClusterReportAction.class);

    @Reference
    private JobDAO jobDAO;

    @Argument(index = 0, name = "name", description = "Job name (ie, GATKUnifiedGenotyper, SAMToolsIndex, PicardAddOrReplaceReadGroups)", required = true, multiValued = false)
    private String name;

    @Argument(index = 1, name = "toEmailAddress", description = "To Email Address", required = true, multiValued = false)
    private String toEmailAddress;

    @Override
    public Object execute() {
        logger.debug("ENTERING doExecute()");

        try {
            Date endDate = new Date();
            Calendar c = Calendar.getInstance();
            c.setTime(endDate);
            c.add(Calendar.WEEK_OF_YEAR, -1);
            Date startDate = c.getTime();

            List<Job> jobList = jobDAO.findByCreatedDateRange(startDate, endDate);
            Set<String> jobNameSet = new HashSet<String>();
            for (Job job : jobList) {
                if (name.equals(job.getName())) {
                    jobNameSet.add(job.getName());
                }
            }
            Set<String> synchronizedJobNameSet = Collections.synchronizedSet(jobNameSet);
            for (String job : synchronizedJobNameSet) {
                logger.info(job);
            }

            if (synchronizedJobNameSet.size() > 1) {
                logger.error("name was too vague: {}", name);
                return null;
            }

            File chartFile = ReportFactory.createJobPerClusterReport(jobList, name, startDate, endDate);

            String subject = String.format("MaPSeq :: Job Per Cluster Report :: %s (%s - %s)", name,
                    DateFormatUtils.format(startDate, "MM/dd"), DateFormatUtils.format(endDate, "MM/dd"));

            EmailAttachment attachment = new EmailAttachment();
            attachment.setPath(chartFile.getAbsolutePath());
            attachment.setDisposition(EmailAttachment.ATTACHMENT);
            attachment.setDescription(subject);
            attachment.setName(chartFile.getName());

            MultiPartEmail email = new MultiPartEmail();
            email.setHostName("localhost");
            email.addTo(toEmailAddress);
            email.setFrom(String.format("%s@unc.edu", System.getProperty("user.name")));
            email.setSubject(subject);
            email.setMsg("See Attached");
            email.attach(attachment);

            email.send();

            chartFile.delete();
            // System.out.println(chartFile.getAbsolutePath());
        } catch (MaPSeqDAOException | EmailException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getToEmailAddress() {
        return toEmailAddress;
    }

    public void setToEmailAddress(String toEmailAddress) {
        this.toEmailAddress = toEmailAddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
