package edu.unc.mapseq.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.AbstractAction;

import edu.unc.mapseq.dao.FlowcellDAO;
import edu.unc.mapseq.dao.MaPSeqDAOBean;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.model.Flowcell;
import edu.unc.mapseq.dao.model.FlowcellStatusType;

@Command(scope = "mapseq", name = "create-flowcell", description = "Create Flowcell")
public class CreateFlowcellAction extends AbstractAction {

    private MaPSeqDAOBean maPSeqDAOBean;

    @Argument(index = 0, name = "baseRunFolder", description = "The folder parent to the flowcell directory", required = true, multiValued = false)
    private String baseRunFolder;

    @Argument(index = 1, name = "name", description = "Name", required = true, multiValued = false)
    private String name;

    @Argument(index = 2, name = "status", description = "Status", required = true, multiValued = false)
    private String status;

    public CreateFlowcellAction() {
        super();
    }

    @Override
    public Object doExecute() {

        Pattern pattern = Pattern.compile("^\\d+_.+_\\d+_.+$");
        Matcher matcher = pattern.matcher(name);
        if (!matcher.matches()) {
            System.err.println("Invalid fastq name: " + name);
            System.err.println("Please use <date>_<machineID>_<technicianID>_<flowcell>");
            System.err.println("For example: 120110_UNC13-SN749_0141_AD0J7WACXX");
            return null;
        }

        Flowcell flowcell = new Flowcell();
        try {
            flowcell.setName(name);
            flowcell.setBaseDirectory(baseRunFolder);
            try {
                FlowcellStatusType statusType = FlowcellStatusType.valueOf(status);
                flowcell.setStatus(statusType);
            } catch (Exception e) {
                System.err.println("Invalid status...Please use:");
                StringBuilder sb = new StringBuilder();
                for (FlowcellStatusType type : FlowcellStatusType.values()) {
                    sb.append(",").append(type.toString());
                }
                System.err.println(sb.toString().replaceFirst(",", ""));
                return null;
            }
            FlowcellDAO flowcellDAO = maPSeqDAOBean.getFlowcellDAO();
            Long flowcellId = flowcellDAO.save(flowcell);
            return flowcellId;
        } catch (MaPSeqDAOException e1) {
            e1.printStackTrace();
        }
        return null;
    }

    public MaPSeqDAOBean getMaPSeqDAOBean() {
        return maPSeqDAOBean;
    }

    public void setMaPSeqDAOBean(MaPSeqDAOBean maPSeqDAOBean) {
        this.maPSeqDAOBean = maPSeqDAOBean;
    }

    public String getBaseRunFolder() {
        return baseRunFolder;
    }

    public void setBaseRunFolder(String baseRunFolder) {
        this.baseRunFolder = baseRunFolder;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
