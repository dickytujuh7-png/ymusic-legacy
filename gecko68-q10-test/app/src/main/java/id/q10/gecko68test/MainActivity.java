package id.q10.gecko68test;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.net.Uri;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.mozilla.geckoview.GeckoRuntime;
import org.mozilla.geckoview.GeckoSession;
import org.mozilla.geckoview.GeckoView;

public class MainActivity extends Activity {
    private static GeckoRuntime runtime;
    private GeckoSession session;
    private GeckoView geckoView;
    private EditText searchBox;
    private TextView status;
    private Button backButton;
    private Button forwardButton;
    private Button homeButton;

    private int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private TextView makeText(String value, int size, int color) {
        TextView v = new TextView(this);
        v.setText(value);
        v.setTextSize(size);
        v.setTextColor(color);
        v.setGravity(Gravity.CENTER);
        return v;
    }

    private Button makeButton(String label) {
        Button b = new Button(this);
        b.setText(label);
        b.setTextSize(13);
        b.setAllCaps(false);
        b.setTextColor(Color.WHITE);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.rgb(42, 52, 66));
        bg.setCornerRadius(dp(5));
        b.setBackgroundDrawable(bg);
        return b;
    }

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setSoftInputMode(android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.rgb(12, 18, 27));

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(dp(8), dp(2), dp(8), dp(2));
        header.setBackgroundColor(Color.rgb(18, 25, 35));

        TextView brand = makeText("YouTube Q10", 18, Color.WHITE);
        brand.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        brand.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        header.addView(brand, new LinearLayout.LayoutParams(0, dp(44), 1));

        homeButton = makeButton("Beranda");
        header.addView(homeButton, new LinearLayout.LayoutParams(dp(82), dp(42)));
        root.addView(header, new LinearLayout.LayoutParams(-1, dp(48)));

        LinearLayout searchRow = new LinearLayout(this);
        searchRow.setOrientation(LinearLayout.HORIZONTAL);
        searchRow.setGravity(Gravity.CENTER_VERTICAL);
        searchRow.setPadding(dp(7), dp(4), dp(7), dp(4));
        searchRow.setBackgroundColor(Color.rgb(25, 34, 46));

        searchBox = new EditText(this);
        searchBox.setSingleLine(true);
        searchBox.setTextSize(15);
        searchBox.setHint("Cari video atau musik...");
        searchBox.setHintTextColor(Color.rgb(165, 175, 190));
        searchBox.setTextColor(Color.WHITE);
        searchBox.setPadding(dp(10), 0, dp(8), 0);
        GradientDrawable searchBg = new GradientDrawable();
        searchBg.setColor(Color.rgb(35, 46, 60));
        searchBg.setCornerRadius(dp(5));
        searchBox.setBackgroundDrawable(searchBg);
        searchBox.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        searchRow.addView(searchBox, new LinearLayout.LayoutParams(0, dp(44), 1));

        Button searchButton = makeButton("Cari");
        searchRow.addView(searchButton, new LinearLayout.LayoutParams(dp(68), dp(42)));
        root.addView(searchRow, new LinearLayout.LayoutParams(-1, dp(52)));

        LinearLayout nav = new LinearLayout(this);
        nav.setOrientation(LinearLayout.HORIZONTAL);
        nav.setPadding(dp(6), 0, dp(6), 0);
        nav.setGravity(Gravity.CENTER_VERTICAL);
        nav.setBackgroundColor(Color.rgb(18, 25, 35));
        backButton = makeButton("‹");
        forwardButton = makeButton("›");
        Button reloadButton = makeButton("Muat ulang");
        nav.addView(backButton, new LinearLayout.LayoutParams(dp(48), dp(36)));
        nav.addView(forwardButton, new LinearLayout.LayoutParams(dp(48), dp(36)));
        nav.addView(reloadButton, new LinearLayout.LayoutParams(dp(104), dp(36)));
        status = makeText("Menyiapkan Gecko 68...", 11, Color.rgb(190, 205, 220));
        status.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        nav.addView(status, new LinearLayout.LayoutParams(0, dp(36), 1));
        root.addView(nav, new LinearLayout.LayoutParams(-1, dp(40)));

        geckoView = new GeckoView(this);
        root.addView(geckoView, new LinearLayout.LayoutParams(-1, 0, 1));
        setContentView(root);

        View.OnClickListener searchAction = v -> searchYouTube();
        searchButton.setOnClickListener(searchAction);
        searchBox.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                searchYouTube();
                return true;
            }
            return false;
        });

        homeButton.setOnClickListener(v -> loadHome());
        reloadButton.setOnClickListener(v -> {
            if (session != null) session.reload();
        });
        backButton.setOnClickListener(v -> {
            if (session != null) session.goBack();
        });
        forwardButton.setOnClickListener(v -> {
            if (session != null) session.goForward();
        });

        try {
            if (runtime == null) runtime = GeckoRuntime.create(this);
            session = new GeckoSession();
            session.setProgressDelegate(new GeckoSession.ProgressDelegate() {
                @Override
                public void onPageStart(GeckoSession s, String url) {
                    runOnUiThread(() -> status.setText("Memuat..."));
                }
                @Override
                public void onPageStop(GeckoSession s, boolean success) {
                    runOnUiThread(() -> status.setText(success ? "Siap" : "Halaman gagal dimuat"));
                }
            });
            session.open(runtime);
            geckoView.setSession(session);
            loadHome();
        } catch (Throwable e) {
            status.setText("Gecko gagal: " + e.getClass().getSimpleName());
        }
    }

    private void hideKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchBox.getWindowToken(), 0);
            searchBox.clearFocus();
        } catch (Exception ignored) { }
    }

    private void searchYouTube() {
        String query = searchBox.getText().toString().trim();
        if (query.length() == 0 || session == null) return;
        hideKeyboard();
        session.loadUri("https://m.youtube.com/results?search_query=" + Uri.encode(query));
    }

    private void loadHome() {
        if (session != null) session.loadUri("https://m.youtube.com/");
    }

    @Override
    public void onBackPressed() {
        if (session != null) {
            try {
                session.goBack();
                return;
            } catch (Throwable ignored) { }
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        if (session != null) {
            session.close();
            session = null;
        }
        super.onDestroy();
    }
}
