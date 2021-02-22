package com.fctorial.api_linter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class AIPExecuter {
    private static final Logger LOGGER = Logger.getInstance(AIPExecuter.class.getPackage().getName());

    static List<AIPWarning> getWarnings(Project project, String fileContent) {
        Path tmp;
        try {
            tmp = Files.createTempFile(null, ".proto");
            writeData(fileContent, tmp);
        } catch (IOException ex) {
            LOGGER.error("Filesystem access error, couldn't run api-linter", ex);
            return new ArrayList<>();
        }

        try {
            return runLinter(project, tmp);
        } finally {
            try {
                Files.delete(tmp);
            } catch (IOException ex) {
                LOGGER.warn("Error while deleting temp file");
            }
        }
    }

    private static List<AIPWarning> runLinter(Project project, Path path) {
        // ref. https://intellij-support.jetbrains.com/hc/en-us/community/posts/360004284939-How-to-trigger-ExternalAnnotator-running-immediately-after-saving-the-code-change-
        final String executable = LinterPathService.getInstance().executable;
        if (executable.equals("")) {
            return null;
        }

        final GeneralCommandLine commandLine = new GeneralCommandLine();
        commandLine.setWorkDirectory(path.getParent().toString());
        commandLine.withEnvironment(System.getenv());

        commandLine.setExePath(executable);

        final String configFile = ProjectConfigService.getConfigFilePath(project);
        if (configFile != null) {
            commandLine.addParameter("--config");
            commandLine.addParameter(configFile);
        }

        for (String pe : ProjectConfigService.getImportPaths(project)) {
            if (! pe.equals("")) {
                commandLine.addParameter("-I");
                commandLine.addParameter(pe);
            }
        }

        commandLine.addParameter("--output-format");
        commandLine.addParameter("json");

        commandLine.addParameter(path.toString());

        try {
            return parseLinterOutput(commandLine.createProcess());
        } catch (Throwable ex) {
            LOGGER.error("Encountered error while running api-linter", ex);
            return null;
        }
    }

    static ObjectMapper mapper = new ObjectMapper();

    private static List<AIPWarning> parseLinterOutput(final Process process) throws IOException {
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            LOGGER.error("Linter process interrupted!");
            return null;
        }
        if (process.exitValue() != 0) {
            List<AIPWarning> result = new ArrayList<>();
            result.add(
                    new AIPWarning(
                            0, 0,
                            10000, 10000,
                            "api-linter executable exited with non-zero status",
                            "api-linter error",
                            ""

                    )
            );
            return result;
        } else {
            List<AIPWarning> result = new ArrayList<>();

            final InputStreamReader opStream = new InputStreamReader(process.getInputStream());

            LinterOutputModel op = mapper.readValue(opStream, LinterOutputModel[].class)[0];

            for (LinterWarning p : op.problems) {
                AIPWarning w = new AIPWarning(
                        // intellij uses 0 based indexing, linter emits 1 based indexes
                        p.location.start_position.line_number - 1,
                        p.location.start_position.column_number - 1,
                        p.location.end_position.line_number - 1,
                        // second location in linter output is inclusive, and intellij treats second location as exclusive
                        p.location.end_position.column_number,
                        p.message,
                        p.rule_id,
                        p.rule_doc_uri
                );
                result.add(w);
            }

            return result;
        }
    }

    private static void writeData(String doc, Path tmp) throws IOException {
        try (BufferedWriter out = Files.newBufferedWriter(tmp, StandardCharsets.UTF_8)) {
            out.write(doc);
        }
    }

}
