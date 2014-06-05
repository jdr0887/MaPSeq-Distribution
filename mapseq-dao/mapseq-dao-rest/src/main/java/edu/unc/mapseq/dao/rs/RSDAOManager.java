package edu.unc.mapseq.dao.rs;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import edu.unc.mapseq.dao.MaPSeqDAOBean;

/**
 * 
 * @author jdr0887
 */
public class RSDAOManager {

    private static RSDAOManager instance;

    private String beanXMLFile = "edu/unc/mapseq/dao/rs/mapseq-dao-beans.xml";

    private ClassPathXmlApplicationContext applicationContext = null;

    public static RSDAOManager getInstance() {
        if (instance == null) {
            instance = new RSDAOManager();
        }
        return instance;
    }

    public static RSDAOManager getInstance(String beanXMLFile) {
        if (instance == null) {
            instance = new RSDAOManager(beanXMLFile);
        }
        return instance;
    }

    private RSDAOManager() {
        this.applicationContext = new ClassPathXmlApplicationContext(this.beanXMLFile);
    }

    private RSDAOManager(String beanXMLFile) {
        this.beanXMLFile = beanXMLFile;
        this.applicationContext = new ClassPathXmlApplicationContext(this.beanXMLFile);
    }

    public MaPSeqDAOBean getMaPSeqDAOBean() {
        MaPSeqDAOBean bean = (MaPSeqDAOBean) applicationContext.getBean("mapseqBean", MaPSeqDAOBean.class);
        return bean;
    }

}
