package com.app.model.LoginModel;

public class UserModel {
	
	public static int FACEBOOK_TYPE = 1;
	public static int PLUS_TYPE = 2;
	public static int TWITTER_TYPE = 3;
	public static int NONE_TYPE = 0;

	public String status = "";
	public String user_id = "";
	public String id = "";
	public String userName = "";
	public String firstName = "";
	public String lastName = "";
	public String pass = "";
	public String token = "";
	public String email = "";
	public String profileImagePath = "";
	public int type = NONE_TYPE;
}
