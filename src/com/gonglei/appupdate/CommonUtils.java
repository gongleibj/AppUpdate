package com.gonglei.appupdate;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Environment;

/**
 * ������
 * @author gonglei
 *
 */
public class CommonUtils {

	/**
	 * ��ð汾����
	 * 
	 * @param context
	 * @return
	 */
	public static String getVersionName(Context context) {
		try {
			final String PackageName = context.getPackageName();
			return context.getPackageManager().getPackageInfo(PackageName, 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * ���sdcard�Ƿ����
	 * 
	 * @return trueΪ���ã�����Ϊ������
	 */
	public static boolean sdCardIsAvailable() {
		String status = Environment.getExternalStorageState();
		if (!status.equals(Environment.MEDIA_MOUNTED))
			return false;
		return true;
	}

	public static float dp2px(Resources resources, float dp) {
		final float scale = resources.getDisplayMetrics().density;
		return dp * scale + 0.5f;
	}

	public static float sp2px(Resources resources, float sp) {
		final float scale = resources.getDisplayMetrics().scaledDensity;
		return sp * scale;
	}
	
	/**
	 * ��װapk
	 */
	public static void installApk(Context context, String path) {
		Uri uri = Uri.fromFile(new File(path));
		Intent intent = new Intent();
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.setDataAndType(uri, "application/vnd.android.package-archive");
		context.startActivity(intent);
	}


}
