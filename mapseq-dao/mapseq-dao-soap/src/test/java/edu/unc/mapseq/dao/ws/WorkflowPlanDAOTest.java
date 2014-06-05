package edu.unc.mapseq.dao.ws;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.junit.Test;

import edu.unc.mapseq.dao.model.WorkflowPlan;
import edu.unc.mapseq.ws.WorkflowPlanService;

public class WorkflowPlanDAOTest {

    @Test
    public void testFindBySequencerRunAndWorkflowName() {
        QName serviceQName = new QName("http://ws.mapseq.unc.edu", "WorkflowPlanService");
        QName portQName = new QName("http://ws.mapseq.unc.edu", "WorkflowPlanPort");
        Service service = Service.create(serviceQName);
        String host = "biodev2.its.unc.edu";
        service.addPort(portQName, SOAPBinding.SOAP11HTTP_BINDING,
                String.format("http://%s:%d/cxf/WorkflowPlanService", host, 8181));
        WorkflowPlanService workflowPlanService = service.getPort(WorkflowPlanService.class);

        List<WorkflowPlan> workflowPlanList = workflowPlanService.findBySequencerRunAndWorkflowName(55852L, "CASAVA");

        for (WorkflowPlan workflowPlan : workflowPlanList) {
            try {
                JAXBContext context = JAXBContext.newInstance(WorkflowPlan.class);
                Marshaller m = context.createMarshaller();
                m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                File moduleClassXMLFile = new File("/tmp", String.format("%s-%d.xml", "WorkflowPlan",
                        workflowPlan.getId()));
                FileWriter fw = new FileWriter(moduleClassXMLFile);
                m.marshal(workflowPlan, fw);
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (PropertyException e1) {
                e1.printStackTrace();
            } catch (JAXBException e1) {
                e1.printStackTrace();
            }
        }

    }

    @Test
    public void testFindByStudyNameAndSampleNameAndWorkflowName() {
        QName serviceQName = new QName("http://ws.mapseq.unc.edu", "WorkflowPlanService");
        QName portQName = new QName("http://ws.mapseq.unc.edu", "WorkflowPlanPort");
        Service service = Service.create(serviceQName);
        String host = "biodev2.its.unc.edu";
        service.addPort(portQName, SOAPBinding.SOAP11HTTP_BINDING,
                String.format("http://%s:%d/cxf/WorkflowPlanService", host, 8181));
        WorkflowPlanService workflowPlanService = service.getPort(WorkflowPlanService.class);
        Client cl = ClientProxy.getClient(workflowPlanService);
        HTTPConduit httpConduit = (HTTPConduit) cl.getConduit();
        httpConduit.getClient().setReceiveTimeout(5 * 60 * 1000);

        List<WorkflowPlan> workflowPlanList = new ArrayList<WorkflowPlan>();

        try {
            long startTime = System.currentTimeMillis();
            workflowPlanList.addAll(workflowPlanService.findByStudyNameAndSampleNameAndWorkflowName("NC_GENES",
                    "NCG_00002%", "CASAVA"));
            long endTime = System.currentTimeMillis();
            System.out.println((endTime - startTime) / 1000);

            startTime = System.currentTimeMillis();
            workflowPlanList.addAll(workflowPlanService.findByStudyNameAndSampleNameAndWorkflowName("NC_GENES",
                    "NCG_00065%", "CASAVA"));
            endTime = System.currentTimeMillis();
            System.out.println((endTime - startTime) / 1000);

            startTime = System.currentTimeMillis();
            workflowPlanList.addAll(workflowPlanService.findByStudyNameAndSampleNameAndWorkflowName("NC_GENES",
                    "NCG_00065%", "NCGenes"));
            endTime = System.currentTimeMillis();
            System.out.println((endTime - startTime) / 1000);

            for (WorkflowPlan workflowPlan : workflowPlanList) {
                System.out.println(workflowPlan.getWorkflowRun().getStatus().toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
