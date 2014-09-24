package edu.unc.mapseq.ws.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.SampleDAO;
import edu.unc.mapseq.dao.model.Sample;
import edu.unc.mapseq.ws.SampleService;

public class SampleServiceImpl implements SampleService {

    private final Logger logger = LoggerFactory.getLogger(SampleServiceImpl.class);

    private SampleDAO sampleDAO;

    public SampleServiceImpl() {
        super();
    }

    @Override
    public Sample findById(Long id) {
        logger.debug("ENTERING findById(Long)");
        Sample sample = null;
        if (id == null) {
            logger.warn("id is null");
            return sample;
        }
        try {
            sample = sampleDAO.findById(id);
            if (sample != null) {
                logger.debug(sample.toString());
            }
        } catch (MaPSeqDAOException e) {
            logger.error("Error", e);
        }
        return sample;
    }

    @Override
    public Long save(Sample sample) {
        logger.debug("ENTERING save(Sample)");
        try {
            sampleDAO.save(sample);
        } catch (MaPSeqDAOException e) {
            logger.error(e.getMessage(), e);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return sample.getId();
    }

    @Override
    public List<Sample> findByCreatedDateRange(String startDate, String endDate) {
        logger.debug("ENTERING findByCreatedDateRange(String, String)");
        List<Sample> ret = new ArrayList<Sample>();

        if (StringUtils.isEmpty(startDate)) {
            logger.warn("startDate is empty");
            return ret;
        }
        if (StringUtils.isEmpty(endDate)) {
            logger.warn("endDate is empty");
            return ret;
        }
        try {
            Date parsedStartDate = DateUtils.parseDate(startDate,
                    new String[] { DateFormatUtils.ISO_DATE_FORMAT.getPattern() });
            Date parsedEndDate = DateUtils.parseDate(endDate,
                    new String[] { DateFormatUtils.ISO_DATE_FORMAT.getPattern() });
            ret.addAll(sampleDAO.findByCreatedDateRange(parsedStartDate, parsedEndDate));
        } catch (ParseException | MaPSeqDAOException e) {
            logger.error("MaPSeqDAOException", e);
        }
        return ret;
    }

    @Override
    public List<Sample> findByFlowcellId(Long flowcellId) {
        logger.debug("ENTERING findByFlowcellId(Long)");
        List<Sample> ret = new ArrayList<Sample>();
        if (flowcellId == null) {
            logger.warn("flowcellId is null");
            return ret;
        }
        try {
            ret.addAll(sampleDAO.findByFlowcellId(flowcellId));
        } catch (MaPSeqDAOException e) {
            logger.error("Error", e);
        }
        return ret;
    }

    @Override
    public List<Sample> findByName(String name) {
        logger.debug("ENTERING findByName(String)");
        List<Sample> ret = new ArrayList<Sample>();
        if (StringUtils.isEmpty(name)) {
            logger.warn("name is empty");
            return ret;
        }
        try {
            ret.addAll(sampleDAO.findByName(name));
        } catch (MaPSeqDAOException e) {
            logger.error("Error", e);
        }
        return ret;
    }

    @Override
    public List<Long> findSampleIdListByFlowcellId(Long flowcellId) {
        logger.debug("ENTERING findSampleIdListByFlowcellId(Long)");
        List<Long> ret = new ArrayList<Long>();
        if (flowcellId == null) {
            logger.warn("flowcellId is null");
            return ret;
        }
        try {
            List<Sample> sampleList = sampleDAO.findByFlowcellId(flowcellId);
            if (sampleList != null) {
                for (Sample sample : sampleList) {
                    ret.add(sample.getId());
                }
            }
        } catch (MaPSeqDAOException e) {
            logger.error("Error", e);
        }
        return ret;
    }

    @Override
    public List<Sample> findByNameAndFlowcellId(String name, Long flowcellId) {
        logger.debug("ENTERING findByNameAndFlowcellId(Long, String)");
        List<Sample> ret = new ArrayList<Sample>();
        if (StringUtils.isEmpty(name)) {
            logger.warn("sampleName is empty");
            return ret;
        }
        if (flowcellId == null) {
            logger.warn("flowcellId is null");
            return ret;
        }
        try {
            ret.addAll(sampleDAO.findByNameAndFlowcellId(name, flowcellId));
        } catch (MaPSeqDAOException e) {
            logger.error("Error", e);
        }
        return ret;
    }

    @Override
    public List<Sample> findByWorkflowRunId(Long workflowRunId) {
        logger.debug("ENTERING findByWorkflowRunId(Long)");
        List<Sample> ret = new ArrayList<Sample>();
        if (workflowRunId == null) {
            logger.warn("workflowRunId is null");
            return ret;
        }
        try {
            ret.addAll(sampleDAO.findByWorkflowRunId(workflowRunId));
        } catch (MaPSeqDAOException e) {
            logger.error("Error", e);
        }
        return ret;
    }

    public SampleDAO getSampleDAO() {
        return sampleDAO;
    }

    public void setSampleDAO(SampleDAO sampleDAO) {
        this.sampleDAO = sampleDAO;
    }

}
