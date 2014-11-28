package com.gonglei.appupdate;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

/**
 * Http请求工具类
 * @author gonglei
 *
 */
public class HttpUtil {

	/**实例化AsyncHttpClient，由开源的AsyncHttpClient提供*/
	private static AsyncHttpClient sHttpUtil = new AsyncHttpClient();

	public static void get(String url, TextHttpResponseHandler handler) {
		sHttpUtil.get(url, handler);
	}

	public static void get(String url, RequestParams params,
			TextHttpResponseHandler handler) {
		sHttpUtil.get(url, params, handler);
	}

	public static void get(String url, FileAsyncHttpResponseHandler handler) {
		sHttpUtil.get(url, handler);
	}
}
