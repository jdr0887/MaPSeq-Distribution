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

import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.SequencerRunDAO;
import edu.unc.mapseq.dao.model.SequencerRun;

/**
 * 
 * @author jdr0887
 */
public class SequencerRunDAOImpl extends BaseEntityDAOImpl<SequencerRun, Long> implements SequencerRunDAO {

    private final Logger logger = LoggerFactory.getLogger(SequencerRunDAOImpl.class);

    public SequencerRunDAOImpl() {
        super(SequencerRun.class);
    }

    @Override
    public Long save(SequencerRun entity) throws MaPSeqDAOException {
        logger.debug("ENTERING save(Long)");
        WebClient client = WebClient.create(getRestServiceURL(), getProviders(), true).type(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
        Response response = client.path("/").post(entity);
        Long id = response.readEntity(Long.class);
        return id;
    }

    @Override
    public List<SequencerRun> findByAccountId(Long id) throws MaPSeqDAOException {
        logger.debug("ENTERING findByAccountId(Long)");
        WebClient client = WebClient.create(getRestServiceURL(), getProviders(), true);
        Collection<? extends SequencerRun> ret = client.path("findByName/{accountId}", id)
                .accept(MediaType.APPLICATION_JSON).getCollection(SequencerRun.class);
        return new ArrayList<SequencerRun>(ret);
    }

    @Override
    public List<SequencerRun> findByName(String name) throws MaPSeqDAOException {
        WebClient client = WebClient.create(getRestServiceURL(), getProviders(), true);
        Collection<? extends SequencerRun> ret = client.path("findByName/{name}", name)
                .accept(MediaType.APPLICATION_JSON).getCollection(SequencerRun.class);
        return new ArrayList<SequencerRun>(ret);
    }

    @Override
    public SequencerRun findById(Long id) throws MaPSeqDAOException {
        logger.debug("ENTERING findById(Long)");
        WebClient client = WebClient.create(getRestServiceURL(), getProviders(), true);
        SequencerRun sequencerRun = client.path("findById/{id}", id).accept(MediaType.APPLICATION_JSON)
                .get(SequencerRun.class);
        return sequencerRun;
    }

    @Override
    public List<SequencerRun> findByStudyName(String name) throws MaPSeqDAOException {
        logger.debug("ENTERING findByStudyName(String)");
        WebClient client = WebClient.create(getRestServiceURL(), getProviders(), true);
        Collection<? extends SequencerRun> ret = client.path("findByStudyName/{name}", name)
                .accept(MediaType.APPLICATION_JSON).getCollection(SequencerRun.class);
        return new ArrayList<SequencerRun>(ret);
    }

    @Override
    public List<SequencerRun> findByExample(SequencerRun sequencerRun) throws MaPSeqDAOException {
        return null;
    }

    @Override
    public List<SequencerRun> findByCreationDateRange(Date startDate, Date endDate) throws MaPSeqDAOException {
        logger.debug("ENTERING findByCreationDateRange(Date, Date)");
        String formattedStartDate = DateFormatUtils.ISO_DATE_FORMAT.format(startDate);
        String formattedEndDate = DateFormatUtils.ISO_DATE_FORMAT.format(endDate);

        WebClient client = WebClient.create(getRestServiceURL(), getProviders(), true);
        Collection<? extends SequencerRun> ret = client
                .path("findByCreationDateRange/{startDate}/{endDate}", formattedStartDate, formattedEndDate)
                .accept(MediaType.APPLICATION_JSON).getCollection(SequencerRun.class);
        return new ArrayList<SequencerRun>(ret);
    }

    @Override
    public List<SequencerRun> findAll() throws MaPSeqDAOException {
        WebClient client = WebClient.create(getRestServiceURL(), getProviders(), true);
        Collection<? extends SequencerRun> ret = client.path("findAll").accept(MediaType.APPLICATION_JSON)
                .getCollection(SequencerRun.class);
        return new ArrayList<SequencerRun>(ret);
    }

}
