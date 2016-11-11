package com.app.bids;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;

import com.app.bids.FragmentChangeActivity;
import com.app.bids.R;
import com.app.model.LoginModel.UserModel;

public class LoadingDialog implements OnClickListener {

	private static Dialog dialog;
	private Activity activity;
	private Context context;
	private FragmentChangeActivity mInterface;

	public LoadingDialog(Activity activity, FragmentChangeActivity mInterface) {
		this.activity = activity;
		this.dialog = new Dialog(activity);
		this.mInterface = mInterface;
		init();
	}

	private void init() {
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before  
		dialog.setContentView(R.layout.progress_bar);
	}

	public static void show() {
		dialog.show();
	}

	public static void dissmiss() {
		dialog.dismiss();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

}
