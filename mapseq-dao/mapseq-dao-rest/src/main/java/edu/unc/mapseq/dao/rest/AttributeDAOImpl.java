package edu.unc.mapseq.dao.rest;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import edu.unc.mapseq.dao.AttributeDAO;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.model.Attribute;

@Component
public class AttributeDAOImpl extends BaseDAOImpl<Attribute, Long> implements AttributeDAO {

    private final Logger logger = LoggerFactory.getLogger(AttributeDAOImpl.class);

    public AttributeDAOImpl() {
        super(Attribute.class);
    }

    @Override
    public Long save(Attribute entity) throws MaPSeqDAOException {
        logger.debug("ENTERING save(Attribute)");
        WebClient client = WebClient.create(getRestServiceURL(), getProviders(), true).type(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
        Response response = client.path("/").post(entity);
        Long id = response.readEntity(Long.class);
        return id;
    }

    @Override
    public Attribute findById(Long id) throws MaPSeqDAOException {
        logger.debug("ENTERING findById(Long)");
        WebClient client = WebClient.create(getRestServiceURL(), getProviders(), true);
        Attribute attribute = client.path("findById/{id}", id).accept(MediaType.APPLICATION_JSON).get(Attribute.class);
        return attribute;
    }

}
