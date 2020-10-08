package rutherfordit.com.instasalary.fragments;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import rutherfordit.com.instasalary.R;

public class TermsFragment extends Fragment {

    View v;
    WebView mWebView;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.layout_terms, null);

        init();

        return v;
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void init() {

        mWebView = (WebView) v.findViewById(R.id.terms_web_view);
        mWebView.loadUrl("https://google.com");

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        mWebView.setWebViewClient(new WebViewClient());

    }

}
