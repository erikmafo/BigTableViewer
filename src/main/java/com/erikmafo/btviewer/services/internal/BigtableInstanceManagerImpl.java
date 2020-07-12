package com.erikmafo.btviewer.services.internal;

import com.erikmafo.btviewer.model.BigtableInstance;
import com.google.gson.Gson;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class BigtableInstanceManagerImpl implements BigtableInstanceManager {

    private final Object mutex = new Object();
    private final Gson gson = new Gson();

    private ProjectDatabase projectDb;

    public BigtableInstanceManagerImpl() {}

    @Override
    public List<String> getProjects() throws IOException {
        return loadProjectDb()
                .getProjects()
                .stream()
                .map(Project::getProjectId)
                .collect(Collectors.toList());
    }

    @Override
    public void removeProject(String projectId) throws IOException {
        var projectDb = loadProjectDb();
        projectDb.removeProject(projectId);
        saveProjectDb();
    }

    @Override
    public List<BigtableInstance> getInstances(String projectId) throws IOException {
        return loadProjectDb().getProjects().stream()
                .filter(p -> projectId.equals(p.getProjectId()))
                .map(Project::getInstances)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    @Override
    public void addInstance(BigtableInstance instance) throws IOException {

        synchronized (mutex) {

        }
        var projectDb = loadProjectDb();
        var project = projectDb.getProject(instance.getProjectId());
        projectDb.addProject();
        saveProjectDb();
    }

    @Override
    public void removeInstance(BigtableInstance instance) throws IOException {
        var projectDb = loadProjectDb();
        projectDb.removeProject();
    }

    private ProjectDatabase loadProjectDb() throws IOException {
        synchronized (mutex) {
            if (projectDb != null) {
                return projectDb;
            }
            var path = getProjectDbPath();
            if (!Files.exists(path)) {
                projectDb = new ProjectDatabase();
            }
            var json = Files.readString(path);
            projectDb = gson.fromJson(json, ProjectDatabase.class);
            return projectDb;
        }
    }

    private void saveProjectDb() throws IOException {
        synchronized (mutex) {
            if (projectDb == null) {
                return;
            }
            var json = gson.toJson(projectDb);
            Files.writeString(getProjectDbPath(), json);
        }
    }

    private Path getProjectDbPath() {
        return AppDataUtil
                .getStorageFolder()
                .resolve("projectdb.json");
    }

    private String getName(Path p) {
        return p.getFileName().toString();
    }

    private static class ProjectDatabase {

        private List<Project> projects = new ArrayList<>();

        public Project getProject(String projectId) {
            return projects.stream()
                    .filter(p -> projectId.equals(p.getProjectId()))
                    .findFirst()
                    .orElse(null);
        }

        public void addProject(Project project) {
            if (projects == null) {
                projects = new ArrayList<>();
            }

            if (getProject(project.getProjectId()) != null) {
                projects.add(new Project(project));
            }
        }

        public void removeProject(String projectId) {
            projects.removeIf(p -> projectId.equals(p.getProjectId()));
        }

        public void addInstance(BigtableInstance instance) {

        }

        public void removeInstance(BigtableInstance instance) {
            var project = getProject(instance.getProjectId());
            if (project != null) {
                project.removeInstance(instance);
            }
        }

        public List<Project> getProjects() {
            return projects;
        }

        public void setProjects(List<Project> projects) {
            this.projects = projects;
        }
    }

    private static class Project {

        private String projectId;
        private List<BigtableInstance> instances = new ArrayList<>();

        public Project() {

        }

        public Project(String projectId, List<BigtableInstance> instances) {
            this.projectId = projectId;
            this.instances = instances.stream().map(BigtableInstance::new).collect(Collectors.toList());
        }

        public Project(Project project) {
            this.projectId = project.getProjectId();
            this.instances = project.getInstances();
        }

        public void removeInstance(BigtableInstance instance) {
            instances.remove(instance);
        }

        public void addInstance(BigtableInstance instance) {
            if (instances == null) {
                instances = new ArrayList<>();
            }

            instances.add(instance);
        }

        public String getProjectId() {
            return projectId;
        }

        public List<BigtableInstance> getInstances() {
            return instances.stream().map(BigtableInstance::new).collect(Collectors.toList());
        }

        public void setInstances(List<BigtableInstance> instances) {
            this.instances = instances;
        }

        public void setProjectId(String projectId) {
            this.projectId = projectId;
        }
    }
}



