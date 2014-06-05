package edu.unc.mapseq.ws.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import edu.unc.mapseq.dao.model.FileData;
import edu.unc.mapseq.dao.model.MimeType;
import edu.unc.mapseq.ws.FileDataService;

public class FileDataServiceImplTest {

    @Test
    public void testDownload() {

        QName serviceQName = new QName("http://ws.mapseq.unc.edu", "FileDataService");
        Service service = Service.create(serviceQName);
        QName portQName = new QName("http://ws.mapseq.unc.edu", "FileDataPort");
        service.addPort(portQName, SOAPBinding.SOAP11HTTP_BINDING,
                String.format("http://%s:%d/cxf/FileDataService", "localhost", 8181));
        FileDataService fileDataService = service.getPort(FileDataService.class);
        Binding binding = ((BindingProvider) service.getPort(portQName, FileDataService.class)).getBinding();
        ((SOAPBinding) binding).setMTOMEnabled(true);
        FileData fileData = fileDataService.findById(128738L);
        DataHandler handler = fileDataService.download(128738L);
        try {
            IOUtils.copyLarge(handler.getInputStream(),
                    new FileOutputStream(new File("/home/jdr0887/tmp", fileData.getName())));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testFindByExample() {

        QName serviceQName = new QName("http://ws.mapseq.unc.edu", "FileDataService");
        Service service = Service.create(serviceQName);
        QName portQName = new QName("http://ws.mapseq.unc.edu", "FileDataPort");
        service.addPort(portQName, SOAPBinding.SOAP11HTTP_BINDING,
                String.format("http://%s:%d/cxf/FileDataService", "biodev2.its.unc.edu", 8181));
        FileDataService fileDataService = service.getPort(FileDataService.class);
        FileData fd = new FileData();
        fd.setMimeType(MimeType.PICARD_MARK_DUPLICATE_METRICS);
        fd.setName("140226_UNC17-D00216_0149_BH8G5LADXX_AAAGCA_L002.fixed-rg.deduped.metrics");
        fd.setPath("/proj/seq/mapseq/RENCI/140226_UNC17-D00216_0149_BH8G5LADXX/NCGenes/L002_AAAGCA");
        List<FileData> fileDataList = fileDataService.findByExample(fd);
        for (FileData fileData : fileDataList) {
            System.out.println(fileData.toString());
        }

    }

}
