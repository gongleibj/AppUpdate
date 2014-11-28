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
 * app�汾���¹�����
 * 
 * @author gonglei
 * 
 */
public class UpdateManager {

	private Context mContext;
	/**���汾���µķ�����URL*/
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
	 * ����������°汾��Ϣ
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
					if (return_value.equals("0")) {// return_valueΪ0�����°汾
						Toast.makeText(mContext, "�Ѿ������°汾~", Toast.LENGTH_SHORT)
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
				Toast.makeText(mContext, "����ʧ����", Toast.LENGTH_SHORT).show();
				Log.d("=========", "failfure = " + arg3);
			}
		});
	}

	/**
	 * �����µ�progressDialog
	 */
	private void showProgressDialog() {
		progressDialog = new ProgressDialog(mContext);
		progressDialog.setCancelable(true);
		progressDialog.setCanceledOnTouchOutside(true);
		progressDialog.setIndeterminate(true);
		progressDialog.setMessage("��������...");
		progressDialog.show();
	}

	/**
	 * �°汾�������ѶԻ���
	 */
	private void showUpdateDialog(String msg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle("�����°汾");
		builder.setMessage(msg);
		builder.setNegativeButton("�Ժ���˵", null);
		builder.setPositiveButton("��������", positiveListener);
		builder.create();
		builder.show();
	}

	private OnClickListener positiveListener = new OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			dialog.dismiss();
			// ��ʾ���ؽ��ȶԻ���
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
	 * ��������·��
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
	 * ��ʼ����
	 */
	public void onDownLoad() {

		// ����������·��Ϊ�ջ������ص�URLΪ�ն���ʾ����ʧ�ܣ�Ȼ���˳���
		if (getLocalpath() == null || info.getUrl() == null
				|| info.getUrl().equals("")) {
			mDownloadDialog.dismiss();
			Toast.makeText(mContext, "����ʧ��~", Toast.LENGTH_SHORT).show();
			return;
		}
		// ���������ļ�
		File dir = new File(getLocalpath());
		if (!dir.exists()) {
			dir.mkdir();
		}
		File file = new File(dir, APK_NAME);
		// ����apk��Ȼ��д�뱾���ļ�
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
				// ������ɺ�ʼ��װ
				CommonUtils.installApk(mContext, getLocalpath() + APK_NAME);
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, Throwable arg2,
					File arg3) {
				// TODO Auto-generated method stub
				mDownloadDialog.dismiss();
				Toast.makeText(mContext, "����ʧ����", Toast.LENGTH_SHORT).show();

			}
		});

	}

}
