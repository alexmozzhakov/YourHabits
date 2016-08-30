package com.yongchun.library.utils;


import com.yongchun.library.model.LocalMediaFolder;

import java.util.List;

public interface LocalMediaLoadListener {
    void loadComplete(List<LocalMediaFolder> folders);
}