package id.q10.music68;

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
import android.widget.LinearLayout;
import android.widget.TextView;

import org.mozilla.geckoview.GeckoRuntime;
import org.mozilla.geckoview.GeckoSession;
import org.mozilla.geckoview.GeckoView;

public class MainActivity extends Activity {
    private static GeckoRuntime runtime;
    private GeckoSession session;
    private EditText searchBox;
    private TextView status;

    private int dp(float v) {
        return (int)(v * getResources().getDisplayMetrics().density + 0.5f);
    }

    private TextView text(String value, int size, int color) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setGravity(Gravity.CENTER_VERTICAL);
        return t;
    }

    private Button button(String label) {
        Button b = new Button(this);
        b.setText(label);
        b.setTextSize(13);
        b.setAllCaps(false);
        b.setTextColor(Color.WHITE);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.rgb(45, 53, 67));
        bg.setCornerRadius(dp(6));
        b.setBackgroundDrawable(bg);
        return b;
    }

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setSoftInputMode(android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.rgb(12, 16, 23));

        LinearLayout header = new LinearLayout(this);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(dp(12), 0, dp(8), 0);
        header.setBackgroundColor(Color.rgb(20, 27, 38));
        TextView title = text("Q10 Music", 19, Color.WHITE);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        header.addView(title, new LinearLayout.LayoutParams(0, dp(48), 1));
        Button home = button("Musik");
        header.addView(home, new LinearLayout.LayoutParams(dp(76), dp(40)));
        root.addView(header, new LinearLayout.LayoutParams(-1, dp(50)));

        LinearLayout search = new LinearLayout(this);
        search.setGravity(Gravity.CENTER_VERTICAL);
        search.setPadding(dp(7), dp(4), dp(7), dp(4));
        search.setBackgroundColor(Color.rgb(28, 36, 48));
        searchBox = new EditText(this);
        searchBox.setSingleLine(true);
        searchBox.setTextSize(15);
        searchBox.setHint("Cari lagu atau artis...");
        searchBox.setHintTextColor(Color.rgb(160, 171, 188));
        searchBox.setTextColor(Color.WHITE);
        searchBox.setPadding(dp(10), 0, dp(8), 0);
        GradientDrawable inputBg = new GradientDrawable();
        inputBg.setColor(Color.rgb(39, 49, 64));
        inputBg.setCornerRadius(dp(6));
        searchBox.setBackgroundDrawable(inputBg);
        searchBox.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        search.addView(searchBox, new LinearLayout.LayoutParams(0, dp(44), 1));
        Button find = button("Cari");
        search.addView(find, new LinearLayout.LayoutParams(dp(66), dp(42)));
        root.addView(search, new LinearLayout.LayoutParams(-1, dp(52)));

        LinearLayout toolbar = new LinearLayout(this);
        toolbar.setGravity(Gravity.CENTER_VERTICAL);
        toolbar.setPadding(dp(6), 0, dp(6), 0);
        toolbar.setBackgroundColor(Color.rgb(20, 27, 38));
        Button back = button("‹");
        Button forward = button("›");
        Button reload = button("Muat ulang");
        toolbar.addView(back, new LinearLayout.LayoutParams(dp(46), dp(36)));
        toolbar.addView(forward, new LinearLayout.LayoutParams(dp(46), dp(36)));
        toolbar.addView(reload, new LinearLayout.LayoutParams(dp(100), dp(36)));
        status = text("Menyiapkan Gecko 68...", 11, Color.rgb(188, 201, 218));
        status.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        toolbar.addView(status, new LinearLayout.LayoutParams(0, dp(36), 1));
        root.addView(toolbar, new LinearLayout.LayoutParams(-1, dp(40)));

        GeckoView view = new GeckoView(this);
        root.addView(view, new LinearLayout.LayoutParams(-1, 0, 1));
        setContentView(root);

        home.setOnClickListener(v -> openHome());
        find.setOnClickListener(v -> searchMusic());
        searchBox.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                searchMusic();
                return true;
            }
            return false;
        });

        try {
            if (runtime == null) runtime = GeckoRuntime.create(this);
            session = new GeckoSession();
            session.setProgressDelegate(new GeckoSession.ProgressDelegate() {
                @Override public void onPageStart(GeckoSession s, String url) {
                    runOnUiThread(() -> status.setText("Memuat musik..."));
                }
                @Override public void onPageStop(GeckoSession s, boolean success) {
                    runOnUiThread(() -> status.setText(success ? "Siap" : "Gagal memuat"));
                }
            });
            session.open(runtime);
            view.setSession(session);
            back.setOnClickListener(v -> { if (session != null && session.canGoBack()) session.goBack(); });
            forward.setOnClickListener(v -> { if (session != null && session.canGoForward()) session.goForward(); });
            reload.setOnClickListener(v -> { if (session != null) session.reload(); });
            openHome();
        } catch (Throwable e) {
            status.setText("Gecko gagal: " + e.getClass().getSimpleName());
        }
    }

    private void hideKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchBox.getWindowToken(), 0);
            searchBox.clearFocus();
        } catch (Exception ignored) { }
    }

    private void searchMusic() {
        String q = searchBox.getText().toString().trim();
        if (q.length() == 0 || session == null) return;
        hideKeyboard();
        session.loadUri("https://music.youtube.com/search?q=" + Uri.encode(q));
    }

    private void openHome() {
        if (session != null) session.loadUri("https://music.youtube.com/");
    }

    @Override public void onBackPressed() {
        if (session != null && session.canGoBack()) {
            session.goBack();
            return;
        }
        super.onBackPressed();
    }

    @Override protected void onDestroy() {
        if (session != null) {
            session.close();
            session = null;
        }
        super.onDestroy();
    }
}
