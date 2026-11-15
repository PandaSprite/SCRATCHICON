package com.scratchicons.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.pm.ShortcutInfoCompat;
import androidx.core.content.pm.ShortcutManagerCompat;
import androidx.core.graphics.drawable.IconCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Activity {
    private static final int REQUEST_IMPORT_PNG = 41;
    private static final String PREFS = "scratchicon_library";
    private static final String KEY_ICONS = "icon_names";
    private static final String KEY_ASSIGNMENTS = "app_assignments";
    private static final int ICON_SIZE = 512;

    private final int background = Color.rgb(13, 13, 13);
    private final int surface = Color.rgb(24, 24, 24);
    private final int white = Color.WHITE;
    private Typeface scratchFont;
    private SharedPreferences preferences;
    private final Map<String, String> iconNames = new LinkedHashMap<>();
    private final Map<String, String> assignments = new LinkedHashMap<>();
    private LinearLayout collectionList;
    private TextView collectionCount;
    private TextView emptyMessage;

    private int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private TextView makeText(String value, float size, int color, boolean bold) {
        TextView text = new TextView(this);
        text.setText(value);
        text.setTextSize(size);
        text.setTextColor(color);
        text.setTypeface(scratchFont, bold ? Typeface.BOLD : Typeface.NORMAL);
        text.setIncludeFontPadding(false);
        return text;
    }

    private LinearLayout.LayoutParams params(int width, int height) {
        return new LinearLayout.LayoutParams(width, height);
    }

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        scratchFont = Typeface.createFromAsset(getAssets(), "fonts/toppin-time-open.otf");
        preferences = getSharedPreferences(PREFS, MODE_PRIVATE);
        loadSavedData();

        getWindow().setStatusBarColor(background);
        getWindow().setNavigationBarColor(background);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(background);
        scroll.setClipToPadding(false);

        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(dp(20), dp(22), dp(20), dp(28));
        scroll.addView(page, new ScrollView.LayoutParams(-1, -2));

        MaterialCardView heroCard = new MaterialCardView(this);
        heroCard.setRadius(dp(22));
        heroCard.setCardBackgroundColor(surface);
        heroCard.setCardElevation(0);
        heroCard.setStrokeWidth(dp(1));
        heroCard.setStrokeColor(white);
        heroCard.setClipToOutline(true);

        FrameLayout hero = new FrameLayout(this);
        hero.setMinimumHeight(dp(250));
        ImageView wallpaper = new ImageView(this);
        wallpaper.setImageResource(R.drawable.scratch_wallpaper);
        wallpaper.setScaleType(ImageView.ScaleType.CENTER_CROP);
        hero.addView(wallpaper, new FrameLayout.LayoutParams(-1, -1));

        View shade = new View(this);
        shade.setBackgroundColor(Color.argb(112, 0, 0, 0));
        hero.addView(shade, new FrameLayout.LayoutParams(-1, -1));

        LinearLayout heroContent = new LinearLayout(this);
        heroContent.setOrientation(LinearLayout.VERTICAL);
        heroContent.setGravity(Gravity.CENTER);
        heroContent.setPadding(dp(18), dp(24), dp(18), dp(24));

        TextView brand = makeText("SCRATCHICON", 38, white, true);
        brand.setGravity(Gravity.CENTER);
        heroContent.addView(brand, params(-1, -2));

        TextView tagline = makeText(
                "A hand-drawn icon pack, perfect for people who like scratchy stuff",
                17, white, false);
        tagline.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams taglineParams = params(-1, -2);
        taglineParams.topMargin = dp(12);
        heroContent.addView(tagline, taglineParams);

        MaterialButton importButton = makeButton("IMPORT ICON", true);
        LinearLayout.LayoutParams importParams = params(-1, dp(54));
        importParams.topMargin = dp(22);
        heroContent.addView(importButton, importParams);
        importButton.setOnClickListener(view -> openPngPicker());

        TextView importHint = makeText("PNG ONLY  |  SAVED AS 512 x 512", 11, white, false);
        importHint.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams importHintParams = params(-1, -2);
        importHintParams.topMargin = dp(9);
        heroContent.addView(importHint, importHintParams);

        hero.addView(heroContent, new FrameLayout.LayoutParams(-1, -1));
        heroCard.addView(hero);
        page.addView(heroCard, params(-1, -2));

        MaterialCardView collectionCard = new MaterialCardView(this);
        collectionCard.setRadius(dp(20));
        collectionCard.setCardBackgroundColor(surface);
        collectionCard.setCardElevation(0);
        collectionCard.setStrokeWidth(dp(1));
        collectionCard.setStrokeColor(white);

        LinearLayout collection = new LinearLayout(this);
        collection.setOrientation(LinearLayout.VERTICAL);
        collection.setPadding(dp(16), dp(18), dp(16), dp(18));

        TextView collectionTitle = makeText("YOUR COLLECTION", 21, white, true);
        collection.addView(collectionTitle);
        collectionCount = makeText("0 ICONS", 13, white, false);
        LinearLayout.LayoutParams countParams = params(-1, -2);
        countParams.topMargin = dp(5);
        collection.addView(collectionCount, countParams);

        emptyMessage = makeText("NOTHING HERE YET\nImport a PNG to start your collection.", 15, white, false);
        emptyMessage.setGravity(Gravity.CENTER);
        emptyMessage.setLineSpacing(dp(5), 1f);
        LinearLayout.LayoutParams emptyParams = params(-1, dp(100));
        emptyParams.topMargin = dp(8);
        collection.addView(emptyMessage, emptyParams);

        collectionList = new LinearLayout(this);
        collectionList.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams listParams = params(-1, -2);
        listParams.topMargin = dp(12);
        collection.addView(collectionList, listParams);
        collectionCard.addView(collection);

        LinearLayout.LayoutParams collectionParams = params(-1, -2);
        collectionParams.topMargin = dp(18);
        page.addView(collectionCard, collectionParams);

        TextView howTo = makeText(
                "ASSIGN an icon to an installed app, then choose ADD TO HOME to create a separate shortcut. This does not replace the original app-drawer icon.",
                13, white, false);
        howTo.setLineSpacing(dp(3), 1f);
        LinearLayout.LayoutParams howToParams = params(-1, -2);
        howToParams.topMargin = dp(12);
        page.addView(howTo, howToParams);

        TextView disclaimerTitle = makeText("DISCLAIMER", 31, white, true);
        LinearLayout.LayoutParams disclaimerTitleParams = params(-1, -2);
        disclaimerTitleParams.topMargin = dp(28);
        page.addView(disclaimerTitle, disclaimerTitleParams);

        TextView disclaimer = makeText(
                "We do not own or claim any rights to Pizza Tower, its characters, or related intellectual property. SCRATCHICON is an independent, unofficial icon pack inspired by Pizza Tower's art style only. It is not affiliated with, endorsed by, or sponsored by the game's rights holders.",
                14, white, false);
        disclaimer.setLineSpacing(dp(4), 1f);
        LinearLayout.LayoutParams disclaimerParams = params(-1, -2);
        disclaimerParams.topMargin = dp(10);
        page.addView(disclaimer, disclaimerParams);

        ImageView peppino = new ImageView(this);
        peppino.setImageResource(R.drawable.peppino);
        peppino.setScaleType(ImageView.ScaleType.FIT_CENTER);
        peppino.setAdjustViewBounds(true);
        peppino.setBackgroundColor(Color.TRANSPARENT);
        LinearLayout.LayoutParams peppinoParams = params(-1, dp(220));
        peppinoParams.topMargin = dp(16);
        page.addView(peppino, peppinoParams);

        LinearLayout credit = new LinearLayout(this);
        credit.setOrientation(LinearLayout.HORIZONTAL);
        credit.setGravity(Gravity.CENTER);
        TextView creditText = makeText("Made with love on a phone by PandaSprite", 12, white, false);
        creditText.setGravity(Gravity.CENTER);
        credit.addView(creditText, new LinearLayout.LayoutParams(0, -2, 1f));
        TextView heart = new TextView(this);
        heart.setText("\u2764\uFE0F");
        heart.setTextSize(16);
        heart.setTextColor(Color.rgb(255, 65, 85));
        heart.setTypeface(Typeface.DEFAULT);
        credit.addView(heart, params(-2, -2));
        LinearLayout.LayoutParams creditParams = params(-1, -2);
        creditParams.topMargin = dp(4);
        page.addView(credit, creditParams);

        setContentView(scroll);
        refreshCollection();
    }

    private MaterialButton makeButton(String label, boolean blackText) {
        MaterialButton button = new MaterialButton(this);
        button.setText(label);
        button.setTextSize(13);
        button.setTypeface(scratchFont, Typeface.BOLD);
        button.setAllCaps(false);
        button.setTextColor(blackText ? Color.BLACK : white);
        button.setBackgroundTintList(ColorStateList.valueOf(white));
        button.setCornerRadius(dp(12));
        button.setElevation(0);
        return button;
    }

    private void openPngPicker() {
        Intent picker = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        picker.addCategory(Intent.CATEGORY_OPENABLE);
        picker.setType("image/png");
        try {
            startActivityForResult(picker, REQUEST_IMPORT_PNG);
        } catch (Exception error) {
            Toast.makeText(this, "No file picker is available on this device.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != REQUEST_IMPORT_PNG || resultCode != RESULT_OK || data == null || data.getData() == null) {
            return;
        }
        Uri uri = data.getData();
        try {
            if (!isPng(uri)) {
                Toast.makeText(this, "Please choose a PNG image.", Toast.LENGTH_LONG).show();
                return;
            }
            Bitmap iconBitmap = decodeNormalizedPng(uri);
            if (iconBitmap == null) {
                Toast.makeText(this, "SCRATCHICON couldn't read that PNG.", Toast.LENGTH_LONG).show();
                return;
            }
            String id = UUID.randomUUID().toString();
            File iconFile = getIconFile(id);
            File thumbFile = getThumbnailFile(id);
            File directory = iconFile.getParentFile();
            if (directory != null && !directory.exists() && !directory.mkdirs()) {
                iconBitmap.recycle();
                Toast.makeText(this, "Couldn't create icon storage.", Toast.LENGTH_LONG).show();
                return;
            }
            boolean saved;
            try (FileOutputStream output = new FileOutputStream(iconFile)) {
                saved = iconBitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
            }
            Bitmap thumbnail = Bitmap.createScaledBitmap(iconBitmap, 128, 128, true);
            try (FileOutputStream output = new FileOutputStream(thumbFile)) {
                thumbnail.compress(Bitmap.CompressFormat.PNG, 100, output);
            }
            if (thumbnail != iconBitmap) thumbnail.recycle();
            iconBitmap.recycle();
            if (!saved) {
                iconFile.delete();
                thumbFile.delete();
                Toast.makeText(this, "Couldn't save that PNG.", Toast.LENGTH_LONG).show();
                return;
            }
            String name = readDisplayName(uri);
            iconNames.put(id, cleanDisplayName(name));
            saveSavedData();
            refreshCollection();
            Toast.makeText(this, "PNG imported: " + cleanDisplayName(name), Toast.LENGTH_SHORT).show();
        } catch (OutOfMemoryError error) {
            Toast.makeText(this, "That image is too large to process. Try a smaller PNG.", Toast.LENGTH_LONG).show();
        } catch (Exception error) {
            Toast.makeText(this, "Import failed: " + error.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private boolean isPng(Uri uri) throws Exception {
        byte[] signature = new byte[] {(byte) 137, 80, 78, 71, 13, 10, 26, 10};
        try (InputStream input = new BufferedInputStream(getContentResolver().openInputStream(uri))) {
            if (input == null) return false;
            for (byte expected : signature) {
                int actual = input.read();
                if (actual < 0 || (byte) actual != expected) return false;
            }
            return true;
        }
    }

    private Bitmap decodeNormalizedPng(Uri uri) throws Exception {
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        try (InputStream input = getContentResolver().openInputStream(uri)) {
            BitmapFactory.decodeStream(input, null, bounds);
        }
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inScaled = false;
        int largest = Math.max(bounds.outWidth, bounds.outHeight);
        int sample = 1;
        while (largest / sample > 1024) sample *= 2;
        options.inSampleSize = sample;

        Bitmap source;
        try (InputStream input = getContentResolver().openInputStream(uri)) {
            source = BitmapFactory.decodeStream(input, null, options);
        }
        if (source == null) return null;

        Bitmap square = Bitmap.createBitmap(ICON_SIZE, ICON_SIZE, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(square);
        canvas.drawColor(Color.TRANSPARENT, android.graphics.PorterDuff.Mode.CLEAR);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG);
        float scale = Math.min((float) ICON_SIZE / source.getWidth(), (float) ICON_SIZE / source.getHeight());
        float width = source.getWidth() * scale;
        float height = source.getHeight() * scale;
        float left = (ICON_SIZE - width) / 2f;
        float top = (ICON_SIZE - height) / 2f;
        canvas.drawBitmap(source, null, new RectF(left, top, left + width, top + height), paint);
        source.recycle();
        return square;
    }

    private String readDisplayName(Uri uri) {
        String name = "Custom icon.png";
        try (Cursor cursor = getContentResolver().query(uri, new String[] {OpenableColumns.DISPLAY_NAME}, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (index >= 0 && cursor.getString(index) != null) name = cursor.getString(index);
            }
        } catch (Exception ignored) { }
        return name;
    }

    private String cleanDisplayName(String input) {
        if (input == null || input.trim().isEmpty()) return "Custom icon";
        String name = input.trim().replaceAll("(?i)\\.png$", "");
        if (name.length() > 32) name = name.substring(0, 32);
        return name;
    }

    private File getIconDirectory() {
        return new File(getFilesDir(), "icons");
    }

    private File getIconFile(String id) {
        return new File(getIconDirectory(), id + ".png");
    }

    private File getThumbnailFile(String id) {
        return new File(getIconDirectory(), id + ".thumb.png");
    }

    private void loadSavedData() {
        iconNames.clear();
        assignments.clear();
        try {
            JSONObject names = new JSONObject(preferences.getString(KEY_ICONS, "{}"));
            Iterator<String> keys = names.keys();
            while (keys.hasNext()) {
                String id = keys.next();
                iconNames.put(id, names.optString(id, "Custom icon"));
            }
            JSONObject savedAssignments = new JSONObject(preferences.getString(KEY_ASSIGNMENTS, "{}"));
            Iterator<String> packages = savedAssignments.keys();
            while (packages.hasNext()) {
                String packageName = packages.next();
                assignments.put(packageName, savedAssignments.optString(packageName, ""));
            }
        } catch (JSONException ignored) { }
    }

    private void saveSavedData() {
        preferences.edit()
                .putString(KEY_ICONS, new JSONObject(iconNames).toString())
                .putString(KEY_ASSIGNMENTS, new JSONObject(assignments).toString())
                .apply();
    }

    private void refreshCollection() {
        if (collectionList == null) return;
        collectionList.removeAllViews();
        List<String> ids = new ArrayList<>(iconNames.keySet());
        Collections.sort(ids, (first, second) -> Long.compare(
                getIconFile(second).lastModified(), getIconFile(first).lastModified()));
        collectionCount.setText(ids.size() + (ids.size() == 1 ? " ICON" : " ICONS"));
        emptyMessage.setVisibility(ids.isEmpty() ? View.VISIBLE : View.GONE);
        for (String id : ids) {
            if (getIconFile(id).exists()) {
                collectionList.addView(createIconRow(id), params(-1, -2));
            }
        }
    }

    private View createIconRow(String id) {
        MaterialCardView item = new MaterialCardView(this);
        item.setRadius(dp(14));
        item.setCardBackgroundColor(Color.rgb(34, 34, 34));
        item.setCardElevation(0);
        item.setStrokeWidth(dp(1));
        item.setStrokeColor(Color.rgb(105, 105, 105));

        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(10), dp(10), dp(10), dp(10));

        LinearLayout top = new LinearLayout(this);
        top.setOrientation(LinearLayout.HORIZONTAL);
        top.setGravity(Gravity.CENTER_VERTICAL);

        ImageView preview = new ImageView(this);
        BitmapFactory.Options thumbOptions = new BitmapFactory.Options();
        thumbOptions.inSampleSize = 1;
        Bitmap thumb = BitmapFactory.decodeFile(getThumbnailFile(id).getAbsolutePath(), thumbOptions);
        if (thumb == null) thumb = BitmapFactory.decodeFile(getIconFile(id).getAbsolutePath());
        if (thumb != null) preview.setImageBitmap(thumb);
        preview.setScaleType(ImageView.ScaleType.FIT_CENTER);
        LinearLayout.LayoutParams previewParams = params(dp(64), dp(64));
        previewParams.rightMargin = dp(12);
        top.addView(preview, previewParams);

        LinearLayout labels = new LinearLayout(this);
        labels.setOrientation(LinearLayout.VERTICAL);
        labels.setGravity(Gravity.CENTER_VERTICAL);
        TextView name = makeText(iconNames.get(id), 15, white, true);
        TextView assigned = makeText(getAssignmentDescription(id), 11, Color.LTGRAY, false);
        assigned.setMaxLines(2);
        LinearLayout.LayoutParams assignedParams = params(-1, -2);
        assignedParams.topMargin = dp(5);
        labels.addView(name, params(-1, -2));
        labels.addView(assigned, assignedParams);
        top.addView(labels, new LinearLayout.LayoutParams(0, -2, 1f));
        box.addView(top);

        LinearLayout actions = new LinearLayout(this);
        actions.setOrientation(LinearLayout.HORIZONTAL);
        actions.setGravity(Gravity.CENTER_VERTICAL);

        MaterialButton assign = makeButton("ASSIGN APP", true);
        LinearLayout.LayoutParams assignParams = new LinearLayout.LayoutParams(0, dp(42), 1f);
        assignParams.topMargin = dp(10);
        assignParams.rightMargin = dp(5);
        actions.addView(assign, assignParams);
        LinearLayout appDropdown = new LinearLayout(this);
        appDropdown.setOrientation(LinearLayout.VERTICAL);
        appDropdown.setPadding(dp(10), dp(8), dp(10), dp(8));
        appDropdown.setBackgroundColor(Color.rgb(18, 18, 18));
        appDropdown.setVisibility(View.GONE);
        LinearLayout.LayoutParams dropdownParams = params(-1, -2);
        dropdownParams.topMargin = dp(10);
        box.addView(appDropdown, dropdownParams);
        assign.setOnClickListener(view -> toggleInstalledApps(id, appDropdown, assign));

        MaterialButton addToHome = makeButton("ADD TO HOME", true);
        LinearLayout.LayoutParams homeParams = new LinearLayout.LayoutParams(0, dp(42), 1f);
        homeParams.topMargin = dp(10);
        homeParams.leftMargin = dp(5);
        actions.addView(addToHome, homeParams);
        addToHome.setOnClickListener(view -> addShortcutForIcon(id));

        box.addView(actions);
        TextView remove = makeText("REMOVE ICON", 11, Color.LTGRAY, true);
        remove.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
        remove.setPadding(dp(8), dp(10), dp(2), dp(2));
        remove.setOnClickListener(view -> confirmRemoveIcon(id));
        box.addView(remove, params(-1, -2));

        item.addView(box);
        LinearLayout.LayoutParams itemParams = params(-1, -2);
        itemParams.bottomMargin = dp(10);
        item.setLayoutParams(itemParams);
        return item;
    }

    private String getAssignmentDescription(String iconId) {
        for (Map.Entry<String, String> entry : assignments.entrySet()) {
            if (iconId.equals(entry.getValue())) {
                try {
                    PackageManager pm = getPackageManager();
                    CharSequence label = pm.getApplicationLabel(pm.getApplicationInfo(entry.getKey(), 0));
                    return "ASSIGNED TO: " + label;
                } catch (Exception ignored) {
                    return "ASSIGNED TO: " + entry.getKey();
                }
            }
        }
        return "NOT ASSIGNED TO AN APP";
    }

    private void toggleInstalledApps(String iconId, LinearLayout dropdown, MaterialButton assignButton) {
        if (dropdown.getVisibility() == View.VISIBLE) {
            dropdown.setVisibility(View.GONE);
            assignButton.setText("ASSIGN APP");
            return;
        }

        dropdown.removeAllViews();
        TextView heading = makeText("CHOOSE AN INSTALLED APP", 12, white, true);
        heading.setPadding(dp(2), dp(4), dp(2), dp(10));
        dropdown.addView(heading, params(-1, -2));

        Intent launcherIntent = new Intent(Intent.ACTION_MAIN, null);
        launcherIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolved = getPackageManager().queryIntentActivities(launcherIntent, 0);
        List<InstalledApp> apps = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        for (ResolveInfo info : resolved) {
            if (info.activityInfo == null) continue;
            String packageName = info.activityInfo.packageName;
            if (packageName == null || packageName.equals(getPackageName()) || !seen.add(packageName)) continue;
            CharSequence label = info.loadLabel(getPackageManager());
            apps.add(new InstalledApp(packageName, label == null ? packageName : label.toString()));
        }
        Collections.sort(apps, Comparator.comparing(app -> app.label.toLowerCase(Locale.ROOT)));
        if (apps.isEmpty()) {
            TextView none = makeText("NO LAUNCHABLE APPS FOUND", 12, Color.LTGRAY, false);
            dropdown.addView(none, params(-1, dp(42)));
        } else {
            ScrollView appScroll = new ScrollView(this);
            appScroll.setFillViewport(false);
            LinearLayout appChoices = new LinearLayout(this);
            appChoices.setOrientation(LinearLayout.VERTICAL);
            for (InstalledApp app : apps) {
                TextView choice = makeText(app.label, 14, white, false);
                choice.setGravity(Gravity.CENTER_VERTICAL);
                choice.setPadding(dp(10), dp(11), dp(10), dp(11));
                choice.setBackgroundColor(Color.rgb(34, 34, 34));
                LinearLayout.LayoutParams choiceParams = params(-1, -2);
                choiceParams.bottomMargin = dp(4);
                appChoices.addView(choice, choiceParams);
                choice.setOnClickListener(view -> {
                    Iterator<Map.Entry<String, String>> iterator = assignments.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<String, String> entry = iterator.next();
                        if (iconId.equals(entry.getValue()) || app.packageName.equals(entry.getKey())) {
                            iterator.remove();
                        }
                    }
                    assignments.put(app.packageName, iconId);
                    saveSavedData();
                    refreshCollection();
                    Toast.makeText(this, "Assigned to " + app.label, Toast.LENGTH_SHORT).show();
                });
            }
            appScroll.addView(appChoices, new ScrollView.LayoutParams(-1, -2));
            dropdown.addView(appScroll, params(-1, dp(220)));
        }
        dropdown.setVisibility(View.VISIBLE);
        assignButton.setText("CLOSE APP LIST");
    }

    private void addShortcutForIcon(String iconId) {
        String targetPackage = null;
        for (Map.Entry<String, String> entry : assignments.entrySet()) {
            if (iconId.equals(entry.getValue())) {
                targetPackage = entry.getKey();
                break;
            }
        }
        if (targetPackage == null) {
            Toast.makeText(this, "Assign this icon to an app first.", Toast.LENGTH_LONG).show();
            return;
        }
        try {
            if (!ShortcutManagerCompat.isRequestPinShortcutSupported(this)) {
                Toast.makeText(this, "Your current launcher doesn't support pinned shortcuts.", Toast.LENGTH_LONG).show();
                return;
            }
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage(targetPackage);
            if (launchIntent == null) {
                Toast.makeText(this, "That app no longer appears to be installed.", Toast.LENGTH_LONG).show();
                return;
            }
            Bitmap bitmap = BitmapFactory.decodeFile(getIconFile(iconId).getAbsolutePath());
            if (bitmap == null) {
                Toast.makeText(this, "The saved PNG couldn't be loaded.", Toast.LENGTH_LONG).show();
                return;
            }
            String appLabel = targetPackage;
            try {
                appLabel = getPackageManager().getApplicationLabel(
                        getPackageManager().getApplicationInfo(targetPackage, 0)).toString();
            } catch (Exception ignored) { }
            String shortLabel = appLabel.length() > 20 ? appLabel.substring(0, 19) + "…" : appLabel;
            String shortcutId = "scratchicon_" + targetPackage.replaceAll("[^A-Za-z0-9_.-]", "_");
            ShortcutInfoCompat shortcut = new ShortcutInfoCompat.Builder(this, shortcutId)
                    .setShortLabel(shortLabel)
                    .setLongLabel(appLabel + " (SCRATCHICON)")
                    .setIcon(IconCompat.createWithBitmap(bitmap))
                    .setIntent(launchIntent)
                    .build();
            boolean accepted = ShortcutManagerCompat.requestPinShortcut(this, shortcut, null);
            bitmap.recycle();
            if (accepted) {
                Toast.makeText(this, "Shortcut request sent. Accept it on your home screen if prompted.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "The launcher didn't accept the shortcut request.", Toast.LENGTH_LONG).show();
            }
        } catch (SecurityException error) {
            Toast.makeText(this, "Android blocked this shortcut request.", Toast.LENGTH_LONG).show();
        } catch (Exception error) {
            Toast.makeText(this, "Couldn't create shortcut: " + error.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void confirmRemoveIcon(String iconId) {
        new AlertDialog.Builder(this)
                .setTitle("Remove this icon?")
                .setMessage("This deletes the saved PNG and clears any app assignment using it.")
                .setNegativeButton("CANCEL", null)
                .setPositiveButton("REMOVE", (dialog, which) -> {
                    iconNames.remove(iconId);
                    Iterator<Map.Entry<String, String>> iterator = assignments.entrySet().iterator();
                    while (iterator.hasNext()) {
                        if (iconId.equals(iterator.next().getValue())) iterator.remove();
                    }
                    getIconFile(iconId).delete();
                    getThumbnailFile(iconId).delete();
                    saveSavedData();
                    refreshCollection();
                })
                .show();
    }

    private static class InstalledApp {
        final String packageName;
        final String label;
        InstalledApp(String packageName, String label) {
            this.packageName = packageName;
            this.label = label;
        }
    }
}
