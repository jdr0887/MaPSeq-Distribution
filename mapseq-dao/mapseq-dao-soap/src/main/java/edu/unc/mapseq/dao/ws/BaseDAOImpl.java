package edu.unc.mapseq.dao.ws;

import java.io.Serializable;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;

import edu.unc.mapseq.config.MaPSeqConfigurationService;
import edu.unc.mapseq.dao.BaseDAO;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.model.Persistable;

public abstract class BaseDAOImpl<T extends Persistable, ID extends Serializable> implements BaseDAO<T, ID> {

    private MaPSeqConfigurationService configurationService;

    private Class<T> persistentClass;

    public BaseDAOImpl() {
        super();
    }

    public BaseDAOImpl(Class<T> persistentClass) {
        super();
        this.persistentClass = persistentClass;
    }

    @Override
    public void delete(List<T> idList) throws MaPSeqDAOException {
    }

    @Override
    public void delete(T entity) throws MaPSeqDAOException {
    }

    public Class<T> getPersistentClass() {
        return persistentClass;
    }

    public void setPersistentClass(Class<T> persistentClass) {
        this.persistentClass = persistentClass;
    }

    public QName getServiceQName() {
        return new QName("http://ws.mapseq.unc.edu", String.format("%sService", getPersistentClass().getSimpleName()));
    }

    public QName getPortQName() {
        return new QName("http://ws.mapseq.unc.edu", String.format("%sPort", getPersistentClass().getSimpleName()));
    }

    public Service getService() {
        Service service = Service.create(getServiceQName());
        String host = getConfigurationService().getWebServiceHost("localhost");
        service.addPort(getPortQName(), SOAPBinding.SOAP11HTTP_MTOM_BINDING,
                String.format("http://%s:%d/cxf/%sService", host, 8181, getPersistentClass().getSimpleName()));
        return service;
    }

    public MaPSeqConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(MaPSeqConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

}
