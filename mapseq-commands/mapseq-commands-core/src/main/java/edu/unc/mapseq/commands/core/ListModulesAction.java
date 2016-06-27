package edu.unc.mapseq.commands.core;

import java.net.URL;
import java.util.Enumeration;
import java.util.Formatter;
import java.util.Locale;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Command(scope = "mapseq", name = "list-modules", description = "List Modules")
@Service
public class ListModulesAction implements Action {

    private static final Logger logger = LoggerFactory.getLogger(ListModulesAction.class);

    @Reference
    private BundleContext context;

    @Override
    public Object execute() {
        logger.debug("ENTERING execute()");
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
            if (bundle.getSymbolicName().equals("mapseq-module-core")) {
                Enumeration<URL> e = bundle.findEntries("edu/unc/mapseq/module/core", "*CLI.class", true);
                formatter.format(format, "Name");
                while (e.hasMoreElements()) {
                    URL url = e.nextElement();
                    String path = url.getPath();
                    formatter.format(format, path.replace("CLI.class", "").replaceFirst("/", "").replace("/", "."));
                    formatter.flush();
                }
            }
            if (bundle.getSymbolicName().equals("mapseq-module-sequencing")) {
                Enumeration<URL> e = bundle.findEntries("edu/unc/mapseq/module/sequencing", "*CLI.class", true);
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

}
