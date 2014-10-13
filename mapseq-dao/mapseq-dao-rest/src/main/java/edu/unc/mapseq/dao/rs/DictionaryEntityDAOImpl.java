package edu.unc.mapseq.dao.rs;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import edu.unc.mapseq.dao.DictionaryEntityDAO;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.model.Persistable;

public abstract class DictionaryEntityDAOImpl<T extends Persistable, ID extends Serializable> extends
        BaseDAOImpl<T, ID> implements DictionaryEntityDAO<T, ID> {

    public DictionaryEntityDAOImpl() {
        super();
    }

    public DictionaryEntityDAOImpl(Class<T> persistentClass) {
        super(persistentClass);
    }

    @Override
    public List<T> findByCreatedDateRange(Date startDate, Date endDate) throws MaPSeqDAOException {
        return null;
    }

}
