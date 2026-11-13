package id.q10.gecko68test;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.mozilla.geckoview.GeckoRuntime;
import org.mozilla.geckoview.GeckoSession;
import org.mozilla.geckoview.GeckoView;

public class MainActivity extends Activity {
    private GeckoSession session;
    private FrameLayout root;
    private TextView status;
    private boolean pageStarted = false;

    private void showStatus(String message) {
        if (status != null) {
            status.setText(message);
            status.setVisibility(View.VISIBLE);
        }
    }

    private void hideStatus() {
        if (status != null) status.setVisibility(View.GONE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        root = new FrameLayout(this);
        GeckoView view = new GeckoView(this);
        root.addView(view, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        status = new TextView(this);
        status.setText("Memulai mesin GeckoView 68...");
        status.setTextColor(Color.WHITE);
        status.setTextSize(16);
        status.setGravity(Gravity.CENTER);
        status.setPadding(28, 28, 28, 28);
        status.setBackgroundColor(Color.rgb(32, 38, 48));
        root.addView(status, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        setContentView(root);

        try {
            GeckoRuntime runtime = GeckoRuntime.create(this);
            session = new GeckoSession();

            session.setProgressDelegate(new GeckoSession.ProgressDelegate() {
                @Override
                public void onPageStart(GeckoSession currentSession, String url) {
                    pageStarted = true;
                    runOnUiThread(() -> showStatus("Memuat halaman:\n" + url));
                }

                @Override
                public void onPageStop(GeckoSession currentSession, boolean success) {
                    runOnUiThread(() -> {
                        if (success) {
                            hideStatus();
                        } else {
                            showStatus("GeckoView berjalan, tetapi halaman gagal dimuat.\nPeriksa koneksi atau dukungan situs.");
                        }
                    });
                }
            });

            session.open(runtime);
            view.setSession(session);
            showStatus("GeckoView aktif. Menghubungkan ke example.com...");
            session.loadUri("https://example.com/");
        } catch (Throwable error) {
            showStatus("GeckoView gagal dimulai.\n\n" +
                    error.getClass().getName() + "\n" +
                    String.valueOf(error.getMessage()));
        }
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
