package com.github.liubin95.log2csv.dialog;

import com.github.liubin95.log2csv.Log2csvBundle;
import com.github.liubin95.log2csv.listener.RegexProcessListener;
import com.github.liubin95.log2csv.model.RegularRes;
import com.github.liubin95.log2csv.services.TextChangeHandler;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.observable.properties.AtomicProperty;
import com.intellij.openapi.observable.properties.ObservableMutableProperty;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.JBColor;
import com.intellij.ui.TextFieldWithAutoCompletionWithCache;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextArea;
import com.intellij.util.ui.FormBuilder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.*;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Log2csvDialog extends DialogWrapper implements RegexProcessListener {
  private static final Logger LOG = Logger.getInstance(Log2csvDialog.class);
  private static final Highlighter.HighlightPainter PAINTER =
      new DefaultHighlighter.DefaultHighlightPainter(JBColor.YELLOW);

  private final DialogModel model = new DialogModel();
  private final Project project;
  private JBTextArea matchTextArea;

  public Log2csvDialog(@Nullable Project project, java.util.List<String> previewString) {
    super(project, false);
    this.setTitle("Log2csv");
    model.setPreviewString(previewString);
    model.metaLog = previewString;
    this.project = project;
    // 必须调用
    init();
  }

  public static boolean isValidRegex(String regex) {
    try {
      Pattern.compile(regex);
      return true; // 正则表达式合法
    } catch (PatternSyntaxException e) {
      return false; // 正则表达式不合法
    }
  }

  public String getRegularString() {
    return this.model.regularString.get();
  }

  @Override
  protected @Nullable JComponent createCenterPanel() {
    final var width = 600;
    final JBLabel placeholder = new JBLabel(Log2csvBundle.message("placeholder"));
    // 添加一个 文本输入框
    // todo 保存用户历史输入记录
    final var regularInput =
        TextFieldWithAutoCompletionWithCache.create(
            this.project, List.of("^(?<time>[^ ]*) {2}(?<level>[A-Z]*)"), false, "");
    // 监听输入变化
    regularInput
        .getDocument()
        .addDocumentListener(
            new DocumentListener() {
              @Override
              public void documentChanged(@NotNull DocumentEvent event) {
                model.regularString.set(event.getDocument().getText());
              }
            });
    this.model.regularString.afterChange(
        e -> {
          // 更新预览
          if (StringUtils.isBlank(this.model.regularString.get())
              || !isValidRegex(this.model.regularString.get())) {
            this.model.setPreviewString(this.model.metaLog);
            return null;
          }
          new TextChangeHandler(this.model.regularString.get(), this.model.metaLog, this).execute();
          return null;
        });

    // 正则匹配结果预览的窗口
    final var previewMatchPanel = new JBScrollPane();
    previewMatchPanel.setPreferredSize(new Dimension(width, 150));
    this.matchTextArea = new JBTextArea(model.previewString.get());
    this.matchTextArea.setEditable(false);
    previewMatchPanel.setViewportView(this.matchTextArea);

    // csv 预览的窗口
    final var previewPane = new JBScrollPane();
    // 设置滚动面板的首选大小
    previewPane.setPreferredSize(new Dimension(width, 300));
    // 添加一个多行文本框
    final var previewTextArea = new JBTextArea(model.previewString.get());
    previewTextArea.setEditable(false);
    // 监听预览变化
    this.model.previewString.afterChange(
        e -> {
          previewTextArea.setText(model.previewString.get());
          return null;
        });
    previewPane.setViewportView(previewTextArea);

    // 使用 FormBuilder 将组件添加到面板中
    return FormBuilder.createFormBuilder()
        .addLabeledComponent(placeholder, regularInput)
        .addComponent(previewMatchPanel)
        .addComponent(previewPane)
        .getPanel();
  }

  @Override
  public void onMatchFound(RegularRes line) {
    LOG.debug("onMatchFound: {}", line);
  }

  @Override
  public void onProcessComplete(List<RegularRes> results) {
    // 更新预览
    model.setPreviewString(results.stream().map(RegularRes::resStr).toList());
    // 更新高亮
    final Highlighter highlighter = this.matchTextArea.getHighlighter();
    highlighter.removeAllHighlights();
    for (int line = 0; line < this.matchTextArea.getLineCount(); line++) {
      try {
        // 第一行是标题，跳过
        var resItem = results.get(line + 1);
        int start = this.matchTextArea.getLineStartOffset(line);
        int end = start + resItem.regularEnd();
        highlighter.addHighlight(start, end, PAINTER);
      } catch (Exception e) {
        LOG.error("onProcessComplete: ", e);
      }
    }
  }

  static class DialogModel {
    private final ObservableMutableProperty<String> regularString = new AtomicProperty<>("");
    private final ObservableMutableProperty<String> previewString = new AtomicProperty<>("");
    private List<String> metaLog = new ArrayList<>();

    public void setPreviewString(List<String> previewString) {
      this.previewString.set(String.join(System.lineSeparator(), previewString));
    }
  }
}
