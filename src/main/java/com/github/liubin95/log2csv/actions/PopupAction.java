package com.github.liubin95.log2csv.actions;

import com.github.liubin95.log2csv.dialog.Log2csvDialog;
import com.github.liubin95.log2csv.listener.RegexProcessListener;
import com.github.liubin95.log2csv.model.RegularRes;
import com.github.liubin95.log2csv.services.TextChangeHandler;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.vfs.VirtualFile;
import java.io.*;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class PopupAction extends AnAction implements RegexProcessListener {
  private BufferedWriter writer;

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    // 获取选中的文件
    final var file = e.getData(CommonDataKeys.VIRTUAL_FILE);

    if (file == null || file.isDirectory()) {
      // 例如：只在选中单个文件时启用
      e.getPresentation().setEnabledAndVisible(false);
      return;
    }
    List<String> lines;
    try (final var inputStream = file.getInputStream();
        final var reader = new BufferedReader(new InputStreamReader(inputStream))) {
      lines = reader.lines().toList();
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
    // 获取文件前5行
    final var linesTop5 = lines.stream().limit(5).toList();
    final var dialog = new Log2csvDialog(e.getProject(), linesTop5);

    // 显示对话框，点击 OK 返回 true
    if (dialog.showAndGet()) {
      // 处理用户输入
      final String regularString = dialog.getRegularString();
      // 新建文件
      final VirtualFile parentDir = file.getParent();
      if (parentDir == null) {
        return;
      }
      WriteAction.run(
          () -> {
            try {
              final String csvFileName = file.getNameWithoutExtension() + ".csv";
              VirtualFile csvFile = parentDir.findChild(csvFileName);
              if (csvFile == null) {
                csvFile = parentDir.createChildData(this, csvFileName);
              }
              this.writer =
                  new BufferedWriter(new OutputStreamWriter(csvFile.getOutputStream(this)));
              new TextChangeHandler(regularString, lines, this).execute();
            } catch (IOException ex) {
              throw new RuntimeException(ex);
            }
          });
    }
  }

  @Override
  public void onMatchFound(RegularRes line) {
    // do nothing
    // todo 一条一条的写入文件
  }

  @Override
  public void onProcessComplete(List<RegularRes> results) {
    // 在 EDT 上执行写操作
    // Schedule the write operation on the EDT with proper context
    ApplicationManager.getApplication()
        .invokeLater(
            () ->
                WriteAction.run(
                    () -> {
                      // write data
                      try {
                        for (RegularRes result : results) {
                          this.writer.write(result.resStr());
                          this.writer.newLine();
                        }
                        this.writer.flush();
                        this.writer.close();
                      } catch (IOException e) {
                        throw new RuntimeException(e);
                      }
                    }));
  }
}
