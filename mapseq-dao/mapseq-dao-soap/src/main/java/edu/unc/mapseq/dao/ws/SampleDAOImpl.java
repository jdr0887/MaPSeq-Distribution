package edu.unc.mapseq.dao.ws;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.ConnectionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.SampleDAO;
import edu.unc.mapseq.dao.model.Sample;
import edu.unc.mapseq.ws.SampleService;

/**
 * 
 * @author jdr0887
 */
public class SampleDAOImpl extends NamedEntityDAOImpl<Sample, Long> implements SampleDAO {

    private final Logger logger = LoggerFactory.getLogger(SampleDAOImpl.class);

    private SampleService sampleService;

    public SampleDAOImpl() {
        super(Sample.class);
    }

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
    public List<Sample> findByNameAndFlowcellId(String name, Long flowcellId) throws MaPSeqDAOException {
        logger.debug("ENTERING findByNameAndFlowcellId(String, Long)");
        List<Sample> ret = sampleService.findByNameAndFlowcellId(name, flowcellId);
        return ret;
    }

    @Override
    public List<Sample> findByCreatedDateRange(Date startDate, Date endDate) throws MaPSeqDAOException {
        logger.debug("ENTERING findByCreatedDateRange(Date, Date)");
        String formattedStartDate = DateFormatUtils.ISO_DATE_FORMAT.format(startDate);
        String formattedEndDate = DateFormatUtils.ISO_DATE_FORMAT.format(endDate);
        List<Sample> htsfSampleList = sampleService.findByCreatedDateRange(formattedStartDate, formattedEndDate);
        return htsfSampleList;
    }

    @Override
    public List<Sample> findByName(String name) throws MaPSeqDAOException {
        logger.debug("ENTERING findByName(String)");
        List<Sample> sampleList = sampleService.findByName(name);
        return sampleList;
    }

}
