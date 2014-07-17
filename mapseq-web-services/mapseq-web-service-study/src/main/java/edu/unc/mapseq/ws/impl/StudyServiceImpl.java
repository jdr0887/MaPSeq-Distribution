package edu.unc.mapseq.ws.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.cxf.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.StudyDAO;
import edu.unc.mapseq.dao.model.Study;
import edu.unc.mapseq.ws.StudyService;

public class StudyServiceImpl implements StudyService {

    private final Logger logger = LoggerFactory.getLogger(StudyServiceImpl.class);

    private StudyDAO studyDAO;

    @Override
    public Study findById(Long id) {
        logger.debug("ENTERING findById(Long)");
        Study study = null;
        if (id == null) {
            logger.warn("id is null");
            return study;
        }
        try {
            study = studyDAO.findById(id);
        } catch (MaPSeqDAOException e) {
            logger.error("Error", e);
        }
        return study;
    }

    @Override
    public Long save(Study study) {
        logger.debug("ENTERING save(Study)");
        Long id = null;
        try {
            id = studyDAO.save(study);
        } catch (MaPSeqDAOException e) {
            logger.error("Error", e);
        }
        return id;
    }

    @Override
    public List<Study> findAll() {
        logger.debug("ENTERING findAll()");
        List<Study> ret = new ArrayList<>();
        try {
            ret.addAll(studyDAO.findAll());
        } catch (MaPSeqDAOException e) {
            logger.error("Error", e);
        }
        return ret;
    }

    @Override
    public List<Study> findByName(String name) {
        logger.debug("ENTERING findByName(String)");
        List<Study> ret = new ArrayList<>();
        if (StringUtils.isEmpty(name)) {
            logger.warn("name is emtpy");
            return ret;
        }
        try {
            ret.addAll(studyDAO.findByName(name));
        } catch (MaPSeqDAOException e) {
            logger.error("Error", e);
        }
        return ret;
    }

    @Override
    public Study findByHTSFSampleId(Long htsfSampleId) {
        logger.debug("ENTERING findByHTSFSampleId(Long)");
        Study study = null;
        if (htsfSampleId == null) {
            logger.warn("htsfSampleId is null");
            return study;
        }
        try {
            study = studyDAO.findByHTSFSampleId(htsfSampleId);
        } catch (MaPSeqDAOException e) {
            logger.error("Error", e);
        }
        return study;
    }

    public StudyDAO getStudyDAO() {
        return studyDAO;
    }

    public void setStudyDAO(StudyDAO studyDAO) {
        this.studyDAO = studyDAO;
    }

}
