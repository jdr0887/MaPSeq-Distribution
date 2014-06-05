package edu.unc.mapseq.ws.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.AccountGroupDAO;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.model.AccountGroup;
import edu.unc.mapseq.ws.AccountGroupService;

public class AccountGroupServiceImpl implements AccountGroupService {

    private final Logger logger = LoggerFactory.getLogger(AccountGroupServiceImpl.class);

    private AccountGroupDAO accountGroupDAO;

    public AccountGroupServiceImpl() {
        super();
    }

    @Override
    public AccountGroup findByName(String name) {
        logger.info("ENTERING findByName(String)");
        try {
            return accountGroupDAO.findByName(name);
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public AccountGroupDAO getAccountGroupDAO() {
        return accountGroupDAO;
    }

    public void setAccountGroupDAO(AccountGroupDAO accountGroupDAO) {
        this.accountGroupDAO = accountGroupDAO;
    }

}
