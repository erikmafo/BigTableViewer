package com.erikmafo.ltviewer.ui.projectexplorer;

import com.erikmafo.ltviewer.services.project.RemoveProjectService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;

import javax.inject.Inject;

public class ProjectItemMenuController {

    private final RemoveProjectService removeProjectService;

    @FXML
    private MenuItem addInstance;

    @FXML
    private MenuItem removeProject;

    private String projectId;

    @Inject
    public ProjectItemMenuController(RemoveProjectService removeProjectService) {
        this.removeProjectService = removeProjectService;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    @FXML
    public void handleRemoveProject(ActionEvent actionEvent) {
        removeProjectService.setProjectId(projectId);
        removeProjectService.restart();
    }
}
