package com.zeng.bbfeeding;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import java.io.File;

/**
 * Created by xianganzeng on 2016/10/27.
 */

public class LikePage extends Page{
    private WebView mWebView;
    private ProgressBar mProgressBar;
    private boolean mLoaded = false;
    private boolean mRedirect = false;

    @Override
    protected void onCreatePage(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.page_like);

        mProgressBar = (ProgressBar)findViewById(R.id.progress_bar);
        mWebView = (WebView)findViewById(R.id.web_view);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDefaultTextEncodingName("utf8");
        // mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                // mProgressBar.setProgress(newProgress);
                if (newProgress == 100) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mProgressBar.setVisibility(View.GONE);
                        }
                    }, 500);
                }
                else {
                    mProgressBar.setVisibility(View.VISIBLE);
                }
            }
        });
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (!mRedirect && URLUtil.isNetworkUrl(url)) {
                    view.loadUrl(url);
                } else {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
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
        mWebView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });

        mWebView.addJavascriptInterface(new JsObject(), "android");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!mLoaded) {
                    mLoaded = true;
                    mWebView.loadUrl(getString(R.string.like_page_url));
                }
            }
        }, 1000);
    }

    @Override
    protected void onShowPage() {
        super.onShowPage();
        if (!mLoaded) {
            mLoaded = true;
            mWebView.loadUrl(getString(R.string.like_page_url));
        }
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

        @JavascriptInterface
        public void enableRedirect(boolean enable ) {mRedirect = enable;}

        @JavascriptInterface
        public int getVersion() {return Config.VERSION;}

        @JavascriptInterface
        public String getMarket() {return Config.MARKET;}

        @JavascriptInterface
        public String getFeedingHistory() {return Data.getInstance().getFeedingHistory();}

        @JavascriptInterface
        public void setFeedingHistory(String history) {Data.getInstance().setFeedingHistory(history);}

        @JavascriptInterface
        public void enableAdmob(boolean enable) { Data.getInstance().setAdmobEnabled(enable); }

        ///////////////////////////////////////////////////////////
        // webview interface
        @JavascriptInterface
        public void wv_canGoBack() {
            mWebView.post(new Runnable() {
                @Override
                public void run() {
                    mWebView.canGoBack();
                }
            });
        }

        @JavascriptInterface
        public void wv_goBack() {
            mWebView.post(new Runnable() {
                @Override
                public void run() {
                    mWebView.goBack();
                }
            });
        }

        @JavascriptInterface
        public void wv_goForward() {
            mWebView.post(new Runnable() {
                @Override
                public void run() {
                    mWebView.goForward();
                }
            });
        }

        @JavascriptInterface
        public void wv_reload() {
            mWebView.post(new Runnable() {
                @Override
                public void run() {
                    mWebView.reload();
                }
            });
        }

        @JavascriptInterface
        public void wv_clearCache(final boolean includeDiskFiles) {
            mWebView.post(new Runnable() {
                @Override
                public void run() {
                    mWebView.clearCache(includeDiskFiles);
                }
            });
        }

        @JavascriptInterface
        public void wv_clearHistory() {
            mWebView.post(new Runnable() {
                @Override
                public void run() {
                    mWebView.clearHistory();
                }
            });
        }
    }
}
