package edu.unc.mapseq.main;

import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;

import edu.unc.mapseq.dao.MaPSeqDAOBean;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.model.Account;
import edu.unc.mapseq.dao.model.Study;
import edu.unc.mapseq.dao.ws.WSDAOManager;

public class CreateStudy implements Callable<Long> {

    private final static HelpFormatter helpFormatter = new HelpFormatter();

    private final static Options cliOptions = new Options();

    private String grant;

    private String name;

    private Long primaryContactId;

    private Long principalInvestigatorId;

    private Boolean approved = Boolean.TRUE;

    public CreateStudy() {
        super();
    }

    @Override
    public Long call() {
        WSDAOManager daoMgr = WSDAOManager.getInstance();
        // RSDAOManager daoMgr = RSDAOManager.getInstance();
        MaPSeqDAOBean maPSeqDAOBean = daoMgr.getMaPSeqDAOBean();

        List<Account> accountList = null;
        try {
            accountList = maPSeqDAOBean.getAccountDAO().findByName(System.getProperty("user.name"));
            if (accountList == null || (accountList != null && accountList.isEmpty())) {
                System.err.printf("Account doesn't exist: %s%n", System.getProperty("user.name"));
                System.err.println("Must register account first");
                return null;
            }
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }

        Account account = accountList.get(0);

        try {
            Study study = new Study();
            study.setName(name);
            study.setApproved(approved);
            study.setCreator(account);
            if (StringUtils.isNotEmpty(grant)) {
                study.setGrant(grant);
            }
            if (primaryContactId != null) {
                study.setPrimaryContact(maPSeqDAOBean.getAccountDAO().findById(primaryContactId));
            }
            if (principalInvestigatorId != null) {
                study.setPrincipalInvestigator(maPSeqDAOBean.getAccountDAO().findById(principalInvestigatorId));
            }
            Long studyId = maPSeqDAOBean.getStudyDAO().save(study);
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

        cliOptions.addOption(OptionBuilder.withArgName("grant").withLongOpt("grant").hasArg().create());
        cliOptions.addOption(OptionBuilder.withArgName("name").withLongOpt("name").isRequired().hasArg().create());
        cliOptions.addOption(OptionBuilder.withArgName("primaryContactId").withLongOpt("primaryContactId").hasArg()
                .create());
        cliOptions.addOption(OptionBuilder.withArgName("principalInvestigatorId")
                .withLongOpt("principalInvestigatorId").hasArg().create());
        cliOptions.addOption(OptionBuilder.withArgName("approved").withLongOpt("approved").hasArg().create());
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
