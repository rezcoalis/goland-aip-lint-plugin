package com.fctorial.api_linter;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.diagnostic.Logger;
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

    static LinterPathService getInstance() {
        LinterPathService service = ServiceManager.getService(LinterPathService.class);
        if (service.executable.equals("")) {
            for (String dirname : System.getenv("PATH").split(File.pathSeparator)) {
                File file = new File(dirname, "api-linter");
                if (file.isFile() && file.canExecute()) {
                    service.executable =  file.getAbsolutePath();
                    break;
                }
            }
        }
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

