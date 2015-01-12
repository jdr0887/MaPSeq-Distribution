package edu.unc.mapseq.commands.migration;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.commands.Option;
import org.apache.karaf.shell.console.AbstractAction;

import edu.unc.mapseq.dao.FlowcellDAO;
import edu.unc.mapseq.dao.MaPSeqDAOBean;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.SampleDAO;
import edu.unc.mapseq.dao.model.FileData;
import edu.unc.mapseq.dao.model.Flowcell;
import edu.unc.mapseq.dao.model.Sample;

@Command(scope = "mapseq", name = "synchronize-file-data-with-file-system", description = "Synchronize File Data entries with FS")
public class SynchronizeFileDataWithFSAction extends AbstractAction {

    private MaPSeqDAOBean maPSeqDAOBean;

    @Option(name = "-d", description = "Do not remove file", required = false, multiValued = false)
    private Boolean dryRun = Boolean.FALSE;

    public SynchronizeFileDataWithFSAction() {
        super();
    }

    @Override
    protected Object doExecute() throws Exception {

        FlowcellDAO flowcellDAO = maPSeqDAOBean.getFlowcellDAO();

        SampleDAO sampleDAO = maPSeqDAOBean.getSampleDAO();

        try {

            List<Flowcell> flowcellList = flowcellDAO.findAll();

            if (flowcellList != null) {

                for (Flowcell flowcell : flowcellList) {

                    List<Sample> sampleList = sampleDAO.findByFlowcellId(flowcell.getId());

                    if (sampleList != null) {

                        for (Sample sample : sampleList) {

                            Set<FileData> sampleFileDataSet = sample.getFileDatas();

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

            }

        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public MaPSeqDAOBean getMaPSeqDAOBean() {
        return maPSeqDAOBean;
    }

    public void setMaPSeqDAOBean(MaPSeqDAOBean maPSeqDAOBean) {
        this.maPSeqDAOBean = maPSeqDAOBean;
    }

    public Boolean getDryRun() {
        return dryRun;
    }

    public void setDryRun(Boolean dryRun) {
        this.dryRun = dryRun;
    }

}
