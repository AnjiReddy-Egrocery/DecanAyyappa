package com.dst.ayyapatelugu.Model;

import java.io.File;

public interface MergeCallback {
    void onSuccess(File file);
    void onError(Exception e);
}
