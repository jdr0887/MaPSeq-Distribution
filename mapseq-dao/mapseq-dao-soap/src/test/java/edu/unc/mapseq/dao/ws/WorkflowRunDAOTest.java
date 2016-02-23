package edu.unc.mapseq.dao.ws;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;

import org.junit.Test;

import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.WorkflowRunDAO;
import edu.unc.mapseq.dao.model.WorkflowRun;
import edu.unc.mapseq.ws.WorkflowRunService;

public class WorkflowRunDAOTest {

    @Test
    public void testSave() {

        WorkflowRun workflowRun = new WorkflowRun();
        workflowRun.setName("test");

        WSDAOManager wsDAOMgr = WSDAOManager.getInstance("edu/unc/mapseq/dao/ws/mapseq-dao-beans-test.xml");
        WorkflowRunDAO workflowRunDAO = wsDAOMgr.getMaPSeqDAOBeanService().getWorkflowRunDAO();
        try {
            Long id = workflowRunDAO.save(workflowRun);
            System.out.println(id);
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testFindByName() {
        QName serviceQName = new QName("http://ws.mapseq.unc.edu", "WorkflowRunService");
        QName portQName = new QName("http://ws.mapseq.unc.edu", "WorkflowRunPort");
        Service service = Service.create(serviceQName);
        String host = "biodev2.its.unc.edu";
        service.addPort(portQName, SOAPBinding.SOAP11HTTP_BINDING,
                String.format("http://%s:%d/cxf/WorkflowRunService", host, 8181));
        WorkflowRunService workflowRunService = service.getPort(WorkflowRunService.class);

        List<WorkflowRun> workflowRunList = workflowRunService.findByName("NCG_00112_Baseline%");

        for (WorkflowRun workflowRun : workflowRunList) {
            try {
                JAXBContext context = JAXBContext.newInstance(WorkflowRun.class);
                Marshaller m = context.createMarshaller();
                m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                File moduleClassXMLFile = new File("/tmp", String.format("%s-%d.xml", "WorkflowRun",
                        workflowRun.getId()));
                FileWriter fw = new FileWriter(moduleClassXMLFile);
                m.marshal(workflowRun, fw);
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (PropertyException e1) {
                e1.printStackTrace();
            } catch (JAXBException e1) {
                e1.printStackTrace();
            }
        }

    }

}
