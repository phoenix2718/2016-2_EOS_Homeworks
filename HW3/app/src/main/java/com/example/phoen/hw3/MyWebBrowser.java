package com.example.phoen.hw3;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

public class MyWebBrowser extends AppCompatActivity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_web_browser);

        // Enable Javascript
        webView = (WebView)findViewById(R.id.my_web_browser_webview);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // For hiding the soft keyboard, which covers the WebView.
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Intent intent = getIntent();
        if(intent != null) {
            // If a hypertext is clicked, ...
            if(intent.getAction() == Intent.ACTION_VIEW) {
                Uri uri = intent.getData();
                EditText editText = (EditText)findViewById(R.id.my_web_browser_edittext);
                editText.setText(uri.toString());
                loadWebPageInEditText();
            }
        }
    }

    public void onButtonClick(View v) {
        loadWebPageInEditText();
    }

    // Load a web page of the URL in the EditText(id: my_web_browser_edittext).
    private void loadWebPageInEditText() {
        EditText editText = (EditText)findViewById(R.id.my_web_browser_edittext);
        final String url = editText.getText().toString();
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);
    }
}
