package edu.unc.mapseq.dao.rest;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.NamedEntityDAO;
import edu.unc.mapseq.dao.model.Persistable;

public abstract class NamedEntityDAOImpl<T extends Persistable, ID extends Serializable> extends BaseDAOImpl<T, ID>
        implements NamedEntityDAO<T, ID> {

    public NamedEntityDAOImpl() {
        super();
    }

    public NamedEntityDAOImpl(Class<T> persistentClass) {
        super(persistentClass);
    }

    @Override
    public List<T> findByFileDataId(Long id) {
        return null;
    }

    @Override
    public List<T> findByCreatedDateRange(Date startDate, Date endDate) throws MaPSeqDAOException {
        return null;
    }

}
