package edu.unc.mapseq.commands;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.AbstractAction;

import edu.unc.mapseq.dao.MaPSeqDAOBean;
import edu.unc.mapseq.dao.PlatformDAO;
import edu.unc.mapseq.dao.model.Platform;

@Command(scope = "mapseq", name = "list-platforms", description = "List Platforms")
public class ListPlatformsAction extends AbstractAction {

    private MaPSeqDAOBean maPSeqDAOBean;

    public ListPlatformsAction() {
        super();
    }

    @Override
    public Object doExecute() {

        List<Platform> platformList = new ArrayList<Platform>();
        PlatformDAO platformDAO = maPSeqDAOBean.getPlatformDAO();
        try {
            platformList.addAll(platformDAO.findAll());
        } catch (Exception e) {
        }

        if (platformList.size() > 0) {
            StringBuilder sb = new StringBuilder();
            Formatter formatter = new Formatter(sb, Locale.US);
            formatter.format("%1$-4s %2$-20s %3$s%n", "ID", "Instrument", "Model");
            for (Platform platform : platformList) {
                formatter.format("%1$-4s %2$-20s %3$s%n", platform.getId(), platform.getInstrument(),
                        platform.getInstrumentModel());
                formatter.flush();
            }
            System.out.println(formatter.toString());
            formatter.close();
        }
        return null;

    }

    public MaPSeqDAOBean getMaPSeqDAOBean() {
        return maPSeqDAOBean;
    }

    public void setMaPSeqDAOBean(MaPSeqDAOBean maPSeqDAOBean) {
        this.maPSeqDAOBean = maPSeqDAOBean;
    }

}
