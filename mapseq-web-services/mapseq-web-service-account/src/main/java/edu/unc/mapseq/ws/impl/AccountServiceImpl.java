package edu.unc.mapseq.ws.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.AccountDAO;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.model.Account;
import edu.unc.mapseq.ws.AccountService;

public class AccountServiceImpl implements AccountService {

    private final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

    private AccountDAO accountDAO;

    @Override
    public Account findById(Long id) {
        logger.info("ENTERING findById(Long)");
        try {
            return accountDAO.findById(id);
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Account findByName(String name) {
        logger.info("ENTERING findByName(String)");
        try {
            return accountDAO.findByName(name);
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Long save(Account account) {
        logger.info("ENTERING save(Account)");
        try {
            return accountDAO.save(account);
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public AccountDAO getAccountDAO() {
        return accountDAO;
    }

    public void setAccountDAO(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

}
