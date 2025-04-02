package com.dst.ayyapatelugu.DataBase;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.dst.ayyapatelugu.Model.LoginDataResponse;
import com.dst.ayyapatelugu.User.LoginActivity;


public class SharedPrefManager {

    static SharedPrefManager sharedPrefManager;
    Context mContext;
    private static final String SHARED_PREF_NAME = "userProfile";
    private static final String STUDENT_ID="userId ";
    private static final String FIRST_NAME="userFirstName";
    private static final String LAST_NAME="userLastName";
    private static final String EMAIL_ID="userEmail";
    private static final String FATHER_MOBILE="userMobile";

    private SharedPrefManager(Context context) {
        mContext=context;
    }

    public static synchronized SharedPrefManager getInstance(Context context){
        if (sharedPrefManager==null){
            sharedPrefManager=new SharedPrefManager(context);
        }
        return sharedPrefManager;
    }
    //insert user data
    public void insertData(LoginDataResponse userInfo){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(STUDENT_ID, userInfo.getResult().getUserId());
        editor.putString(FIRST_NAME, userInfo.getResult().getUserFirstName());
        editor.putString(LAST_NAME, userInfo.getResult().getUserLastName());
        editor.putString(EMAIL_ID, userInfo.getResult().getUserEmail());
        editor.putString(FATHER_MOBILE, userInfo.getResult().getUserMobile());

        editor.commit();



    }

    public LoginDataResponse.Result getUserData(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        LoginDataResponse.Result userInfo=new LoginDataResponse.Result(
                sharedPreferences.getString(STUDENT_ID,null),
                sharedPreferences.getString(FIRST_NAME,null),
                sharedPreferences.getString(LAST_NAME,null),
                sharedPreferences.getString(EMAIL_ID,null),
                sharedPreferences.getString(FATHER_MOBILE,null));

        return userInfo;
    }

    public boolean isLoggedIn(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        if (sharedPreferences.getString(STUDENT_ID, null) != null){
            return true;
        }
        return false;
    }
    public void isLoggedOut(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.clear();
        editor.apply();

    }
}
