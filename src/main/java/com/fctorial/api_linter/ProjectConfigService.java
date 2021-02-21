package com.fctorial.api_linter;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.util.FileContentUtil;
import com.intellij.util.messages.MessageBus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@State(name = "ProjectConfig", storages = { @Storage("api_linter_proj.xml")})
public class ProjectConfigService implements PersistentStateComponent<ProjectConfigService> {
    private String jsonFile;
    private String yamlFile;
    private List<String> importPaths;

    private ProjectConfigService() {

    }

    private ProjectConfigService(Project project) {
        jsonFile = new File(project.getBasePath(), ".api-linter.json").getPath();
        yamlFile = new File(project.getBasePath(), ".api-linter.yaml").getPath();
        importPaths = new ArrayList<>();
        importPaths.add(project.getBasePath());
        MessageBus messageBus = project.getMessageBus();
        messageBus.connect().subscribe(VirtualFileManager.VFS_CHANGES, new BulkFileListener() {
            @Override
            public void after(List<? extends VFileEvent> events) {
                for (VFileEvent ev : events) {
                    if (ev.getPath().equals(jsonFile) || ev.getPath().equals(yamlFile)) {
                        reparseProtos(project);
                        return;
                    }
                }
            }
        });
    }
    private void reparseProtos(Project project) {
        if (!project.isDisposed()) {
            VirtualFile[] files = FileEditorManager.getInstance(project).getOpenFiles();
            ArrayList<VirtualFile> fileList = new ArrayList<>(files.length);

            PsiManager psiManager = PsiManager.getInstance(project);
            for (VirtualFile file : files) {
                PsiFile psiFile = psiManager.findFile(file);
                String path = psiFile.getVirtualFile().getPath();
                if (path.endsWith(".proto")){
                    fileList.add(psiFile.getVirtualFile());
                }
            }
            FileContentUtil.reparseFiles(project, fileList, false);
        }
    }

    private String getConfigFilePath() {
        if (new File(jsonFile).exists() && new File(jsonFile).isFile()) {
            return jsonFile;
        }
        if (new File(yamlFile).exists() && new File(yamlFile).isFile()) {
            return yamlFile;
        }
        return null;
    }

    public static String getConfigFilePath(Project project) {
        return project.getService(ProjectConfigService.class).getConfigFilePath();
    }

    public static List<String> getImportPaths(Project project) {
        return project.getService(ProjectConfigService.class).importPaths;
    }

    public static void doReparse(Project project) {
        project.getService(ProjectConfigService.class).reparseProtos(project);
    }

    @Override
    public @Nullable ProjectConfigService getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull ProjectConfigService projectConfigService) {
        if (projectConfigService.getImportPaths() != null)
            setImportPaths(projectConfigService.getImportPaths());
    }

    public String getJsonFile() {
        return jsonFile;
    }

    public void setJsonFile(String jsonFile) {
        this.jsonFile = jsonFile;
    }

    public String getYamlFile() {
        return yamlFile;
    }

    public void setYamlFile(String yamlFile) {
        this.yamlFile = yamlFile;
    }

    public List<String> getImportPaths() {
        return importPaths;
    }

    public void setImportPaths(List<String> p) {
        importPaths = p;
    }
}
