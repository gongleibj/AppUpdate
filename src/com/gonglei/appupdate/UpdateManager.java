package com.gonglei.appupdate;

import java.io.File;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

/**
 * app版本更新管理类
 * 
 * @author gonglei
 * 
 */
public class UpdateManager {

	private Context mContext;
	/**检查版本更新的服务器URL*/
	private static final String URL = "";
	private NewVersionInfo info;
	private Dialog mDownloadDialog;
	private DonutProgress mProgress;
	private static final String APK_NAME = "test.apk";
	private int progress = 0;
	private ProgressDialog progressDialog;

	public UpdateManager(Context context) {
		this.mContext = context;
	}

	/**
	 * 检查服务端最新版本信息
	 */
	public void checkUpdate() {
		showProgressDialog();

		String version = CommonUtils.getVersionName(mContext);
		RequestParams params = new RequestParams();
		params.add("version", version);
		HttpUtil.get(URL, params, new TextHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, Header[] arg1, String arg2) {
				// TODO Auto-generated method stub
				progressDialog.cancel();
				try {
					JSONObject object = new JSONObject(arg2);
					String return_value = object.getString("return_value");
					if (return_value.equals("0")) {// return_value为0是最新版本
						Toast.makeText(mContext, "已经是最新版本~", Toast.LENGTH_SHORT)
								.show();
					} else {
						info = new NewVersionInfo();
						info.setVersion(object.getString("version"));
						info.setUrl(object.getString("url"));
						info.setDescription(object.getString("description"));

						if (!info.getDescription().equals("")
								|| info.getDescription() != null) {
							showUpdateDialog(info.getDescription());
						} else {
							showUpdateDialog("fix bugs");
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			@Override
			public void onFailure(int arg0, Header[] arg1, String arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
				progressDialog.cancel();
				Toast.makeText(mContext, "更新失败了", Toast.LENGTH_SHORT).show();
				Log.d("=========", "failfure = " + arg3);
			}
		});
	}

	/**
	 * 检查更新的progressDialog
	 */
	private void showProgressDialog() {
		progressDialog = new ProgressDialog(mContext);
		progressDialog.setCancelable(true);
		progressDialog.setCanceledOnTouchOutside(true);
		progressDialog.setIndeterminate(true);
		progressDialog.setMessage("检查更新中...");
		progressDialog.show();
	}

	/**
	 * 新版本更新提醒对话框
	 */
	private void showUpdateDialog(String msg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle("发现新版本");
		builder.setMessage(msg);
		builder.setNegativeButton("稍后再说", null);
		builder.setPositiveButton("立即下载", positiveListener);
		builder.create();
		builder.show();
	}

	private OnClickListener positiveListener = new OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			dialog.dismiss();
			// 显示下载进度对话框
			LayoutInflater inflater = LayoutInflater.from(mContext);
			View v = inflater.inflate(R.layout.dialog_update_layout, null);
			mProgress = (DonutProgress) v.findViewById(R.id.progress);
			mDownloadDialog = new Dialog(mContext, R.style.dialog);
			mDownloadDialog.setContentView(v);
			mDownloadDialog.setCanceledOnTouchOutside(true);
			mDownloadDialog.setCancelable(true);
			Window w = mDownloadDialog.getWindow();
			w.setGravity(Gravity.CENTER);
			w.setBackgroundDrawable(new BitmapDrawable());
			mDownloadDialog.show();

			onDownLoad();
		}
	};

	/**
	 * 本地下载路径
	 * 
	 * @return
	 */
	private String getLocalpath() {
		if (CommonUtils.sdCardIsAvailable()) {
			return Environment.getExternalStorageDirectory().getPath() + "/"
					+ "apkdownload/";
		}
		return null;
	}

	/**
	 * 开始下载
	 */
	public void onDownLoad() {

		// 若本地下载路径为空或者下载的URL为空都显示下载失败，然后退出。
		if (getLocalpath() == null || info.getUrl() == null
				|| info.getUrl().equals("")) {
			mDownloadDialog.dismiss();
			Toast.makeText(mContext, "下载失败~", Toast.LENGTH_SHORT).show();
			return;
		}
		// 创建本地文件
		File dir = new File(getLocalpath());
		if (!dir.exists()) {
			dir.mkdir();
		}
		File file = new File(dir, APK_NAME);
		// 下载apk，然后写入本地文件
		HttpUtil.get(info.getUrl(), new FileAsyncHttpResponseHandler(file) {

			@Override
			public void onProgress(int bytesWritten, int totalSize) {
				// TODO Auto-generated method stub
				progress = (int) ((bytesWritten * 1.0 / totalSize) * 100);
				mProgress.setProgress(progress);

				super.onProgress(bytesWritten, totalSize);
			}

			@Override
			public void onSuccess(int arg0, Header[] arg1, File arg2) {
				// TODO Auto-generated method stub
				mDownloadDialog.dismiss();
				// 下载完成后开始安装
				CommonUtils.installApk(mContext, getLocalpath() + APK_NAME);
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, Throwable arg2,
					File arg3) {
				// TODO Auto-generated method stub
				mDownloadDialog.dismiss();
				Toast.makeText(mContext, "下载失败了", Toast.LENGTH_SHORT).show();

			}
		});

	}

}
