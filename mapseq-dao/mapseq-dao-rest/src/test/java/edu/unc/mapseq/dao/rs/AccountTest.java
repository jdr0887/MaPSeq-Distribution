package edu.unc.mapseq.dao.rs;

import java.util.List;

import org.junit.Test;

import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.model.Account;

public class AccountTest {

    @Test
    public void testFindByName() {
        RSDAOManager daoMgr = RSDAOManager.getInstance("edu/unc/mapseq/dao/rs/mapseq-dao-beans-test.xml");
        try {
            List<Account> accountList = daoMgr.getMaPSeqDAOBean().getAccountDAO().findByName("jdr0887");
            System.out.println(accountList.get(0).toString());
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }
    }

}
