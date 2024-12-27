package com.github.liubin95.log2csv.listener;

import com.github.liubin95.log2csv.model.RegularRes;
import java.util.List;

public interface RegexProcessListener {
    void onMatchFound(RegularRes line);

    void onProcessComplete(List<RegularRes> results);

//    void onError(String error);
}
