package edu.unc.mapseq.ws.impl;

import java.util.ArrayList;
import java.util.List;

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
    public List<AccountGroup> findByName(String name) {
        logger.info("ENTERING findByName(String)");
        List<AccountGroup> ret = new ArrayList<>();
        try {
            ret.addAll(accountGroupDAO.findByName(name));
        } catch (MaPSeqDAOException e) {
            logger.error("Error", e);
        }
        return ret;
    }

    public AccountGroupDAO getAccountGroupDAO() {
        return accountGroupDAO;
    }

    public void setAccountGroupDAO(AccountGroupDAO accountGroupDAO) {
        this.accountGroupDAO = accountGroupDAO;
    }

}
