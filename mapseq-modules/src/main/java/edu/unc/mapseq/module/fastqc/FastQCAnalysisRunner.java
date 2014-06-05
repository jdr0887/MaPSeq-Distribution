package edu.unc.mapseq.module.fastqc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import uk.ac.babraham.FastQC.Analysis.AnalysisListener;
import uk.ac.babraham.FastQC.Modules.QCModule;
import uk.ac.babraham.FastQC.Sequence.Sequence;
import uk.ac.babraham.FastQC.Sequence.SequenceFile;
import uk.ac.babraham.FastQC.Sequence.SequenceFormatException;

/**
 * Creating this rediculous class since FastQC doesn't know how to do threading
 * 
 * @author jdr0887
 */
public class FastQCAnalysisRunner implements Runnable {

    private SequenceFile file;

    private QCModule[] modules;

    private List<AnalysisListener> listeners = new ArrayList<AnalysisListener>();

    private int percentComplete = 0;

    public FastQCAnalysisRunner(SequenceFile file) {
        this.file = file;
    }

    public void addAnalysisListener(AnalysisListener l) {
        if (l != null && !listeners.contains(l)) {
            listeners.add(l);
        }
    }

    public void removeAnalysisListener(AnalysisListener l) {
        if (l != null && listeners.contains(l)) {
            listeners.remove(l);
        }
    }

    public void startAnalysis(QCModule[] modules) {
        this.modules = modules;
        for (int i = 0; i < modules.length; i++) {
            modules[i].reset();
        }
    }

    public void run() {

        Iterator<AnalysisListener> i = listeners.iterator();
        while (i.hasNext()) {
            i.next().analysisStarted(file);
        }

        int seqCount = 0;
        while (file.hasNext()) {
            ++seqCount;
            Sequence seq;
            try {
                seq = file.next();
            } catch (SequenceFormatException e) {
                i = listeners.iterator();
                while (i.hasNext()) {
                    i.next().analysisExceptionReceived(file, e);
                }
                return;
            }

            for (int m = 0; m < modules.length; m++) {
                if (seq.isFiltered() && modules[m].ignoreFilteredSequences())
                    continue;
                modules[m].processSequence(seq);
            }

            if (file.getPercentComplete() == percentComplete + 5) {

                percentComplete = (int) file.getPercentComplete();

                i = listeners.iterator();
                while (i.hasNext()) {
                    i.next().analysisUpdated(file, seqCount, percentComplete);
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                }
            }
        }

        i = listeners.iterator();
        while (i.hasNext()) {
            i.next().analysisComplete(file, modules);
        }

    }

}
