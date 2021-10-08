package dev.alis.os.api_linter;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.TextFieldWithHistoryWithBrowseButton;
import com.intellij.ui.components.*;
import com.intellij.util.Function;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class ApplicationConfigurationPanel implements SearchableConfigurable {
    private static final Logger LOGGER = Logger.getInstance(ApplicationConfigurationPanel.class.getPackage().getName());
    private final LinterPathService appState;
    private final ProjectConfigService projState;
    private final Project project;

    private TextFieldWithHistoryWithBrowseButton apiLinterExePathField;
    private JPanel rootPanel;
    private JButton addButton;
    private JScrollPane scroll;
    private JPanel panel;
    private ArrayList<String> importPathsList;

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
        if (!apiLinterExePathField.getText().equals(appState.executable)) {
            return true;
        }
        if (importPathsList.size() != projState.getImportPaths().size()) {
            return true;
        }
        for (int i = 0; i < importPathsList.size(); i++) {
            if (!importPathsList.get(i).equals(projState.getImportPaths().get(i))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void apply() {
        appState.executable = apiLinterExePathField.getText();
        projState.setImportPaths(new ArrayList<>(importPathsList));
        ProjectConfigService.doReparse(project);
    }

    @Override
    public void reset() {
        loadSettings();
    }

    private void loadSettings() {
        apiLinterExePathField.setText(appState.executable);
        importPathsList = new ArrayList<>(projState.getImportPaths());
        syncList();
    }

    private void syncList() {
        panel.removeAll();
        for (int i=0; i<importPathsList.size(); i++) {
            String path = importPathsList.get(i);
            int finalI = i;
            panel.add(
                    new ImportPathEntryUI(
                            path,
                            (String newValue) -> {
                                importPathsList.set(finalI, newValue);
                                syncList();
                                return null;
                            },
                            (Void v) -> {
                                importPathsList.remove(finalI);
                                syncList();
                                return null;
                            }
                    )
            );
        }
        panel.revalidate();
        panel.paintImmediately(0, 0, 100000, 100000);
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
        addButton.addActionListener((ActionEvent event) -> {
            final VirtualFile file = FileChooser.chooseFile(
                    FileChooserDescriptorFactory.createSingleFileDescriptor(),
                    null,
                    null
            );
            if (file == null) {
                return;
            }
            importPathsList.add(file.getPath());
            syncList();
        });
    }

    private void createUIComponents() {
        panel = new JBPanel<>();
        BoxLayout b = new BoxLayout(panel, BoxLayout.Y_AXIS);
        panel.setLayout(b);
    }
}

class ImportPathEntryUI extends JBPanel<ImportPathEntryUI> {
    private final String path;
    private final JButton removeBtn;
    JLabel text;

    public ImportPathEntryUI(String path, Function<String, Void> onEdit, Function<Void, Void> onDelete) {
        this.path = path;
        this.text = new JBLabel(path);
        this.text.setMaximumSize(new Dimension(10000, 30));
        this.text.setSize(0, 1);
        this.removeBtn = new JButton("-");
        this.removeBtn.setMaximumSize(new Dimension(100, 30));
        this.removeBtn.addActionListener((ActionEvent ev) -> onDelete.fun(null));
        BoxLayout layout = new BoxLayout(this, BoxLayout.X_AXIS);
        setLayout(layout);
        add(this.text);
        add(this.removeBtn);
    }
}

