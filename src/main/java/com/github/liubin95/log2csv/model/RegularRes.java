package com.github.liubin95.log2csv.model;

import java.util.List;

public record RegularRes(List<String> res, String resStr, int regularEnd) {
  private static final String SEPARATOR = ",";

  public RegularRes(List<String> res, int regularEnd) {
    this(
        res,
        String.join(
            SEPARATOR,
            res.stream()
                .map(
                    it -> {
                      if (it.contains(SEPARATOR)) {
                        return "\"" + it.replace("\"", "\"\"") + "\"";
                      }
                      return it;
                    })
                .toList()),
        regularEnd);
  }
}
