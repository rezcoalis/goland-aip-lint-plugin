package dev.alis.os.api_linter;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

public class URIAction implements IntentionAction {
    private final String id;
    private final String uri;

    public URIAction(String id, String uri) {
        this.id = id;
        this.uri = uri;
    }

    @Override
    public @IntentionName @NotNull String getText() {
        return id;
    }

    @Override
    public @NotNull @IntentionFamilyName String getFamilyName() {
        return "DocIntention";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile psiFile) {
        return true;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile psiFile) throws IncorrectOperationException {
        Utils.browse(project, uri);
    }

    @Override
    public boolean startInWriteAction() {
        return false;
    }
}
