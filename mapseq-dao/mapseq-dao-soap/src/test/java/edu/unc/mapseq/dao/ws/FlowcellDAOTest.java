package edu.unc.mapseq.dao.ws;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;

import edu.unc.mapseq.dao.FlowcellDAO;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.model.Attribute;
import edu.unc.mapseq.dao.model.FileData;
import edu.unc.mapseq.dao.model.Flowcell;
import edu.unc.mapseq.dao.model.Sample;
import edu.unc.mapseq.dao.soap.SOAPDAOManager;

public class FlowcellDAOTest {

    @Test
    public void testFindByCreationDateRange() {
        SOAPDAOManager daoMgr = SOAPDAOManager.getInstance();
        try {

            Date parsedStartDate = DateUtils.parseDate("2014-07-01",
                    new String[] { DateFormatUtils.ISO_DATE_FORMAT.getPattern() });
            Date parsedEndDate = DateUtils.parseDate("2014-07-11",
                    new String[] { DateFormatUtils.ISO_DATE_FORMAT.getPattern() });

            List<Flowcell> entityList = daoMgr.getMaPSeqDAOBeanService().getFlowcellDAO()
                    .findByCreatedDateRange(parsedStartDate, parsedEndDate);
            if (entityList != null && entityList.size() > 0) {
                for (Flowcell flowcell : entityList) {
                    // System.out.println(flowcell.toString());
                    Set<String> attributeNameSet = new HashSet<String>();

                    List<Sample> sampleList = daoMgr.getMaPSeqDAOBeanService().getSampleDAO()
                            .findByFlowcellId(flowcell.getId());
                    for (Sample sample : sampleList) {
                        // System.out.println(sample.toString());
                        Set<Attribute> attributeSet = sample.getAttributes();
                        for (Attribute attribute : attributeSet) {
                            // System.out.printf("%s:%s%n", attribute.getName(), attribute.getValue());
                            attributeNameSet.add(attribute.getName());
                        }
                    }

                    if (!attributeNameSet.contains("q30YieldPassingFiltering")) {
                        System.out.println(flowcell.toString());
                    }
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testSave() {
        SOAPDAOManager daoMgr = SOAPDAOManager.getInstance();
        FlowcellDAO flowcellDAO = daoMgr.getMaPSeqDAOBeanService().getFlowcellDAO();
        try {
            Flowcell entity = new Flowcell("test");
            entity.setBaseDirectory("adsf");
            Long id = flowcellDAO.save(entity);
            System.out.println(id);
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void ncgenesMigrationPull() {
        try {
            SOAPDAOManager daoMgr = SOAPDAOManager.getInstance();
            List<Flowcell> entityList = daoMgr.getMaPSeqDAOBeanService().getFlowcellDAO().findByStudyName("NC_GENES");
            if (CollectionUtils.isNotEmpty(entityList)) {
                for (Flowcell flowcell : entityList) {
                    JAXBContext context = JAXBContext.newInstance(Flowcell.class);
                    Marshaller m = context.createMarshaller();
                    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                    File moduleClassXMLFile = new File("/tmp/flowcells",
                            String.format("%s-%d.xml", "Flowcell", flowcell.getId()));
                    FileWriter fw = new FileWriter(moduleClassXMLFile);
                    m.marshal(flowcell, fw);
                }
            }
        } catch (MaPSeqDAOException | JAXBException | IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void ncgenesMigrationPush() {
        try {
            SOAPDAOManager daoMgr = SOAPDAOManager.getInstance();

            Files.list(new File("/tmp/flowcells").toPath()).parallel().forEach(a -> {
                try {
                    JAXBContext context = JAXBContext.newInstance(Flowcell.class);
                    Unmarshaller unmarshaller = context.createUnmarshaller();
                    Flowcell flowcell = (Flowcell) unmarshaller.unmarshal(a.toFile());
                    System.out.println(flowcell.toString());

                    if (CollectionUtils.isNotEmpty(flowcell.getAttributes())) {

                    }

                    if (CollectionUtils.isNotEmpty(flowcell.getFileDatas())) {
                        for (FileData fileData : flowcell.getFileDatas()) {
                            fileData.setId(null);
                            // daoMgr.getMaPSeqDAOBeanService().getFileDataDAO().save(fileData);
                            System.out.println(fileData.toString());
                        }
                    }

                    flowcell.setId(null);
                    // daoMgr.getMaPSeqDAOBeanService().getFlowcellDAO().save(flowcell);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void createRsyncScripts() {
        File script = new File("/tmp", "rsync-ncgenes-flowcells.sh");
        if (script.exists()) {
            script.delete();
        }
        try (FileWriter fw = new FileWriter(script); BufferedWriter bw = new BufferedWriter(fw)) {

            bw.write("#!/bin/bash");
            bw.newLine();

            Set<String> flowcellNameSet = new HashSet<>();

            Files.list(new File("/tmp/flowcells").toPath()).parallel().forEach(a -> {
                try {
                    JAXBContext context = JAXBContext.newInstance(Flowcell.class);
                    Unmarshaller unmarshaller = context.createUnmarshaller();
                    Flowcell flowcell = (Flowcell) unmarshaller.unmarshal(a.toFile());
                    flowcellNameSet.add(flowcell.getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            Collections.synchronizedSet(flowcellNameSet);
            
            List<String> flowcellNameList = new ArrayList<>(flowcellNameSet);
            flowcellNameList.sort((a, b) -> a.compareTo(b));
            flowcellNameList.forEach(a -> {
                try {
                    bw.write(String.format(
                            "rsync -a --rsh='ssh -c arcfour' rc_renci.svc@152.19.198.149:/proj/seq/mapseq/RENCI/%1$s/ /projects/sequence_analysis/medgenwork/NC_GENES/analysis/%1$s/",
                            a));
                    bw.newLine();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
