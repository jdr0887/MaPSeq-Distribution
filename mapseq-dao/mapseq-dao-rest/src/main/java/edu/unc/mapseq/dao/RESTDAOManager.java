package edu.unc.mapseq.dao;

import org.springframework.beans.BeansException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import edu.unc.mapseq.dao.MaPSeqDAOBeanService;

/**
 * 
 * @author jdr0887
 */
public class RESTDAOManager {

    private static RESTDAOManager instance;

    private MaPSeqDAOBeanService maPSeqDAOBeanService = null;

    public static RESTDAOManager getInstance() {
        if (instance == null) {
            instance = new RESTDAOManager();
        }
        return instance;
    }

    private RESTDAOManager() {
        try (ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(
                "classpath:/edu/unc/mapseq/dao/rest/mapseq-dao-beans.xml")) {
            this.maPSeqDAOBeanService = applicationContext.getBean(MaPSeqDAOBeanService.class);
        } catch (BeansException e) {
            e.printStackTrace();
        }
    }

    public MaPSeqDAOBeanService getMaPSeqDAOBeanService() {
        return this.maPSeqDAOBeanService;
    }

}