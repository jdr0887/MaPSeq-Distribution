package edu.unc.mapseq.commands;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.AbstractAction;

import edu.unc.mapseq.dao.MaPSeqDAOBean;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.SequencerRunDAO;
import edu.unc.mapseq.dao.model.Account;
import edu.unc.mapseq.dao.model.SequencerRun;
import edu.unc.mapseq.dao.model.SequencerRunStatusType;

@Command(scope = "mapseq", name = "create-sequencer-run", description = "Create SequencerRun")
public class CreateSequencerRunAction extends AbstractAction {

    private MaPSeqDAOBean mapseqDAOBean;

    @Argument(index = 0, name = "Platform Identifier", description = "Platform Id", required = true, multiValued = false)
    private Long platformId;

    @Argument(index = 1, name = "baseRunFolder", description = "The folder parent to the flowcell directory", required = true, multiValued = false)
    private String baseRunFolder;

    @Argument(index = 2, name = "name", description = "Name", required = true, multiValued = false)
    private String name;

    @Argument(index = 3, name = "status", description = "Status", required = true, multiValued = false)
    private String status;

    public CreateSequencerRunAction() {
        super();
    }

    @Override
    public Object doExecute() {

        Account account = null;
        try {
            account = mapseqDAOBean.getAccountDAO().findByName(System.getProperty("user.name"));
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }

        if (account == null) {
            System.err.println("Must register account first");
            return null;
        }

        Pattern pattern = Pattern.compile("^\\d+_.+_\\d+_.+$");
        Matcher matcher = pattern.matcher(name);
        if (!matcher.matches()) {
            System.err.println("Invalid fastq name: " + name);
            System.err.println("Please use <date>_<machineID>_<technicianID>_<flowcell>");
            System.err.println("For example: 120110_UNC13-SN749_0141_AD0J7WACXX");
            return null;
        }

        Date creationDate = new Date();
        SequencerRun sequencerRun = new SequencerRun();
        try {
            sequencerRun.setCreator(account);
            sequencerRun.setName(name);
            sequencerRun.setBaseDirectory(baseRunFolder);
            sequencerRun.setCreationDate(creationDate);
            sequencerRun.setModificationDate(creationDate);
            sequencerRun.setPlatform(mapseqDAOBean.getPlatformDAO().findById(this.platformId));
            try {
                SequencerRunStatusType statusType = SequencerRunStatusType.valueOf(status);
                sequencerRun.setStatus(statusType);
            } catch (Exception e) {
                System.err.println("Invalid status...Please use:");
                StringBuilder sb = new StringBuilder();
                for (SequencerRunStatusType type : SequencerRunStatusType.values()) {
                    sb.append(",").append(type.toString());
                }
                System.err.println(sb.toString().replaceFirst(",", ""));
                return null;
            }
            SequencerRunDAO sequencerRunDAO = mapseqDAOBean.getSequencerRunDAO();
            Long sequencerRunId = sequencerRunDAO.save(sequencerRun);
            return sequencerRunId;
        } catch (MaPSeqDAOException e1) {
            e1.printStackTrace();
        }
        return null;
    }

    public MaPSeqDAOBean getMapseqDAOBean() {
        return mapseqDAOBean;
    }

    public void setMapseqDAOBean(MaPSeqDAOBean mapseqDAOBean) {
        this.mapseqDAOBean = mapseqDAOBean;
    }

}
