package com.recursivepenguin.android.ioiogithubbell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class OauthActivity extends Activity {

	WebView mWebView;
	HttpClient mClient = new DefaultHttpClient();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mWebView = new WebView(this);
		mWebView.setWebViewClient(mWebViewClient);
		setContentView(mWebView);

		String urlString = "https://github.com/login/oauth/authorize";
		urlString += "?client_id=" + Common.CLIENT_ID;

		mWebView.loadUrl(urlString);
	}

	WebViewClient mWebViewClient = new WebViewClient() {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {

			String code = Uri.parse(url).getQueryParameter("code");

			if (code != null) {
				new FetchAccessToken().execute(code);
			}

			return false;
		}
	};

	class FetchAccessToken extends AsyncTask<String, Object, Object> {

		@Override
		protected Object doInBackground(String... code) {
			
			if (code.length > 0) {
			
				HttpPost post = new HttpPost("https://github.com/login/oauth/access_token");
				HttpParams params = new BasicHttpParams();
				params.setParameter("client_id", Common.CLIENT_ID);
				params.setParameter("client_secret", Common.CLIENT_SECRET);
				params.setParameter("code", code[0]);
				post.setParams(params);
				
				try {
					HttpResponse response = mClient.execute(post);
					HttpEntity entity = response.getEntity();
					InputStream inputStream = entity.getContent();
					BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
					StringBuilder total = new StringBuilder(inputStream.available());
					String line;
					while ((line = r.readLine()) != null) {
					    total.append(line);
					}
					
					Log.d("", total.toString());
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			return null;
		}
	}
}
