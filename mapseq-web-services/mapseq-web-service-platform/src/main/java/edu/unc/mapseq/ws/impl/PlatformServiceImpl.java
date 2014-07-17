package edu.unc.mapseq.ws.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.PlatformDAO;
import edu.unc.mapseq.dao.model.Platform;
import edu.unc.mapseq.ws.PlatformService;

public class PlatformServiceImpl implements PlatformService {

    private final Logger logger = LoggerFactory.getLogger(PlatformServiceImpl.class);

    private PlatformDAO platformDAO;

    @Override
    public Platform findById(Long id) {
        logger.debug("ENTERING findById(Long)");
        try {
            return this.platformDAO.findById(id);
        } catch (MaPSeqDAOException e) {
            logger.error("Error", e);
        }
        return null;
    }

    @Override
    public List<Platform> findAll() {
        logger.debug("ENTERING findAll()");
        List<Platform> results = new ArrayList<Platform>();
        try {
            results.addAll(this.platformDAO.findAll());
        } catch (MaPSeqDAOException e) {
            logger.error("Error", e);
        }
        return results;
    }

    @Override
    public Platform findByInstrumentAndModel(String instrument, String model) {
        logger.debug("ENTERING findByInstrumentAndModel(String, String)");
        try {
            return this.platformDAO.findByInstrumentAndModel(instrument, model);
        } catch (MaPSeqDAOException e) {
            logger.error("Error", e);
        }
        return null;
    }

    @Override
    public List<Platform> findByInstrument(String instrument) {
        logger.debug("ENTERING findByName(String)");
        try {
            return this.platformDAO.findByInstrument(instrument);
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public PlatformDAO getPlatformDAO() {
        return platformDAO;
    }

    public void setPlatformDAO(PlatformDAO platformDAO) {
        this.platformDAO = platformDAO;
    }

}
