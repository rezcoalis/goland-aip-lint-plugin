package com.fctorial.api_linter;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.TextFieldWithHistoryWithBrowseButton;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ApplicationConfigurationPanel implements SearchableConfigurable {
    private static final Logger LOGGER = Logger.getInstance(ApplicationConfigurationPanel.class.getPackage().getName());
    private final LinterPathService appState;
    private final ProjectConfigService projState;
    private final Project project;

    private TextFieldWithHistoryWithBrowseButton apiLinterExePathField;
    private JPanel rootPanel;
    private TextFieldWithHistoryWithBrowseButton importPathField;

    public ApplicationConfigurationPanel(Project project) {
        this.project = project;
        this.appState = LinterPathService.getInstance().getState();
        this.projState = project.getService(ProjectConfigService.class);
    }

    @NotNull
    @Override
    public String getId() {
        return "aip_linter";
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "AIP Linter";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        loadSettings();
        addListeners();
        return rootPanel;
    }

    @Override
    public boolean isModified() {
        return !apiLinterExePathField.getText().equals(appState.executable) || !importPathField.getText().equals(projState.importPath);
    }

    @Override
    public void apply() throws ConfigurationException {
        appState.executable = apiLinterExePathField.getText();
        projState.importPath = importPathField.getText();
        ProjectConfigService.doReparse(project);
    }

    @Override
    public void reset() {
        loadSettings();
    }

    private void loadSettings() {
        apiLinterExePathField.setText(appState.executable);
        importPathField.setText(projState.importPath);
    }

    private void addListeners() {
        apiLinterExePathField.addActionListener((ActionEvent event) -> {
            final VirtualFile file = FileChooser.chooseFile(
                    FileChooserDescriptorFactory.createSingleFileDescriptor(),
                    null,
                    null
            );
            if (file == null) {
                return;
            }
            apiLinterExePathField.setText(file.getPath());
        });
        importPathField.addActionListener((ActionEvent event) -> {
            final VirtualFile file = FileChooser.chooseFile(
                    FileChooserDescriptorFactory.createSingleFileDescriptor(),
                    null,
                    null
            );
            if (file == null) {
                return;
            }
            importPathField.setText(file.getPath());
        });
    }
}
