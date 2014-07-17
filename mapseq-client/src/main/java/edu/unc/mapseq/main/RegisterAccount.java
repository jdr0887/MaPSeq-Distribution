package edu.unc.mapseq.main;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
import edu.unc.mapseq.dao.model.Account;
import edu.unc.mapseq.dao.model.AccountGroup;
import edu.unc.mapseq.dao.ws.WSDAOManager;

public class RegisterAccount implements Callable<Long> {

    private final static HelpFormatter helpFormatter = new HelpFormatter();

    private final static Options cliOptions = new Options();

    public RegisterAccount() {
        super();
    }

    @Override
    public Long call() {
        WSDAOManager daoMgr = WSDAOManager.getInstance();
        // RSDAOManager daoMgr = RSDAOManager.getInstance();

        MaPSeqDAOBean mapseqDAOBean = daoMgr.getMaPSeqDAOBean();

        try {
            List<Account> accountList = mapseqDAOBean.getAccountDAO().findByName(System.getProperty("user.name"));

            if (accountList == null || (accountList != null && accountList.isEmpty())) {
                return createAccount();
            }

            if (accountList != null && !accountList.isEmpty()) {
                System.out.println("Account already exists for: " + System.getProperty("user.name"));
                return accountList.get(0).getId();
            }
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private Long createAccount() {
        WSDAOManager daoMgr = WSDAOManager.getInstance();
        // RSDAOManager daoMgr = RSDAOManager.getInstance();
        MaPSeqDAOBean mapseqDAOBean = daoMgr.getMaPSeqDAOBean();
        try {
            Account account = new Account();
            Set<AccountGroup> accountGroupSet = new HashSet<AccountGroup>();
            List<AccountGroup> accountGroupList = mapseqDAOBean.getAccountGroupDAO().findByName("public");
            accountGroupSet.addAll(accountGroupList);
            account.setAccountGroups(accountGroupSet);
            account.setName(System.getProperty("user.name"));
            Long accountId = mapseqDAOBean.getAccountDAO().save(account);
            return accountId;
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("static-access")
    public static void main(String[] args) {
        cliOptions.addOption(OptionBuilder.withArgName("help").withDescription("print this help message")
                .withLongOpt("help").create("?"));

        CommandLineParser commandLineParser = new GnuParser();
        RegisterAccount main = new RegisterAccount();
        try {
            CommandLine commandLine = commandLineParser.parse(cliOptions, args);
            if (commandLine.hasOption("?")) {
                helpFormatter.printHelp(main.getClass().getSimpleName(), cliOptions);
                return;
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
