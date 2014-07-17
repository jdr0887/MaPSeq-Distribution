package edu.unc.mapseq.dao.ws;

import java.util.List;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.ConnectionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.AccountGroupDAO;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.model.AccountGroup;
import edu.unc.mapseq.ws.AccountGroupService;

/**
 * 
 * @author jdr0887
 */
public class AccountGroupDAOImpl extends BaseEntityDAOImpl<AccountGroup, Long> implements AccountGroupDAO {

    private final Logger logger = LoggerFactory.getLogger(AccountGroupDAOImpl.class);

    private AccountGroupService accountGroupService;

    public AccountGroupDAOImpl() {
        super(AccountGroup.class);
    }

    public void init() {
        accountGroupService = getService().getPort(AccountGroupService.class);
        Client cl = ClientProxy.getClient(accountGroupService);
        HTTPConduit httpConduit = (HTTPConduit) cl.getConduit();
        httpConduit.getClient().setReceiveTimeout(getConfigurationService().getWebServiceTimeout());
        httpConduit.getClient().setConnectionTimeout(0);
        httpConduit.getClient().setConnection(ConnectionType.CLOSE);
    }

    @Override
    public Long save(AccountGroup entity) throws MaPSeqDAOException {
        return null;
    }

    @Override
    public AccountGroup findById(Long id) throws MaPSeqDAOException {
        return null;
    }

    @Override
    public List<AccountGroup> findByName(String name) throws MaPSeqDAOException {
        logger.debug("ENTERING findById(Long)");
        List<AccountGroup> ret = accountGroupService.findByName(name);
        return ret;
    }

}
