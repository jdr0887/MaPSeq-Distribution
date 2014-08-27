package edu.unc.mapseq.dao.rs;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import edu.unc.mapseq.dao.MaPSeqDAOBean;

/**
 * 
 * @author jdr0887
 */
public class RSDAOManager {

    private static RSDAOManager instance;

    private MaPSeqDAOBean bean;

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
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(
                "edu/unc/mapseq/dao/rs/mapseq-dao-beans.xml");
        this.bean = (MaPSeqDAOBean) applicationContext.getBean("mapseqBean", MaPSeqDAOBean.class);
        applicationContext.close();
    }

    private RSDAOManager(String beanXMLFile) {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(beanXMLFile);
        this.bean = (MaPSeqDAOBean) applicationContext.getBean("mapseqBean", MaPSeqDAOBean.class);
        applicationContext.close();
    }

    public MaPSeqDAOBean getMaPSeqDAOBean() {
        return bean;
    }

}