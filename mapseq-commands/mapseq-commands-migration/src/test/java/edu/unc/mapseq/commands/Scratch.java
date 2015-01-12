package edu.unc.mapseq.commands;

import org.junit.Test;

public class Scratch {

    @Test
    public void test() {
        String flowcellName = "121119_UNC12-SN629_0239_BC11DEACXX";
        String path = "/proj/seq/mapseq/RENCI/121119_UNC12-SN629_0239_BC11DEACXX/NIDAUCSF/L002_AGTTCC";
        String pathToFlowcell = path.substring(0, path.indexOf(flowcellName) - 1);
        System.out.println(pathToFlowcell);
    }

}
