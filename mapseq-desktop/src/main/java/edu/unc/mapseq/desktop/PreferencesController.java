package edu.unc.mapseq.desktop;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import org.controlsfx.dialog.Dialogs;
import org.apache.commons.lang3.StringUtils;

import edu.unc.mapseq.config.MaPSeqConfigurationService;
import edu.unc.mapseq.dao.rs.RSDAOManager;

public class PreferencesController {

    private Main main;

    private Stage dialogStage;

    @FXML
    private TextField server;

    @FXML
    private TextField port;

    private boolean okClicked = false;

    public PreferencesController() {
        super();
    }

    @FXML
    private void initialize() {
        Preferences prefs = Preferences.userRoot();
        server.setText(prefs.get("server", "localhost"));
        port.setText(prefs.get("port", "8181"));
    }

    @FXML
    private void handleOK() {
        if (isInputValid()) {
            Preferences prefs = Preferences.userRoot();
            prefs.put("server", server.getText());
            prefs.put("port", port.getText());
            try {
                prefs.flush();
            } catch (BackingStoreException e) {
                e.printStackTrace();
            }
            System.getProperties().setProperty(MaPSeqConfigurationService.WEB_SERVICE_HOST,
                    prefs.get("server", "localhost"));
            main.setDaoMgr(RSDAOManager.getInstance());

            okClicked = true;
            dialogStage.close();
        }
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    private boolean isInputValid() {
        String errorMessage = "";

        if (StringUtils.isEmpty(server.getText())) {
            errorMessage += "No valid server!\n";
        }
        if (StringUtils.isEmpty(port.getText())) {
            errorMessage += "No valid port!\n";
        }

        if (StringUtils.isEmpty(errorMessage)) {
            return true;
        } else {
            Dialogs.create().title("Invalid Fields").masthead("Please correct invalid fields").message(errorMessage)
                    .showError();
            return false;
        }
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    public void setOkClicked(boolean okClicked) {
        this.okClicked = okClicked;
    }

    public Stage getDialogStage() {
        return dialogStage;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }

}
