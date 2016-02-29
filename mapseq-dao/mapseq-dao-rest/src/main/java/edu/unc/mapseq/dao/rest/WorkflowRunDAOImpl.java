package edu.unc.mapseq.dao.rest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.WorkflowRunDAO;
import edu.unc.mapseq.dao.model.WorkflowRun;

@Component
public class WorkflowRunDAOImpl extends NamedEntityDAOImpl<WorkflowRun, Long> implements WorkflowRunDAO {

    private final Logger logger = LoggerFactory.getLogger(WorkflowRunDAOImpl.class);

    public WorkflowRunDAOImpl() {
        super(WorkflowRun.class);
    }

    @Override
    public Long save(WorkflowRun entity) throws MaPSeqDAOException {
        logger.debug("ENTERING save(WorkflowRun)");
        WebClient client = WebClient.create(getRestServiceURL(), getProviders(), true).type(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
        Response response = client.path("/").post(entity);
        Long id = response.readEntity(Long.class);
        return id;
    }

    @Override
    public WorkflowRun findById(Long id) throws MaPSeqDAOException {
        logger.debug("ENTERING findById(Long)");
        WebClient client = WebClient.create(getRestServiceURL(), getProviders(), true);
        WorkflowRun workflowRun = client.path("findById/{id}", id).accept(MediaType.APPLICATION_JSON)
                .get(WorkflowRun.class);
        return workflowRun;
    }

    @Override
    public List<WorkflowRun> findByName(String name) throws MaPSeqDAOException {
        logger.debug("ENTERING findByName(String)");
        WebClient client = WebClient.create(getRestServiceURL(), getProviders(), true);
        Collection<? extends WorkflowRun> ret = client.path("findByName/{name}", name)
                .accept(MediaType.APPLICATION_JSON).getCollection(WorkflowRun.class);
        return new ArrayList<WorkflowRun>(ret);
    }

    @Override
    public List<WorkflowRun> findByCreatedDateRange(Date startDate, Date endDate) throws MaPSeqDAOException {
        logger.debug("ENTERING findByCreatedDateRange(String)");

        String formattedStartDate = DateFormatUtils.ISO_DATE_FORMAT.format(startDate);
        String formattedEndDate = DateFormatUtils.ISO_DATE_FORMAT.format(endDate);
        WebClient client = WebClient.create(getRestServiceURL(), getProviders(), true);

        Collection<? extends WorkflowRun> ret = client
                .path("findByCreatedDateRange/{startDate}/{endDate}", formattedStartDate, formattedEndDate)
                .accept(MediaType.APPLICATION_JSON).getCollection(WorkflowRun.class);
        return new ArrayList<WorkflowRun>(ret);
    }

    @Override
    public List<WorkflowRun> findByStudyNameAndSampleNameAndWorkflowName(String studyName, String sampleName,
            String workflowName) throws MaPSeqDAOException {
        logger.debug("ENTERING findByStudyNameAndSampleNameAndWorkflowName(String, String, String)");
        WebClient client = WebClient.create(getRestServiceURL(), getProviders(), true);
        Collection<? extends WorkflowRun> ret = client
                .path("findByStudyNameAndSampleNameAndWorkflowName/{studyName}/{sampleName}/{workflowName}", studyName,
                        sampleName, workflowName).accept(MediaType.APPLICATION_JSON).getCollection(WorkflowRun.class);
        return new ArrayList<WorkflowRun>(ret);
    }

    @Override
    public List<WorkflowRun> findByWorkflowId(Long id) throws MaPSeqDAOException {
        logger.debug("ENTERING findByWorkflowId(Long)");
        WebClient client = WebClient.create(getRestServiceURL(), getProviders(), true);
        Collection<? extends WorkflowRun> ret = client.path("findByWorkflowId/{workflowId}", id)
                .accept(MediaType.APPLICATION_JSON).getCollection(WorkflowRun.class);
        return new ArrayList<WorkflowRun>(ret);
    }

    @Override
    public WorkflowRun findByWorkflowRunAttemptId(Long workflowRunAttemptId) throws MaPSeqDAOException {
        logger.debug("ENTERING findByWorkflowRunAttemptId(Long)");
        WebClient client = WebClient.create(getRestServiceURL(), getProviders(), true);
        WorkflowRun workflowRun = client
                .path("findByWorkflowRunAttemptId/{workflowRunAttemptId}", workflowRunAttemptId)
                .accept(MediaType.APPLICATION_JSON).get(WorkflowRun.class);
        return workflowRun;
    }

    @Override
    public List<WorkflowRun> findByFlowcellId(Long flowcellId) throws MaPSeqDAOException {
        logger.debug("ENTERING findByFlowcellId(Long)");
        WebClient client = WebClient.create(getRestServiceURL(), getProviders(), true);
        Collection<? extends WorkflowRun> ret = client.path("findByFlowcellId/{flowcellId}", flowcellId)
                .accept(MediaType.APPLICATION_JSON).getCollection(WorkflowRun.class);
        return new ArrayList<WorkflowRun>(ret);
    }

    @Override
    public List<WorkflowRun> findByFlowcellIdAndWorkflowId(Long flowcellId, Long workflowId) throws MaPSeqDAOException {
        logger.debug("ENTERING findByFlowcellIdAndWorkflowId(Long, Long)");
        WebClient client = WebClient.create(getRestServiceURL(), getProviders(), true);
        Collection<? extends WorkflowRun> ret = client
                .path("findByFlowcellIdAndWorkflowId/{flowcellId}/{workflowId}", flowcellId, workflowId)
                .accept(MediaType.APPLICATION_JSON).getCollection(WorkflowRun.class);
        return new ArrayList<WorkflowRun>(ret);
    }

    @Override
    public List<WorkflowRun> findBySampleId(Long sampleId) throws MaPSeqDAOException {
        logger.debug("ENTERING findBySampleId(Long)");
        WebClient client = WebClient.create(getRestServiceURL(), getProviders(), true);
        Collection<? extends WorkflowRun> ret = client.path("findBySampleId/{sampleId}", sampleId)
                .accept(MediaType.APPLICATION_JSON).getCollection(WorkflowRun.class);
        return new ArrayList<WorkflowRun>(ret);
    }

    @Override
    public List<WorkflowRun> findByStudyId(Long studyId) throws MaPSeqDAOException {
        logger.debug("ENTERING findByStudyId(Long)");
        WebClient client = WebClient.create(getRestServiceURL(), getProviders(), true);
        Collection<? extends WorkflowRun> ret = client.path("findByStudyId/{studyId}", studyId)
                .accept(MediaType.APPLICATION_JSON).getCollection(WorkflowRun.class);
        return new ArrayList<WorkflowRun>(ret);
    }

}
