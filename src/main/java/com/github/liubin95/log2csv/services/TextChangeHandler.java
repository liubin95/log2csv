package com.github.liubin95.log2csv.services;

import com.github.liubin95.log2csv.listener.RegexProcessListener;
import com.github.liubin95.log2csv.model.RegularRes;
import com.intellij.openapi.diagnostic.Logger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.*;
import org.jetbrains.annotations.NotNull;

public class TextChangeHandler extends SwingWorker<List<RegularRes>, RegularRes> {
  private static final Logger LOG = Logger.getInstance(TextChangeHandler.class);
  // 正则表达式模式，用于匹配命名捕获组的定义
  private static final Pattern NAMED_GROUP_PATTERN =
      Pattern.compile("\\(\\?<([a-zA-Z][a-zA-Z0-9]*)>");

  private final String regular;
  private final List<String> texts;
  private final RegexProcessListener listener;

  public TextChangeHandler(String regular, List<String> texts, RegexProcessListener listener) {
    LOG.debug("TextChangeHandler init regular [{}] texts length", regular, texts.size());
    this.regular = regular;
    this.texts = texts;
    this.listener = listener;
  }

  /**
   * 从正则表达式模式字符串中提取命名捕获组的名称。
   *
   * @param regex 正则表达式模式字符串
   * @return 命名捕获组名称的列表
   */
  public static @NotNull List<String> extractNamedGroups(String regex) {
    List<String> namedGroups = new ArrayList<>();
    Matcher matcher = NAMED_GROUP_PATTERN.matcher(regex);
    while (matcher.find()) {
      namedGroups.add(matcher.group(1));
    }
    return namedGroups;
  }

  private @NotNull RegularRes getRegularRes(
      String text, @NotNull Pattern pattern, List<String> namedGroups) {
    var matcher = pattern.matcher(text);
    final var matched = new ArrayList<String>();
    int end = 0;
    if (matcher.find()) {
      for (String groupName : namedGroups) {
        try {
          matched.add(matcher.group(groupName));
        } catch (IllegalStateException | IllegalArgumentException ignored) {
          matched.add("");
        }
      }
      end = matcher.end();
    }
    return new RegularRes(matched, end);
  }

  @Override
  protected void done() {
    try {
      listener.onProcessComplete(get());
    } catch (InterruptedException | ExecutionException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  protected void process(List<RegularRes> chunks) {
    for (RegularRes res : chunks) {
      listener.onMatchFound(res);
    }
  }

  @Override
  protected List<RegularRes> doInBackground() {
    final var result = new ArrayList<RegularRes>();
    // 处理正则表达式转义
    final var pattern = Pattern.compile(this.regular);
    final var namedGroups = TextChangeHandler.extractNamedGroups(this.regular);

    // publish title
    RegularRes titleRes = new RegularRes(namedGroups, 0);
    publish(titleRes);
    result.add(titleRes);

    // 获取所有命名组
    for (String text : this.texts) {
      final RegularRes rowRes = this.getRegularRes(text, pattern, namedGroups);
      // publish row
      publish(rowRes);
      result.add(rowRes);
    }
    return result;
  }
}
