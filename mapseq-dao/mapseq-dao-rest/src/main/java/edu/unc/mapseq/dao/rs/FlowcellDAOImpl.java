package edu.unc.mapseq.dao.rs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.FlowcellDAO;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.model.Flowcell;

/**
 * 
 * @author jdr0887
 */
public class FlowcellDAOImpl extends NamedEntityDAOImpl<Flowcell, Long> implements FlowcellDAO {

    private final Logger logger = LoggerFactory.getLogger(FlowcellDAOImpl.class);

    public FlowcellDAOImpl() {
        super(Flowcell.class);
    }

    @Override
    public Long save(Flowcell entity) throws MaPSeqDAOException {
        logger.debug("ENTERING save(Long)");
        WebClient client = WebClient.create(getRestServiceURL(), getProviders(), true).type(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
        Response response = client.path("/").post(entity);
        Long id = response.readEntity(Long.class);
        return id;
    }

    @Override
    public List<Flowcell> findByName(String name) throws MaPSeqDAOException {
        WebClient client = WebClient.create(getRestServiceURL(), getProviders(), true);
        Collection<? extends Flowcell> ret = client.path("findByName/{name}", name).accept(MediaType.APPLICATION_JSON)
                .getCollection(Flowcell.class);
        return new ArrayList<Flowcell>(ret);
    }

    @Override
    public Flowcell findById(Long id) throws MaPSeqDAOException {
        logger.debug("ENTERING findById(Long)");
        WebClient client = WebClient.create(getRestServiceURL(), getProviders(), true);
        Flowcell flowcell = client.path("findById/{id}", id).accept(MediaType.APPLICATION_JSON).get(Flowcell.class);
        return flowcell;
    }

    @Override
    public List<Flowcell> findByStudyName(String name) throws MaPSeqDAOException {
        logger.debug("ENTERING findByStudyName(String)");
        WebClient client = WebClient.create(getRestServiceURL(), getProviders(), true);
        Collection<? extends Flowcell> ret = client.path("findByStudyName/{name}", name)
                .accept(MediaType.APPLICATION_JSON).getCollection(Flowcell.class);
        return new ArrayList<Flowcell>(ret);
    }

    @Override
    public List<Flowcell> findByCreatedDateRange(Date startDate, Date endDate) throws MaPSeqDAOException {
        logger.debug("ENTERING findByCreatedDateRange(Date, Date)");
        String formattedStartDate = DateFormatUtils.ISO_DATE_FORMAT.format(startDate);
        String formattedEndDate = DateFormatUtils.ISO_DATE_FORMAT.format(endDate);
        WebClient client = WebClient.create(getRestServiceURL(), getProviders(), true);
        Collection<? extends Flowcell> ret = client
                .path("findByCreatedDateRange/{startDate}/{endDate}", formattedStartDate, formattedEndDate)
                .accept(MediaType.APPLICATION_JSON).getCollection(Flowcell.class);
        return new ArrayList<Flowcell>(ret);
    }

    @Override
    public List<Flowcell> findAll() throws MaPSeqDAOException {
        WebClient client = WebClient.create(getRestServiceURL(), getProviders(), true);
        Collection<? extends Flowcell> ret = client.path("findAll").accept(MediaType.APPLICATION_JSON)
                .getCollection(Flowcell.class);
        return new ArrayList<Flowcell>(ret);
    }

    @Override
    public List<Flowcell> findByWorkflowRunId(Long arg0) throws MaPSeqDAOException {
        return null;
    }

    @Override
    public List<Flowcell> findByExample(Flowcell flowcell) throws MaPSeqDAOException {
        return null;
    }

    @Override
    public void addAttribute(Long attributeId, Long flowcellId) throws MaPSeqDAOException {
        WebClient client = WebClient.create(getRestServiceURL(), getProviders(), true);
        client.path("/addAttribute/{attributeId}", attributeId).put(flowcellId);
    }

    @Override
    public void addFileData(Long fileDataId, Long flowcellId) throws MaPSeqDAOException {
        WebClient client = WebClient.create(getRestServiceURL(), getProviders(), true);
        client.path("/addFileData/{fileDataId}", fileDataId).put(flowcellId);
    }

}
