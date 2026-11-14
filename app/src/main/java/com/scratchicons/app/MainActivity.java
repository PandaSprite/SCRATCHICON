package com.scratchicons.app;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.*;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
public class MainActivity extends Activity {
    int bg = Color.rgb(20,18,24);
    int white = Color.WHITE;
    int muted = Color.LTGRAY;
    int accent = Color.rgb(208,188,255);
    TextView makeText(String value, int size, int color) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        return t;
    }
    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(bg);
        getWindow().setNavigationBarColor(bg);
        LinearLayout page = new LinearLayout(this);
        page.setOrientation(1);
        page.setPadding(24,32,24,24);
        page.setBackgroundColor(bg);
    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(bg);
        getWindow().setNavigationBarColor(bg);
        LinearLayout page = new LinearLayout(this);
        page.setOrientation(1);
        page.setPadding(24,32,24,24);
        page.setBackgroundColor(bg);
        TextView subtitle = makeText("A home for your custom app artwork
.",14,muted);        LinearLayout.LayoutParams subParams = new LinearLayout.LayoutPar
ams(-1,-2);        subParams.topMargin = 8;        page.addView(subtitle,subParams);        MaterialButt
on importButton 
= new Mate

rialButton(this);        importButton.setText("＋  Import icon");
        importButton.setTextColor(Color.rgb(45,35,60));        importButton.setBackgroundTintList(a
ndroid.content.res.ColorS
tateList.valueOf(accent));        LinearLayout.LayoutParams buttonParam
s = new LinearLayout.LayoutParams(-1,56);        buttonParams.topMargin = 28;        page.addView(importButton,buttonParams);  
      importBut
ton.setOnClickListener(v ->            Toast.mak

eText(this,"Image importing is coming next!",Toas
t.LENGTH_SHORT).show());        MaterialCardView card = new MaterialCardV

iew(this);        card.setRadius(22);        card.setCardBack
groundColor(Color.rgb(39
,36,44));        card.setCardElevation(0);        L


inearLayout content = new LinearLayout(this);        content.setOrientation(1);        content.setPadding(20,20,20,24);
        TextView ti
tle = make
Text("Your collection",19,white);        ti
tle.
setTypeface(null,1);        content.addView(title);        content.addView(makeText("

0 icons",13,muted));        TextView empty = makeText("Nothing here yet",18,white);        empty.setGravity(Gravity.CENT

ER);        Li
nearLayout.LayoutParams emptyParams = new LinearLayout.LayoutParams(-1,1
10);        emptyParams.topMargin = 16;        content.addView(empty,emptyParams);        Text
View hint = makeText(
"Import a drawin
g
 to start building your collection.",13,muted);        hint.setGravity(Gravity.CENTER);        content.addVi
ew(hint);        card.addView(content);        Line
arLayout.LayoutParams cardParams =

 new Li
nearLayout.LayoutParams(-1,-2);        cardParams.topMargin = 28;        page.addView(card,cardParams);        setConten
tView(page);  
  }}