package dev.alis.os.api_linter;

import com.intellij.openapi.components.*;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.Nullable;

import java.io.File;

@State(name = "LinterPathConfig", storages = { @Storage("api_linter.xml")})
public class LinterPathService implements PersistentStateComponent<LinterPathService> {
    private static final Logger LOGGER = Logger.getInstance(LinterPathService.class.getPackage().getName());

    public String executable = "";

    public LinterPathService() {
        LOGGER.debug("ProjectService instantiated.");
    }

    static LinterPathService getInstance(Project project) {
        LinterPathService service = project.getService(LinterPathService.class);
        if (service.executable.equals("")) {
            for (String dirname : System.getenv("PATH").split(File.pathSeparator)) {
                System.out.println("Trying " + dirname);
                File file = new File(dirname, "api-linter");
                if (file.isFile() && file.canExecute()) {
                    service.executable =  file.getAbsolutePath();
                    break;
                }
                File exe = new File(dirname, "api-linter.exe");
                if (exe.isFile() && exe.canExecute()) {
                    service.executable =  exe.getAbsolutePath();
                    break;
                }
            }
        }
        System.out.println("Found " + service.executable);
        return service;
    }

    @Nullable
    @Override
    public LinterPathService getState() {
        return this;
    }

    @Override
    public void loadState(LinterPathService state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    @Override
    public String toString() {
        return "LinterPathConfigService[" + this.executable + "]";
    }

}

