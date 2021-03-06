package edu.unc.mapseq.workflow.core;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jms.MessageListener;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.AttributeDAO;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.WorkflowRunDAO;
import edu.unc.mapseq.dao.model.Attribute;
import edu.unc.mapseq.dao.model.Workflow;
import edu.unc.mapseq.dao.model.WorkflowRun;
import edu.unc.mapseq.workflow.WorkflowBeanService;
import edu.unc.mapseq.workflow.WorkflowException;
import edu.unc.mapseq.workflow.model.WorkflowAttribute;
import edu.unc.mapseq.workflow.model.WorkflowEntity;
import edu.unc.mapseq.workflow.model.WorkflowMessage;

public abstract class AbstractMessageListener implements MessageListener {

    private static final Logger logger = LoggerFactory.getLogger(AbstractMessageListener.class);

    private WorkflowBeanService workflowBeanService;

    private String workflowName;

    public AbstractMessageListener() {
        super();
    }

    protected WorkflowRun getWorkflowRun(Workflow workflow, WorkflowEntity workflowEntity) throws WorkflowException {
        logger.debug("ENTERING getWorkflowRun(Workflow, WorkflowEntity)");
        WorkflowRun workflowRun = null;
        if (StringUtils.isNotEmpty(workflowEntity.getName())) {
            workflowRun = new WorkflowRun(workflowEntity.getName());
            workflowRun.setWorkflow(workflow);
            Set<Attribute> attributes = parseAttributes(workflowRun.getAttributes(), workflowEntity.getAttributes());
            workflowRun.setAttributes(attributes);
        }
        return workflowRun;
    }

    protected Set<Attribute> parseAttributes(Set<Attribute> attributeSet, List<WorkflowAttribute> workflowAttributes) {
        logger.debug("ENTERING parseAttributes(Set<Attribute>, List<WorkflowAttribute>)");

        AttributeDAO attributeDAO = getWorkflowBeanService().getMaPSeqDAOBeanService().getAttributeDAO();

        Set<String> attributeNameSet = new HashSet<String>();
        for (Attribute attribute : attributeSet) {
            attributeNameSet.add(attribute.getName());
        }

        if (CollectionUtils.isEmpty(workflowAttributes)) {
            return attributeSet;
        }

        for (WorkflowAttribute workflowEntityAttribute : workflowAttributes) {

            try {
                if (!attributeNameSet.contains(workflowEntityAttribute.getName())) {
                    Attribute attribute = new Attribute(workflowEntityAttribute.getName(), workflowEntityAttribute.getValue());
                    attribute.setId(workflowBeanService.getMaPSeqDAOBeanService().getAttributeDAO().save(attribute));
                    attributeSet.add(attribute);
                } else {
                    for (Attribute attribute : attributeSet) {
                        if (workflowEntityAttribute.getName().equals(attribute.getName())) {
                            attribute.setValue(workflowEntityAttribute.getValue());
                            attributeDAO.save(attribute);
                            break;
                        }
                    }
                }
            } catch (MaPSeqDAOException e) {
                logger.error("Error", e);
            }

        }
        return attributeSet;
    }

    protected WorkflowRun createWorkflowRun(WorkflowMessage workflowMessage, Workflow workflow) throws WorkflowException {
        logger.debug("ENTERING createWorkflowRun(WorkflowMessage, Workflow)");

        WorkflowRun workflowRun = null;

        for (WorkflowEntity entity : workflowMessage.getEntities()) {
            if (StringUtils.isNotEmpty(entity.getEntityType()) && WorkflowRun.class.getSimpleName().equals(entity.getEntityType())) {
                workflowRun = getWorkflowRun(workflow, entity);
            }
        }

        if (workflowRun == null) {
            logger.warn("WorkflowRun is null...not running anything");
            throw new WorkflowException("WorkflowRun is null...not running anything");
        }

        WorkflowRunDAO workflowRunDAO = getWorkflowBeanService().getMaPSeqDAOBeanService().getWorkflowRunDAO();
        try {
            workflowRun.setId(workflowRunDAO.save(workflowRun));
            logger.debug("WorkflowRun: {}", workflowRun.toString());
        } catch (MaPSeqDAOException e) {
            throw new WorkflowException(e);
        }

        return workflowRun;
    }

    public WorkflowBeanService getWorkflowBeanService() {
        return workflowBeanService;
    }

    public void setWorkflowBeanService(WorkflowBeanService workflowBeanService) {
        this.workflowBeanService = workflowBeanService;
    }

    public String getWorkflowName() {
        return workflowName;
    }

    public void setWorkflowName(String workflowName) {
        this.workflowName = workflowName;
    }

}
