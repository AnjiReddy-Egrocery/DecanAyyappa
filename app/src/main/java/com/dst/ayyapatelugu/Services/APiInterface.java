package com.dst.ayyapatelugu.Services;

import com.dst.ayyapatelugu.Model.ActivitiesDetailsResponse;
import com.dst.ayyapatelugu.Model.AnadanamDetailResponse;
import com.dst.ayyapatelugu.Model.AnadanamList;
import com.dst.ayyapatelugu.Model.AyyappaTempleList;
import com.dst.ayyapatelugu.Model.AyyappaTempleMapDataResponse;
import com.dst.ayyapatelugu.Model.BajanaManadaliListModel;
import com.dst.ayyapatelugu.Model.BajanaMandaliDetailsResponse;
import com.dst.ayyapatelugu.Model.BajanaMandaliList;
import com.dst.ayyapatelugu.Model.BajanaSongDetailsResponse;
import com.dst.ayyapatelugu.Model.BajanaSongsList;
import com.dst.ayyapatelugu.Model.BlogResponse;
import com.dst.ayyapatelugu.Model.BookDetailsResponse;
import com.dst.ayyapatelugu.Model.BooksListModel;
import com.dst.ayyapatelugu.Model.CalenderDataResponse;
import com.dst.ayyapatelugu.Model.DecaratorDetailsResponse;
import com.dst.ayyapatelugu.Model.ForgotDataResponse;
import com.dst.ayyapatelugu.Model.GuruSwamiDetailsResponse;
import com.dst.ayyapatelugu.Model.GuruSwamiList;
import com.dst.ayyapatelugu.Model.ImagesModel;
import com.dst.ayyapatelugu.Model.ImagesResponse;
import com.dst.ayyapatelugu.Model.KaryakarmamList;
import com.dst.ayyapatelugu.Model.LoginDataResponse;
import com.dst.ayyapatelugu.Model.MapDataResponse;
import com.dst.ayyapatelugu.Model.NewsDetailsResponse;
import com.dst.ayyapatelugu.Model.NewsList;
import com.dst.ayyapatelugu.Model.NityaPoojaModel;
import com.dst.ayyapatelugu.Model.PadayatraBrundam;
import com.dst.ayyapatelugu.Model.PadayatraResponse;
import com.dst.ayyapatelugu.Model.ProductDetailsResponse;
import com.dst.ayyapatelugu.Model.ProductList;
import com.dst.ayyapatelugu.Model.ResetPasswordResponse;
import com.dst.ayyapatelugu.Model.SevaList;
import com.dst.ayyapatelugu.Model.SharanughosaModel;
import com.dst.ayyapatelugu.Model.SignUpWithGmail;
import com.dst.ayyapatelugu.Model.StudentUpdateProfile;
import com.dst.ayyapatelugu.Model.TeluguCalenderDataResponse;
import com.dst.ayyapatelugu.Model.TempleDetailsResponse;
import com.dst.ayyapatelugu.Model.TempleMapDataResponse;
import com.dst.ayyapatelugu.Model.TemplesList;
import com.dst.ayyapatelugu.Model.TourseDetailsResponse;
import com.dst.ayyapatelugu.Model.UserDataResponse;
import com.dst.ayyapatelugu.Model.UserUpdateProfile;
import com.dst.ayyapatelugu.Model.VerifyUserDataResponse;
import com.dst.ayyapatelugu.Model.VideoResponse;
import com.dst.ayyapatelugu.Model.YatraList;
import com.dst.ayyapatelugu.Model.decoratorListModel;
import com.dst.ayyapatelugu.Model.panchagamModel;


import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface APiInterface {

    @POST("APICalls/Decorators/index")
    Call<decoratorListModel> getDecoratorsList();

    @POST("APICalls/Books/index")
    Call<BooksListModel> getBookList();

    @POST("APICalls/Padayatrabrundams/index")
    Call<PadayatraResponse> getPadayatraList();

    @POST("APICalls/Blogs/index")
    Call<BlogResponse> getBlogList();

    @POST("APICalls/Guruswami/index")
    Call<GuruSwamiList> getGuruSwamiList();

    @POST("APICalls/Yatralu/index")
    Call<YatraList> getYatraList();

    @POST("APICalls/Bajanamandali/index")
    Call<BajanaMandaliList> getBajamandaliList();


    @POST("APICalls/Products/index")
    Call<ProductList> getProductList();

    @POST("APICalls/Sevasamasthalu/index")
    Call<SevaList> getSevaList();

    @POST("APICalls/News/index")
    Call<NewsList> getNewsList();
    @FormUrlEncoded
    @POST("APICalls/News/info")
    Call<NewsDetailsResponse> getNewsDetails(
            @Field("newsId") String newsId
    );

    @FormUrlEncoded
    @POST("APICalls/Guruswami/info")
    Call<GuruSwamiDetailsResponse> getGuruSwamiDetails(
            @Field("guruswamiId") String guruswamiId
    );

    @FormUrlEncoded
    @POST("APICalls/Bajanamandali/info")
    Call<BajanaMandaliDetailsResponse> getBajanamandaliDetails(
            @Field("bajanamandaliId") String bajanamandaliId
    );

    @FormUrlEncoded
    @POST("APICalls/Yatralu/info")
    Call<TourseDetailsResponse> getTourseDetails(
            @Field("tourpackageId") String tourpackageId
    );

    @FormUrlEncoded
    @POST("APICalls/Decorators/info")
    Call<DecaratorDetailsResponse> getDecaratorDetails(
            @Field("decoratorId") String decoratorId
    );

    @FormUrlEncoded
    @POST("APICalls/Products/info")
    Call<ProductDetailsResponse> getProductDetails(
            @Field("productId") String productId
    );

    @FormUrlEncoded
    @POST("APICalls/Books/info")
    Call<BookDetailsResponse> getBookDetails(
            @Field("bookId") String bookId
    );

    @FormUrlEncoded
    @POST("APICalls/Activities/info")
    Call<ActivitiesDetailsResponse> getActivitiesDetails(
            @Field("activitiesId") String activitiesId
    );

    @FormUrlEncoded
    @POST("APICalls/Bajanasongs/info")
    Call<BajanaSongDetailsResponse> getBajanaSongDetails(
            @Field("songId") String songId
    );
    @FormUrlEncoded
    @POST("APICalls/Temples/info")
    Call<TempleDetailsResponse> getTempleDetails(
            @Field("templeId") String templeId
    );
    @FormUrlEncoded
    @POST("APICalls/Annadhanams/info")
    Call<AnadanamDetailResponse> getAnadanamDetails(
            @Field("annadhanamId") String annadhanamId
    );
    @FormUrlEncoded
    @POST("APICalls/imagesOneByOne")
    Call<ImagesResponse> getImages(
            @Field("startIndex") int startIndex
    );

    @FormUrlEncoded
    @POST("APICalls/videosOneByOne")
    Call<VideoResponse> getVideos(
            @Field("startIndex") int startIndex
    );


    @FormUrlEncoded
    @POST("APICalls/Padayatrabrundams/info")
    Call<PadayatraResponse> getPadayatraDetails(
            @Field("padayatrabrundamId") String padayatrabrundamId
    );
    @FormUrlEncoded
    @POST("APICalls/Blogs/info")
    Call<BlogResponse> getBlogDetails(
            @Field("blogId") String blogId
    );
    @POST("APICalls/Activities/index")
    Call<KaryakarmamList> getKaryakaramamList();

    @POST("APICalls/Bajanasongs/index")
    Call<BajanaSongsList> getBajanaSongsList();

    @POST("APICalls/Temples/index")
    Call<TemplesList> getTempleList();

    @POST("APICalls/Annadhanams/index")
    Call<AnadanamList> getAnadanam();
    @POST("APICalls/Temples/ayyappaTemples")
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
            @Part("pwd") RequestBody pwd,
            @Part("isIOS") RequestBody isIOS
    );

    @Multipart
    @POST("APICalls/Users/updateUserFlyerInfo")
    Call<StudentUpdateProfile> studentUpdatePost(@Part("userId") RequestBody studentId,
                                                 @Part("nameOnFlyer") RequestBody firstName,
                                                 @Part("designationOnFlyer") RequestBody middleName,
                                                 @Part MultipartBody.Part profilePic); // Image name as part);

    @Multipart
    @POST("APICalls/updateProfileInfo")
    Call<UserUpdateProfile> userUpdatePost(@Part("registerId") RequestBody studentId,
                                           @Part("firstName") RequestBody firstName,
                                           @Part("lastName") RequestBody lastName,
                                           @Part("mobileNumber") RequestBody mobileNumber,
                                           @Part("emailId") RequestBody emailId,
                                           @Part("nameOnFlyer") RequestBody nameOnFlyer,
                                           @Part("designationOnFlyer") RequestBody designationOnFlyer,
                                           @Part MultipartBody.Part profilePic,
                                           @Part MultipartBody.Part picOnFlyer); // Image name as part);
    @Multipart
    @POST("APICalls/Users/verifyUserAccount")
    Call<VerifyUserDataResponse> verifyData(@Part("registerId") RequestBody registerId,
                                            @Part("otp") RequestBody otp);

    @Multipart
    @POST("APICalls/Users/userLogin")
    Call<LoginDataResponse> LoginData(@Part("loginMobile") RequestBody loginMobile ,
                                      @Part("loginPassword") RequestBody loginPassword);

    @FormUrlEncoded
    @POST("APICalls/Calendar/index")
    Call<CalenderDataResponse> calenderData(
            @Field("Year") RequestBody year
    );

    @Multipart
    @POST("APICalls/panchangmonth")
    Call<TeluguCalenderDataResponse> telugucalenderdata(@Part("month") RequestBody month,
                                                        @Part("year") RequestBody year);

    @POST("APICalls/Annadhanams/index")
    Call<MapDataResponse> getMapList();

    @POST("APICalls/Temples/index")
    Call<TempleMapDataResponse> getTempleMapList();

    @POST("APICalls/Temples/ayyappaTemples")
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

    @Multipart
    @POST("APICalls/panchang")
    Call<panchagamModel> getPanchaamData(@Part("date") RequestBody date );

    @Multipart
    @POST("APICalls/updateProfileInfo")
    Call<ResponseBody> updateProfile(
            @Part("registerId") RequestBody registerId,
            @Part("firstName") RequestBody firstName,
            @Part("lastName") RequestBody lastName,
            @Part("mobileNumber") RequestBody mobileNumber,
            @Part("emailId") RequestBody emailId,
            @Part("nameOnFlyer") RequestBody nameOnFlyer,
            @Part("designationOnFlyer") RequestBody designationOnFlyer,

            @Part MultipartBody.Part profilePic,
            @Part MultipartBody.Part picOnFlyer
    );



}
