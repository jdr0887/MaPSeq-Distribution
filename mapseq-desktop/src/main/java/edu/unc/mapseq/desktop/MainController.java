package edu.unc.mapseq.desktop;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class MainController {

    private Main main;

    public MainController() {
        super();
    }

    @FXML
    private void handleAbout(final ActionEvent event) {
        provideAboutFunctionality();
    }

    @FXML
    private void handleKeyInput(final InputEvent event) {
        if (event instanceof KeyEvent) {
            final KeyEvent keyEvent = (KeyEvent) event;
            if (keyEvent.isControlDown() && keyEvent.getCode() == KeyCode.A) {
                provideAboutFunctionality();
            }
        }
    }

    @FXML
    private void showWorkflows() {
        main.showWorkflows();
    }

    @FXML
    private void showStudies() {
        main.showStudies();
    }

    @FXML
    private void showFlowcells() {
        main.showFlowcells();
    }

    @FXML
    private void handleExit() {
        System.exit(0);
    }

    @FXML
    private void showPreferences() {
        main.showPreferences();
    }

    @FXML
    private void showWorkflowRunAttemptCount() {
        main.showWorkflowRunAttemptCount();
    }

    @FXML
    private void showWorkflowRunAttemptDuration() {
        main.showWorkflowRunAttemptDuration();
    }

    private void provideAboutFunctionality() {
        System.out.println("You clicked on About!");
    }

    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }

}
