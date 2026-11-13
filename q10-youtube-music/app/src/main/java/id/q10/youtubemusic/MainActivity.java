package id.q10.youtubemusic;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.mozilla.geckoview.GeckoRuntime;
import org.mozilla.geckoview.GeckoSession;
import org.mozilla.geckoview.GeckoView;

public class MainActivity extends Activity {
    private GeckoView geckoView;
    private GeckoSession session;
    private EditText searchBox;

    private int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.rgb(12, 14, 18));

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.VERTICAL);
        header.setPadding(dp(12), dp(9), dp(12), dp(10));
        header.setBackgroundColor(Color.rgb(20, 23, 29));

        TextView title = new TextView(this);
        title.setText("Q10  YOUTUBE MUSIC");
        title.setTextColor(Color.WHITE);
        title.setTextSize(17);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        title.setGravity(Gravity.CENTER_VERTICAL);
        header.addView(title, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(28)));

        TextView subtitle = new TextView(this);
        subtitle.setText("Cari lagu dan putar langsung dari YouTube");
        subtitle.setTextColor(Color.rgb(164, 174, 190));
        subtitle.setTextSize(11);
        header.addView(subtitle, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(20)));

        LinearLayout searchRow = new LinearLayout(this);
        searchRow.setOrientation(LinearLayout.HORIZONTAL);
        searchRow.setGravity(Gravity.CENTER_VERTICAL);
        searchRow.setPadding(0, dp(7), 0, 0);

        searchBox = new EditText(this);
        searchBox.setSingleLine(true);
        searchBox.setTextSize(14);
        searchBox.setTextColor(Color.WHITE);
        searchBox.setHintTextColor(Color.rgb(145, 153, 166));
        searchBox.setHint("Cari judul lagu atau penyanyi");
        searchBox.setPadding(dp(11), 0, dp(8), 0);
        searchBox.setBackgroundColor(Color.rgb(42, 47, 57));
        searchBox.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        searchRow.addView(searchBox, new LinearLayout.LayoutParams(0, dp(42), 1));

        Button searchButton = new Button(this);
        searchButton.setText("CARI");
        searchButton.setTextSize(12);
        searchButton.setTextColor(Color.WHITE);
        searchButton.setAllCaps(false);
        searchButton.setBackgroundColor(Color.rgb(205, 35, 48));
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(dp(76), dp(42));
        buttonParams.leftMargin = dp(7);
        searchRow.addView(searchButton, buttonParams);
        header.addView(searchRow);

        root.addView(header, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        geckoView = new GeckoView(this);
        root.addView(geckoView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f));
        setContentView(root);

        GeckoRuntime runtime = GeckoRuntime.create(this);
        session = new GeckoSession();
        session.open(runtime);
        geckoView.setSession(session);
        session.loadUri("https://m.youtube.com/");

        searchButton.setOnClickListener(view -> searchYouTube());
        searchBox.setOnEditorActionListener((textView, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                searchYouTube();
                return true;
            }
            return false;
        });
    }

    private void searchYouTube() {
        String query = searchBox.getText().toString().trim();
        if (query.length() == 0) {
            searchBox.setError("Masukkan judul lagu atau penyanyi");
            return;
        }
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.hideSoftInputFromWindow(searchBox.getWindowToken(), 0);
        } catch (Exception ignored) { }
        session.loadUri("https://m.youtube.com/results?search_query=" + Uri.encode(query));
    }

    @Override
    public void onBackPressed() {
        if (session != null) session.goBack();
        else super.onBackPressed();
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
