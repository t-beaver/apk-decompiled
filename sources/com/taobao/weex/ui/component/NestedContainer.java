package com.taobao.weex.ui.component;

import android.view.ViewGroup;
import com.taobao.weex.WXSDKInstance;

public interface NestedContainer {

    public interface OnNestedInstanceEventListener {
        void onCreated(NestedContainer nestedContainer, WXSDKInstance wXSDKInstance);

        void onException(NestedContainer nestedContainer, String str, String str2);

        boolean onPreCreate(NestedContainer nestedContainer, String str);

        String transformUrl(String str);
    }

    ViewGroup getViewContainer();

    void reload();

    void renderNewURL(String str);

    void setOnNestEventListener(OnNestedInstanceEventListener onNestedInstanceEventListener);
}
