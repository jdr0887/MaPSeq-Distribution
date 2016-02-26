package edu.unc.mapseq.dao.soap;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.ConnectionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import edu.unc.mapseq.dao.AttributeDAO;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.model.Attribute;
import edu.unc.mapseq.ws.AttributeService;

/**
 * 
 * @author jdr0887
 */
@Component
public class AttributeDAOImpl extends BaseDAOImpl<Attribute, Long> implements AttributeDAO {

    private final Logger logger = LoggerFactory.getLogger(AttributeDAOImpl.class);

    private AttributeService attributeService;

    public AttributeDAOImpl() {
        super(Attribute.class);
    }

    public void init() {
        this.attributeService = getService().getPort(AttributeService.class);
        Client cl = ClientProxy.getClient(attributeService);
        HTTPConduit httpConduit = (HTTPConduit) cl.getConduit();
        httpConduit.getClient().setReceiveTimeout(getConfigurationService().getWebServiceTimeout());
        httpConduit.getClient().setConnectionTimeout(0);
        httpConduit.getClient().setConnection(ConnectionType.CLOSE);
    }

    @Override
    public Long save(Attribute entity) throws MaPSeqDAOException {
        logger.debug("ENTERING save(Attribute)");
        Long id = attributeService.save(entity);
        return id;
    }

    @Override
    public Attribute findById(Long id) throws MaPSeqDAOException {
        logger.debug("ENTERING findById(Long)");
        return attributeService.findById(id);
    }

}
