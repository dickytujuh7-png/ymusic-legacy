package id.q10.gecko68test;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.mozilla.geckoview.GeckoRuntime;
import org.mozilla.geckoview.GeckoSession;
import org.mozilla.geckoview.GeckoView;

public class MainActivity extends Activity {
    private GeckoSession session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout root = new FrameLayout(this);
        GeckoView view = new GeckoView(this);
        root.addView(view, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        setContentView(root);

        try {
            GeckoRuntime runtime = GeckoRuntime.create(this);
            session = new GeckoSession();
            session.open(runtime);
            view.setSession(session);
            session.loadUri("https://example.com/");
        } catch (Throwable error) {
            TextView message = new TextView(this);
            message.setText("GeckoView tidak dapat dimulai.\n\n" + error);
            message.setTextSize(16);
            int pad = 24;
            message.setPadding(pad, pad, pad, pad);
            root.removeAllViews();
            root.addView(message);
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
