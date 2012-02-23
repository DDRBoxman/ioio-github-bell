package com.recursivepenguin.android.ioiogithubbell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
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

	public static Map<String, String> getQueryMap(String query) {
		String[] params = query.split("&");
		Map<String, String> map = new HashMap<String, String>();
		for (String param : params) {
			String name = param.split("=")[0];
			String value = param.split("=")[1];
			map.put(name, value);
		}
		return map;
	}

	class FetchAccessToken extends AsyncTask<String, Object, String> {

		@Override
		protected String doInBackground(String... code) {

			if (code.length > 0) {

				String postUrl = "https://github.com/login/oauth/access_token";
				postUrl += "?client_id=" + Common.CLIENT_ID;
				postUrl += "&client_secret=" + Common.CLIENT_SECRET;
				postUrl += "&code=" + code[0];

				HttpPost post = new HttpPost(postUrl);

				try {
					HttpResponse response = mClient.execute(post);
					HttpEntity entity = response.getEntity();
					InputStream inputStream = entity.getContent();
					BufferedReader r = new BufferedReader(
							new InputStreamReader(inputStream));
					StringBuilder total = new StringBuilder(
							inputStream.available());
					String line;
					while ((line = r.readLine()) != null) {
						total.append(line);
					}

					String result = total.toString();
					Map<String, String> data = getQueryMap(result);
					return data.get("access_token");
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

		@Override
		protected void onPostExecute(String result) {
			if (result != null) {
				Log.d("", result);
			}
		}
		
		
	}
}
