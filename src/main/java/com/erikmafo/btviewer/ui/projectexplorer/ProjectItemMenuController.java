package com.erikmafo.btviewer.ui.projectexplorer;

import com.erikmafo.btviewer.services.project.RemoveProjectService;
import com.erikmafo.btviewer.services.instance.SaveInstanceService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;

import javax.inject.Inject;

public class ProjectItemMenuController {

    @FXML
    private MenuItem addInstance;

    @FXML
    private MenuItem removeProject;

    private String projectId;

    private final RemoveProjectService removeProjectService;
    private final SaveInstanceService saveInstanceService;

    @Inject
    public ProjectItemMenuController(
            RemoveProjectService removeProjectService,
            SaveInstanceService saveInstanceService) {
        this.removeProjectService = removeProjectService;
        this.saveInstanceService = saveInstanceService;
    }

    @FXML
    public void initialize() {

    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    @FXML
    public void handleRemoveProject(ActionEvent actionEvent) {
        removeProjectService.setProjectId(projectId);
        removeProjectService.restart();
    }

    @FXML
    public void handleAddInstance(ActionEvent actionEvent) {

    }
}
