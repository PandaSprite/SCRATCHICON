package com.scratchicons.app;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView screen = new TextView(this);
        screen.setText("SCRATCHICONS");
        screen.setTextSize(28);
        screen.setGravity(17);
        setContentView(screen);
    }
}
