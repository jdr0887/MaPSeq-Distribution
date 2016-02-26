package edu.unc.mapseq.dao;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import edu.unc.mapseq.dao.MaPSeqDAOBeanService;

/**
 * 
 * @author jdr0887
 */
public class SOAPDAOManager {

    private static SOAPDAOManager instance;

    private MaPSeqDAOBeanService maPSeqDAOBeanService;

    public static SOAPDAOManager getInstance() {
        if (instance == null) {
            instance = new SOAPDAOManager();
        }
        return instance;
    }

    private SOAPDAOManager() {
        try (ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(
                String.format("classpath:/%s", "edu/unc/mapseq/dao/soap/mapseq-dao-beans.xml"))) {
            this.maPSeqDAOBeanService = applicationContext.getBean(MaPSeqDAOBeanService.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public MaPSeqDAOBeanService getMaPSeqDAOBeanService() {
        return this.maPSeqDAOBeanService;
    }

}
