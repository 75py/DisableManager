package com.nagopy.android.disablemanager.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.os.Environment;

import com.nagopy.android.disablemanager.dialog.FileOpenDialog.OnOpenFileSelectedListner;

public class FileChooserDialogFragment extends DialogFragment {
	public static final String KEY_EXTENSIONS = "KEY_EXTENSIONS";
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

	public void init(OnOpenFileSelectedListner listner, String... extensions) {
		Bundle args = new Bundle();
		args.putSerializable(KEY_LISTENER, listner);
		args.putStringArray(KEY_EXTENSIONS, extensions);
		setArguments(args);
	}
}
