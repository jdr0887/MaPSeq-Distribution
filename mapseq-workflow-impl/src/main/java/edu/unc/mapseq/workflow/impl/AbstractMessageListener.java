package edu.unc.mapseq.workflow.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jms.MessageListener;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.FlowcellDAO;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.SampleDAO;
import edu.unc.mapseq.dao.WorkflowRunDAO;
import edu.unc.mapseq.dao.model.Attribute;
import edu.unc.mapseq.dao.model.Flowcell;
import edu.unc.mapseq.dao.model.Sample;
import edu.unc.mapseq.dao.model.Workflow;
import edu.unc.mapseq.dao.model.WorkflowRun;
import edu.unc.mapseq.workflow.WorkflowBeanService;
import edu.unc.mapseq.workflow.WorkflowException;
import edu.unc.mapseq.workflow.model.WorkflowEntity;
import edu.unc.mapseq.workflow.model.WorkflowEntityAttribute;

public abstract class AbstractMessageListener implements MessageListener {

    public static final Logger logger = LoggerFactory.getLogger(AbstractMessageListener.class);

    private WorkflowBeanService workflowBeanService;

    public AbstractMessageListener() {
        super();
    }

    protected Flowcell getFlowcell(WorkflowEntity workflowEntity) throws WorkflowException {
        logger.debug("ENTERING getFlowcell(WorkflowEntity)");
        Flowcell flowcell = null;

        FlowcellDAO flowcellDAO = workflowBeanService.getMaPSeqDAOBean().getFlowcellDAO();

        if (workflowEntity.getGuid() != null) {
            try {
                flowcell = flowcellDAO.findById(workflowEntity.getGuid());
            } catch (MaPSeqDAOException e) {
                e.printStackTrace();
            }
        }

        if (flowcell == null) {
            throw new WorkflowException("No Flowcell found");
        }

        if (workflowEntity.getAttributes() != null && workflowEntity.getAttributes().size() > 0) {
            flowcell.setAttributes(parseAttributes(flowcell.getAttributes(), workflowEntity.getAttributes()));
            try {
                flowcellDAO.save(flowcell);
            } catch (MaPSeqDAOException e) {
                e.printStackTrace();
            }
        }

        logger.debug("Found Flowcell: {}", flowcell.toString());
        return flowcell;
    }

    protected Sample getSample(WorkflowEntity workflowEntity) throws WorkflowException {
        logger.debug("ENTERING getSample(WorkflowEntity)");
        Sample sample = null;

        SampleDAO sampleDAO = workflowBeanService.getMaPSeqDAOBean().getSampleDAO();

        if (workflowEntity.getGuid() != null) {
            try {
                sample = sampleDAO.findById(workflowEntity.getGuid());
            } catch (MaPSeqDAOException e) {
                e.printStackTrace();
            }
        }

        if (sample == null) {
            throw new WorkflowException("No Sample found");
        }

        if (workflowEntity.getAttributes() != null && workflowEntity.getAttributes().size() > 0) {
            sample.setAttributes(parseAttributes(sample.getAttributes(), workflowEntity.getAttributes()));
            try {
                sampleDAO.save(sample);
            } catch (MaPSeqDAOException e) {
                e.printStackTrace();
            }
        }

        logger.debug("Found Sample: {}", sample.toString());
        return sample;
    }

    protected WorkflowRun getWorkflowRun(Workflow workflow, WorkflowEntity workflowEntity) throws WorkflowException {
        logger.debug("ENTERING getWorkflowRun(Workflow, JSONObject)");

        WorkflowRun workflowRun = null;
        WorkflowRunDAO workflowRunDAO = workflowBeanService.getMaPSeqDAOBean().getWorkflowRunDAO();

        if (StringUtils.isNotEmpty(workflowEntity.getName())) {

            workflowRun = new WorkflowRun();
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

    private Set<Attribute> parseAttributes(Set<Attribute> attributeSet,
            List<WorkflowEntityAttribute> workflowEntityAttributes) {

        Set<String> attributeNameSet = new HashSet<String>();
        for (Attribute attribute : attributeSet) {
            attributeNameSet.add(attribute.getName());
        }

        for (WorkflowEntityAttribute workflowEntityAttribute : workflowEntityAttributes) {

            if (!attributeNameSet.contains(workflowEntityAttribute.getName())) {
                Attribute attribute = new Attribute(workflowEntityAttribute.getName(),
                        workflowEntityAttribute.getValue());
                attributeSet.add(attribute);
            } else {
                for (Attribute attribute : attributeSet) {
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
