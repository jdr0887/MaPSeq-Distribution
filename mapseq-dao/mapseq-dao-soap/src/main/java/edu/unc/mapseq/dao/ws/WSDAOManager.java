package edu.unc.mapseq.dao.ws;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import edu.unc.mapseq.dao.MaPSeqDAOBeanService;

/**
 * 
 * @author jdr0887
 */
public class WSDAOManager {

    private static WSDAOManager instance;

    private String beanXMLFile = "edu/unc/mapseq/dao/ws/mapseq-dao-beans.xml";

    private ClassPathXmlApplicationContext applicationContext = null;

    public static WSDAOManager getInstance() {
        if (instance == null) {
            instance = new WSDAOManager();
        }
        return instance;
    }

    public static WSDAOManager getInstance(String beanXMLFile) {
        if (instance == null) {
            instance = new WSDAOManager(beanXMLFile);
        }
        return instance;
    }

    private WSDAOManager() {
        this.applicationContext = new ClassPathXmlApplicationContext(this.beanXMLFile);
    }

    private WSDAOManager(String beanXMLFile) {
        this.beanXMLFile = beanXMLFile;
        this.applicationContext = new ClassPathXmlApplicationContext(this.beanXMLFile);
    }

    public MaPSeqDAOBeanService getMaPSeqDAOBeanService() {
        MaPSeqDAOBeanService bean = (MaPSeqDAOBeanService) applicationContext.getBean("mapseqDAOBeanService",
                MaPSeqDAOBeanService.class);
        return bean;
    }

}
