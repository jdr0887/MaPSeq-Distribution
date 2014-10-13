package edu.unc.mapseq.desktop;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.prefs.Preferences;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.config.MaPSeqConfigurationService;
import edu.unc.mapseq.dao.FlowcellDAO;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.StudyDAO;
import edu.unc.mapseq.dao.WorkflowDAO;
import edu.unc.mapseq.dao.WorkflowRunAttemptDAO;
import edu.unc.mapseq.dao.model.Flowcell;
import edu.unc.mapseq.dao.model.Study;
import edu.unc.mapseq.dao.model.Workflow;
import edu.unc.mapseq.dao.model.WorkflowRunAttempt;
import edu.unc.mapseq.dao.model.WorkflowRunAttemptStatusType;
import edu.unc.mapseq.dao.rs.RSDAOManager;

public class Main extends Application {

    private final Logger logger = LoggerFactory.getLogger(Main.class);

    private Stage primaryStage;

    private BorderPane rootLayout;

    private RSDAOManager daoMgr;

    public Main() {
        super();
    }

    @Override
    public void start(final Stage primaryStage) throws Exception {

        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("MaPSeq");
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("main.fxml"));
            rootLayout = (BorderPane) loader.load();

            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);

            MainController controller = loader.getController();
            controller.setMain(this);

            primaryStage.show();

            Preferences prefs = Preferences.userRoot();
            if (prefs.get("server", null) == null) {
                showPreferences();
            } else {
                System.getProperties().setProperty(MaPSeqConfigurationService.WEB_SERVICE_HOST,
                        prefs.get("server", "localhost"));
                daoMgr = RSDAOManager.getInstance();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public boolean showPreferences() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("preferences.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Preferences");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Set the person into the controller.
            PreferencesController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setMain(this);

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();

            return controller.isOkClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void showFlowcells() {

        try {
            FlowcellDAO flowcellDAO = daoMgr.getMaPSeqDAOBean().getFlowcellDAO();
            List<Flowcell> flowcellList = flowcellDAO.findAll();

            TableView<Flowcell> table = new TableView<Flowcell>();
            ObservableList<Flowcell> observableList = FXCollections.observableArrayList(flowcellList);

            TableColumn<Flowcell, Long> idColumn = new TableColumn<Flowcell, Long>("Id");
            idColumn.setMinWidth(20);
            idColumn.setCellValueFactory(new PropertyValueFactory<Flowcell, Long>("id"));

            TableColumn<Flowcell, String> nameColumn = new TableColumn<Flowcell, String>("Name");
            // nameColumn.setMinWidth(320);
            nameColumn.setCellValueFactory(new PropertyValueFactory<Flowcell, String>("name"));

            table.setItems(observableList);
            table.getColumns().addAll(idColumn, nameColumn);

            final VBox vbox = new VBox();
            vbox.setSpacing(5);
            vbox.getChildren().add(table);

            rootLayout.setCenter(vbox);
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }

    }

    public void showStudies() {
        try {
            StudyDAO studyDAO = daoMgr.getMaPSeqDAOBean().getStudyDAO();
            List<Study> studyList = studyDAO.findAll();

            TableView<Study> table = new TableView<Study>();
            ObservableList<Study> observableList = FXCollections.observableArrayList(studyList);

            TableColumn<Study, Long> idColumn = new TableColumn<Study, Long>("Id");
            idColumn.setMinWidth(20);
            idColumn.setCellValueFactory(new PropertyValueFactory<Study, Long>("id"));

            TableColumn<Study, String> nameColumn = new TableColumn<Study, String>("Name");
            // nameColumn.setMinWidth(320);
            nameColumn.setCellValueFactory(new PropertyValueFactory<Study, String>("name"));

            table.setItems(observableList);
            table.getColumns().addAll(idColumn, nameColumn);

            final VBox vbox = new VBox();
            vbox.setSpacing(5);
            vbox.getChildren().add(table);

            rootLayout.setCenter(vbox);
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }
    }

    public void showWorkflows() {
        try {
            WorkflowDAO workflowDAO = daoMgr.getMaPSeqDAOBean().getWorkflowDAO();
            List<Workflow> workflowList = workflowDAO.findAll();

            TableView<Workflow> table = new TableView<Workflow>();
            ObservableList<Workflow> observableList = FXCollections.observableArrayList(workflowList);

            TableColumn<Workflow, Long> idColumn = new TableColumn<Workflow, Long>("Id");
            idColumn.setMinWidth(20);
            idColumn.setCellValueFactory(new PropertyValueFactory<Workflow, Long>("id"));

            TableColumn<Workflow, String> nameColumn = new TableColumn<Workflow, String>("Name");
            // nameColumn.setMinWidth(320);
            nameColumn.setCellValueFactory(new PropertyValueFactory<Workflow, String>("name"));

            table.setItems(observableList);
            table.getColumns().addAll(idColumn, nameColumn);

            final VBox vbox = new VBox();
            vbox.setSpacing(5);
            vbox.getChildren().add(table);

            rootLayout.setCenter(vbox);
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }
    }

    public void showWorkflowRunAttemptCount() {
        try {

            Date endDate = new Date();

            Calendar c = Calendar.getInstance();
            c.setTime(endDate);
            c.add(Calendar.WEEK_OF_YEAR, -1);
            Date startDate = c.getTime();

            WorkflowDAO workflowDAO = daoMgr.getMaPSeqDAOBean().getWorkflowDAO();
            List<Workflow> workflows = workflowDAO.findAll();

            WorkflowRunAttemptDAO workflowRunAttemptDAO = daoMgr.getMaPSeqDAOBean().getWorkflowRunAttemptDAO();

            List<PieChart.Data> data = new ArrayList<PieChart.Data>();
            for (Workflow key : workflows) {
                List<WorkflowRunAttempt> attempts = workflowRunAttemptDAO.findByCreatedDateRangeAndWorkflowId(
                        startDate, endDate, key.getId());
                List<WorkflowRunAttempt> doneAttempts = new ArrayList<WorkflowRunAttempt>();
                if (attempts != null && !attempts.isEmpty()) {
                    for (WorkflowRunAttempt attempt : attempts) {
                        if (attempt.getStatus().equals(WorkflowRunAttemptStatusType.DONE)) {
                            doneAttempts.add(attempt);
                        }
                    }
                    data.add(new PieChart.Data(key.getName(), doneAttempts.size()));
                }
            }

            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(data);
            PieChart chart = new PieChart(pieChartData);
            chart.setCache(false);
            chart.setTitle("WorkflowRunAttempt Counts");
            chart.setLegendSide(Side.LEFT);

            final VBox vbox = new VBox();
            vbox.setSpacing(5);
            vbox.getChildren().add(chart);

            rootLayout.setCenter(vbox);
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }
    }

    public void showWorkflowRunAttemptDuration() {

        try {

            Date endDate = new Date();

            Calendar c = Calendar.getInstance();
            c.setTime(endDate);
            c.add(Calendar.WEEK_OF_YEAR, -1);
            Date startDate = c.getTime();

            WorkflowDAO workflowDAO = daoMgr.getMaPSeqDAOBean().getWorkflowDAO();
            List<Workflow> workflows = workflowDAO.findAll();

            WorkflowRunAttemptDAO workflowRunAttemptDAO = daoMgr.getMaPSeqDAOBean().getWorkflowRunAttemptDAO();

            List<PieChart.Data> data = new ArrayList<PieChart.Data>();
            for (Workflow key : workflows) {
                List<WorkflowRunAttempt> attempts = workflowRunAttemptDAO.findByCreatedDateRangeAndWorkflowId(
                        startDate, endDate, key.getId());
                List<WorkflowRunAttempt> doneAttempts = new ArrayList<WorkflowRunAttempt>();
                if (attempts != null && !attempts.isEmpty()) {
                    for (WorkflowRunAttempt attempt : attempts) {
                        if (attempt.getStatus().equals(WorkflowRunAttemptStatusType.DONE)) {
                            doneAttempts.add(attempt);
                        }
                    }
                    int duration = 0;
                    for (WorkflowRunAttempt attempt : doneAttempts) {
                        Date sDate = attempt.getStarted();
                        Date eDate = attempt.getFinished();
                        long value = TimeUnit.MILLISECONDS.toMinutes(eDate.getTime() - sDate.getTime());
                        duration += value;
                    }
                    PieChart.Data pieChartData = new PieChart.Data(key.getName(), duration);
                    data.add(pieChartData);
                }
            }

            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(data);
            PieChart chart = new PieChart(pieChartData);

            chart.setTitle("WorkflowRunAttempt Duration");
            chart.setLegendSide(Side.LEFT);

            final VBox vbox = new VBox();
            vbox.setSpacing(5);
            vbox.getChildren().add(chart);

            rootLayout.setCenter(vbox);
        } catch (MaPSeqDAOException e) {
            e.printStackTrace();
        }

    }

    public RSDAOManager getDaoMgr() {
        return daoMgr;
    }

    public void setDaoMgr(RSDAOManager daoMgr) {
        this.daoMgr = daoMgr;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public BorderPane getRootLayout() {
        return rootLayout;
    }

    public void setRootLayout(BorderPane rootLayout) {
        this.rootLayout = rootLayout;
    }

    public static void main(String[] args) {
        try {
            Main.launch(Main.class, args);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

}
