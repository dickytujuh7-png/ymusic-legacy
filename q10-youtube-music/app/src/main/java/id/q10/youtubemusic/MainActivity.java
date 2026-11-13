package id.q10.youtubemusic;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.net.Uri;

public class MainActivity extends Activity {
    private final int BG = Color.rgb(13, 15, 19);
    private final int PANEL = Color.rgb(24, 27, 33);
    private final int PANEL2 = Color.rgb(34, 38, 46);
    private final int RED = Color.rgb(235,  fifty(),  sixty());
    private final int MUTED = Color.rgb(157, 165, 178);
    private EditText searchBox;
    private WebView webView;
    private ProgressBar progress;
    private TextView status;
    private TextView pageTitle;

    private int fifty() { return  fiftyValue; }
    private int sixty() { return  sixtyValue; }
    private final int fiftyValue = 54;
    private final int sixtyValue = 64;

    private int dp(float v) {
        return (int)(v * getResources().getDisplayMetrics().density + 0.5f);
    }

    private GradientDrawable shape(int color, int radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(dp(radius));
        return d;
    }

    private TextView text(String value, int size, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return t;
    }

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        buildInterface();
    }

    private void buildInterface() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(BG);

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(dp(16), dp(10), dp(16), dp(8));

        LinearLayout logo = new LinearLayout(this);
        logo.setGravity(Gravity.CENTER);
        logo.setBackground(shape(RED, 10));
        TextView mark = text("Y", 23, Color.WHITE, true);
        mark.setGravity(Gravity.CENTER);
        logo.addView(mark, new LinearLayout.LayoutParams(dp(42), dp(42)));
        header.addView(logo);

        LinearLayout brand = new LinearLayout(this);
        brand.setOrientation(LinearLayout.VERTICAL);
        brand.setPadding(dp(10), 0, 0, 0);
        TextView title = text("YMUSIC", 19, Color.WHITE, true);
        TextView sub = text("Q10 EDITION  •  ONLINE", 9, MUTED, true);
        brand.addView(title);
        brand.addView(sub);
        header.addView(brand, new LinearLayout.LayoutParams(0, -2, 1));

        status = text("SIAP", 10, Color.rgb(100, 205, 145), true);
        status.setGravity(Gravity.CENTER);
        header.addView(status);
        root.addView(header);

        LinearLayout searchPanel = new LinearLayout(this);
        searchPanel.setOrientation(LinearLayout.VERTICAL);
        searchPanel.setPadding(dp(14), dp(10), dp(14), dp(11));
        searchPanel.setBackground(shape(PANEL, 14));
        LinearLayout.LayoutParams sp = new LinearLayout.LayoutParams(-1, -2);
        sp.setMargins(dp(12), dp(2), dp(12), dp(9));
        root.addView(searchPanel, sp);

        TextView heading = text("Temukan musik favoritmu", 15, Color.WHITE, true);
        searchPanel.addView(heading);
        TextView hint = text("Cari lagu, artis, atau album di YouTube", 10, MUTED, false);
        LinearLayout.LayoutParams hp = new LinearLayout.LayoutParams(-1, -2);
        hp.topMargin = dp(3);
        searchPanel.addView(hint, hp);

        LinearLayout searchRow = new LinearLayout(this);
        searchRow.setOrientation(LinearLayout.HORIZONTAL);
        searchRow.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams srp = new LinearLayout.LayoutParams(-1, dp(44));
        srp.topMargin = dp(10);
        searchPanel.addView(searchRow, srp);

        searchBox = new EditText(this);
        searchBox.setSingleLine(true);
        searchBox.setTextSize(13);
        searchBox.setTextColor(Color.WHITE);
        searchBox.setHintTextColor(Color.rgb(130, 138, 151));
        searchBox.setHint("Ketik judul lagu atau penyanyi");
        searchBox.setPadding(dp(12), 0, dp(8), 0);
        searchBox.setBackground(shape(PANEL2, 9));
        searchBox.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        searchRow.addView(searchBox, new LinearLayout.LayoutParams(0, -1, 1));

        Button search = new Button(this);
        search.setText("CARI");
        search.setTextSize(11);
        search.setTextColor(Color.WHITE);
        search.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        search.setAllCaps(false);
        search.setPadding(0, 0, 0, 0);
        search.setBackground(shape(RED, 9));
        LinearLayout.LayoutParams bp = new LinearLayout.LayoutParams(dp(68), -1);
        bp.leftMargin = dp(7);
        searchRow.addView(search, bp);

        LinearLayout quick = new LinearLayout(this);
        quick.setOrientation(LinearLayout.HORIZONTAL);
        quick.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams qp = new LinearLayout.LayoutParams(-1, dp(30));
        qp.topMargin = dp(7);
        searchPanel.addView(quick, qp);
        addQuickChip(quick, "Lagu populer", "top songs Indonesia");
        addQuickChip(quick, "Akustik", "acoustic music");
        addQuickChip(quick, "Live", "live music");

        LinearLayout contentHead = new LinearLayout(this);
        contentHead.setGravity(Gravity.CENTER_VERTICAL);
        contentHead.setPadding(dp(16), dp(1), dp(16), dp(6));
        pageTitle = text("Jelajahi YouTube", 12, Color.WHITE, true);
        contentHead.addView(pageTitle, new LinearLayout.LayoutParams(0, -2, 1));
        TextView source = text("HASIL MUSIK", 9, MUTED, true);
        contentHead.addView(source);
        root.addView(contentHead);

        progress = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        progress.setMax(100);
        progress.setProgress(0);
        progress.setProgressDrawable(getResources().getDrawable(android.R.drawable.progress_horizontal));
        root.addView(progress, new LinearLayout.LayoutParams(-1, dp(3)));

        webView = new WebView(this);
        webView.setBackgroundColor(BG);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setLoadsImagesAutomatically(true);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setMediaPlaybackRequiresUserGesture(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override public void onPageStarted(WebView view, String url, android.graphics.Bitmap icon) {
                status.setText("MEMUAT");
                pageTitle.setText("Memuat halaman musik");
                progress.setVisibility(View.VISIBLE);
            }
            @Override public void onPageFinished(WebView view, String url) {
                status.setText("ONLINE");
                pageTitle.setText("Jelajahi YouTube");
                progress.setVisibility(View.GONE);
            }
            @Override public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override public void onProgressChanged(WebView view, int value) {
                progress.setProgress(value);
                if (value >= 100) progress.setVisibility(View.GONE);
            }
        });
        root.addView(webView, new LinearLayout.LayoutParams(-1, 0, 1));
        TextView footer = text("Pilih video di YouTube untuk mulai memutar musik", 9, MUTED, false);
        footer.setGravity(Gravity.CENTER);
        footer.setPadding(dp(8), dp(7), dp(8), dp(7));
        root.addView(footer, new LinearLayout.LayoutParams(-1, -2));

        setContentView(root);
        search.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { searchYouTube(); }
        });
        searchBox.setOnEditorActionListener((v, action, event) -> {
            if (action == EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                searchYouTube();
                return true;
            }
            return false;
        });
        webView.loadUrl("https://m.youtube.com/");
    }

    private void addQuickChip(LinearLayout row, String label, final String query) {
        TextView chip = text(label, 9, Color.rgb(220, 224, 231), true);
        chip.setGravity(Gravity.CENTER);
        chip.setPadding(dp(9), 0, dp(9), 0);
        chip.setBackground(shape(PANEL2, 20));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-2, dp(27));
        p.rightMargin = dp(6);
        row.addView(chip, p);
        chip.setOnClickListener(v -> {
            searchBox.setText(query);
            searchYouTube();
        });
    }

    private void searchYouTube() {
        String q = searchBox.getText().toString().trim();
        if (q.length() == 0) {
            searchBox.setError("Masukkan judul lagu atau artis");
            return;
        }
        try {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.hideSoftInputFromWindow(searchBox.getWindowToken(), 0);
        } catch (Exception ignored) {}
        pageTitle.setText("Hasil pencarian");
        webView.loadUrl("https://m.youtube.com/results?search_query=" + Uri.encode(q));
    }

    @Override public void onBackPressed() {
        if (webView != null && webView.canGoBack()) webView.goBack();
        else super.onBackPressed();
    }

    @Override protected void onPause() {
        if (webView != null) webView.onPause();
        super.onPause();
    }

    @Override protected void onResume() {
        super.onResume();
        if (webView != null) webView.onResume();
    }

    @Override protected void onDestroy() {
        if (webView != null) {
            webView.stopLoading();
            webView.loadUrl("about:blank");
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }
}
