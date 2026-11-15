package com.scratchicons.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class MainActivity extends Activity {
    private Typeface scratchFont;

    private static final int WHITE = Color.WHITE;
    private static final int INK = Color.rgb(28, 16, 18);

    private static class IconItem {
        final String label;
        final String mappedApp;
        final int image;

        IconItem(String label, String mappedApp, int image) {
            this.label = label;
            this.mappedApp = mappedApp;
            this.image = image;
        }
    }

    private final IconItem[] icons = new IconItem[] {
            new IconItem("YouTube", "YouTube", R.drawable.ic_youtube),
            new IconItem("Chrome", "Chrome", R.drawable.ic_chrome),
            new IconItem("Gmail", "Gmail", R.drawable.ic_gmail),
            new IconItem("Maps", "Google Maps", R.drawable.ic_maps),
            new IconItem("Camera", "Camera", R.drawable.ic_camera),
            new IconItem("Phone", "Phone", R.drawable.ic_phone),
            new IconItem("Settings", "Settings", R.drawable.ic_settings),
            new IconItem("Photos", "Google Photos", R.drawable.ic_photos),
            new IconItem("Clock", "Clock", R.drawable.ic_clock),
            new IconItem("Play Store", "Google Play Store", R.drawable.ic_store),
            new IconItem("Contacts", "Contacts", R.drawable.ic_contacts),
            new IconItem("Files", "Files", R.drawable.ic_files)
    };

    private int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private LinearLayout.LayoutParams linearParams(int width, int height) {
        return new LinearLayout.LayoutParams(width, height);
    }

    private TextView text(String value, float size, int color, boolean bold) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(size);
        view.setTextColor(color);
        view.setTypeface(scratchFont, bold ? Typeface.BOLD : Typeface.NORMAL);
        view.setIncludeFontPadding(false);
        view.setShadowLayer(dp(3), 0, dp(2), Color.argb(185, 22, 10, 12));
        return view;
    }

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        scratchFont = Typeface.createFromAsset(getAssets(), "fonts/toppin-time-open.otf");

        getWindow().setStatusBarColor(Color.rgb(117, 47, 29));
        getWindow().setNavigationBarColor(Color.rgb(67, 22, 25));

        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(Color.rgb(215, 126, 38));

        ImageView wallpaper = new ImageView(this);
        wallpaper.setImageResource(R.drawable.pizza_background);
        wallpaper.setScaleType(ImageView.ScaleType.FIT_XY);
        root.addView(wallpaper, new FrameLayout.LayoutParams(-1, -1));

        View shade = new View(this);
        shade.setBackgroundColor(Color.argb(64, 31, 13, 15));
        root.addView(shade, new FrameLayout.LayoutParams(-1, -1));

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setClipToPadding(false);
        scroll.setBackgroundColor(Color.TRANSPARENT);

        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(dp(18), dp(22), dp(18), dp(34));
        page.setBackgroundColor(Color.TRANSPARENT);
        scroll.addView(page, new ScrollView.LayoutParams(-1, -2));

        // Hero: transparent over the pizza wallpaper, not a separate banner background.
        LinearLayout hero = new LinearLayout(this);
        hero.setOrientation(LinearLayout.VERTICAL);
        hero.setGravity(Gravity.CENTER);
        hero.setPadding(dp(8), dp(24), dp(8), dp(24));

        TextView title = text("SCRATCHICON", 39, WHITE, true);
        title.setGravity(Gravity.CENTER);
        hero.addView(title, linearParams(-1, -2));

        TextView tagline = text(
                "A hand-drawn icon pack, perfect for people who like scratchy stuff",
                16, WHITE, false);
        tagline.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams taglineParams = linearParams(-1, -2);
        taglineParams.topMargin = dp(12);
        hero.addView(tagline, taglineParams);

        MaterialButton apply = button("APPLY ICON PACK");
        LinearLayout.LayoutParams applyParams = linearParams(-1, dp(54));
        applyParams.topMargin = dp(22);
        hero.addView(apply, applyParams);
        apply.setOnClickListener(view -> showApplyInstructions());

        TextView applyHint = text("BUILT-IN ARTWORK  |  NO IMPORTS  |  NO SHORTCUTS", 10, WHITE, false);
        applyHint.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams hintParams = linearParams(-1, -2);
        hintParams.topMargin = dp(9);
        hero.addView(applyHint, hintParams);
        page.addView(hero, linearParams(-1, -2));

        MaterialCardView galleryCard = card();
        LinearLayout gallery = new LinearLayout(this);
        gallery.setOrientation(LinearLayout.VERTICAL);
        gallery.setPadding(dp(14), dp(17), dp(14), dp(16));

        TextView galleryTitle = text("BUILT-IN ICONS", 23, WHITE, true);
        gallery.addView(galleryTitle, linearParams(-1, -2));

        TextView count = text("12 PLACEHOLDER DESIGNS  /  APPFILTER MAPPINGS INCLUDED", 10, WHITE, false);
        LinearLayout.LayoutParams countParams = linearParams(-1, -2);
        countParams.topMargin = dp(5);
        gallery.addView(count, countParams);

        LinearLayout grid = new LinearLayout(this);
        grid.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams gridParams = linearParams(-1, -2);
        gridParams.topMargin = dp(15);
        gallery.addView(grid, gridParams);

        for (int i = 0; i < icons.length; i += 2) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.TOP);
            IconItem first = icons[i];
            row.addView(createIconTile(first), new LinearLayout.LayoutParams(0, -2, 1f));
            if (i + 1 < icons.length) {
                View gap = new View(this);
                row.addView(gap, new LinearLayout.LayoutParams(dp(10), 1));
                row.addView(createIconTile(icons[i + 1]), new LinearLayout.LayoutParams(0, -2, 1f));
            }
            LinearLayout.LayoutParams rowParams = linearParams(-1, -2);
            rowParams.bottomMargin = dp(10);
            grid.addView(row, rowParams);
        }
        galleryCard.addView(gallery);
        LinearLayout.LayoutParams galleryParams = linearParams(-1, -2);
        galleryParams.topMargin = dp(12);
        page.addView(galleryCard, galleryParams);

        MaterialCardView compatibilityCard = card();
        LinearLayout compatibility = new LinearLayout(this);
        compatibility.setOrientation(LinearLayout.VERTICAL);
        compatibility.setPadding(dp(15), dp(15), dp(15), dp(15));
        TextView compatibilityTitle = text("ICON PACK COMPATIBILITY", 20, WHITE, true);
        compatibility.addView(compatibilityTitle);
        TextView compatibilityBody = text(
                "SCRATCHICON includes standard appfilter mappings for launchers that support third-party icon packs. On supported ColorOS versions, look in the Home screen Icons panel for installed third-party packs. Other compatible launchers have their own icon-pack settings. Unmapped apps keep their original icons until we add artwork and a mapping for them.",
                13, WHITE, false);
        compatibilityBody.setLineSpacing(dp(4), 1f);
        LinearLayout.LayoutParams compatibilityBodyParams = linearParams(-1, -2);
        compatibilityBodyParams.topMargin = dp(9);
        compatibility.addView(compatibilityBody, compatibilityBodyParams);
        compatibilityCard.addView(compatibility);
        LinearLayout.LayoutParams compatibilityParams = linearParams(-1, -2);
        compatibilityParams.topMargin = dp(12);
        page.addView(compatibilityCard, compatibilityParams);

        TextView disclaimerTitle = text("DISCLAIMER", 31, WHITE, true);
        LinearLayout.LayoutParams disclaimerTitleParams = linearParams(-1, -2);
        disclaimerTitleParams.topMargin = dp(26);
        page.addView(disclaimerTitle, disclaimerTitleParams);

        TextView disclaimer = text(
                "We do not own or claim any rights to Pizza Tower, its characters, or related intellectual property. SCRATCHICON is an independent, unofficial icon pack inspired by Pizza Tower's art style only. It is not affiliated with, endorsed by, or sponsored by the game's rights holders.",
                14, WHITE, false);
        disclaimer.setLineSpacing(dp(4), 1f);
        LinearLayout.LayoutParams disclaimerParams = linearParams(-1, -2);
        disclaimerParams.topMargin = dp(10);
        page.addView(disclaimer, disclaimerParams);

        ImageView peppino = new ImageView(this);
        peppino.setImageResource(R.drawable.peppino);
        peppino.setScaleType(ImageView.ScaleType.FIT_CENTER);
        peppino.setAdjustViewBounds(true);
        peppino.setBackgroundColor(Color.TRANSPARENT);
        LinearLayout.LayoutParams peppinoParams = linearParams(-1, dp(220));
        peppinoParams.topMargin = dp(18);
        page.addView(peppino, peppinoParams);

        TextView credit = text("Made with love on a phone by PandaSprite ❤️", 12, WHITE, false);
        credit.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams creditParams = linearParams(-1, -2);
        creditParams.topMargin = dp(5);
        page.addView(credit, creditParams);

        root.addView(scroll, new FrameLayout.LayoutParams(-1, -1));
        setContentView(root);
    }

    private MaterialCardView card() {
        MaterialCardView card = new MaterialCardView(this);
        card.setRadius(dp(19));
        card.setCardBackgroundColor(Color.argb(190, 31, 19, 20));
        card.setCardElevation(0);
        card.setStrokeWidth(dp(1));
        card.setStrokeColor(Color.argb(190, 255, 241, 205));
        card.setUseCompatPadding(false);
        return card;
    }

    private MaterialButton button(String label) {
        MaterialButton button = new MaterialButton(this);
        button.setText(label);
        button.setTextSize(13);
        button.setTypeface(scratchFont, Typeface.BOLD);
        button.setAllCaps(false);
        button.setTextColor(INK);
        button.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.rgb(255, 239, 190)));
        button.setCornerRadius(dp(13));
        button.setElevation(dp(2));
        return button;
    }

    private View createIconTile(IconItem item) {
        MaterialCardView tile = new MaterialCardView(this);
        tile.setRadius(dp(15));
        tile.setCardBackgroundColor(Color.argb(155, 27, 17, 19));
        tile.setCardElevation(0);
        tile.setStrokeWidth(dp(1));
        tile.setStrokeColor(Color.argb(110, 255, 239, 190));

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.HORIZONTAL);
        content.setGravity(Gravity.CENTER_VERTICAL);
        content.setPadding(dp(9), dp(9), dp(7), dp(9));

        ImageView image = new ImageView(this);
        image.setImageResource(item.image);
        image.setScaleType(ImageView.ScaleType.FIT_CENTER);
        content.addView(image, linearParams(dp(56), dp(56)));

        LinearLayout labels = new LinearLayout(this);
        labels.setOrientation(LinearLayout.VERTICAL);
        labels.setGravity(Gravity.CENTER_VERTICAL);
        labels.setPadding(dp(8), 0, 0, 0);
        TextView name = text(item.label, 13, WHITE, true);
        TextView mapped = text(item.mappedApp, 10, Color.rgb(255, 222, 174), false);
        labels.addView(name, linearParams(-1, -2));
        LinearLayout.LayoutParams mappedParams = linearParams(-1, -2);
        mappedParams.topMargin = dp(3);
        labels.addView(mapped, mappedParams);
        content.addView(labels, new LinearLayout.LayoutParams(0, -2, 1f));

        tile.addView(content);
        return tile;
    }

    private void showApplyInstructions() {
        new AlertDialog.Builder(this)
                .setTitle("APPLY SCRATCHICON")
                .setMessage(
                        "ColorOS (where third-party icon packs are supported):\n" +
                        "1. Return to your Home screen.\n" +
                        "2. Touch and hold an empty area.\n" +
                        "3. Open Icons and select SCRATCHICON from the installed packs.\n\n" +
                        "Other compatible launchers:\n" +
                        "Open that launcher's Settings, find Icon style or Themes, then select SCRATCHICON.\n\n" +
                        "This applies the pack inside a compatible launcher. Android does not let an ordinary app force icons to change in launchers that do not support icon packs.")
                .setPositiveButton("GOT IT", null)
                .show();
    }
}
