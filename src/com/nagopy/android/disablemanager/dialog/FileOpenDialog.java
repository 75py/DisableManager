package com.nagopy.android.disablemanager.dialog;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.nagopy.android.disablemanager.R;

public class FileOpenDialog implements AdapterView.OnItemClickListener, AdapterView.OnKeyListener {
	public static final String TEMP_DIR = "common_util_fileopendialog_tempdir";

	protected OnOpenFileSelectedListner mOnOpenFileSelectedListner;

	private Activity mActivity;
	private AlertDialog mDialog;
	private ListView lv;
	private FileArrayAdapter adapter;
	private List<File> mHistory;
	private List<String> mExtensionFilter;
	private Drawable icon_folder, icon_file;

	private String defaultPathLabel;
	private String storageDirectory;

	/**
	 * 前回のパスを記憶させるためのキー
	 */
	private String tempDirKey;

	public static interface OnOpenFileSelectedListner extends Serializable {
		public abstract void onOpenFileSelected(File file);

		public abstract void onOpenFileCanceled();
	}

	public FileOpenDialog setOnOpenFileSelectedListner(OnOpenFileSelectedListner l) {
		mOnOpenFileSelectedListner = l;
		return this;
	}

	protected class FileArrayAdapter extends ArrayAdapter<File> {
		private int resourceId;

		public FileArrayAdapter(Context context, int resourceId) {
			super(context, resourceId);
			this.resourceId = resourceId;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final File file = (File) getItem(position);
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(
						Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(resourceId, null);
			}
			TextView tv = (TextView) convertView.findViewById(R.id.util_lib_fod_lo_label);
			// ImageView iv = (ImageView) convertView.findViewById(R.id.fod_lo_icon);

			String name = file.getName();
			if (name.equalsIgnoreCase("..")) {
				tv.setText("..");
				tv.setCompoundDrawables(icon_folder, null, null, null);
			} else if (name.equalsIgnoreCase("...")) {
				tv.setText(defaultPathLabel);
				tv.setCompoundDrawables(icon_folder, null, null, null);
			} else {
				tv.setText(name);
				if (file.isDirectory()) {
					tv.setCompoundDrawables(icon_folder, null, null, null);
				} else {
					tv.setCompoundDrawables(icon_file, null, null, null);
				}
			}

			return convertView;
		}
	}

	public FileOpenDialog addExtensionFilter(String... ext) {
		if (mExtensionFilter == null) {
			mExtensionFilter = new ArrayList<String>();
		}
		for (String s : ext) {
			mExtensionFilter.add(s);
		}
		return this;
	}

	public void clearExtensionFilter() {
		mExtensionFilter.clear();
	}

	public FileOpenDialog(Activity ac) {
		mActivity = ac;
		icon_folder = ac.getResources().getDrawable(R.drawable.util_lib_ic_folder);
		icon_folder.setBounds(0, 0, icon_folder.getIntrinsicWidth(), icon_folder.getIntrinsicHeight());
		icon_file = ac.getResources().getDrawable(R.drawable.util_lib_ic_file);
		icon_file.setBounds(0, 0, icon_file.getIntrinsicWidth(), icon_file.getIntrinsicHeight());
		defaultPathLabel = ac.getString(R.string.fod_back_to_default_path);
		storageDirectory = Environment.getExternalStorageDirectory().getAbsolutePath();
	}

	public AlertDialog createDialog(String start_path, String tempDirKey) {
		this.tempDirKey = tempDirKey;
		File currDir = new File(start_path);
		if (!currDir.isDirectory()) {
			// return null;
			currDir = new File(storageDirectory);
		}

		if (mExtensionFilter == null) {
			mExtensionFilter = new ArrayList<String>();
		}
		mHistory = new ArrayList<File>();

		File temp = currDir.getParentFile();
		while (temp != null) {
			mHistory.add(0, temp);
			temp = temp.getParentFile();
		}

		mHistory.add(currDir);
		lv = new ListView(mActivity);
		lv.setId(100);
		lv.setScrollingCacheEnabled(false);
		adapter = new FileArrayAdapter(mActivity, R.layout.fod_list_row);

		lv.setAdapter(adapter);
		lv.setOnItemClickListener(this);
		lv.setOnKeyListener(this);

		mDialog = new AlertDialog.Builder(mActivity).setTitle(currDir.getName()).setView(lv)
				.setCancelable(false).setNegativeButton(android.R.string.cancel, new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						mOnOpenFileSelectedListner.onOpenFileCanceled();
						dialog.cancel();
					}
				}).create();
		drawDialog(currDir);

		return mDialog;
	}

	private void drawDialog(File dir) {

		// Log.d("debug", "drawDialog:" + dir.toString());
		PreferenceManager.getDefaultSharedPreferences(mActivity).edit().putString(tempDirKey, dir.toString())
				.commit();

		// mDialog.setTitle(dir.getName());
		mDialog.setTitle(mHistory.get(mHistory.size() - 1).getAbsolutePath());

		adapter.clear();
		if (mHistory.size() > 1) {// ���ɖ߂�p
			adapter.add(new File(dir, ".."));
		}

		File[] files = dir.listFiles();
		if (files != null) {
			// �t�@�C�����Ń\�[�g
			File[] sorted_files = new File[files.length];
			ArrayList<CharSequence> al_fName = new ArrayList<CharSequence>();
			for (File f : files) {
				al_fName.add(f.getName());
			}
			String[] oa_fName = al_fName.toArray(new String[al_fName.size()]);
			java.util.Arrays.sort(oa_fName, String.CASE_INSENSITIVE_ORDER);
			int array_length = oa_fName.length;
			for (int i = 0; i < array_length; i++) {
				sorted_files[i] = files[al_fName.indexOf(oa_fName[i])];
			}
			al_fName.clear();

			for (File file : sorted_files) {
				if (file.isDirectory()) {
					adapter.add(file);
				} else if (mExtensionFilter.isEmpty()) {
					adapter.add(file);
				} else {
					int point = file.getName().lastIndexOf('.');
					if (point != -1) {
						String ext = file.getName().substring(point + 1);
						if (mExtensionFilter.contains(ext)) {
							adapter.add(file);
						}
					}
				}
			}
		}

		// �f�t�H�ɖ߂�p�̂��
		if (!dir.toString().equalsIgnoreCase(storageDirectory)) {
			adapter.add(new File(dir, "..."));
		}

		lv.setAdapter(adapter);
	}

	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		File file = (File) parent.getItemAtPosition(position);

		if (position == lv.getCount() - 1 && file.getName().equals("...")) {
			new AlertDialog.Builder(mActivity)
					.setMessage(mActivity.getString(R.string.fod_back_to_default_path_mes, storageDirectory))
					.setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							mHistory.clear();
							mHistory = new ArrayList<File>();
							File sd = new File(storageDirectory);
							File temp = sd.getParentFile();
							while (temp != null) {
								mHistory.add(0, temp);
								temp = temp.getParentFile();
							}
							mHistory.add(sd);
							drawDialog(sd);
						}
					}).setNegativeButton("No", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					}).show();
			return;
		}

		if (mHistory.size() > 1 && position == 0) {
			int last_id = mHistory.size() - 1;
			File up_dir = mHistory.get(last_id).getParentFile();
			mHistory.remove(last_id);
			drawDialog(up_dir);
			return;
		}

		if (file.isDirectory()) {
			mHistory.add(file);
			drawDialog(file);

		} else {
			mOnOpenFileSelectedListner.onOpenFileSelected(file);
			mDialog.dismiss();
		}

	}

	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if (KeyEvent.ACTION_UP == event.getAction() && KeyEvent.KEYCODE_BACK == keyCode) {
			if (mHistory.size() > 1) {
				int last_id = mHistory.size() - 1;
				File up_dir = mHistory.get(last_id).getParentFile();
				mHistory.remove(last_id);
				drawDialog(up_dir);
			} else {
				PreferenceManager.getDefaultSharedPreferences(mActivity).edit()
						.putString(tempDirKey, storageDirectory).commit();
				mOnOpenFileSelectedListner.onOpenFileCanceled();
				mDialog.dismiss();
			}
			return true;
		}
		return false;
	}
}
