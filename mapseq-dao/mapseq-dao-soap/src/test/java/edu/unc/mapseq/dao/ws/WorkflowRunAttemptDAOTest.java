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

import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.model.WorkflowRun;
import edu.unc.mapseq.dao.model.WorkflowRunAttempt;
import edu.unc.mapseq.ws.WorkflowRunAttemptService;
import edu.unc.mapseq.ws.WorkflowRunService;

public class WorkflowRunAttemptDAOTest {

    @Test
    public void testFindByWorkflowRunId() throws MaPSeqDAOException {
        QName serviceQName = new QName("http://ws.mapseq.unc.edu", "WorkflowRunAttemptService");
        QName portQName = new QName("http://ws.mapseq.unc.edu", "WorkflowRunAttemptPort");
        Service service = Service.create(serviceQName);
        // String host = "biodev2.its.unc.edu";
        String host = "152.19.198.146";
        service.addPort(portQName, SOAPBinding.SOAP11HTTP_BINDING,
                String.format("http://%s:%d/cxf/WorkflowRunAttemptService", host, 8181));
        WorkflowRunAttemptService workflowRunAttemptService = service.getPort(WorkflowRunAttemptService.class);
        List<WorkflowRunAttempt> workflowRunAttemptList = workflowRunAttemptService.findByWorkflowRunId(2053707L);
        for (WorkflowRunAttempt attempt : workflowRunAttemptList) {
            try {
                JAXBContext context = JAXBContext.newInstance(WorkflowRunAttempt.class);
                Marshaller m = context.createMarshaller();
                m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                File out = new File("/tmp", String.format("%s-%d.xml", "WorkflowPlanAttempt", attempt.getId()));
                FileWriter fw = new FileWriter(out);
                m.marshal(attempt, fw);
            } catch (IOException | JAXBException e1) {
                e1.printStackTrace();
            }
        }
    }

    @Test
    public void testFindByStudyNameAndSampleNameAndWorkflowName() {
        QName serviceQName = new QName("http://ws.mapseq.unc.edu", "WorkflowRunService");
        QName portQName = new QName("http://ws.mapseq.unc.edu", "WorkflowRunPort");
        Service service = Service.create(serviceQName);
        String host = "biodev2.its.unc.edu";
        service.addPort(portQName, SOAPBinding.SOAP11HTTP_BINDING,
                String.format("http://%s:%d/cxf/WorkflowRunService", host, 8181));
        WorkflowRunService workflowRunService = service.getPort(WorkflowRunService.class);
        Client cl = ClientProxy.getClient(workflowRunService);
        HTTPConduit httpConduit = (HTTPConduit) cl.getConduit();
        httpConduit.getClient().setReceiveTimeout(5 * 60 * 1000);

        List<WorkflowRun> workflowRunList = new ArrayList<WorkflowRun>();

        try {
            long startTime = System.currentTimeMillis();
            workflowRunList.addAll(workflowRunService.findByStudyNameAndSampleNameAndWorkflowName("NC_GENES",
                    "NCG_00517%", "CASAVA"));
            long endTime = System.currentTimeMillis();
            System.out.println((endTime - startTime) / 1000);

            // long startTime = System.currentTimeMillis();
            // workflowPlanList.addAll(workflowPlanService.findByStudyNameAndSampleNameAndWorkflowName("NC_GENES",
            // "NCG_00002%", "CASAVA"));
            // long endTime = System.currentTimeMillis();
            // System.out.println((endTime - startTime) / 1000);
            //
            // startTime = System.currentTimeMillis();
            // workflowPlanList.addAll(workflowPlanService.findByStudyNameAndSampleNameAndWorkflowName("NC_GENES",
            // "NCG_00065%", "CASAVA"));
            // endTime = System.currentTimeMillis();
            // System.out.println((endTime - startTime) / 1000);
            //
            // startTime = System.currentTimeMillis();
            // workflowPlanList.addAll(workflowPlanService.findByStudyNameAndSampleNameAndWorkflowName("NC_GENES",
            // "NCG_00065%", "NCGenes"));
            // endTime = System.currentTimeMillis();
            // System.out.println((endTime - startTime) / 1000);

            for (WorkflowRun workflowRun : workflowRunList) {
                for (WorkflowRunAttempt workflowRunAttempt : workflowRun.getAttempts()) {
                    System.out.println(workflowRunAttempt.getStatus().toString());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
