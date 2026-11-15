package org.andromaxlite.launcher;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.util.LruCache;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Minimal HOME screen for the Andromax Prime Android 4.4 firmware.
 * Uses platform widgets only; no support-library or background-service dependency.
 */
public final class MainActivity extends Activity {
    private GridView appGrid;
    private AppAdapter adapter;
    private final ArrayList<AppEntry> entries = new ArrayList<AppEntry>();
    // Keep only a small number of launcher icons resident on a 512 MB device.
    private final LruCache<String, Drawable> iconCache = new LruCache<String, Drawable>(12);

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        buildLayout();
        reloadApplications();
    }

    private void buildLayout() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(0xFF101820);
        root.setPadding(dp(5), dp(4), dp(5), dp(3));

        TextView title = new TextView(this);
        title.setText(getString(R.string.title));
        title.setTextColor(0xFFEAF7FA);
        title.setTextSize(17);
        title.setGravity(Gravity.CENTER_VERTICAL);
        title.setPadding(dp(7), 0, dp(7), 0);
        root.addView(title, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(32)));

        appGrid = new GridView(this);
        appGrid.setNumColumns(3);
        appGrid.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        appGrid.setHorizontalSpacing(dp(4));
        appGrid.setVerticalSpacing(dp(4));
        appGrid.setPadding(dp(2), dp(3), dp(2), dp(3));
        appGrid.setClipToPadding(false);
        appGrid.setSelector(new ColorDrawable(0x00000000));
        appGrid.setChoiceMode(GridView.CHOICE_MODE_SINGLE);
        appGrid.setVerticalScrollBarEnabled(false);
        appGrid.setFocusable(true);
        appGrid.setFocusableInTouchMode(false);

        LinearLayout.LayoutParams gridParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1.0f);
        root.addView(appGrid, gridParams);

        TextView footer = new TextView(this);
        footer.setText(getString(R.string.key_help));
        footer.setTextColor(0xFF9CB6C0);
        footer.setTextSize(10);
        footer.setGravity(Gravity.CENTER);
        footer.setSingleLine(true);
        root.addView(footer, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(23)));

        setContentView(root);

        adapter = new AppAdapter();
        appGrid.setAdapter(adapter);
        appGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                launchApplication(position);
            }
        });
    }

    private void reloadApplications() {
        entries.clear();
        PackageManager pm = getPackageManager();
        Intent query = new Intent(Intent.ACTION_MAIN, null);
        query.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> matches = pm.queryIntentActivities(query, 0);
        for (ResolveInfo info : matches) {
            if (info.activityInfo == null) {
                continue;
            }
            if (getPackageName().equals(info.activityInfo.packageName)) {
                continue;
            }
            CharSequence label = info.loadLabel(pm);
            Intent launch = new Intent(Intent.ACTION_MAIN);
            launch.addCategory(Intent.CATEGORY_LAUNCHER);
            launch.setClassName(info.activityInfo.packageName, info.activityInfo.name);
            launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            entries.add(new AppEntry(label == null ? info.activityInfo.name : label.toString(),
                    info, launch));
        }
        Collections.sort(entries, new Comparator<AppEntry>() {
            @Override
            public int compare(AppEntry left, AppEntry right) {
                return left.label.compareToIgnoreCase(right.label);
            }
        });
        adapter.notifyDataSetChanged();
        if (!entries.isEmpty()) {
            appGrid.setSelection(0);
        }
    }

    private Drawable loadIcon(ResolveInfo info) {
        String key = info.activityInfo.packageName + "/" + info.activityInfo.name;
        Drawable icon = iconCache.get(key);
        if (icon == null) {
            try {
                icon = info.loadIcon(getPackageManager());
                if (icon != null) {
                    iconCache.put(key, icon);
                }
            } catch (RuntimeException ignored) {
                // A package can disappear while the launcher list is being refreshed.
            }
        }
        return icon;
    }

    private void launchApplication(int position) {
        if (position < 0 || position >= entries.size()) {
            return;
        }
        Intent launch = entries.get(position).intent;
        try {
            startActivity(launch);
        } catch (RuntimeException ignored) {
            // A removed/disabled app should not crash or close the HOME screen.
            reloadApplications();
        }
    }

    private void selectNumberedItem(int keyCode) {
        int index;
        if (keyCode >= KeyEvent.KEYCODE_1 && keyCode <= KeyEvent.KEYCODE_9) {
            index = keyCode - KeyEvent.KEYCODE_1;
        } else if (keyCode == KeyEvent.KEYCODE_0) {
            index = 9;
        } else {
            return;
        }
        if (index < entries.size()) {
            appGrid.setSelection(index);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            reloadApplications();
            return true;
        }
        if ((keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9)) {
            selectNumberedItem(keyCode);
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER
                || keyCode == KeyEvent.KEYCODE_ENTER
                || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) {
            launchApplication(appGrid.getSelectedItemPosition());
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        // Keep the HOME surface available; BACK should not close the launcher.
        if (appGrid != null && appGrid.getSelectedItemPosition() >= 0) {
            appGrid.requestFocus();
        }
    }

    private int dp(int value) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (value * density + 0.5f);
    }

    private static final class AppEntry {
        final String label;
        final ResolveInfo resolveInfo;
        final Intent intent;

        AppEntry(String label, ResolveInfo resolveInfo, Intent intent) {
            this.label = label;
            this.resolveInfo = resolveInfo;
            this.intent = intent;
        }
    }

    private final class AppAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return entries.size();
        }

        @Override
        public Object getItem(int position) {
            return entries.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, android.view.ViewGroup parent) {
            TextView tile = (convertView instanceof TextView)
                    ? (TextView) convertView : new TextView(MainActivity.this);
            AppEntry entry = entries.get(position);
            tile.setText(entry.label);
            tile.setTextColor(0xFFEAF7FA);
            tile.setTextSize(11);
            tile.setGravity(Gravity.CENTER);
            tile.setMaxLines(2);
            tile.setMinHeight(dp(78));
            tile.setPadding(dp(2), dp(3), dp(2), dp(3));
            tile.setCompoundDrawablePadding(dp(2));
            tile.setBackgroundResource(R.drawable.app_tile);
            tile.setFocusable(true);
            tile.setFocusableInTouchMode(false);
            Drawable icon = loadIcon(entry.resolveInfo);
            if (icon != null) {
                // Give each cell its own drawable instance to avoid shared callback/bounds state.
                Drawable.ConstantState constantState = icon.getConstantState();
                if (constantState != null) {
                    icon = constantState.newDrawable(getResources()).mutate();
                } else {
                    icon = icon.mutate();
                }
                int iconSize = dp(31);
                icon.setBounds(0, 0, iconSize, iconSize);
            }
            tile.setCompoundDrawables(null, icon, null, null);
            return tile;
        }
    }
}
