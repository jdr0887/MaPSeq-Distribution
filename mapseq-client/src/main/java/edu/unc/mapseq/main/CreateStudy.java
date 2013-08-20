package edu.unc.mapseq.main;

import java.util.Date;
import java.util.concurrent.Callable;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import edu.unc.mapseq.dao.MaPSeqDAOBean;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.model.Study;
import edu.unc.mapseq.dao.rs.RSDAOManager;

public class CreateStudy implements Callable<Long> {

    private final static HelpFormatter helpFormatter = new HelpFormatter();

    private final static Options cliOptions = new Options();

    private String grant;

    private String name;

    private Long primaryContactId;

    private Long principalInvestigatorId;

    private Boolean approved;

    public CreateStudy() {
        super();
    }

    @Override
    public Long call() {
        //WSDAOManager daoMgr = WSDAOManager.getInstance();
        RSDAOManager daoMgr = RSDAOManager.getInstance();
        MaPSeqDAOBean mapseqDAOBean = daoMgr.getMaPSeqDAOBean();
        Date d = new Date();
        try {
            Study study = new Study();
            study.setCreationDate(d);
            study.setModificationDate(d);
            study.setApproved(approved);
            study.setCreator(mapseqDAOBean.getAccountDAO().findByName(System.getProperty("user.name")));
            study.setGrant(grant);
            study.setName(name);
            if (primaryContactId != null) {
                study.setPrimaryContact(mapseqDAOBean.getAccountDAO().findById(primaryContactId));
            }
            if (principalInvestigatorId != null) {
                study.setPrincipalInvestigator(mapseqDAOBean.getAccountDAO().findById(principalInvestigatorId));
            }
            Long studyId = mapseqDAOBean.getStudyDAO().save(study);
            return studyId;
        } catch (MaPSeqDAOException e1) {
            e1.printStackTrace();
        }
        return null;
    }

    public String getGrant() {
        return grant;
    }

    public void setGrant(String grant) {
        this.grant = grant;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getPrimaryContactId() {
        return primaryContactId;
    }

    public void setPrimaryContactId(Long primaryContactId) {
        this.primaryContactId = primaryContactId;
    }

    public Long getPrincipalInvestigatorId() {
        return principalInvestigatorId;
    }

    public void setPrincipalInvestigatorId(Long principalInvestigatorId) {
        this.principalInvestigatorId = principalInvestigatorId;
    }

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    @SuppressWarnings("static-access")
    public static void main(String[] args) {

        cliOptions.addOption(OptionBuilder.withArgName("grant").isRequired().hasArg().create("grant"));
        cliOptions.addOption(OptionBuilder.withArgName("name").isRequired().hasArg().create("name"));
        cliOptions.addOption(OptionBuilder.withArgName("primaryContactId").hasArg().create("primaryContactId"));
        cliOptions.addOption(OptionBuilder.withArgName("principalInvestigatorId").hasArg()
                .create("principalInvestigatorId"));
        cliOptions.addOption(OptionBuilder.withArgName("approved").isRequired().hasArg().create("approved"));
        cliOptions.addOption(OptionBuilder.withArgName("help").withDescription("print this help message")
                .withLongOpt("help").create("?"));

        CommandLineParser commandLineParser = new GnuParser();
        CreateStudy main = new CreateStudy();
        try {
            CommandLine commandLine = commandLineParser.parse(cliOptions, args);
            if (commandLine.hasOption("?")) {
                helpFormatter.printHelp(main.getClass().getSimpleName(), cliOptions);
                return;
            }

            if (commandLine.hasOption("name")) {
                main.setName(commandLine.getOptionValue("name"));
            }

            if (commandLine.hasOption("grant")) {
                main.setGrant(commandLine.getOptionValue("grant"));
            }

            if (commandLine.hasOption("primaryContactId")) {
                main.setPrimaryContactId(Long.valueOf(commandLine.getOptionValue("primaryContactId")));
            }

            if (commandLine.hasOption("principalInvestigatorId")) {
                main.setPrincipalInvestigatorId(Long.valueOf(commandLine.getOptionValue("principalInvestigatorId")));
            }

            if (commandLine.hasOption("approved")) {
                main.setApproved(Boolean.valueOf(commandLine.getOptionValue("approved")));
            }

            System.out.println(main.call());
        } catch (ParseException e) {
            System.err.println(("Parsing Failed: " + e.getMessage()));
            helpFormatter.printHelp(main.getClass().getSimpleName(), cliOptions);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
