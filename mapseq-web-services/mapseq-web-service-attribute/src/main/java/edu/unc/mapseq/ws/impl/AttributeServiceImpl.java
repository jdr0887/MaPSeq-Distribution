package edu.unc.mapseq.ws.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.AttributeDAO;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.model.Attribute;
import edu.unc.mapseq.ws.AttributeService;

public class AttributeServiceImpl implements AttributeService {

    private final Logger logger = LoggerFactory.getLogger(AttributeServiceImpl.class);

    private AttributeDAO attributeDAO;

    public AttributeServiceImpl() {
        super();
    }

    @Override
    public Attribute findById(Long id) {
        logger.debug("ENTERING findById(Long)");
        Attribute attribute = null;
        if (id == null) {
            logger.warn("id is null");
            return attribute;
        }
        try {
            attribute = attributeDAO.findById(id);
            if (attribute != null) {
                logger.debug(attribute.toString());
            }
        } catch (MaPSeqDAOException e) {
            logger.error("Error", e);
        }
        return attribute;
    }

    @Override
    public Long save(Attribute entity) {
        logger.debug("ENTERING save(Attribute)");
        try {
            attributeDAO.save(entity);
        } catch (MaPSeqDAOException e) {
            logger.error("Error", e);
        }
        return entity.getId();
    }

    public AttributeDAO getAttributeDAO() {
        return attributeDAO;
    }

    public void setAttributeDAO(AttributeDAO attributeDAO) {
        this.attributeDAO = attributeDAO;
    }

}
