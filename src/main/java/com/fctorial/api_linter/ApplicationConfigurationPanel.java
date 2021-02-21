package com.fctorial.api_linter;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.TextFieldWithHistoryWithBrowseButton;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ApplicationConfigurationPanel implements SearchableConfigurable {
    private static final Logger LOGGER = Logger.getInstance(ApplicationConfigurationPanel.class.getPackage().getName());
    private final LinterPathService state;

    private TextFieldWithHistoryWithBrowseButton apiLinterExePathField;
    private JPanel rootPanel;

    public ApplicationConfigurationPanel() {
        this.state = LinterPathService.getInstance().getState();
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
        return !apiLinterExePathField.getText().equals(state.executable);
    }

    @Override
    public void apply() throws ConfigurationException {
        state.executable = apiLinterExePathField.getText();
    }

    @Override
    public void reset() {
        loadSettings();
    }

    private void loadSettings() {
        apiLinterExePathField.setText(state.executable);
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
    }
}
