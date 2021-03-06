package com.zeng.bbfeeding;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

/**
 * Created by xianganzeng on 2016/10/27.
 */

public class CouponPage extends Page{
    private WebView mWebView;
    private ProgressBar mProgressBar;
    private boolean mLoaded = false;

    @Override
    protected void onCreatePage(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.page_coupon);

        mProgressBar = (ProgressBar)findViewById(R.id.progress_bar);

        mWebView = (WebView)findViewById(R.id.web_view);
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDefaultTextEncodingName("utf8");
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setDisplayZoomControls(false);

        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                mProgressBar.setProgress(newProgress);
                if (newProgress == 100) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mProgressBar.setVisibility(View.GONE);
                        }
                    }, 300);
                }
                else {
                    mProgressBar.setVisibility(View.VISIBLE);
                }
            }
        });
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (URLUtil.isNetworkUrl(url)) {
                    Intent intent = new Intent(getContext(), BrowserActivity.class);
                    intent.putExtra("url", url);
                    startActivity(intent);
                } else {
                    final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    PackageManager pkM = getContext().getPackageManager();
                    ResolveInfo info = pkM.resolveActivity(intent, 0);
                    if (info != null) {
                        String appName = pkM.getApplicationLabel(info.activityInfo.applicationInfo).toString();
                        Drawable appIcon = pkM.getApplicationIcon(info.activityInfo.applicationInfo);
                        new AlertDialog.Builder(getContext())
                                .setIcon(appIcon)
                                .setTitle(String.format(getString(R.string.active_external_app_note), appName))
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                                | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                    }
                                })
                                .show();
                    }
                }
                return true;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                view.loadUrl("about:blank");
                super.onReceivedError(view, errorCode, description, failingUrl);
                view.loadData(String.format(getString(R.string.web_error_info), errorCode, description), "text/html", "utf8");
            }
        });
        mWebView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        mWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String s, String s1, String s2, String s3, long l) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(s));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }catch (android.content.ActivityNotFoundException anfe){}
            }
        });

        mWebView.addJavascriptInterface(new JsInterface(), "ajs");
    }

    @Override
    protected void onShowPage() {
        super.onShowPage();
        if (!mLoaded) {
            mLoaded = true;
            mWebView.loadUrl(getString(R.string.coupon_page_url));
        }
    }


    class JsInterface {

        @JavascriptInterface
        public void actionSend(final String title, final String content) {
            mWebView.post(new Runnable() {
                @Override
                public void run() {
                    Intent share = new Intent(android.content.Intent.ACTION_SEND);
                    share.setType("text/plain");
                    share.putExtra(Intent.EXTRA_TEXT, content);
                    startActivity(Intent.createChooser(share, title));
                }
            });
        }

        @JavascriptInterface
        public void actionView(final String url) {
            mWebView.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } catch (android.content.ActivityNotFoundException anfe) {
                    }
                }
            });
        }

        @JavascriptInterface
        public String getVersionName() {
            String versionName = "";
            try {
                versionName = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0).versionName;
            } catch (android.content.pm.PackageManager.NameNotFoundException e) {
            }

            return versionName;
        }

        @JavascriptInterface
        public String getPackageName() {
            return getContext().getPackageName();
        }

        @JavascriptInterface
        public void loadUrl(final String url) {
            mWebView.post(new Runnable() {
                @Override
                public void run() {
                    mWebView.loadUrl(url);
                }
            });
        }
    }
}
