package edu.unc.mapseq.dao.ws;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.ConnectionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.AccountDAO;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.model.Account;
import edu.unc.mapseq.ws.AccountService;

/**
 * 
 * @author jdr0887
 */
public class AccountDAOImpl extends BaseEntityDAOImpl<Account, Long> implements AccountDAO {

    private final Logger logger = LoggerFactory.getLogger(AccountDAOImpl.class);

    private AccountService accountService;

    public AccountDAOImpl() {
        super(Account.class);
    }

    public void init() {
        accountService = getService().getPort(AccountService.class);
        Client cl = ClientProxy.getClient(accountService);
        HTTPConduit httpConduit = (HTTPConduit) cl.getConduit();
        httpConduit.getClient().setReceiveTimeout(getConfigurationService().getWebServiceTimeout());
        httpConduit.getClient().setConnectionTimeout(0);
        httpConduit.getClient().setConnection(ConnectionType.CLOSE);
    }

    @Override
    public Long save(Account entity) throws MaPSeqDAOException {
        logger.debug("ENTERING save(Account)");
        Long accountId = accountService.save(entity);
        return accountId;
    }

    @Override
    public Account findById(Long id) throws MaPSeqDAOException {
        logger.debug("ENTERING findById(Long)");
        Account account = accountService.findById(id);
        return account;
    }

    @Override
    public Account findByName(String name) throws MaPSeqDAOException {
        logger.debug("ENTERING findByName(String)");
        Account account = accountService.findByName(name);
        return account;
    }

}
