package edu.unc.mapseq.dao.rest;

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
import org.springframework.stereotype.Component;

import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.SampleDAO;
import edu.unc.mapseq.dao.model.Sample;

@Component
public class SampleDAOImpl extends NamedEntityDAOImpl<Sample, Long> implements SampleDAO {

    private static final Logger logger = LoggerFactory.getLogger(SampleDAOImpl.class);

    public SampleDAOImpl() {
        super(Sample.class);
    }

    @Override
    public Sample findById(Long id) throws MaPSeqDAOException {
        logger.debug("ENTERING findById(Long)");
        WebClient client = WebClient.create(getRestServiceURL(), getProviders(), true);
        Sample sample = client.path("findById/{id}", id).accept(MediaType.APPLICATION_JSON).get(Sample.class);
        return sample;
    }

    @Override
    public Long save(Sample entity) throws MaPSeqDAOException {
        logger.debug("ENTERING save(HTSFSample)");
        WebClient client = WebClient.create(getRestServiceURL()).type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);
        Response response = client.path("/").post(entity);
        Long id = response.readEntity(Long.class);
        return id;
    }

    @Override
    public List<Sample> findByLaneIndexAndBarcode(Integer laneIndex, String barcode) throws MaPSeqDAOException {
        WebClient client = WebClient.create(getRestServiceURL(), getProviders(), true);
        Collection<? extends Sample> results = client.path("findByLaneIndexAndBarcode/{laneIndex}/{barcode}", laneIndex, barcode)
                .accept(MediaType.APPLICATION_JSON).getCollection(Sample.class);
        return new ArrayList<Sample>(results);
    }

    @Override
    public List<Sample> findByFlowcellId(Long flowcellId) throws MaPSeqDAOException {
        WebClient client = WebClient.create(getRestServiceURL(), getProviders(), true);
        Collection<? extends Sample> results = client.path("findByFlowcellId/{id}", flowcellId).accept(MediaType.APPLICATION_JSON)
                .getCollection(Sample.class);
        return new ArrayList<Sample>(results);
    }

    @Override
    public List<Sample> findByNameAndFlowcellId(String name, Long flowcellId) throws MaPSeqDAOException {
        WebClient client = WebClient.create(getRestServiceURL(), getProviders(), true);
        Collection<? extends Sample> results = client.path("findByNameAndFlowcellId/{flowcellId}/{sampleName}", flowcellId, name)
                .accept(MediaType.APPLICATION_JSON).getCollection(Sample.class);
        return new ArrayList<Sample>(results);
    }

    @Override
    public List<Sample> findByStudyId(Long studyId) throws MaPSeqDAOException {
        WebClient client = WebClient.create(getRestServiceURL(), getProviders(), true);
        Collection<? extends Sample> results = client.path("findByStudyId/{studyId}", studyId).accept(MediaType.APPLICATION_JSON)
                .getCollection(Sample.class);
        return new ArrayList<Sample>(results);
    }

    @Override
    public List<Sample> findByFlowcellNameAndSampleNameAndLaneIndex(String flowcellName, String sampleName, Integer laneIndex)
            throws MaPSeqDAOException {
        logger.debug("ENTERING findByFlowcellNameAndSampleNameAndLaneIndex(String, String, Integer)");
        WebClient client = WebClient.create(getRestServiceURL(), getProviders(), true);
        Collection<? extends Sample> results = client
                .path("findByFlowcellNameAndSampleNameAndLaneIndex/{flowcellName}/{sampleName}/laneIndex", flowcellName, sampleName,
                        laneIndex)
                .accept(MediaType.APPLICATION_JSON).getCollection(Sample.class);
        return new ArrayList<Sample>(results);
    }

    @Override
    public List<Sample> findByCreatedDateRange(Date startDate, Date endDate) throws MaPSeqDAOException {
        logger.debug("ENTERING findByCreatedDateRange(Date, Date)");
        String formattedStartDate = DateFormatUtils.ISO_DATE_FORMAT.format(startDate);
        String formattedEndDate = DateFormatUtils.ISO_DATE_FORMAT.format(endDate);
        WebClient client = WebClient.create(getRestServiceURL(), getProviders(), true);
        Collection<? extends Sample> ret = client.path("findByCreatedDateRange/{startDate}/{endDate}", formattedStartDate, formattedEndDate)
                .accept(MediaType.APPLICATION_JSON).getCollection(Sample.class);
        return new ArrayList<Sample>(ret);
    }

    @Override
    public List<Sample> findByName(String name) throws MaPSeqDAOException {
        WebClient client = WebClient.create(getRestServiceURL(), getProviders(), true);
        Collection<? extends Sample> results = client.path("findByName/{name}", name).accept(MediaType.APPLICATION_JSON)
                .getCollection(Sample.class);
        return new ArrayList<Sample>(results);
    }

    @Override
    public List<Sample> findByWorkflowRunId(Long arg0) throws MaPSeqDAOException {
        return null;
    }

    @Override
    public void addFileData(Long fileDataId, Long sampleId) throws MaPSeqDAOException {
        logger.debug("ENTERING addFileData(Long, Long)");
        WebClient client = WebClient.create(getRestServiceURL(), getProviders(), true);
        client.path("/addFileData/{fileDataId}/{sampleId}", fileDataId, sampleId).post(null);
    }

}
