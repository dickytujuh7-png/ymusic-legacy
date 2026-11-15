package id.ymusic.kaiossimulator;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class MainActivity extends Activity {
    private WebView screen;
    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        screen = new WebView(this);
        WebSettings settings = screen.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
        screen.setWebChromeClient(new WebChromeClient());
        setContentView(screen);
        screen.loadUrl("file:///android_asset/index.html");
    }
    @Override public void onBackPressed() {
        if (screen != null && screen.canGoBack()) screen.goBack(); else super.onBackPressed();
    }
}
