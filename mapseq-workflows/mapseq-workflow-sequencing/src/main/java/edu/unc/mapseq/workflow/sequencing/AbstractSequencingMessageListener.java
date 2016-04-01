package edu.unc.mapseq.workflow.sequencing;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.FlowcellDAO;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.SampleDAO;
import edu.unc.mapseq.dao.model.Attribute;
import edu.unc.mapseq.dao.model.Flowcell;
import edu.unc.mapseq.dao.model.Sample;
import edu.unc.mapseq.dao.model.Workflow;
import edu.unc.mapseq.dao.model.WorkflowRun;
import edu.unc.mapseq.workflow.WorkflowException;
import edu.unc.mapseq.workflow.core.AbstractMessageListener;
import edu.unc.mapseq.workflow.model.WorkflowEntity;
import edu.unc.mapseq.workflow.model.WorkflowMessage;

public abstract class AbstractSequencingMessageListener extends AbstractMessageListener {

    private static final Logger logger = LoggerFactory.getLogger(AbstractSequencingMessageListener.class);

    public AbstractSequencingMessageListener() {
        super();
    }

    protected Flowcell getFlowcell(WorkflowEntity workflowEntity) throws WorkflowException {
        logger.debug("ENTERING getFlowcell(WorkflowEntity)");
        FlowcellDAO flowcellDAO = getWorkflowBeanService().getMaPSeqDAOBeanService().getFlowcellDAO();

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

        Set<Attribute> attributes = parseAttributes(flowcell.getAttributes(), workflowEntity.getAttributes());
        flowcell.setAttributes(attributes);
        try {
            flowcellDAO.save(flowcell);
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }

        logger.debug("Found Flowcell: {}", flowcell.toString());
        return flowcell;
    }

    protected Sample getSample(WorkflowEntity workflowEntity) throws WorkflowException {
        logger.debug("ENTERING getSample(WorkflowEntity)");
        SampleDAO sampleDAO = getWorkflowBeanService().getMaPSeqDAOBeanService().getSampleDAO();

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

        Set<Attribute> attributes = parseAttributes(sample.getAttributes(), workflowEntity.getAttributes());
        sample.setAttributes(attributes);
        try {
            sampleDAO.save(sample);
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }

        logger.debug("Found Sample: {}", sample.toString());
        return sample;
    }

    protected WorkflowRun createWorkflowRun(WorkflowMessage workflowMessage, Workflow workflow)
            throws WorkflowException {
        logger.debug("ENTERING createWorkflowRun(WorkflowMessage, Workflow)");

        Set<Flowcell> flowcellSet = new HashSet<Flowcell>();
        WorkflowRun workflowRun = null;

        for (WorkflowEntity entity : workflowMessage.getEntities()) {
            if (StringUtils.isNotEmpty(entity.getEntityType())
                    && Flowcell.class.getSimpleName().equals(entity.getEntityType())) {
                Flowcell flowcell = getFlowcell(entity);
                flowcellSet.add(flowcell);
            }
        }

        Set<Sample> sampleSet = new HashSet<Sample>();
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

}
