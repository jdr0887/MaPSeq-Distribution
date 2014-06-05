package edu.unc.mapseq.workflow;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jms.MessageListener;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.HTSFSampleDAO;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.PlatformDAO;
import edu.unc.mapseq.dao.SequencerRunDAO;
import edu.unc.mapseq.dao.WorkflowRunDAO;
import edu.unc.mapseq.dao.model.Account;
import edu.unc.mapseq.dao.model.EntityAttribute;
import edu.unc.mapseq.dao.model.HTSFSample;
import edu.unc.mapseq.dao.model.Platform;
import edu.unc.mapseq.dao.model.SequencerRun;
import edu.unc.mapseq.dao.model.Workflow;
import edu.unc.mapseq.dao.model.WorkflowRun;
import edu.unc.mapseq.dao.model.WorkflowRunStatusType;
import edu.unc.mapseq.workflow.model.WorkflowEntity;
import edu.unc.mapseq.workflow.model.WorkflowEntityAttribute;

public abstract class AbstractMessageListener implements MessageListener {

    public static final Logger logger = LoggerFactory.getLogger(AbstractMessageListener.class);

    private WorkflowBeanService workflowBeanService;

    public AbstractMessageListener() {
        super();
    }

    protected SequencerRun getSequencerRun(WorkflowEntity workflowEntity) throws WorkflowException {
        logger.debug("ENTERING getSequencerRun(WorkflowEntity)");
        SequencerRun sequencerRun = null;

        SequencerRunDAO sequencerRunDAO = workflowBeanService.getMaPSeqDAOBean().getSequencerRunDAO();

        if (workflowEntity.getGuid() != null) {
            try {
                sequencerRun = sequencerRunDAO.findById(workflowEntity.getGuid());
            } catch (MaPSeqDAOException e) {
                e.printStackTrace();
            }
        }

        if (sequencerRun == null) {
            throw new WorkflowException("No SequencerRun found");
        }

        if (workflowEntity.getAttributes() != null && workflowEntity.getAttributes().size() > 0) {
            sequencerRun.setAttributes(parseAttributes(sequencerRun.getAttributes(), workflowEntity.getAttributes()));
            try {
                sequencerRunDAO.save(sequencerRun);
            } catch (MaPSeqDAOException e) {
                e.printStackTrace();
            }
        }

        logger.debug("Found SequencerRun: {}", sequencerRun.toString());
        return sequencerRun;
    }

    protected HTSFSample getHTSFSample(WorkflowEntity workflowEntity) throws WorkflowException {
        logger.debug("ENTERING getHTSFSample(WorkflowEntity)");
        HTSFSample htsfSample = null;

        HTSFSampleDAO htsfSampleDAO = workflowBeanService.getMaPSeqDAOBean().getHTSFSampleDAO();

        if (workflowEntity.getGuid() != null) {
            try {
                htsfSample = htsfSampleDAO.findById(workflowEntity.getGuid());
            } catch (MaPSeqDAOException e) {
                e.printStackTrace();
            }
        }

        if (htsfSample == null) {
            throw new WorkflowException("No HTSFSample found");
        }

        if (workflowEntity.getAttributes() != null && workflowEntity.getAttributes().size() > 0) {
            htsfSample.setAttributes(parseAttributes(htsfSample.getAttributes(), workflowEntity.getAttributes()));
            try {
                htsfSampleDAO.save(htsfSample);
            } catch (MaPSeqDAOException e) {
                e.printStackTrace();
            }
        }

        logger.debug("Found HTSFSample: {}", htsfSample.toString());
        return htsfSample;
    }

    protected WorkflowRun getWorkflowRun(Workflow workflow, WorkflowEntity workflowEntity, Account account)
            throws WorkflowException {
        logger.debug("ENTERING getWorkflowRun(Workflow, JSONObject, Account)");

        WorkflowRun workflowRun = null;
        WorkflowRunDAO workflowRunDAO = workflowBeanService.getMaPSeqDAOBean().getWorkflowRunDAO();

        if (StringUtils.isNotEmpty(workflowEntity.getName())) {

            workflowRun = new WorkflowRun();
            workflowRun.setStatus(WorkflowRunStatusType.PENDING);
            workflowRun.setCreator(account);
            workflowRun.setName(workflowEntity.getName());
            workflowRun.setWorkflow(workflow);

            if (workflowEntity.getAttributes() != null && workflowEntity.getAttributes().size() > 0) {
                workflowRun.setAttributes(parseAttributes(workflowRun.getAttributes(), workflowEntity.getAttributes()));
                try {
                    Long id = workflowRunDAO.save(workflowRun);
                    workflowRun.setId(id);
                } catch (MaPSeqDAOException e) {
                    e.printStackTrace();
                }
            }

        }

        if (workflowRun != null) {
            logger.debug("WorkflowRun: {}", workflowRun.toString());
        }

        return workflowRun;
    }

    protected Platform getPlatform(WorkflowEntity workflowEntity) throws WorkflowException {
        logger.debug("ENTERING getPlatform(WorkflowEntity)");
        Platform platform = null;
        PlatformDAO platformDAO = workflowBeanService.getMaPSeqDAOBean().getPlatformDAO();

        if (workflowEntity.getGuid() != null) {
            try {
                platform = platformDAO.findById(workflowEntity.getGuid());
            } catch (MaPSeqDAOException e) {
                e.printStackTrace();
            }
        }

        if (platform == null) {
            throw new WorkflowException("No Platform found");
        }
        logger.debug("Found Platform: {}", platform.toString());
        return platform;
    }

    private Set<EntityAttribute> parseAttributes(Set<EntityAttribute> attributeSet,
            List<WorkflowEntityAttribute> workflowEntityAttributes) {

        Set<String> attributeNameSet = new HashSet<String>();
        for (EntityAttribute attribute : attributeSet) {
            attributeNameSet.add(attribute.getName());
        }

        for (WorkflowEntityAttribute workflowEntityAttribute : workflowEntityAttributes) {

            if (!attributeNameSet.contains(workflowEntityAttribute.getName())) {
                EntityAttribute attribute = new EntityAttribute(workflowEntityAttribute.getName(),
                        workflowEntityAttribute.getValue());
                attributeSet.add(attribute);
            } else {
                for (EntityAttribute attribute : attributeSet) {
                    if (workflowEntityAttribute.getName().equals(attribute.getName())) {
                        attribute.setValue(workflowEntityAttribute.getValue());
                        break;
                    }
                }
            }

        }
        return attributeSet;
    }

    public WorkflowBeanService getWorkflowBeanService() {
        return workflowBeanService;
    }

    public void setWorkflowBeanService(WorkflowBeanService workflowBeanService) {
        this.workflowBeanService = workflowBeanService;
    }

}
