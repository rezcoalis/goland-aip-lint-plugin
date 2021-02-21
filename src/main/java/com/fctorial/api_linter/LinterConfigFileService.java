package com.fctorial.api_linter;

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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LinterConfigFileService {
    private final File jsonFile;
    private final File yamlFile;

    private LinterConfigFileService(Project project) {
        jsonFile = new File(project.getBasePath(), ".api-linter.json");
        yamlFile = new File(project.getBasePath(), ".api-linter.yaml");
        MessageBus messageBus = project.getMessageBus();
        messageBus.connect().subscribe(VirtualFileManager.VFS_CHANGES, new BulkFileListener() {
            @Override
            public void after(List<? extends VFileEvent> events) {
                for (VFileEvent ev : events) {
                    if (ev.getPath().equals(jsonFile.getPath()) || ev.getPath().equals(yamlFile.getPath())) {
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
                            System.out.println("nice");
                            FileContentUtil.reparseFiles(project, fileList, false);
                        }
                    }
                }
            }
        });

    }

    private String getConfigFilePath() {
        if (jsonFile.exists() && jsonFile.isFile()) {
            return jsonFile.getPath();
        }
        if (yamlFile.exists() && yamlFile.isFile()) {
            return yamlFile.getPath();
        }
        return null;
    }

    public static String getConfigFilePath(Project project) {

        return project.getService(LinterConfigFileService.class).getConfigFilePath();
    }
}
