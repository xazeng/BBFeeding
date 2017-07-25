package com.zeng.bbfeeding;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.zeng.bbfeeding.databinding.ActivityBrowserBinding;

public class BrowserActivity extends AppCompatActivity {

    private ActivityBrowserBinding mBinding = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_browser);

        mBinding.gobackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBinding.webView.canGoBack()) {
                    mBinding.webView.goBack();
                } else {
                    finish();
                }
            }
        });

        initWebView();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if (mBinding.webView.canGoBack()) {
                mBinding.webView.goBack();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    private void initWebView(){
        mBinding.webView.getSettings().setJavaScriptEnabled(true);
        mBinding.webView.getSettings().setDefaultTextEncodingName("utf8");
        // mBinding.webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mBinding.webView.setHorizontalScrollBarEnabled(false);
        mBinding.webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                mBinding.progressBar.setProgress(newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                mBinding.titleTextView.setText(title);
            }
        });
        mBinding.webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (URLUtil.isNetworkUrl(url)) {
                    mBinding.titleTextView.setText(url);
                    view.loadUrl(url);
                } else {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } catch (android.content.ActivityNotFoundException anfe) {
                    }
                }
                return true;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                view.loadUrl("about:blank");
            }
        });
        mBinding.webView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });

        mBinding.webView.addJavascriptInterface(new JsObject(), "android");

        String url = getIntent().getStringExtra("url");
        mBinding.titleTextView.setText(url);
        mBinding.webView.loadUrl(url);
    }

    class JsObject {
        @JavascriptInterface
        public void actionSend(String title, String content) {
            Intent share = new Intent(android.content.Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT, content);
            startActivity(Intent.createChooser(share, title));
        }

        @JavascriptInterface
        public boolean actionView(String uri) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri)));
            } catch (android.content.ActivityNotFoundException anfe) {
                return false;
            }
            return true;
        }

        ///////////////////////////////////////////////////////////
        // webview interface
        @JavascriptInterface
        public void wv_canGoBack() {
            mBinding.webView.post(new Runnable() {
                @Override
                public void run() {
                    mBinding.webView.canGoBack();
                }
            });
        }

        @JavascriptInterface
        public void wv_goBack() {
            mBinding.webView.post(new Runnable() {
                @Override
                public void run() {
                    mBinding.webView.goBack();
                }
            });
        }

        @JavascriptInterface
        public void wv_goForward() {
            mBinding.webView.post(new Runnable() {
                @Override
                public void run() {
                    mBinding.webView.goForward();
                }
            });
        }

        @JavascriptInterface
        public void wv_reload() {
            mBinding.webView.post(new Runnable() {
                @Override
                public void run() {
                    mBinding.webView.reload();
                }
            });
        }

        @JavascriptInterface
        public void wv_clearCache(final boolean includeDiskFiles) {
            mBinding.webView.post(new Runnable() {
                @Override
                public void run() {
                    mBinding.webView.clearCache(includeDiskFiles);
                }
            });
        }

        @JavascriptInterface
        public void wv_clearHistory() {
            mBinding.webView.post(new Runnable() {
                @Override
                public void run() {
                    mBinding.webView.clearHistory();
                }
            });
        }
    }
}
