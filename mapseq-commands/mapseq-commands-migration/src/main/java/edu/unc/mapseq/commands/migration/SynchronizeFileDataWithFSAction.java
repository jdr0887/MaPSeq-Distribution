package edu.unc.mapseq.commands.migration;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.FlowcellDAO;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.SampleDAO;
import edu.unc.mapseq.dao.model.FileData;
import edu.unc.mapseq.dao.model.Flowcell;
import edu.unc.mapseq.dao.model.Sample;

@Command(scope = "mapseq", name = "synchronize-file-data-with-file-system", description = "Synchronize File Data entries with FS")
@Service
public class SynchronizeFileDataWithFSAction implements Action {

    private static final Logger logger = LoggerFactory.getLogger(SynchronizeFileDataWithFSAction.class);

    @Reference
    private FlowcellDAO flowcellDAO;

    @Reference
    private SampleDAO sampleDAO;

    @Option(name = "-d", description = "Do not remove file", required = false, multiValued = false)
    private Boolean dryRun = Boolean.FALSE;

    public SynchronizeFileDataWithFSAction() {
        super();
    }

    @Override
    public Object execute() {

        try {

            List<Flowcell> flowcellList = flowcellDAO.findAll();

            if (CollectionUtils.isEmpty(flowcellList)) {
                logger.warn("No Flowcells found");
                return null;
            }

            for (Flowcell flowcell : flowcellList) {

                List<Sample> sampleList = sampleDAO.findByFlowcellId(flowcell.getId());

                if (CollectionUtils.isEmpty(sampleList)) {
                    logger.warn("No Samples found");
                    continue;
                }

                for (Sample sample : sampleList) {

                    Set<FileData> sampleFileDataSet = sample.getFileDatas();

                    if (CollectionUtils.isEmpty(sampleFileDataSet)) {
                        logger.warn("No FileDatas found");
                        continue;
                    }

                    if (sampleFileDataSet != null) {

                        Iterator<FileData> sampleFileDataIter = sampleFileDataSet.iterator();

                        while (sampleFileDataIter.hasNext()) {

                            FileData fileData = sampleFileDataIter.next();
                            File f = new File(fileData.getPath(), fileData.getName());

                            if (!f.exists() && !dryRun) {
                                sampleFileDataIter.remove();
                            } else {
                                System.out.println(f.getAbsolutePath());
                            }

                        }

                        if (!dryRun) {
                            sampleDAO.save(sample);
                        }

                    }

                }

                Set<FileData> sequencerRunFileDataSet = flowcell.getFileDatas();

                if (sequencerRunFileDataSet != null) {

                    Iterator<FileData> sequencerRunFileDataIter = sequencerRunFileDataSet.iterator();

                    while (sequencerRunFileDataIter.hasNext()) {

                        FileData fileData = sequencerRunFileDataIter.next();
                        File f = new File(fileData.getPath(), fileData.getName());

                        if (!f.exists() && !dryRun) {
                            sequencerRunFileDataIter.remove();
                        } else {
                            System.out.println(f.getAbsolutePath());
                        }

                    }

                    if (!dryRun) {
                        flowcellDAO.save(flowcell);
                    }

                }

            }

        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Boolean getDryRun() {
        return dryRun;
    }

    public void setDryRun(Boolean dryRun) {
        this.dryRun = dryRun;
    }

}
