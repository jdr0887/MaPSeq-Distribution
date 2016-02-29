package edu.unc.mapseq.dao.rest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import edu.unc.mapseq.dao.FileDataDAO;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.model.FileData;

@Component
public class FileDataDAOImpl extends BaseDAOImpl<FileData, Long> implements FileDataDAO {

    private final Logger logger = LoggerFactory.getLogger(FileDataDAOImpl.class);

    public FileDataDAOImpl() {
        super(FileData.class);
    }

    @Override
    public Long save(FileData entity) throws MaPSeqDAOException {
        logger.debug("ENTERING save(FileData)");
        WebClient client = WebClient.create(getRestServiceURL(), getProviders(), true).type(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
        Response response = client.path("/").post(entity);
        Long id = response.readEntity(Long.class);
        return id;
    }

    @Override
    public FileData findById(Long id) throws MaPSeqDAOException {
        logger.debug("ENTERING findById(Long)");
        WebClient client = WebClient.create(getRestServiceURL(), getProviders(), true);
        FileData fileData = client.path("findById/{id}", id).accept(MediaType.APPLICATION_JSON).get(FileData.class);
        return fileData;
    }

    @Override
    public List<FileData> findByExample(FileData fileData) {
        logger.debug("ENTERING findByExample(FileData)");
        WebClient client = WebClient.create(getRestServiceURL(), getProviders(), true).type(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
        Collection<? extends FileData> ret = client.path("findByExample")
                .postAndGetCollection(fileData, FileData.class);
        return new ArrayList<FileData>(ret);
    }

}
