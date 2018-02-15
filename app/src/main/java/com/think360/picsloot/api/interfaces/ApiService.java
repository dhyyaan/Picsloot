package com.think360.picsloot.api.interfaces;

import com.think360.picsloot.api.data.Responce;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
/**
 * Created by think360 on 18/04/17.
 */

public interface ApiService {

    @FormUrlEncoded
    @POST("send_otp")
    Call<Responce.SendOtpResponce> sendOTP(@Field("mobile_no") String mobile_no, @Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("verify_otp")
    Call<Responce.VerifyOtpResponce> verifyOtp(@Field("user_id") String mobile_no, @Field("otp") String otp);
    @Multipart
    @POST("profile_pic_upload")
    Call<Responce.UploadProfilePicResponce> profilePicUpload( @Part MultipartBody.Part profile_pic);

    @FormUrlEncoded
    @POST("registration_complete")
    Call<Responce.RegCompleteResponce> registration( @Field("name") String name,
                                                    @Field("email") String email,@Field("form") String form,
                                                    @Field("social_id") String social_id, @Field("device_type") String device_type,
                                                    @Field("device_id") String device_id,@Field("profile_image") String profile_image);
    @FormUrlEncoded
    @POST("latest_order")
    Call<Responce.LatestOrderResponce> latestOrder(@Field("user_id") String user_id);

   @FormUrlEncoded
    @POST("view_order")
    Call<Responce.ViewOrderResponce> viewOrder(@Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("show_profile")
    Call<Responce.ShowProfileResponce> showProfile(@Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("save_profile")
    Call<Responce.SaveProfileResponce> saveProfile(@Field("user_id") String user_id, @Field("name") String name, @Field("email") String email,
                                                   @Field("mobile_no") String mobile_no, @Field("profile_image") String profile_image);


    @POST("all_states")
    Call<Responce.StateResponce> allStates();

    @FormUrlEncoded
    @POST("all_cities")
    Call<Responce.CityResponce> allCities(@Field("state_id") String state_id);

   @FormUrlEncoded
    @POST("edit_profile")
    Call<Responce.EditDeliveryAddResponce> editDelivaryAddress(@Field("user_id") String user_id,@Field("address") String address,
                                                               @Field("state_id") String state_id,@Field("city_id") String city_id,
                                                               @Field("pin_code") String pin_code,@Field("comment") String comment);
    @FormUrlEncoded
    @POST("delete_address")
    Call<Responce.EditDeliveryAddResponce> adeleteDeliveryAddress(@Field("user_id") String user_id);

    @Multipart
    @POST("confirm_order")
    Call<Responce.EditDeliveryAddResponce> confirmOrder(@Part("user_id") RequestBody user_id,@Part MultipartBody.Part image1,@Part MultipartBody.Part image2,
                                                                  @Part MultipartBody.Part image3,@Part MultipartBody.Part image4,@Part MultipartBody.Part image5,
                                                                  @Part MultipartBody.Part image6,@Part("address") RequestBody address, @Part("state") RequestBody state,
                                                                  @Part("city") RequestBody city,@Part("pin_code") RequestBody pin_code,@Part("comment") RequestBody comment);

    @FormUrlEncoded
    @POST("social_login")
    Call<Responce.SocialLoginAddResponce> socialLogin(@Field("social_id") String social_id,
                                                      @Field("form") String form, @Field("device_id") String device_id,
                                                      @Field("device_type") String device_type,@Field("email") String email);

    @FormUrlEncoded
    @POST("resend_otp")
    Call<Responce.ResendOtpResponce> resendOtp(@Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("notification_lists")
    Call<Responce.NotificationResponce> getNotification(@Field("user_id") String user_id, @Field("device_id") String device_id);

    @FormUrlEncoded
    @POST("question")
    Call<Responce.QuestionResponce> question(@Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("answer")
    Call<Responce.AnswerResponce> ans(@Field("user_id") String user_id, @Field("answer") String answer);

    @FormUrlEncoded
    @POST("share")
    Call<Responce.ShareResponce> share(@Field("user_id") String user_id);

}
