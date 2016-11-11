package com.app.model.login;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.app.bids.FragmentChangeActivity;
import com.app.bids.R;
import com.app.model.LoginModel.UserModel;

public class LoginDialog2 implements OnClickListener {

	private static Dialog dialog;
	private Activity activity;
	private ImageView facebookIcon;
	private ImageView twitterIcon;
	private ImageView plusIcon;
	private FragmentChangeActivity mInterface;

	public LoginDialog2(Activity activity, FragmentChangeActivity mInterface) {
		this.activity = activity;
		this.dialog = new Dialog(activity);
		this.mInterface = mInterface;
		init();
	}

	private void init() {
		dialog.setContentView(R.layout.progress_bar);
		mInterface.onFacebookClick();
				
//		dialog.setContentView(R.layout.login_main);
//		facebookIcon = (ImageView) dialog.findViewById(R.id.facebook_icon);
//		twitterIcon = (ImageView) dialog.findViewById(R.id.twitter_icon);
//		plusIcon = (ImageView) dialog.findViewById(R.id.plus_icon);
//		facebookIcon.setOnClickListener(this);
//		twitterIcon.setOnClickListener(this);
//		plusIcon.setOnClickListener(this);
	}

	public static void show() {
		dialog.show();
	}

	public static void dissmiss() {
		dialog.dismiss();
	}

	@Override
	public void onClick(View v) {
		if (v == facebookIcon) {
			mInterface.onFacebookClick();
		} else if (v == plusIcon) {
//			mInterface.onPlusClick();
		} else if (v == twitterIcon) {
//			mInterface.onTwitterClick();
		}
	}
}
