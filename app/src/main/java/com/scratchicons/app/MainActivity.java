package com.scratchicons.app;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.res.ColorStateList;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class MainActivity extends Activity {
    private final int background = Color.rgb(20, 18, 24);
    private final int surface = Color.rgb(39, 36, 44);
    private final int primaryText = Color.rgb(245, 240, 250);
    private final int secondaryText = Color.rgb(190, 184, 198);
    private final int accent = Color.rgb(208, 188, 255);

    private int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private TextView makeText(String value, float size, int color, boolean bold) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(size);
        view.setTextColor(color);
        if (bold) {
            view.setTypeface(null, android.graphics.Typeface.BOLD);
        }
        return view;
    }

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(background);
        getWindow().setNavigationBarColor(background);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(background);

        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(dp(24), dp(28), dp(24), dp(28));
        scroll.addView(page);

        TextView brand = makeText("SCRATCHICON", 14, accent, true);
        page.addView(brand);

        TextView heading = makeText("Your icons.\nYour style.", 32, primaryText, true);
        LinearLayout.LayoutParams headingParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        headingParams.topMargin = dp(28);
        page.addView(heading, headingParams);

        TextView subtitle = makeText("A home for your custom app artwork.", 14, secondaryText, false);
        LinearLayout.LayoutParams subtitleParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        subtitleParams.topMargin = dp(8);
        page.addView(subtitle, subtitleParams);

        MaterialButton importButton = new MaterialButton(this);
        importButton.setText("＋  Import icon");
        importButton.setTextColor(Color.rgb(45, 35, 60));
        importButton.setBackgroundTintList(ColorStateList.valueOf(accent));
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(56));
        buttonParams.topMargin = dp(28);
        page.addView(importButton, buttonParams);
        importButton.setOnClickListener(view -> Toast.makeText(
                this, "Image importing is coming next!", Toast.LENGTH_SHORT).show());

        MaterialCardView card = new MaterialCardView(this);
        card.setRadius(dp(22));
        card.setCardBackgroundColor(surface);
        card.setCardElevation(0);
        card.setStrokeWidth(dp(1));
        card.setStrokeColor(Color.rgb(68, 63, 75));

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(20), dp(20), dp(20), dp(24));

        TextView collectionTitle = makeText("Your collection", 19, primaryText, true);
        content.addView(collectionTitle);
        content.addView(makeText("0 icons", 13, secondaryText, false));

        TextView empty = makeText("Nothing here yet", 18, primaryText, true);
        empty.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams emptyParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(110));
        emptyParams.topMargin = dp(16);
        content.addView(empty, emptyParams);

        TextView hint = makeText(
                "Import a drawing to start building your collection.", 13, secondaryText, false);
        hint.setGravity(Gravity.CENTER);
        content.addView(hint);
        card.addView(content);

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cardParams.topMargin = dp(28);
        page.addView(card, cardParams);

        setContentView(scroll);
    }
}
