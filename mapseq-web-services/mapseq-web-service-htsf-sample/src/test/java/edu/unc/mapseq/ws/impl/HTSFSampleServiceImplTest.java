package edu.unc.mapseq.ws.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

import edu.unc.mapseq.dao.model.EntityAttribute;
import edu.unc.mapseq.dao.model.HTSFSample;
import edu.unc.mapseq.ws.HTSFSampleService;

public class HTSFSampleServiceImplTest {

    @Test
    public void testSerialize() {
        QName serviceQName = new QName("http://ws.mapseq.unc.edu", "HTSFSampleService");
        QName portQName = new QName("http://ws.mapseq.unc.edu", "HTSFSamplePort");
        Service service = Service.create(serviceQName);
        String host = "biodev2.its.unc.edu";
        service.addPort(portQName, SOAPBinding.SOAP11HTTP_MTOM_BINDING,
                String.format("http://%s:%d/cxf/HTSFSampleService", host, 8181));
        HTSFSampleService htsfSampleService = service.getPort(HTSFSampleService.class);
        Client cl = ClientProxy.getClient(htsfSampleService);
        HTTPConduit httpConduit = (HTTPConduit) cl.getConduit();
        httpConduit.getClient().setReceiveTimeout(5 * 60 * 1000);

        List<Long> idList = Arrays.asList(487268L, 487381L, 487950L, 490741L, 490882L, 621100L, 621514L, 621584L,
                1659854L);

        FileWriter fw = null;

        for (Long id : idList) {

            try {
                File resultsFile = new File("/tmp", String.format("HTSFSample-%d.xml", id));
                fw = new FileWriter(resultsFile);
                JAXBContext context = JAXBContext.newInstance(HTSFSample.class);
                Marshaller m = context.createMarshaller();
                m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                m.marshal(htsfSampleService.findById(id), fw);
            } catch (PropertyException e) {
                e.printStackTrace();
            } catch (JAXBException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fw != null) {
                    try {
                        fw.flush();
                        fw.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }

    }

    @Test
    public void testOCDAttribute() {
        QName serviceQName = new QName("http://ws.mapseq.unc.edu", "HTSFSampleService");
        QName portQName = new QName("http://ws.mapseq.unc.edu", "HTSFSamplePort");
        Service service = Service.create(serviceQName);
        String host = "biodev2.its.unc.edu";
        service.addPort(portQName, SOAPBinding.SOAP11HTTP_MTOM_BINDING,
                String.format("http://%s:%d/cxf/HTSFSampleService", host, 8181));
        HTSFSampleService htsfSampleService = service.getPort(HTSFSampleService.class);

        List<HTSFSample> sampleList = new ArrayList<HTSFSample>();
        sampleList.addAll(htsfSampleService.findBySequencerRunId(191541L));
        sampleList.addAll(htsfSampleService.findBySequencerRunId(191738L));
        sampleList.addAll(htsfSampleService.findBySequencerRunId(190345L));
        sampleList.addAll(htsfSampleService.findBySequencerRunId(192405L));
        sampleList.addAll(htsfSampleService.findBySequencerRunId(190520L));
        sampleList.addAll(htsfSampleService.findBySequencerRunId(191372L));
        sampleList.addAll(htsfSampleService.findBySequencerRunId(191192L));

        for (HTSFSample sample : sampleList) {

            Set<EntityAttribute> entityAttributeSet = sample.getAttributes();

            Set<String> entityAttributeKeySet = new HashSet<String>();
            if (entityAttributeSet != null) {
                for (EntityAttribute attribute : entityAttributeSet) {
                    entityAttributeKeySet.add(attribute.getName());
                }
            }

            if (!entityAttributeKeySet.contains("observedClusterDensity")) {
                System.out.println(sample.toString());
            }
        }

    }

}
