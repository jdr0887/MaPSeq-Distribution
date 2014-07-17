package edu.unc.mapseq.dao.ws;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.ConnectionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.JobDAO;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.model.Job;
import edu.unc.mapseq.ws.JobService;

/**
 * 
 * @author jdr0887
 */
public class JobDAOImpl extends BaseEntityDAOImpl<Job, Long> implements JobDAO {

    private final Logger logger = LoggerFactory.getLogger(JobDAOImpl.class);

    private JobService jobService;

    public JobDAOImpl() {
        super(Job.class);
    }

    public void init() {
        jobService = getService().getPort(JobService.class);
        Client cl = ClientProxy.getClient(jobService);
        HTTPConduit httpConduit = (HTTPConduit) cl.getConduit();
        httpConduit.getClient().setReceiveTimeout(getConfigurationService().getWebServiceTimeout());
        httpConduit.getClient().setConnectionTimeout(0);
        httpConduit.getClient().setConnection(ConnectionType.CLOSE);
    }

    @Override
    public List<Job> findFileDataByIdAndWorkflowId(Long fileDataId, String clazzName, Long workflowId)
            throws MaPSeqDAOException {
        return null;
    }

    @Override
    public Long save(Job entity) throws MaPSeqDAOException {
        logger.debug("ENTERING save(Job)");
        // tried to use WstxOutputFactory to replace control chars on server side....to no avail
        if (StringUtils.isNotEmpty(entity.getStderr())) {
            StringBuilder sb = new StringBuilder(entity.getStderr());
            int idx = sb.length();
            while (idx-- > 0) {
                if (sb.charAt(idx) < 0x20 && sb.charAt(idx) != 0x9 && sb.charAt(idx) != 0xA && sb.charAt(idx) != 0xD) {
                    sb.deleteCharAt(idx);
                }
            }
            entity.setStderr(sb.toString());
        }

        if (StringUtils.isNotEmpty(entity.getStdout())) {
            StringBuilder sb = new StringBuilder(entity.getStdout());
            int idx = sb.length();
            while (idx-- > 0) {
                if (sb.charAt(idx) < 0x20 && sb.charAt(idx) != 0x9 && sb.charAt(idx) != 0xA && sb.charAt(idx) != 0xD) {
                    sb.deleteCharAt(idx);
                }
            }
            entity.setStdout(sb.toString());
        }
        Long id = jobService.save(entity);
        entity.setId(id);
        return id;
    }

    @Override
    public List<Job> findByCreatorAndWorkflowIdAndCreationDateRange(Long accountId, Long workflowId, Date startDate,
            Date endDate) throws MaPSeqDAOException {
        logger.debug("ENTERING findByCreatorAndWorkflowIdAndCreationDateRange(Long, Long, Date, Date)");
        String formattedStartDate = DateFormatUtils.ISO_DATE_FORMAT.format(startDate);
        String formattedEndDate = DateFormatUtils.ISO_DATE_FORMAT.format(endDate);
        List<Job> ret = null;
        try {
            ret = jobService.findByCreatorAndWorkflowIdAndCreationDateRange(accountId, workflowId, formattedStartDate,
                    formattedEndDate);
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return ret;
    }

    @Override
    public List<Job> findFileDataById(Long fileDataId, String clazzName) throws MaPSeqDAOException {
        return null;
    }

    @Override
    public List<Job> findByWorkflowRunId(Long id) throws MaPSeqDAOException {
        return null;
    }

    @Override
    public List<Job> findByName(String arg0) throws MaPSeqDAOException {
        return null;
    }

    @Override
    public Job findById(Long id) throws MaPSeqDAOException {
        logger.debug("ENTERING findById(Long)");
        Job job = jobService.findById(id);
        return job;
    }

}
