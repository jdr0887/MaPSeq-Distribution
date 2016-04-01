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

import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.WorkflowDAO;
import edu.unc.mapseq.dao.model.Workflow;

@Component
public class WorkflowDAOImpl extends DictionaryEntityDAOImpl<Workflow, Long> implements WorkflowDAO {

    private static final Logger logger = LoggerFactory.getLogger(WorkflowDAOImpl.class);

    public WorkflowDAOImpl() {
        super(Workflow.class);
    }

    @Override
    public Long save(Workflow entity) throws MaPSeqDAOException {
        logger.debug("ENTERING save(Workflow)");
        WebClient client = WebClient.create(getRestServiceURL(), getProviders(), true).type(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
        Response response = client.path("/").post(entity);
        Long id = response.readEntity(Long.class);
        return id;
    }

    @Override
    public Workflow findById(Long id) throws MaPSeqDAOException {
        WebClient client = WebClient.create(getRestServiceURL(), getProviders(), true);
        Workflow workflow = client.path("findById/{id}", id).accept(MediaType.APPLICATION_JSON).get(Workflow.class);
        return workflow;
    }

    @Override
    public List<Workflow> findAll() throws MaPSeqDAOException {
        WebClient client = WebClient.create(getRestServiceURL(), getProviders(), true);
        Collection<? extends Workflow> ret = client.path("findAll").accept(MediaType.APPLICATION_JSON)
                .getCollection(Workflow.class);
        return new ArrayList<Workflow>(ret);
    }

    @Override
    public List<Workflow> findByName(String name) throws MaPSeqDAOException {
        WebClient client = WebClient.create(getRestServiceURL(), getProviders(), true);
        Collection<? extends Workflow> ret = client.path("findByName/{name}", name).accept(MediaType.APPLICATION_JSON)
                .getCollection(Workflow.class);
        return new ArrayList<Workflow>(ret);
    }

}
