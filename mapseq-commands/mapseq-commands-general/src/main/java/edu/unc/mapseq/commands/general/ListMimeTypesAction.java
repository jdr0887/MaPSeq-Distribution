package edu.unc.mapseq.commands.general;

import java.util.Formatter;
import java.util.Locale;

import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.console.AbstractAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.model.MimeType;

@Command(scope = "mapseq", name = "list-mime-types", description = "List MimeTypes")
public class ListMimeTypesAction extends AbstractAction {

    private final Logger logger = LoggerFactory.getLogger(ListMimeTypesAction.class);

    @Override
    protected Object doExecute() throws Exception {
        logger.debug("ENTERING doExecute()");

        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(sb, Locale.US);
        String format = "%1$-40s %2$s%n";

        MimeType[] values = MimeType.values();

        for (MimeType m : values) {
            formatter.format(format, m.toString(), m.getName());
            formatter.flush();
        }
        System.out.println(formatter.toString());
        formatter.close();

        return null;
    }

}
