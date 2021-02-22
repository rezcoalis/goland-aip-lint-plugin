package com.fctorial.api_linter;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.ExternalAnnotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static org.locationtech.jts.math.MathUtil.clamp;

public class AIPAnnotator extends ExternalAnnotator<Editor, List<AIPWarning>> {
    private static final Logger LOGGER = Logger.getInstance(AIPAnnotator.class);

    @Nullable
    @Override
    public Editor collectInformation(@NotNull PsiFile file, @NotNull Editor editor, boolean hasErrors) {
        return editor;
    }

    @Nullable
    @Override
    public List<AIPWarning> doAnnotate(Editor editor) {
        return AIPExecuter.getWarnings(editor.getProject() ,editor.getDocument().getText());
    }

    @Override
    public void apply(@NotNull PsiFile file, List<AIPWarning> warnings, @NotNull AnnotationHolder holder) {
        Document document = PsiDocumentManager.getInstance(file.getProject()).getDocument(file);
        if (document == null) {
            return;
        }
        warnings.forEach(warning -> {
            int startOffset = StringUtil.lineColToOffset(file.getText(), warning.y1, warning.x1);
            startOffset = clamp(startOffset, 0, file.getTextLength()-1);
            int endOffset = StringUtil.lineColToOffset(file.getText(), warning.y2, warning.x2);
            if (endOffset == -1) {
                endOffset = file.getTextLength()-1;
            }
            endOffset = clamp(endOffset, 0, file.getTextLength()-1);
            if (startOffset >= endOffset) {
                return;
            }
            TextRange range = new TextRange(startOffset, endOffset);
            holder.newAnnotation(HighlightSeverity.WARNING, warning.rule_id + ": " + warning.reason)
                    .range(range)
                    .withFix(new URIAction(warning.rule_id, warning.rule_doc_uri))
                    .create();
        });
    }
}

