package com.app.bids;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.webkit.WebView;

public class GifView2 extends WebView {
	public static final String HTML_FORMAT = "<html><body style=\"text-align: center;  vertical-align:right;background-color: transparent;\"><img src = \"%s\" /></body></html>";

	public GifView2(Context context, Drawable drawable) {
		super(context);
		final String html = String.format(HTML_FORMAT, drawable);

		setBackgroundColor(Color.TRANSPARENT);
		loadDataWithBaseURL("", html, "text/html", "UTF-8", "");
	}

}
