package com.uet.fries.edoo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by TooNies1810 on 8/20/16.
 */
public class WebviewActivity extends AppCompatActivity {

    private static final String TAG = "WebviewActivity";
    private LinearLayout container;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.uet.fries.edoo.R.layout.activity_webview);

        initViews();
    }

    private void initViews() {
        toolbar = (Toolbar) findViewById(com.uet.fries.edoo.R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        container = (LinearLayout) findViewById(com.uet.fries.edoo.R.id.container);

        final WebView webView = new WebView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        container.addView(webView, params);

        Intent mIntent = getIntent();
        final String url = mIntent.getStringExtra("url");

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.setWebViewClient(new MyWebviewClient());
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        webView.postDelayed(new Runnable() {

            @Override
            public void run() {
                webView.loadUrl(url);
            }
        }, 500);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();

        callHiddenWebViewMethod("onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();

        callHiddenWebViewMethod("onResume");
    }

    @Override
    protected void onDestroy() {
        int countView = container.getChildCount();
        for (int i = 0; i < countView; i++) {
            View view = container.getChildAt(i);
            if (view instanceof WebView){
                WebView webView = (WebView) view;
                webView.loadUrl("about:blank");
//                Log.i(TAG, "ok load blank");
            }
        }
        container.removeAllViews();
        super.onDestroy();
    }

    private void callHiddenWebViewMethod(String name){
        int countView = container.getChildCount();
        for (int i = 0; i < countView; i++) {
            View view = container.getChildAt(i);
            if (view instanceof WebView){
                WebView webView = (WebView) view;
                try {
                    Method method = WebView.class.getMethod(name);
                    method.invoke(webView);
                } catch (NoSuchMethodException e) {
//                    Log.error("No such method: " + name, e);
                } catch (IllegalAccessException e) {
//                    Log.error("Illegal Access: " + name, e);
                } catch (InvocationTargetException e) {
//                    Log.error("Invocation Target Exception: " + name, e);
                }
            }
        }

    }

    class MyWebviewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}
