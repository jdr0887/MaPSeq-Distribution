package edu.unc.mapseq.commands;

import java.net.URL;
import java.util.Enumeration;
import java.util.Formatter;
import java.util.Locale;

import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.console.AbstractAction;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Command(scope = "mapseq", name = "list-modules", description = "List Modules")
public class ListModulesAction extends AbstractAction {

    private final Logger logger = LoggerFactory.getLogger(ListModulesAction.class);

    private BundleContext context = null;

    @Override
    protected Object doExecute() throws Exception {
        logger.debug("ENTERING doExecute()");
        if (context == null) {
            logger.error("context is null");
            return null;
        }
        Bundle[] bundleArray = context.getBundles();

        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(sb, Locale.US);
        String format = "%s%n";

        for (Bundle bundle : bundleArray) {
            logger.info("bundle: {}", bundle.getLocation());
            if (bundle.getSymbolicName().equals("mapseq-modules")) {
                Enumeration<URL> e = bundle.findEntries("edu/unc/mapseq/module", "*CLI.class", true);
                formatter.format(format, "Name");
                while (e.hasMoreElements()) {
                    URL url = e.nextElement();
                    String path = url.getPath();
                    formatter.format(format, path.replace("CLI.class", "").replaceFirst("/", "").replace("/", "."));
                    formatter.flush();
                }
            }
        }
        System.out.println(formatter.toString());
        formatter.close();

        return null;
    }

    public BundleContext getContext() {
        return context;
    }

    public void setContext(BundleContext context) {
        this.context = context;
    }

}
