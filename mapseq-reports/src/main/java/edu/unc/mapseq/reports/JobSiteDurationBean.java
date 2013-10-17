package edu.unc.mapseq.reports;

import java.util.ArrayList;
import java.util.List;

public class JobSiteDurationBean {

    private String jobName;

    private String siteName;

    private List<Long> duration = new ArrayList<Long>();

    public JobSiteDurationBean(String jobName, String siteName) {
        super();
        this.jobName = jobName;
        this.siteName = siteName;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public List<Long> getDuration() {
        return duration;
    }

    public void setDuration(List<Long> duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", jobName, siteName);
    }

}
