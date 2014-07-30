package edu.unc.mapseq.dao.ws;

import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;

import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.model.Attribute;
import edu.unc.mapseq.dao.model.Flowcell;
import edu.unc.mapseq.dao.model.Sample;

public class FlowcellDAOTest {

    @Test
    public void testFindByCreationDateRange() {
        WSDAOManager daoMgr = WSDAOManager.getInstance("edu/unc/mapseq/dao/ws/mapseq-dao-beans-test.xml");
        try {

            Date parsedStartDate = DateUtils.parseDate("2014-07-01",
                    new String[] { DateFormatUtils.ISO_DATE_FORMAT.getPattern() });
            Date parsedEndDate = DateUtils.parseDate("2014-07-11",
                    new String[] { DateFormatUtils.ISO_DATE_FORMAT.getPattern() });

            List<Flowcell> entityList = daoMgr.getMaPSeqDAOBean().getFlowcellDAO()
                    .findByCreatedDateRange(parsedStartDate, parsedEndDate);
            if (entityList != null && entityList.size() > 0) {
                for (Flowcell flowcell : entityList) {
                    // System.out.println(sequencerRun.toString());
                    Set<String> attributeNameSet = new HashSet<String>();

                    List<Sample> sampleList = daoMgr.getMaPSeqDAOBean().getSampleDAO()
                            .findByFlowcellId(flowcell.getId());
                    for (Sample sample : sampleList) {
                        // System.out.println(htsfSample.toString());
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

}
