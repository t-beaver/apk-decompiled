package com.dcloud.android.v4.view;

import android.view.MotionEvent;

class MotionEventCompatGingerbread {
    MotionEventCompatGingerbread() {
    }

    public static int getSource(MotionEvent motionEvent) {
        return motionEvent.getSource();
    }
}
