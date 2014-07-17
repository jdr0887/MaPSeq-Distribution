package edu.unc.mapseq.dao.ws;

import java.util.List;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.ConnectionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.PlatformDAO;
import edu.unc.mapseq.dao.model.Platform;
import edu.unc.mapseq.ws.PlatformService;

/**
 * 
 * @author jdr0887
 */
public class PlatformDAOImpl extends BaseEntityDAOImpl<Platform, Long> implements PlatformDAO {

    private final Logger logger = LoggerFactory.getLogger(PlatformDAOImpl.class);

    private PlatformService platformService;

    public PlatformDAOImpl() {
        super(Platform.class);
    }

    public void init() {
        platformService = getService().getPort(PlatformService.class);
        Client cl = ClientProxy.getClient(platformService);
        HTTPConduit httpConduit = (HTTPConduit) cl.getConduit();
        httpConduit.getClient().setReceiveTimeout(getConfigurationService().getWebServiceTimeout());
        httpConduit.getClient().setConnectionTimeout(0);
        httpConduit.getClient().setConnection(ConnectionType.CLOSE);
    }

    @Override
    public List<Platform> findAll() throws MaPSeqDAOException {
        logger.debug("ENTERING findAll()");
        List<Platform> results = platformService.findAll();
        return results;
    }

    @Override
    public Long save(Platform entity) throws MaPSeqDAOException {
        return null;
    }

    @Override
    public Platform findById(Long id) throws MaPSeqDAOException {
        logger.debug("ENTERING findById(Long)");
        Platform result = platformService.findById(id);
        return result;
    }

    @Override
    public List<Platform> findByName(String arg0) throws MaPSeqDAOException {
        return null;
    }

    @Override
    public Platform findByInstrumentAndModel(String instrument, String model) throws MaPSeqDAOException {
        logger.debug("ENTERING findByInstrumentAndModel(String, String)");
        Platform result = platformService.findByInstrumentAndModel(instrument, model);
        return result;
    }

    @Override
    public List<Platform> findByInstrument(String instrument) throws MaPSeqDAOException {
        logger.debug("ENTERING findByInstrument(String)");
        List<Platform> results = platformService.findByInstrument(instrument);
        return results;
    }

}
