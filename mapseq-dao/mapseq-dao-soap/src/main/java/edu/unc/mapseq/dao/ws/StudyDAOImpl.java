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
import edu.unc.mapseq.dao.StudyDAO;
import edu.unc.mapseq.dao.model.Study;
import edu.unc.mapseq.ws.StudyService;

/**
 * 
 * @author jdr0887
 */
public class StudyDAOImpl extends BaseDAOImpl<Study, Long> implements StudyDAO {

    private final Logger logger = LoggerFactory.getLogger(StudyDAOImpl.class);

    private StudyService studyService;

    public StudyDAOImpl() {
        super(Study.class);
    }

    public void init() {
        studyService = getService().getPort(StudyService.class);
        Client cl = ClientProxy.getClient(studyService);
        HTTPConduit httpConduit = (HTTPConduit) cl.getConduit();
        httpConduit.getClient().setReceiveTimeout(getConfigurationService().getWebServiceTimeout());
        httpConduit.getClient().setConnectionTimeout(0);
        httpConduit.getClient().setConnection(ConnectionType.CLOSE);
    }

    @Override
    public Long save(Study entity) throws MaPSeqDAOException {
        logger.debug("ENTERING save(Study)");
        Long id = studyService.save(entity);
        return id;
    }

    @Override
    public List<Study> findByName(String name) throws MaPSeqDAOException {
        logger.debug("ENTERING findByName(String)");
        List<Study> ret = studyService.findByName(name);
        return ret;
    }

    @Override
    public Study findById(Long id) throws MaPSeqDAOException {
        logger.debug("ENTERING findById(Long)");
        Study study = studyService.findById(id);
        return study;
    }

    @Override
    public List<Study> findAll() throws MaPSeqDAOException {
        logger.debug("ENTERING findAll()");
        List<Study> ret = studyService.findAll();
        return ret;
    }

    @Override
    public Study findBySampleId(Long sampleId) throws MaPSeqDAOException {
        logger.debug("ENTERING findBySampleId(Long)");
        Study study = studyService.findBySampleId(sampleId);
        return study;
    }

    @Override
    public List<Study> findByCreatedDateRange(Date startDate, Date endDate) throws MaPSeqDAOException {
        logger.debug("ENTERING findByCreatedDateRange(Date, Date)");
        String formattedStartDate = DateFormatUtils.ISO_DATE_FORMAT.format(startDate);
        String formattedEndDate = DateFormatUtils.ISO_DATE_FORMAT.format(endDate);
        List<Study> ret = studyService.findByCreatedDateRange(formattedStartDate, formattedEndDate);
        return ret;
    }

}
