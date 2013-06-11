package edu.unc.mapseq.commands;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.AbstractAction;

import edu.unc.mapseq.dao.HTSFSampleDAO;
import edu.unc.mapseq.dao.MaPSeqDAOBean;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.SequencerRunDAO;
import edu.unc.mapseq.dao.model.FileData;
import edu.unc.mapseq.dao.model.HTSFSample;
import edu.unc.mapseq.dao.model.SequencerRun;

@Command(scope = "mapseq", name = "synchronize-file-data-with-file-system", description = "Synchronize File Data entries with FS")
public class SynchronizeFileDataWithFSAction extends AbstractAction {

    private MaPSeqDAOBean maPSeqDAOBean;

    @Argument(index = 0, name = "sequencerRunId", description = "Sequencer Run Identifier", required = true, multiValued = true)
    private List<Long> sequencerRunIdList;

    @Override
    protected Object doExecute() throws Exception {

        SequencerRunDAO sequencerRunDAO = maPSeqDAOBean.getSequencerRunDAO();

        HTSFSampleDAO htsfSampleDAO = maPSeqDAOBean.getHTSFSampleDAO();

        try {
            List<SequencerRun> sequencerRunList = sequencerRunDAO.findAll();

            if (sequencerRunList != null) {

                for (SequencerRun sequencerRun : sequencerRunList) {
                    List<HTSFSample> htsfSampleList = htsfSampleDAO.findBySequencerRunId(sequencerRun.getId());

                    if (htsfSampleList != null) {

                        for (HTSFSample sample : htsfSampleList) {
                            Set<FileData> sampleFileDataSet = sample.getFileDatas();
                            if (sampleFileDataSet != null) {
                                Iterator<FileData> sampleFileDataIter = sampleFileDataSet.iterator();
                                while (sampleFileDataIter.hasNext()) {
                                    FileData fileData = sampleFileDataIter.next();
                                    File f = new File(fileData.getPath(), fileData.getName());
                                    if (!f.exists()) {
                                        sampleFileDataIter.remove();
                                    }
                                }
                                htsfSampleDAO.save(sample);
                            }
                        }

                    }

                    Set<FileData> sequencerRunFileDataSet = sequencerRun.getFileDatas();
                    if (sequencerRunFileDataSet != null) {
                        Iterator<FileData> sequencerRunFileDataIter = sequencerRunFileDataSet.iterator();
                        while (sequencerRunFileDataIter.hasNext()) {
                            FileData fileData = sequencerRunFileDataIter.next();
                            File f = new File(fileData.getPath(), fileData.getName());
                            if (!f.exists()) {
                                sequencerRunFileDataIter.remove();
                            }
                        }
                        sequencerRunDAO.save(sequencerRun);
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

    public List<Long> getSequencerRunIdList() {
        return sequencerRunIdList;
    }

    public void setSequencerRunIdList(List<Long> sequencerRunIdList) {
        this.sequencerRunIdList = sequencerRunIdList;
    }

}
