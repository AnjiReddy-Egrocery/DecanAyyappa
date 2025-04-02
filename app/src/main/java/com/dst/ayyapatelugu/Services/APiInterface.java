package com.dst.ayyapatelugu.Services;

import com.dst.ayyapatelugu.Model.AyyappaTempleList;
import com.dst.ayyapatelugu.Model.AyyappaTempleMapDataResponse;
import com.dst.ayyapatelugu.Model.BajanaManadaliListModel;
import com.dst.ayyapatelugu.Model.BajanaMandaliList;
import com.dst.ayyapatelugu.Model.BooksListModel;
import com.dst.ayyapatelugu.Model.CalenderDataResponse;
import com.dst.ayyapatelugu.Model.ForgotDataResponse;
import com.dst.ayyapatelugu.Model.GuruSwamiList;
import com.dst.ayyapatelugu.Model.KaryakarmamList;
import com.dst.ayyapatelugu.Model.LoginDataResponse;
import com.dst.ayyapatelugu.Model.MapDataResponse;
import com.dst.ayyapatelugu.Model.NewsList;
import com.dst.ayyapatelugu.Model.NityaPoojaModel;
import com.dst.ayyapatelugu.Model.ProductList;
import com.dst.ayyapatelugu.Model.ResetPasswordResponse;
import com.dst.ayyapatelugu.Model.SevaList;
import com.dst.ayyapatelugu.Model.SharanughosaModel;
import com.dst.ayyapatelugu.Model.SignUpWithGmail;
import com.dst.ayyapatelugu.Model.TempleMapDataResponse;
import com.dst.ayyapatelugu.Model.TemplesList;
import com.dst.ayyapatelugu.Model.UserDataResponse;
import com.dst.ayyapatelugu.Model.VerifyUserDataResponse;
import com.dst.ayyapatelugu.Model.YatraList;
import com.dst.ayyapatelugu.Model.decoratorListModel;


import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface APiInterface {

    @GET("APICalls/Decorators/index")
    Call<decoratorListModel> getDecoratorsList();

    @GET("APICalls/Books/index")
    Call<BooksListModel> getBookList();

    @GET("APICalls/Guruswami/index")
    Call<GuruSwamiList> getGuruSwamiList();

    @GET("APICalls/Yatralu/index")
    Call<YatraList> getYatraList();

    @GET("APICalls/Bajanamandali/index")
    Call<BajanaMandaliList> getBajamandaliList();


    @GET("APICalls/Products/index")
    Call<ProductList> getProductList();

    @GET("APICalls/Sevasamasthalu/index")
    Call<SevaList> getSevaList();

    @GET("APICalls/News/index")
    Call<NewsList> getNewsList();

    @GET("APICalls/Activities/index")
    Call<KaryakarmamList> getKaryakaramamList();

    @GET("APICalls/Temples/index")
    Call<TemplesList> getTempleList();

    @GET("APICalls/Temples/ayyappaTemples")
    Call<AyyappaTempleList> getAyyappaTempleList();

    @POST("APICalls/Bajanamandali/info")
    Call<BajanaMandaliList> postBajanaMandali(@Body BajanaManadaliListModel bajanaMandaliList);


    @Multipart
    @POST("APICalls/Users/userRegistration")
    Call<UserDataResponse> postData(
            @Part("firstName") RequestBody firstName,
            @Part("lastName") RequestBody lastName,
            @Part("emailId") RequestBody emailId,
            @Part("mobileNumber") RequestBody mobileNumber,
            @Part("pwd") RequestBody pwd
    );

    @Multipart
    @POST("APICalls/Users/verifyUserAccount")
    Call<VerifyUserDataResponse> verifyData(@Part("registerId") RequestBody registerId,
                                            @Part("otp") RequestBody otp);

    @Multipart
    @POST("APICalls/Users/userLogin")
    Call<LoginDataResponse> LoginData(@Part("loginMobile") RequestBody loginMobile ,
                                      @Part("loginPassword") RequestBody loginPassword);

    @Multipart
    @POST("APICalls/Calendar/index")
    Call<CalenderDataResponse> calenderData(@Part("year") RequestBody year);

    @GET("APICalls/Annadhanams/Index")
    Call<MapDataResponse> getMapList();

    @GET("APICalls/Temples/Index")
    Call<TempleMapDataResponse> getTempleMapList();

    @GET("APICalls/Temples/ayyappaTemples")
    Call<AyyappaTempleMapDataResponse> getAyyaooaTempleMapList();

    @Multipart
    @POST("APICalls/Users/loginWithGmail")
    Call<SignUpWithGmail> PostSignUp(@Part("displayname") RequestBody displayname,
                                     @Part("email") RequestBody email,
                                     @Part("profilepic") RequestBody profilepic );

    @Multipart
    @POST("APICalls/Activities/info")
    Call<NityaPoojaModel> PostActivityId(@Part("activitiesId") RequestBody activitiesId);

    @Multipart
    @POST("APICalls/Activities/info")
    Call<SharanughosaModel> PostActivity(@Part("activitiesId") RequestBody activitiesId);

    @Multipart
    @POST("APICalls/Users/requestToResetPassword")
    Call<ForgotDataResponse> forgotData(@Part("userName") RequestBody userName);

    @Multipart
    @POST("APICalls/Users/updateAccountPassword")
    Call<ResetPasswordResponse> resetData(@Part("registerId") RequestBody registerId ,
                                          @Part("otp") RequestBody otp,
                                          @Part("pwd") RequestBody pwd);



}
