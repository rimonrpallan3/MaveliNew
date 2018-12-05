package com.mavelinetworks.mavelideals.webservices;

import android.support.annotation.Nullable;

import com.mavelinetworks.mavelideals.RetorHelper.OfferList;
import com.mavelinetworks.mavelideals.activities.firstotppage.model.FirstOTPModel;
import com.mavelinetworks.mavelideals.activities.otppagesubmit.model.OTPModel;
import com.mavelinetworks.mavelideals.classes.UserDetails;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface WebServices {
    //@GET("driver/getDriver/")
   // Call<MainClass> doGetUserList(@Query("page") String page);
    //http://10.1.1.18/sayara/user/booking/--pickup_loc: 9.731235,76.355463 -- user_id 89

    @GET("webservice/getOffertypes")
    Call<OfferList> doGetUserList();

    @FormUrlEncoded
    @POST("webservice/createAppUser")
    public Call<UserDetails> registerUser(@Nullable @Field("name") String name,
                                          @Nullable @Field("password") String password,
                                          @Nullable @Field("mail") String email,
                                          @Nullable @Field("tel") String phone,
                                          @Nullable @Field("typeAuth") String typeAuth);

    @FormUrlEncoded
    @Headers({"Debug:true", "token-0:8D84F7C637120FB21B56DB2BBC34446D6B958336", "Language:en", "Api-key-android:8ad480c3706f4e573330f43877e5d27e", "macadr:08:00:27:20:6D:8D", "token-1:64dc4d3b9500419a4a1e9e0e07bfe3c4", "apiKey:00-1", "ipAddress:value"})
    @POST("user/signIn")
    public Call<UserDetails> loginUser2(
            @Nullable @Field("login") String login,
                                       @Nullable @Field("guest_id") String gstId,
                                       @Nullable @Field("mac_address") String mcAdd,
                                       @Nullable @Field("lat") String lat,
                                       @Nullable @Field("lng") String lng,
                                       @Nullable @Field("password") String passwd);


    @FormUrlEncoded
    @POST("webservice/signInAppUser")
    public Call<UserDetails> loginUser(@Nullable @Field("email") String email,
                                       @Nullable @Field("password") String passwd);


    @FormUrlEncoded
    @POST("webservice/verifyPhone")
    public Call<FirstOTPModel> getOtp(@Nullable @Field("mobile") String email);

    @FormUrlEncoded
    @POST("webservice/verifyPhone")
    public Call<OTPModel> resendOtp(@Nullable @Field("mobile") String email);

    @FormUrlEncoded
    @POST("webservice/verifyOtp")
    public Call<OTPModel> verifyOtp(@Nullable @Field("otp") String otp,
                                    @Nullable @Field("session_id") String session_id);

  /*  @FormUrlEncoded
    @POST("user/register/")
    public Call<UserDetails> registerUser(@Nullable @Field("name") String name,
                                          @Nullable @Field("password") String password,
                                          @Nullable @Field("email") String email,
                                          @Nullable @Field("phone") String phone,
                                          @Nullable @Field("country") String country,
                                          @Nullable @Field("city") String city);
    @FormUrlEncoded
    @POST("user/login/")
    public Call<UserDetails> loginUser(@Nullable @Field("email") String email,
                                       @Nullable @Field("password") String passwd,
                                       @Nullable @Field("token") String fireBaseToken);

    @FormUrlEncoded
    @POST("user/updateProfile/")
    Call<UserDetails> updateProfile(@Nullable @Field("name") String name,
                                    @Nullable @Field("userID") int id,
                                    @Nullable @Field("password") String password,
                                    @Nullable @Field("phone") String phone,
                                    @Nullable @Field("country") String country,
                                    @Nullable @Field("city") String city);
    @FormUrlEncoded
    @POST("user/updateProfile/")
    Call<UserDetails> updateProfilePass(@Nullable @Field("password") String name,
                                        @Nullable @Field("user_id") int id);
    @FormUrlEncoded
    @POST("user/updateProfile/")
    Call<UserDetails> updateProfileName(@Nullable @Field("name") String name,
                                        @Nullable @Field("user_id") int id,
                                        @Nullable @Field("password") String password,
                                        @Nullable @Field("city") String city);
    @FormUrlEncoded
    @POST("user/updateProfile/")
    Call<UserDetails> updateProfilePhno(@Nullable @Field("phone") String name,
                                        @Nullable @Field("user_id") int id);
    @FormUrlEncoded
    @POST("user/updateProfile/")
    Call<UserDetails> updateProfileCity(@Nullable @Field("city") String name,
                                        @Nullable @Field("user_id") int id);
    @FormUrlEncoded
    @POST("user/tripHistory/")
    Call<List<TripDetails>> getTripHistory(@Field("user_id") int userId,
                                           @Nullable @Field("page") int page);

    @FormUrlEncoded
    @POST("user/driverProfile/")
    Call<DTDModel> getDriverProfileDetail(@Nullable @Field("driver_id") int userId,
                                          @Nullable @Field("user_id") int tripId);

    @FormUrlEncoded
    @POST("user/getCars/")
    Call<TripCarDetails> getTripDetails(@Nullable @Field("pickup_loc") String latLng,
                                        @Nullable @Field("user_id") int userId);
    @POST("FCMUpdateServlet")
    public Call<UserDetails> updateFCMId(@Nullable @Field("user_id") int userId,
                                         @Nullable @Field("token") String token);
    @FormUrlEncoded
    @POST("user/confirmTrip/")
    public Call<TripResponse> startTrip(@Nullable @Field("user_id") int userId,
                                        @Nullable @Field("pickup_loc") String nameStartLoc,
                                        @Nullable @Field("pickup_address") String nameStart,
                                        @Nullable @Field("drop_loc") String nameEndLoc,
                                        @Nullable @Field("drop_address") String nameEnd,
                                        @Nullable @Field("distance") String distanceKm,
                                        @Nullable @Field("amount") String costFairSet,
                                        @Nullable @Field("car_id") String driveCarId,
                                        @Nullable @Field("pay_type") String paymentType);

    @Multipart
    @POST("user/updateProfile/")
    public Call<UserDetails> uploadProfileImg(@Part MultipartBody.Part profileImg, @Part("user_id") RequestBody userID);

    @GET("/maps/api/place/autocomplete/json?")
    Call<PlacesResults> getPlaceSearch(@Query("input") String input, @Query("types") String types, @Query("key") String key);
    //https://developers.google.com/maps/documentation/directions/intro
    @GET("/maps/api/directions/json?")
    Call<GetPaths> getTripRoute(@Query("origin") String source, @Query("destination") String destination, @Query("key") String key);
    @GET("/maps/api/place/details/json?")
    Call<PlaceDetail> getPlaceDetail(@Query("placeid") String source, @Query("key") String key);
    @GET("maps/api/directions/json?")
    public Call<GetPaths> getPaths(@Query("origin") String origin, @Query("destination") String dest, @Query("sensor") Boolean sensor, @Query("key") String key);
*/

    /* @Multipart
    @POST("DriverRegisterServlet")
    Call<UserModel> uploadFile(@Part MultipartBody.Part licenseFile, @Part MultipartBody.Part rcFile, @Part MultipartBody.Part profileFile, @Part("name") RequestBody name);
    @Multipart
    @POST("DriverProfileUpdateServlet")
    Call<UserModel> driverProfileUpdate(@Part MultipartBody.Part licenseFile, @Part MultipartBody.Part rcFile, @Part MultipartBody.Part profileFile, @Part("name") RequestBody name);

    @POST("AndroidLoginServlet")
    //@FormUrlEncoded
    public Call<UserModel> loginUser(@Body UserModel userModel);

    @POST("AndroidRegisterServlet")
    public Call<UserModel> registerUser(@Body UserModel userModel);

    @POST("AndroidUpdateProfileServlet")
    public Call<UserModel> updateProfile(@Body UserModel userModel);

    @POST("TripServlet")
    public Call<TripDetailsModel> userRequestTrip(@Body TripDetailsModel tripModel);

    @GET("HistoryServlet")
    public Call<List<TripDetailsModel>> getUserHistory(@Query("user_name") String email);

    @GET("DriverHistoryServlet")
    public Call<List<TripDetailsModel>> getDriverHistory(@Query("user_name") String email);

    @GET("AndroidGetProfileServlet")
    public Call<UserModel> getUserInfo(@Query("user_name") String email);

    @GET("DriverAcceptRejectServlet")
    public Call<UserModel> acceptRejectTrip(@Query("id") String id, @Query("status") String status);

    @POST("FCMUpdateServlet")
    public Call<UserModel> updateFCMId(@Body UserModel userModel);


    @GET("json")
    public Call<GetPaths> getPaths(@Query("origin") String origin, @Query("destination") String dest, @Query("sensor") String sensor);

    @POST("RechargeServlet")
    Call<RechargeModel> userWalletRecharge(@Body RechargeModel rechargeModel);

    @POST("LocationUpdateServlet")
    Call<UserModel> driverLocationUpdate(@Body UserModel userModel);

    @GET("GetPriceInfoServlet")
    Call<AutoChargeModel> updateFee();

    @GET("DriverProfileServlet")
    Call<UserModel> getDriverInfo(@Query("user_name") String email);

    @GET("StartStopTripServlet")
    Call<UserModel> startStopTrip(@Query("id") String id, @Query("status") String status, @Query("payment_mode") String paymentMode);

    @GET("UserFeedbackServlet")
    Call<UserModel> userFeedBack(@Query("id") String id, @Query("rating") String userRating);
    @GET("DriverLocationServlet")
    Call<UserModel> getDriverLocation(@Query("user_name") String username);*/
}
