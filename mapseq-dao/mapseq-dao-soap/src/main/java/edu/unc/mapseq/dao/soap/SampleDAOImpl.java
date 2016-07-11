package edu.unc.mapseq.dao.soap;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.ConnectionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.SampleDAO;
import edu.unc.mapseq.dao.model.Sample;
import edu.unc.mapseq.ws.SampleService;

/**
 * 
 * @author jdr0887
 */
@Component
public class SampleDAOImpl extends NamedEntityDAOImpl<Sample, Long> implements SampleDAO {

    private final Logger logger = LoggerFactory.getLogger(SampleDAOImpl.class);

    private SampleService sampleService;

    public SampleDAOImpl() {
        super(Sample.class);
    }

    @PostConstruct
    public void init() {
        sampleService = getService().getPort(SampleService.class);
        Client cl = ClientProxy.getClient(sampleService);
        HTTPConduit httpConduit = (HTTPConduit) cl.getConduit();
        httpConduit.getClient().setReceiveTimeout(getConfigurationService().getWebServiceTimeout());
        httpConduit.getClient().setConnectionTimeout(0);
        httpConduit.getClient().setConnection(ConnectionType.CLOSE);
    }

    @Override
    public Sample findById(Long id) throws MaPSeqDAOException {
        logger.debug("ENTERING findById(Long)");
        Sample sample = sampleService.findById(id);
        return sample;
    }

    @Override
    public Long save(Sample entity) throws MaPSeqDAOException {
        logger.debug("ENTERING save(Sample)");
        Long id = sampleService.save(entity);
        return id;
    }

    @Override
    public List<Sample> findByFlowcellId(Long flowcellId) throws MaPSeqDAOException {
        logger.debug("ENTERING findByFlowcellId(Long)");
        List<Sample> sampleList = sampleService.findByFlowcellId(flowcellId);
        return sampleList;
    }

    @Override
    public List<Sample> findByStudyId(Long studyId) throws MaPSeqDAOException {
        logger.debug("ENTERING findByStudyId(Long)");
        List<Sample> sampleList = sampleService.findByStudyId(studyId);
        return sampleList;
    }

    @Override
    public List<Sample> findByNameAndFlowcellId(String name, Long flowcellId) throws MaPSeqDAOException {
        logger.debug("ENTERING findByNameAndFlowcellId(String, Long)");
        List<Sample> ret = sampleService.findByNameAndFlowcellId(name, flowcellId);
        return ret;
    }

    @Override
    public List<Sample> findByLaneIndexAndBarcode(Integer laneIndex, String barcode) throws MaPSeqDAOException {
        logger.debug("ENTERING findByLaneIndexAndBarcode(Integer, String)");
        List<Sample> ret = sampleService.findByLaneIndexAndBarcode(laneIndex, barcode);
        return ret;
    }

    @Override
    public List<Sample> findByFlowcellNameAndSampleNameAndLaneIndex(String flowcellName, String sampleName, Integer laneIndex)
            throws MaPSeqDAOException {
        logger.debug("ENTERING findByFlowcellNameAndSampleNameAndLaneIndex(String, String, Integer)");
        List<Sample> ret = sampleService.findByFlowcellNameAndSampleNameAndLaneIndex(flowcellName, sampleName, laneIndex);
        return ret;
    }

    @Override
    public List<Sample> findByCreatedDateRange(Date startDate, Date endDate) throws MaPSeqDAOException {
        logger.debug("ENTERING findByCreatedDateRange(Date, Date)");
        String formattedStartDate = DateFormatUtils.ISO_DATE_FORMAT.format(startDate);
        String formattedEndDate = DateFormatUtils.ISO_DATE_FORMAT.format(endDate);
        List<Sample> sampleList = sampleService.findByCreatedDateRange(formattedStartDate, formattedEndDate);
        return sampleList;
    }

    @Override
    public List<Sample> findByName(String name) throws MaPSeqDAOException {
        logger.debug("ENTERING findByName(String)");
        List<Sample> sampleList = sampleService.findByName(name);
        return sampleList;
    }

    @Override
    public List<Sample> findByWorkflowRunId(Long workflowRunId) throws MaPSeqDAOException {
        logger.debug("ENTERING findByWorkflowRunId(Long)");
        List<Sample> sampleList = sampleService.findByWorkflowRunId(workflowRunId);
        return sampleList;
    }

    @Override
    public void addFileData(Long fileDataId, Long sampleId) throws MaPSeqDAOException {
        logger.debug("ENTERING addFileData(Long, Long)");
        sampleService.addFileData(fileDataId, sampleId);
    }

}
