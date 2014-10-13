package edu.unc.mapseq.dao.ws;

import java.io.Serializable;

import edu.unc.mapseq.dao.DictionaryEntityDAO;
import edu.unc.mapseq.dao.model.Persistable;

public abstract class DictionaryEntityDAOImpl<T extends Persistable, ID extends Serializable> extends
        BaseDAOImpl<T, ID> implements DictionaryEntityDAO<T, ID> {

    public DictionaryEntityDAOImpl() {
        super();
    }

    public DictionaryEntityDAOImpl(Class<T> persistentClass) {
        super(persistentClass);
    }

}
