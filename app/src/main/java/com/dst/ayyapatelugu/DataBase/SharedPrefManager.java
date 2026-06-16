package com.dst.ayyapatelugu.DataBase;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.dst.ayyapatelugu.Model.LoginDataResponse;

public class SharedPrefManager {

    private static SharedPrefManager sharedPrefManager;
    private Context mContext;

    private static final String SHARED_PREF_NAME = "userProfile";

    private static final String STUDENT_ID = "userId";
    private static final String FIRST_NAME = "userFirstName";
    private static final String LAST_NAME = "userLastName";
    private static final String EMAIL_ID = "userEmail";
    private static final String FATHER_MOBILE = "userMobile";
    private static final String KEY_PROFILE_PIC = "profile_pic";

    private SharedPrefManager(Context context) {
        mContext = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (sharedPrefManager == null) {
            sharedPrefManager = new SharedPrefManager(context);
        }
        return sharedPrefManager;
    }

    // ================= LOGIN DATA =================

    public void insertData(LoginDataResponse userInfo) {

        SharedPreferences sharedPreferences =
                mContext.getSharedPreferences(
                        SHARED_PREF_NAME,
                        Context.MODE_PRIVATE);

        SharedPreferences.Editor editor =
                sharedPreferences.edit();

        editor.putString(
                STUDENT_ID,
                userInfo.getResult().getUserId());

        editor.putString(
                FIRST_NAME,
                userInfo.getResult().getUserFirstName());

        editor.putString(
                LAST_NAME,
                userInfo.getResult().getUserLastName());

        editor.putString(
                EMAIL_ID,
                userInfo.getResult().getUserEmail());

        editor.putString(
                FATHER_MOBILE,
                userInfo.getResult().getUserMobile());

        editor.apply();
    }

    public void saveProfilePic(String profilePic) {

        String userId = getUserId();

        SharedPreferences sharedPreferences =
                mContext.getSharedPreferences(
                        SHARED_PREF_NAME,
                        Context.MODE_PRIVATE);

        sharedPreferences.edit()
                .putString("profile_pic_" + userId,
                        profilePic)
                .apply();

        Log.d("SP_SAVE",
                "Saved = " + profilePic);
    }

    public String getProfilePic() {

        String userId = getUserId();

        SharedPreferences sharedPreferences =
                mContext.getSharedPreferences(
                        SHARED_PREF_NAME,
                        Context.MODE_PRIVATE);

        String pic = sharedPreferences.getString(
                "profile_pic_" + userId,
                "");

        Log.d("SP_GET", "Get = " + pic);

        return pic;
    }

    public void updateUserData(String firstName,
                               String lastName,
                               String email,
                               String mobile) {

        SharedPreferences sharedPreferences =
                mContext.getSharedPreferences(
                        SHARED_PREF_NAME,
                        Context.MODE_PRIVATE);

        SharedPreferences.Editor editor =
                sharedPreferences.edit();

        editor.putString(FIRST_NAME, firstName);
        editor.putString(LAST_NAME, lastName);
        editor.putString(EMAIL_ID, email);
        editor.putString(FATHER_MOBILE, mobile);

        editor.apply();
    }

    public LoginDataResponse.Result getUserData() {

        SharedPreferences sharedPreferences =
                mContext.getSharedPreferences(
                        SHARED_PREF_NAME,
                        Context.MODE_PRIVATE);

        return new LoginDataResponse.Result(
                sharedPreferences.getString(STUDENT_ID, null),
                sharedPreferences.getString(FIRST_NAME, null),
                sharedPreferences.getString(LAST_NAME, null),
                sharedPreferences.getString(EMAIL_ID, null),
                sharedPreferences.getString(FATHER_MOBILE, null)
        );
    }

    public String getUserId() {

        SharedPreferences sharedPreferences =
                mContext.getSharedPreferences(
                        SHARED_PREF_NAME,
                        Context.MODE_PRIVATE);

        return sharedPreferences.getString(
                STUDENT_ID,
                "");
    }

    public boolean isLoggedIn() {

        SharedPreferences sharedPreferences =
                mContext.getSharedPreferences(
                        SHARED_PREF_NAME,
                        Context.MODE_PRIVATE);

        return sharedPreferences.getString(
                STUDENT_ID,
                null) != null;
    }

    // ================= LOGOUT =================

    public void logout() {

        SharedPreferences sharedPreferences =
                mContext.getSharedPreferences(
                        SHARED_PREF_NAME,
                        Context.MODE_PRIVATE);

        SharedPreferences.Editor editor =
                sharedPreferences.edit();

        editor.remove(STUDENT_ID);
        editor.remove(FIRST_NAME);
        editor.remove(LAST_NAME);
        editor.remove(EMAIL_ID);
        editor.remove(FATHER_MOBILE);

        editor.apply();
    }

    // ================= FLYER DATA USER-WISE =================

    public void saveFlyerData(
            String flyerName,
            String flyerDesignation,
            String flyerPic) {

        String userId = getUserId();

        if (userId == null || userId.isEmpty()) {
            return;
        }

        SharedPreferences sharedPreferences =
                mContext.getSharedPreferences(
                        SHARED_PREF_NAME,
                        Context.MODE_PRIVATE);

        SharedPreferences.Editor editor =
                sharedPreferences.edit();

        editor.putString(
                "flyer_name_" + userId,
                flyerName);

        editor.putString(
                "flyer_designation_" + userId,
                flyerDesignation);

        editor.putString("flyer_pic_" + userId, flyerPic);

        editor.apply();
    }

    public String getFlyerName() {

        String userId = getUserId();

        SharedPreferences sharedPreferences =
                mContext.getSharedPreferences(
                        SHARED_PREF_NAME,
                        Context.MODE_PRIVATE);

        return sharedPreferences.getString(
                "flyer_name_" + userId,
                "");
    }

    public String getFlyerDesignation() {

        String userId = getUserId();

        SharedPreferences sharedPreferences =
                mContext.getSharedPreferences(
                        SHARED_PREF_NAME,
                        Context.MODE_PRIVATE);

        return sharedPreferences.getString(
                "flyer_designation_" + userId,
                "");
    }

    public String getFlyerPic() {

        String userId = getUserId();

        SharedPreferences sharedPreferences =
                mContext.getSharedPreferences(
                        SHARED_PREF_NAME,
                        Context.MODE_PRIVATE);

        return sharedPreferences.getString(
                "flyer_pic_" + userId,
                "");
    }

    public void clearFlyerData() {

        String userId = getUserId();

        SharedPreferences sharedPreferences =
                mContext.getSharedPreferences(
                        SHARED_PREF_NAME,
                        Context.MODE_PRIVATE);

        SharedPreferences.Editor editor =
                sharedPreferences.edit();

        editor.remove("flyer_name_" + userId);
        editor.remove("flyer_designation_" + userId);
        editor.remove("flyer_pic_" + userId);

        editor.apply();
    }
}