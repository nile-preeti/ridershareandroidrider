package com.rideshare.app.connection;



import com.rideshare.app.pojo.AppVersionUpdateResponse;
import com.rideshare.app.pojo.CallingDriverResponse;
import com.rideshare.app.pojo.CheckAppVersionResponse;
import com.rideshare.app.pojo.CheckDeviceTokenResponse;
import com.rideshare.app.pojo.LogoutResponse;
import com.rideshare.app.pojo.PendingRequestPojo;
import com.rideshare.app.pojo.SignupResponse;
import com.rideshare.app.pojo.UpdateLoginLogoutResponse;
import com.rideshare.app.pojo.cancelRideCount.CancelRideCount;
import com.rideshare.app.pojo.changepassword.ChangePasswordResponse;
import com.rideshare.app.pojo.delete_account.DeleteAccount;
import com.rideshare.app.pojo.getprofile.GetProfile;
import com.rideshare.app.pojo.google.DistanceMatrixResponse;
import com.rideshare.app.pojo.help_question.QuestionCategoryResponse;
import com.rideshare.app.pojo.help_question.SubCategoryResponse;
import com.rideshare.app.pojo.help_question.SubmitAnswerResponse;
import com.rideshare.app.pojo.profileresponse.ProfileResponse;
import com.rideshare.app.pojo.spend.PendingPojoResponse;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;


public interface ApiNetworkCall {

    @FormUrlEncoded
    @POST("change_password")
    Call<ChangePasswordResponse> changePassword(
            @Header("Authorization") String authorization,
            @FieldMap Map<String, String> params);

//    @FormUrlEncoded
//    @POST("update_profile_of_user")
//    Call<ProfileResponse> updateProfile(
//            @Header("Authorization") String authorization,
//            @Field("name") String name,
//            @Field("mobile") String mobile,
//            @Field("country_code") String countryCode
//    );

//    @Multipart
//    @POST("update_profile_of_user")
//    Call<ProfileResponse> updateProfile(
//            @Header("Authorization") String authorization,
//            @Part MultipartBody.Part file,
//            @Part("name") RequestBody name,
//            @Part("mobile") RequestBody mobile,
//            @Part("country_code") RequestBody countryCode
//    );

    @Multipart
    @POST("update_profile_of_user")
    Call<ProfileResponse> updateProfile(
            @Header("Authorization") String authorization,
            @Part MultipartBody.Part file,
            @Part("name") RequestBody name,
            @Part("last_name") RequestBody lname,
            @Part("name_title") RequestBody request_title_name,
            @Part("mobile") RequestBody mobile,
            @Part("country_code") RequestBody countryCode,
            @Part("identification_document_id") RequestBody docId,
            @Part("identification_issue_date") RequestBody issueDate,
            @Part("identification_expiry_date") RequestBody expireDate,
            @Part MultipartBody.Part identityFile
    );

    @GET("get_profile")
    Call<GetProfile> getProfile(@Header("Authorization") String authorization);

    @FormUrlEncoded
    @POST("resend")
    Call<ChangePasswordResponse> sendOTP(
            @FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("forgot-password")
    Call<ChangePasswordResponse> resetPassword(
            @FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("give_feedback")
    Call<ChangePasswordResponse> giveFeedBack(
            @Header("Authorization") String authorization,
            @FieldMap Map<String, String> params);

    @GET("delete_account")
    Call<DeleteAccount> deleteAccount(@Header("Authorization") String authorization);

    @FormUrlEncoded
    @POST("logout")
    Call<LogoutResponse> logoutStatus(
            @Header("Authorization") String authorization,
            @FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("save_answer")
    Call<SubmitAnswerResponse> submitAnswer(
            @Header("Authorization") String authorization,
            @FieldMap Map<String, String> params);

    @Multipart
    @POST("audio_capture")
    Call<ChangePasswordResponse> uploadRecording(
            @Header("Authorization") String authorization,
            @Part MultipartBody.Part file,
            @Part("ride_id") RequestBody rideId);

    @GET("get_count_cancelled_ride")
    Call<CancelRideCount> getCancelRideCount(
            @Header("Authorization") String authorization,
            @Query(value = "ride_id", encoded = true) String model_name);


    @GET("get_question_category")
    Call<QuestionCategoryResponse> getQuestionCategory(
            @Header("Authorization") String authorization);

    @GET("maps/api/distancematrix/json")
    Call<DistanceMatrixResponse> getDistanceFromDistanceMatrix(
            @Query(value = "origins", encoded = true) String origin,
            @Query(value = "destinations", encoded = true) String destination,
            @Query(value = "key", encoded = true) String key
    );

    @GET("get_question_answer")
    Call<SubCategoryResponse> getQuestionSubCategory(
            @Header("Authorization") String authorization,
            @Query(value = "question_category_id", encoded = true) String id);


    @GET("rides")
    Call<PendingPojoResponse> getSpendDetails(@Header("Authorization") String authorization,
                                              @Query(value = "status") String status,
                                              @Query(value = "from") String fromDate,
                                              @Query(value = "to") String toDate,
                                              @Query(value = "per_page") String perPage,
                                              @Query(value = "page_no") String pageNo
    );


    @POST("checkdevicetoken")
    Call<CheckDeviceTokenResponse> checkDeviceToken(
            @Header("Authorization") String authorization);

    @FormUrlEncoded
    @POST("forward_call")
    Call<CallingDriverResponse> callingDriverApi(
            @Header("Authorization") String authorization,
            @FieldMap Map<String,String> param);

    @GET("rides")
    Call<PendingPojoResponse> getDistance(@Header("Authorization") String authorization,
                                              @Query(value = "status") String status,
                                              @Query(value = "from") String fromDate,
                                              @Query(value = "to") String toDate,
                                              @Query(value = "per_page") String perPage,
                                              @Query(value = "page_no") String pageNo
    );

    @FormUrlEncoded
    @POST("updateloginlogout")
    Call<UpdateLoginLogoutResponse> updateLoginLogoutApi(
            @Header("Authorization") String authorization,
            @FieldMap Map<String,String> param);

    @FormUrlEncoded
    @POST("app-version-check")
    Call<CheckAppVersionResponse> checkAppVersionApi(
            @Header("Authorization") String authorization,
            @FieldMap Map<String,String> param);

    @FormUrlEncoded
    @POST("app-version-update")
    Call<AppVersionUpdateResponse> updateAppVersionApi(
            @Header("Authorization") String authorization,
            @FieldMap Map<String,String> param);

//    @FormUrlEncoded
//    @POST("register")
//    Call<SignupResponse> signin(
//            @FieldMap Map<String,String> param);

    @Multipart
    @POST("register")
    Call<SignupResponse> signin(
            @PartMap Map<String,RequestBody> param,
            @Part MultipartBody.Part userIcon,
            @Part MultipartBody.Part verification_id);
}