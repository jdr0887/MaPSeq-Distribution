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

import edu.unc.mapseq.dao.HTSFSampleDAO;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.model.HTSFSample;
import edu.unc.mapseq.ws.HTSFSampleService;

public class HTSFSampleServiceImpl implements HTSFSampleService {

    private final Logger logger = LoggerFactory.getLogger(HTSFSampleServiceImpl.class);

    private HTSFSampleDAO htsfSampleDAO;

    @Override
    public HTSFSample findById(Long id) {
        logger.debug("ENTERING findById(Long)");
        HTSFSample htsfSample = null;
        if (id == null) {
            logger.warn("id is null");
            return htsfSample;
        }
        try {
            htsfSample = htsfSampleDAO.findById(id);
            if (htsfSample != null) {
                logger.debug(htsfSample.toString());
            }
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }
        return htsfSample;
    }

    @Override
    public Long save(HTSFSample htsfSample) {
        logger.debug("ENTERING save(HTSFSample)");
        try {
            htsfSampleDAO.save(htsfSample);
        } catch (MaPSeqDAOException e) {
            logger.error(e.getMessage(), e);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return htsfSample.getId();
    }

    @Override
    public List<HTSFSample> findByCreationDateRange(String startDate, String endDate) {
        logger.debug("ENTERING findByDateRange");
        List<HTSFSample> ret = new ArrayList<HTSFSample>();

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
            ret.addAll(htsfSampleDAO.findByCreationDateRange(parsedStartDate, parsedEndDate));
        } catch (ParseException | MaPSeqDAOException e) {
            logger.error("MaPSeqDAOException", e);
        }
        return ret;
    }

    @Override
    public List<HTSFSample> findBySequencerRunId(Long sequencerRunId) {
        logger.debug("ENTERING findBySequencerRunId(Long)");
        List<HTSFSample> ret = new ArrayList<HTSFSample>();
        if (sequencerRunId == null) {
            logger.warn("sequencerRunId is null");
            return ret;
        }
        try {
            ret.addAll(htsfSampleDAO.findBySequencerRunId(sequencerRunId));
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    @Override
    public List<HTSFSample> findByName(String name) {
        logger.debug("ENTERING findByName(String)");
        List<HTSFSample> ret = new ArrayList<HTSFSample>();
        if (StringUtils.isEmpty(name)) {
            logger.warn("name is empty");
            return ret;
        }
        try {
            ret.addAll(htsfSampleDAO.findByName(name));
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    @Override
    public List<Long> findHTSFSampleIdListBySequencerRunId(Long sequencerRunId) {
        logger.debug("ENTERING findHTSFSampleIdListBySequencerRunId(Long)");
        List<Long> ret = new ArrayList<Long>();
        if (sequencerRunId == null) {
            logger.warn("sequencerRunId is null");
            return ret;
        }
        try {
            List<HTSFSample> sampleList = htsfSampleDAO.findBySequencerRunId(sequencerRunId);
            if (sampleList != null) {
                for (HTSFSample sample : sampleList) {
                    ret.add(sample.getId());
                }
            }
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    @Override
    public List<HTSFSample> findBySequencerRunIdAndSampleName(Long sequencerRunId, String sampleName) {
        logger.debug("ENTERING findBySequencerRunIdAndSampleName(Long, String)");
        List<HTSFSample> ret = new ArrayList<HTSFSample>();
        if (sequencerRunId == null) {
            logger.warn("sequencerRunId is null");
            return ret;
        }
        if (StringUtils.isEmpty(sampleName)) {
            logger.warn("sampleName is empty");
            return ret;
        }
        try {
            ret.addAll(htsfSampleDAO.findBySequencerRunIdAndSampleName(sequencerRunId, sampleName));
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public HTSFSampleDAO getHtsfSampleDAO() {
        return htsfSampleDAO;
    }

    public void setHtsfSampleDAO(HTSFSampleDAO htsfSampleDAO) {
        this.htsfSampleDAO = htsfSampleDAO;
    }

}
