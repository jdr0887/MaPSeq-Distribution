package edu.unc.mapseq.dao.rs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

import edu.unc.mapseq.config.MaPSeqConfigurationService;
import edu.unc.mapseq.dao.BaseDAO;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.model.Persistable;

public abstract class BaseDAOImpl<T extends Persistable, ID extends Serializable> implements BaseDAO<T, ID> {

    private Class<T> persistentClass;

    private final List<Object> providers = new ArrayList<Object>();

    private MaPSeqConfigurationService configurationService;

    public BaseDAOImpl() {
        super();
        JacksonJaxbJsonProvider provider = new JacksonJaxbJsonProvider();
        ObjectMapper mapper = new ObjectMapper();
        mapper.getDeserializationConfig().useRootWrapping();
        // mapper.enable(MapperFeature.USE_WRAPPER_NAME_AS_PROPERTY_NAME);
        provider.setMapper(mapper);
        providers.add(provider);
    }

    public BaseDAOImpl(Class<T> persistentClass) {
        this();
        this.persistentClass = persistentClass;
    }

    @Override
    public void delete(List<T> idList) throws MaPSeqDAOException {
        // TODO Auto-generated method stub
    }

    @Override
    public void delete(T entity) throws MaPSeqDAOException {
        // TODO Auto-generated method stub

    }

    public Class<T> getPersistentClass() {
        return persistentClass;
    }

    public void setPersistentClass(Class<T> persistentClass) {
        this.persistentClass = persistentClass;
    }

    public List<Object> getProviders() {
        return providers;
    }

    public String getRestServiceURL() {
        return String.format("http://%1$s:%2$d/cxf/%3$s/%3$sService",
                getConfigurationService().getWebServiceHost("localhost"), 8181, getPersistentClass().getSimpleName());
    }

    public MaPSeqConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(MaPSeqConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

}
