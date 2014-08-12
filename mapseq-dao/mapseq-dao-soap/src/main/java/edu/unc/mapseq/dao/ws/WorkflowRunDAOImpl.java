package edu.unc.mapseq.dao.ws;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.ConnectionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.WorkflowRunDAO;
import edu.unc.mapseq.dao.model.WorkflowRun;
import edu.unc.mapseq.ws.WorkflowRunService;

/**
 * 
 * @author jdr0887
 */
public class WorkflowRunDAOImpl extends NamedEntityDAOImpl<WorkflowRun, Long> implements WorkflowRunDAO {

    private final Logger logger = LoggerFactory.getLogger(WorkflowRunDAOImpl.class);

    private WorkflowRunService workflowRunService;

    public WorkflowRunDAOImpl() {
        super(WorkflowRun.class);
    }

    public void init() {
        workflowRunService = getService().getPort(WorkflowRunService.class);
        Client cl = ClientProxy.getClient(workflowRunService);
        HTTPConduit httpConduit = (HTTPConduit) cl.getConduit();
        httpConduit.getClient().setReceiveTimeout(getConfigurationService().getWebServiceTimeout());
        httpConduit.getClient().setConnectionTimeout(0);
        httpConduit.getClient().setConnection(ConnectionType.CLOSE);
    }

    @Override
    public Long save(WorkflowRun workflowRun) throws MaPSeqDAOException {
        logger.debug("ENTERING save(WorkflowRun)");
        Long id = workflowRunService.save(workflowRun);
        return id;
    }

    @Override
    public WorkflowRun findById(Long id) throws MaPSeqDAOException {
        logger.debug("ENTERING findById(Long)");
        WorkflowRun workflowRun = workflowRunService.findById(id);
        return workflowRun;
    }

    @Override
    public List<WorkflowRun> findByName(String name) throws MaPSeqDAOException {
        logger.debug("ENTERING findByName(String)");
        List<WorkflowRun> ret = new ArrayList<WorkflowRun>();
        try {
            ret.addAll(workflowRunService.findByName(name));
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return ret;
    }

    @Override
    public WorkflowRun findByWorkflowRunAttemptId(Long workflowRunAttemptId) throws MaPSeqDAOException {
        logger.debug("ENTERING findByWorkflowRunAttemptId(Long)");
        WorkflowRun workflowRun = workflowRunService.findByWorkflowRunAttemptId(workflowRunAttemptId);
        return workflowRun;
    }

    @Override
    public List<WorkflowRun> findByCreatedDateRange(Date startDate, Date endDate) throws MaPSeqDAOException {
        logger.debug("ENTERING findByCreatedDateRange(Date, Date)");
        String formattedStartDate = DateFormatUtils.ISO_DATE_FORMAT.format(startDate);
        String formattedEndDate = DateFormatUtils.ISO_DATE_FORMAT.format(endDate);
        List<WorkflowRun> ret = new ArrayList<WorkflowRun>();
        try {
            ret.addAll(workflowRunService.findByCreatedDateRange(formattedStartDate, formattedEndDate));
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return ret;
    }

    @Override
    public List<WorkflowRun> findByStudyNameAndSampleNameAndWorkflowName(String studyName, String sampleName,
            String workflowName) throws MaPSeqDAOException {
        logger.debug("ENTERING findByStudyNameAndSampleNameAndWorkflowName(String, String, String)");
        List<WorkflowRun> ret = new ArrayList<WorkflowRun>();
        try {
            ret.addAll(workflowRunService.findByStudyNameAndSampleNameAndWorkflowName(studyName, sampleName,
                    workflowName));
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return ret;
    }

    @Override
    public List<WorkflowRun> findByWorkflowId(Long workflowId) throws MaPSeqDAOException {
        logger.debug("ENTERING findByWorkflowId(Long)");
        List<WorkflowRun> ret = new ArrayList<WorkflowRun>();
        try {
            ret.addAll(workflowRunService.findByWorkflowId(workflowId));
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return ret;
    }

    @Override
    public List<WorkflowRun> findByFlowcellId(Long flowcellId) throws MaPSeqDAOException {
        logger.debug("ENTERING findByFlowcellId(Long)");
        List<WorkflowRun> ret = new ArrayList<WorkflowRun>();
        try {
            ret.addAll(workflowRunService.findByWorkflowId(flowcellId));
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return ret;
    }

    @Override
    public List<WorkflowRun> findByFlowcellIdAndWorkflowId(Long flowcellId, Long workflowId) throws MaPSeqDAOException {
        logger.debug("ENTERING findByFlowcellIdAndWorkflowId(Long)");
        List<WorkflowRun> ret = new ArrayList<WorkflowRun>();
        try {
            ret.addAll(workflowRunService.findByFlowcellIdAndWorkflowId(flowcellId, workflowId));
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return ret;
    }

    @Override
    public List<WorkflowRun> findBySampleId(Long sampleId) throws MaPSeqDAOException {
        logger.debug("ENTERING findBySampleId(Long)");
        List<WorkflowRun> ret = new ArrayList<WorkflowRun>();
        try {
            ret.addAll(workflowRunService.findBySampleId(sampleId));
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return ret;
    }

    @Override
    public List<WorkflowRun> findByStudyId(Long studyId) throws MaPSeqDAOException {
        logger.debug("ENTERING findByStudyId(Long)");
        List<WorkflowRun> ret = new ArrayList<WorkflowRun>();
        try {
            ret.addAll(workflowRunService.findByStudyId(studyId));
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return ret;
    }

}
