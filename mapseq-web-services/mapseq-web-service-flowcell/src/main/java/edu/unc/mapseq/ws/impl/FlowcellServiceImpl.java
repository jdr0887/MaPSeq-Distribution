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

import edu.unc.mapseq.dao.FlowcellDAO;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.model.Flowcell;
import edu.unc.mapseq.ws.FlowcellService;

public class FlowcellServiceImpl implements FlowcellService {

    private final Logger logger = LoggerFactory.getLogger(FlowcellServiceImpl.class);

    private FlowcellDAO flowcellDAO;

    public FlowcellServiceImpl() {
        super();
    }

    @Override
    public Flowcell findById(Long id) {
        logger.debug("ENTERING findById(Long)");
        Flowcell flowcell = null;
        try {
            flowcell = flowcellDAO.findById(id);
            logger.debug(flowcell.toString());
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }
        return flowcell;
    }

    @Override
    public List<Flowcell> findByCreatedDateRange(String startDate, String endDate) {
        logger.debug("ENTERING findByDateRange");
        List<Flowcell> ret = new ArrayList<Flowcell>();

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
            ret.addAll(flowcellDAO.findByCreatedDateRange(parsedStartDate, parsedEndDate));
        } catch (ParseException | MaPSeqDAOException e) {
            logger.error("MaPSeqDAOException", e);
        }
        return ret;
    }

    @Override
    public List<Flowcell> findAll() {
        logger.debug("ENTERING findAll(Long)");
        List<Flowcell> ret = new ArrayList<Flowcell>();
        try {
            ret.addAll(flowcellDAO.findAll());
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    @Override
    public Long save(Flowcell sequencerRun) {
        logger.debug("ENTERING save(SequencerRun)");
        try {
            return flowcellDAO.save(sequencerRun);
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Flowcell> findByStudyName(String name) {
        logger.debug("ENTERING findByStudyName(String)");
        List<Flowcell> ret = new ArrayList<Flowcell>();
        try {
            ret.addAll(flowcellDAO.findByStudyName(name));
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    @Override
    public List<Flowcell> findByName(String flowcellName) {
        logger.debug("ENTERING findByName(String)");
        try {
            List<Flowcell> ret = flowcellDAO.findByName(flowcellName);
            return ret;
        } catch (MaPSeqDAOException e) {
        }
        return null;
    }

    public FlowcellDAO getFlowcellDAO() {
        return flowcellDAO;
    }

    public void setFlowcellDAO(FlowcellDAO flowcellDAO) {
        this.flowcellDAO = flowcellDAO;
    }

}
