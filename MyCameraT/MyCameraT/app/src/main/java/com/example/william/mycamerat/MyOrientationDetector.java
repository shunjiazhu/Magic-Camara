package com.example.william.mycamerat;

import android.content.Context;
import android.util.Log;
import android.view.OrientationEventListener;

/**
 * @author zw.yan
 *
 */
public class MyOrientationDetector extends OrientationEventListener {
    int Orientation;
    public MyOrientationDetector(Context context ) {
        super(context );
    }
    @Override
    public void onOrientationChanged(int orientation) {
        this.Orientation=orientation;
    }
    public int getOrientation(){
        return Orientation;
    }
}