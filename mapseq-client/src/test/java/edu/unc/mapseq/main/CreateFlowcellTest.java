package edu.unc.mapseq.main;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.model.Flowcell;
import edu.unc.mapseq.dao.model.Sample;
import edu.unc.mapseq.dao.model.Study;
import edu.unc.mapseq.dao.ws.WSDAOManager;

public class CreateFlowcellTest {

    @Test
    public void testFileNameRegex() {
        String good = "120904_UNC14-SN744_0267_BD15MRACXX";
        Pattern pattern = Pattern.compile("^\\d+_.+_\\d+_.+_\\w+$");
        Matcher matcher = pattern.matcher(good);
        assertTrue(matcher.matches());

        String bad = "TestFlowcell";
        matcher = pattern.matcher(bad);
        assertFalse(matcher.matches());
    }

    @Test
    public void testRun() {

        WSDAOManager daoMgr = WSDAOManager.getInstance();

        try {
            String sampleSheetContent = IOUtils.toString(
                    this.getClass().getClassLoader().getResourceAsStream("edu/unc/mapseq/main/SampleSheet.csv"));
            LineNumberReader lnr = new LineNumberReader(new StringReader(sampleSheetContent));
            lnr.readLine();
            String line;

            Flowcell flowcell = new Flowcell();
            flowcell.setBaseDirectory("asdfasdf");

            try {
                Long flowcellId = daoMgr.getMaPSeqDAOBeanService().getFlowcellDAO().save(flowcell);
                flowcell.setId(flowcellId);
            } catch (MaPSeqDAOException e1) {
                e1.printStackTrace();
            }

            while ((line = lnr.readLine()) != null) {

                String[] st = line.split(",");
                String flowcellProper = st[0];
                String laneIndex = st[1];
                String sampleId = st[2];
                String sampleRef = st[3];
                String index = st[4];
                String description = st[5];
                String control = st[6];
                String recipe = st[7];
                String operator = st[8];
                String sampleProject = st[9];

                Study study = daoMgr.getMaPSeqDAOBeanService().getStudyDAO().findByName(sampleProject).get(0);
                if (study == null) {
                    study = new Study();
                    study.setName(sampleProject);
                    daoMgr.getMaPSeqDAOBeanService().getStudyDAO().save(study);
                }

                Sample sample = new Sample();
                sample.setBarcode(index);
                sample.setLaneIndex(Integer.valueOf(laneIndex));
                sample.setName(sampleId);
                // sample.setFlowcell(flowcell);
                sample.setStudy(study);

                daoMgr.getMaPSeqDAOBeanService().getSampleDAO().save(sample);
            }

        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
