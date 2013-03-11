package com.nagopy.android.disablemanager.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.os.Environment;

import com.nagopy.android.disablemanager.dialog.FileOpenDialog.OnOpenFileSelectedListner;

/**
 * ファイル選択ダイアログ
 */
public class FileChooserDialogFragment extends DialogFragment {

	/**
	 * 拡張子を保存する際のキー
	 */
	public static final String KEY_EXTENSIONS = "KEY_EXTENSIONS";

	/**
	 * リスナーを保存する際のキー
	 */
	public static final String KEY_LISTENER = "KEY_LISTENER";

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		FileOpenDialog fileOpenDialog = new FileOpenDialog(getActivity());
		Bundle bundle = getArguments();

		String[] extensions = bundle.getStringArray(KEY_EXTENSIONS);
		if (extensions != null && extensions.length > 0) {
			fileOpenDialog.addExtensionFilter(extensions);
		}

		OnOpenFileSelectedListner listner = (OnOpenFileSelectedListner) bundle.getSerializable(KEY_LISTENER);
		if (listner != null) {
			fileOpenDialog.setOnOpenFileSelectedListner(listner);
		}

		return fileOpenDialog.createDialog(Environment.getExternalStorageDirectory().getAbsolutePath(),
				"tempDir");
	}

	/**
	 * 初期設定
	 * @param listner
	 *           リスナー
	 * @param extensions
	 *           表示する拡張子たち
	 */
	public void init(OnOpenFileSelectedListner listner, String... extensions) {
		Bundle args = new Bundle();
		args.putSerializable(KEY_LISTENER, listner);
		args.putStringArray(KEY_EXTENSIONS, extensions);
		setArguments(args);
	}
}
