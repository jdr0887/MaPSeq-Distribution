package edu.unc.mapseq.workflow.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jms.MessageListener;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.AttributeDAO;
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
import edu.unc.mapseq.workflow.model.WorkflowAttribute;
import edu.unc.mapseq.workflow.model.WorkflowEntity;
import edu.unc.mapseq.workflow.model.WorkflowMessage;

public abstract class AbstractMessageListener implements MessageListener {

    public static final Logger logger = LoggerFactory.getLogger(AbstractMessageListener.class);

    private WorkflowBeanService workflowBeanService;

    public AbstractMessageListener() {
        super();
    }

    protected Flowcell getFlowcell(WorkflowEntity workflowEntity) throws WorkflowException {
        logger.debug("ENTERING getFlowcell(WorkflowEntity)");
        FlowcellDAO flowcellDAO = workflowBeanService.getMaPSeqDAOBean().getFlowcellDAO();

        Flowcell flowcell = null;
        if (workflowEntity.getId() != null) {
            try {
                flowcell = flowcellDAO.findById(workflowEntity.getId());
            } catch (MaPSeqDAOException e) {
                e.printStackTrace();
            }
        }

        if (flowcell == null) {
            throw new WorkflowException("No Flowcell found");
        }

        if (workflowEntity.getAttributes() != null && !workflowEntity.getAttributes().isEmpty()) {
            for (Attribute attribute : parseAttributes(flowcell.getAttributes(), workflowEntity.getAttributes())) {
                try {
                    flowcellDAO.addAttribute(attribute.getId(), flowcell.getId());
                } catch (MaPSeqDAOException e) {
                    e.printStackTrace();
                }
            }
        }

        logger.debug("Found Flowcell: {}", flowcell.toString());
        return flowcell;
    }

    protected Sample getSample(WorkflowEntity workflowEntity) throws WorkflowException {
        logger.debug("ENTERING getSample(WorkflowEntity)");
        SampleDAO sampleDAO = workflowBeanService.getMaPSeqDAOBean().getSampleDAO();

        Sample sample = null;
        if (workflowEntity.getId() != null) {
            try {
                sample = sampleDAO.findById(workflowEntity.getId());
            } catch (MaPSeqDAOException e) {
                e.printStackTrace();
            }
        }

        if (sample == null) {
            throw new WorkflowException("No Sample found");
        }

        if (workflowEntity.getAttributes() != null && !workflowEntity.getAttributes().isEmpty()) {
            for (Attribute attribute : parseAttributes(sample.getAttributes(), workflowEntity.getAttributes())) {
                try {
                    sampleDAO.addAttribute(attribute.getId(), sample.getId());
                } catch (MaPSeqDAOException e) {
                    e.printStackTrace();
                }
            }
        }

        logger.debug("Found Sample: {}", sample.toString());
        return sample;
    }

    protected WorkflowRun getWorkflowRun(Workflow workflow, WorkflowEntity workflowEntity) throws WorkflowException {
        logger.debug("ENTERING getWorkflowRun(Workflow, JSONObject)");

        WorkflowRunDAO workflowRunDAO = workflowBeanService.getMaPSeqDAOBean().getWorkflowRunDAO();

        WorkflowRun workflowRun = null;
        if (StringUtils.isNotEmpty(workflowEntity.getName())) {

            workflowRun = new WorkflowRun();
            workflowRun.setName(workflowEntity.getName());
            workflowRun.setWorkflow(workflow);

            if (workflowEntity.getAttributes() != null && !workflowEntity.getAttributes().isEmpty()) {
                for (Attribute attribute : parseAttributes(workflowRun.getAttributes(), workflowEntity.getAttributes())) {
                    try {
                        workflowRunDAO.addAttribute(attribute.getId(), workflowRun.getId());
                    } catch (MaPSeqDAOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }

        if (workflowRun != null) {
            logger.debug("WorkflowRun: {}", workflowRun.toString());
        }

        return workflowRun;
    }

    private Set<Attribute> parseAttributes(Set<Attribute> attributeSet, List<WorkflowAttribute> workflowAttributes) {

        AttributeDAO attributeDAO = getWorkflowBeanService().getMaPSeqDAOBean().getAttributeDAO();

        Set<String> attributeNameSet = new HashSet<String>();
        for (Attribute attribute : attributeSet) {
            attributeNameSet.add(attribute.getName());
        }

        for (WorkflowAttribute workflowEntityAttribute : workflowAttributes) {

            try {
                if (!attributeNameSet.contains(workflowEntityAttribute.getName())) {
                    Attribute attribute = new Attribute(workflowEntityAttribute.getName(),
                            workflowEntityAttribute.getValue());
                    attribute.setId(attributeDAO.save(attribute));
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

    protected WorkflowRun createWorkflowRun(WorkflowMessage workflowMessage, Workflow workflow)
            throws WorkflowException {
        logger.debug("ENTERING createWorkflowRun(WorkflowMessage, Workflow)");

        Set<Flowcell> flowcellSet = new HashSet<Flowcell>();
        Set<Sample> sampleSet = new HashSet<Sample>();
        WorkflowRun workflowRun = null;

        for (WorkflowEntity entity : workflowMessage.getEntities()) {
            if (StringUtils.isNotEmpty(entity.getEntityType())
                    && Flowcell.class.getSimpleName().equals(entity.getEntityType())) {
                Flowcell flowcell = getFlowcell(entity);
                flowcellSet.add(flowcell);
            }
        }

        for (WorkflowEntity entity : workflowMessage.getEntities()) {
            if (StringUtils.isNotEmpty(entity.getEntityType())
                    && Sample.class.getSimpleName().equals(entity.getEntityType())) {
                Sample sample = getSample(entity);
                sampleSet.add(sample);
            }
        }

        if (flowcellSet.isEmpty() && sampleSet.isEmpty()) {
            logger.warn("flowcellSet & sampleSet are both empty...not running anything");
            throw new WorkflowException("flowcellSet & sampleSet are both empty...not running anything");
        }

        for (WorkflowEntity entity : workflowMessage.getEntities()) {
            if (StringUtils.isNotEmpty(entity.getEntityType())
                    && WorkflowRun.class.getSimpleName().equals(entity.getEntityType())) {
                workflowRun = getWorkflowRun(workflow, entity);
            }
        }

        if (workflowRun == null) {
            logger.warn("WorkflowRun is null...not running anything");
            throw new WorkflowException("WorkflowRun is null...not running anything");
        }

        if (!flowcellSet.isEmpty()) {
            workflowRun.setFlowcells(flowcellSet);
        }

        if (!sampleSet.isEmpty()) {
            workflowRun.setSamples(sampleSet);
        }

        return workflowRun;
    }

    public WorkflowBeanService getWorkflowBeanService() {
        return workflowBeanService;
    }

    public void setWorkflowBeanService(WorkflowBeanService workflowBeanService) {
        this.workflowBeanService = workflowBeanService;
    }

}
