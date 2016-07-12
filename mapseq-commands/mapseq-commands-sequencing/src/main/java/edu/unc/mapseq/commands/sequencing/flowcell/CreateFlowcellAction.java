package edu.unc.mapseq.commands.sequencing.flowcell;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import edu.unc.mapseq.dao.FlowcellDAO;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.model.Flowcell;

@Command(scope = "mapseq", name = "create-flowcell", description = "Create Flowcell")
@Service
public class CreateFlowcellAction implements Action {

    @Reference
    private FlowcellDAO flowcellDAO;

    @Option(name = "--baseRunFolder", description = "The folder parent to the flowcell directory", required = true, multiValued = false)
    private String baseRunFolder;

    @Option(name = "--name", description = "Name", required = true, multiValued = false)
    private String name;

    public CreateFlowcellAction() {
        super();
    }

    @Override
    public Object execute() {

        Pattern pattern = Pattern.compile("^\\d+_.+_\\d+_.+$");
        Matcher matcher = pattern.matcher(name);
        if (!matcher.matches()) {
            System.err.println("Invalid fastq name: " + name);
            System.err.println("Please use <date>_<machineID>_<technicianID>_<flowcell>");
            System.err.println("For example: 120110_UNC13-SN749_0141_AD0J7WACXX");
            return null;
        }

        try {
            Flowcell flowcell = new Flowcell(name);
            flowcell.setBaseDirectory(baseRunFolder);
            Long flowcellId = flowcellDAO.save(flowcell);
            return flowcellId;
        } catch (MaPSeqDAOException e1) {
            e1.printStackTrace();
        }
        return null;
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

}
