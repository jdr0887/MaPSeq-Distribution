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
import edu.unc.mapseq.dao.model.EntityAttribute;
import edu.unc.mapseq.dao.model.HTSFSample;
import edu.unc.mapseq.dao.model.SequencerRun;

public class SequencerRunDAOTest {

    @Test
    public void testFindByCreationDateRange() {
        WSDAOManager daoMgr = WSDAOManager.getInstance("edu/unc/mapseq/dao/ws/mapseq-dao-beans-test.xml");
        try {

            Date parsedStartDate = DateUtils.parseDate("2014-07-01",
                    new String[] { DateFormatUtils.ISO_DATE_FORMAT.getPattern() });
            Date parsedEndDate = DateUtils.parseDate("2014-07-11",
                    new String[] { DateFormatUtils.ISO_DATE_FORMAT.getPattern() });

            List<SequencerRun> entityList = daoMgr.getMaPSeqDAOBean().getSequencerRunDAO()
                    .findByCreationDateRange(parsedStartDate, parsedEndDate);
            if (entityList != null && entityList.size() > 0) {
                for (SequencerRun sequencerRun : entityList) {
                    // System.out.println(sequencerRun.toString());
                    Set<String> attributeNameSet = new HashSet<String>();

                    List<HTSFSample> htsfSampleList = daoMgr.getMaPSeqDAOBean().getHTSFSampleDAO()
                            .findBySequencerRunId(sequencerRun.getId());
                    for (HTSFSample htsfSample : htsfSampleList) {
                        // System.out.println(htsfSample.toString());
                        Set<EntityAttribute> attributeSet = htsfSample.getAttributes();
                        for (EntityAttribute attribute : attributeSet) {
                            // System.out.printf("%s:%s%n", attribute.getName(), attribute.getValue());
                            attributeNameSet.add(attribute.getName());
                        }
                    }

                    if (!attributeNameSet.contains("q30YieldPassingFiltering")) {
                        System.out.println(sequencerRun.toString());
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
