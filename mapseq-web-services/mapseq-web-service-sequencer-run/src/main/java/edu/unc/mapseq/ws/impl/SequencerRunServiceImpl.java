package edu.unc.mapseq.ws.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.cxf.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.SequencerRunDAO;
import edu.unc.mapseq.dao.model.SequencerRun;
import edu.unc.mapseq.ws.SequencerRunService;

public class SequencerRunServiceImpl implements SequencerRunService {

    private final Logger logger = LoggerFactory.getLogger(SequencerRunServiceImpl.class);

    private SequencerRunDAO sequencerRunDAO;

    public SequencerRunServiceImpl() {
        super();
    }

    @Override
    public SequencerRun findById(Long id) {
        logger.debug("ENTERING findById(Long)");
        SequencerRun sequencerRun = null;
        try {
            sequencerRun = sequencerRunDAO.findById(id);
            logger.debug(sequencerRun.toString());
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }
        return sequencerRun;
    }

    @Override
    public List<SequencerRun> findByAccountId(Long id) {
        logger.debug("ENTERING findByAccountId(Long)");
        List<SequencerRun> ret = new ArrayList<SequencerRun>();
        try {
            ret.addAll(sequencerRunDAO.findByAccountId(id));
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    @Override
    public List<SequencerRun> findByCreationDateRange(String startDate, String endDate) {
        logger.debug("ENTERING findByDateRange");
        List<SequencerRun> ret = new ArrayList<SequencerRun>();

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
            ret.addAll(sequencerRunDAO.findByCreationDateRange(parsedStartDate, parsedEndDate));
        } catch (ParseException | MaPSeqDAOException e) {
            logger.error("MaPSeqDAOException", e);
        }
        return ret;
    }

    @Override
    public List<SequencerRun> findAll() {
        logger.debug("ENTERING findAll(Long)");
        List<SequencerRun> ret = new ArrayList<SequencerRun>();
        try {
            ret.addAll(sequencerRunDAO.findAll());
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    @Override
    public Long save(SequencerRun sequencerRun) {
        logger.debug("ENTERING save(SequencerRun)");
        try {
            return sequencerRunDAO.save(sequencerRun);
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<SequencerRun> findByStudyName(String name) {
        logger.debug("ENTERING findByStudyName(String)");
        List<SequencerRun> ret = new ArrayList<SequencerRun>();
        try {
            ret.addAll(sequencerRunDAO.findByStudyName(name));
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    @Override
    public List<SequencerRun> findByName(String flowcellName) {
        logger.debug("ENTERING findByName(String)");
        try {
            List<SequencerRun> ret = sequencerRunDAO.findByName(flowcellName);
            return ret;
        } catch (MaPSeqDAOException e) {
        }
        return null;
    }

    public SequencerRunDAO getSequencerRunDAO() {
        return sequencerRunDAO;
    }

    public void setSequencerRunDAO(SequencerRunDAO sequencerRunDAO) {
        this.sequencerRunDAO = sequencerRunDAO;
    }

}
