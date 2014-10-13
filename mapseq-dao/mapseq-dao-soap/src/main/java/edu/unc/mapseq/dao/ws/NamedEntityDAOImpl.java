package edu.unc.mapseq.dao.ws;

import java.io.Serializable;
import java.util.List;

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

}
