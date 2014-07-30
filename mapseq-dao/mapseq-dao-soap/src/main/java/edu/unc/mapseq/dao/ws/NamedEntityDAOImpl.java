package edu.unc.mapseq.dao.ws;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import edu.unc.mapseq.dao.BaseEntityDAO;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.model.FileData;
import edu.unc.mapseq.dao.model.Persistable;

public abstract class BaseEntityDAOImpl<T extends Persistable, ID extends Serializable> extends BaseDAOImpl<T, ID>
        implements BaseEntityDAO<T, ID> {

    public BaseEntityDAOImpl() {
    }

    public BaseEntityDAOImpl(Class<T> persistentClass) {
        super(persistentClass);
    }

    @Override
    public List<T> findFileDataById(Long id) {
        return null;
    }

    @Override
    public List<FileData> findByExample(Long id, FileData fileData) throws MaPSeqDAOException {
        return null;
    }

    @Override
    public List<T> findByCreator(Long id) throws MaPSeqDAOException {
        return null;
    }

    @Override
    public List<T> findByCreatorAndCreationDateRange(Long id, Date startDate, Date endDate) throws MaPSeqDAOException {
        return null;
    }

}
