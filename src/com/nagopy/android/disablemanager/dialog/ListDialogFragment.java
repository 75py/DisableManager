package com.nagopy.android.disablemanager.dialog;

import java.io.Serializable;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * ダイアログでリストを表示するためのフラグメント
 */
public class ListDialogFragment extends DialogFragment {

	/**
	 * 配列のリソースIDを保存するためのキー
	 */
	private static final String KEY_ARRAYSTRING_RES_ID = "KEY_ARRAYSTRING_RES_ID";

	/**
	 * ダイアログのタイトルを保存するためのキー
	 */
	private static final String KEY_DIALOG_TITLE = "KEY_DIALOG_TITLE";

	/**
	 * リスナーを保存するためのキー
	 */
	private static final String KEY_ON_ITEM_CLICK_LISTENER = "KEY_ON_ITEM_CLICK_LISTENER";

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		ListView listView = new ListView(getActivity().getApplicationContext());
		String[] array = getActivity().getResources().getStringArray(getArrayResId());
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, array);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(getOnItemClickListener());

		return new AlertDialog.Builder(getActivity()).setView(listView).setTitle(getDialogTitle()).create();
	}

	/**
	 * 保存した配列のリソースIDを取得する
	 * @return 保存しておいた配列のリソースID
	 */
	private int getArrayResId() {
		return getArguments().getInt(KEY_ARRAYSTRING_RES_ID);
	}

	/**
	 * 保存したリスナーを取得する
	 * @return 保存しておいたリスナー
	 */
	private OnItemClickListener getOnItemClickListener() {
		return (OnItemClickListener) getArguments().get(KEY_ON_ITEM_CLICK_LISTENER);
	}

	/**
	 * @return 保存しておいたダイアログのタイトル
	 */
	private String getDialogTitle() {
		return getArguments().getString(KEY_DIALOG_TITLE);
	}

	/**
	 * 配列のリソースIDとリスナーを設定する
	 * @param title
	 *           ダイアログのタイトル
	 * @param arrayResId
	 *           配列のリソースID
	 * @param listener
	 *           リスナー
	 */
	public void init(String title, int arrayResId, OnListDialogItemClickListener listener) {
		Bundle bundle = new Bundle();
		bundle.putString(KEY_DIALOG_TITLE, title);
		bundle.putInt(KEY_ARRAYSTRING_RES_ID, arrayResId);
		bundle.putSerializable(KEY_ON_ITEM_CLICK_LISTENER, listener);
		setArguments(bundle);
	}

	/**
	 * このフラグメントで使うリスナー。Listのアイテムが選ばれたときに呼ばれる
	 */
	@SuppressWarnings("serial")
	public abstract static class OnListDialogItemClickListener implements OnItemClickListener, Serializable {
	}
}
