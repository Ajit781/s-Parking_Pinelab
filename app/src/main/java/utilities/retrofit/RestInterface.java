package utilities.retrofit;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import utilities.constants.Constant;

public interface RestInterface {


    @FormUrlEncoded
    @POST(Constant.STUDENT_REGISTRATION)
    Call<ResponseBody> Registration(@Field(Constant.FIRST_NAME) String first_name,
                                    @Field(Constant.LAST_NAME) String last_name,
                                    @Field(Constant.EMAIL_ID) String email_id,
                                    @Field(Constant.PASSWORD) String password,
                                    @Field(Constant.PHONE) String phone,
                                    @Field(Constant.DEVICE_ID) String device_id,
                                    @Field(Constant.PUSH_ID) String push_id,
                                    @Field(Constant.LATITUDE) String latitude,
                                    @Field(Constant.LONGITUDE) String longitude,
                                    @Field(Constant.LOGIN_TYPE) String login_type,
                                    @Field(Constant.SOCIAL_ID) String social_id


    );


    @FormUrlEncoded
    @POST(Constant.GETOTPVERIFY)
    Call<ResponseBody> GetOTPVerify(@Field(Constant.U_ID) String u_id,
                                    @Field(Constant.NEW_OTP) String new_otp

    );

    @FormUrlEncoded
    @POST(Constant.GETSUBSCRIPTION_LIST)
    Call<ResponseBody> GetSubscriptionPlanList(@Field(Constant.SUBSCRIPTION_LIST) String subscrib_list


    );

    @FormUrlEncoded
    @POST(Constant.GETSTREAMLIST)
    Call<ResponseBody> GetStreamList(@Field(Constant.STREAM_LIST) String stream_list


    );


    @FormUrlEncoded
    @POST(Constant.GETSUBJECTLIST)
    Call<ResponseBody> GetSubjectList(@Field(Constant.CLASSES) String classes,
                                      @Field(Constant.STREAM_ID) String stream_id

    );

    @FormUrlEncoded
    @POST(Constant.GETSUBJECTCHAPTERLIST)
    Call<ResponseBody> GetSubjectChapterList(@Field(Constant.SUBJECT_ID) String subject_id,
                                             @Field(Constant.U_ID1) String user_id


    );

    @FormUrlEncoded
    @POST(Constant.SETSTUDENTPROFILE)
    Call<ResponseBody> SetStudentProfile(@Field(Constant.U_ID1) String user_id,
                                         @Field(Constant.PHONE) String phone,
                                         @Field(Constant.FIRST_NAME) String first_name,
                                         @Field(Constant.LAST_NAME) String last_name,
                                         @Field(Constant.SCHOOL_NAME) String school,
                                         @Field(Constant.GENDER) String gender,
                                         @Field(Constant.DOB) String dob


    );

    @FormUrlEncoded
    @POST(Constant.GETCHAPTERQUIZSTART)
    Call<ResponseBody> GetChapterWiseQuizStart(@Field(Constant.U_ID) String U_ID,
                                               @Field(Constant.SUBJECT_ID) String subject_id
                                       /*  @Field(Constant.CHAPTER_ID) String chapter_id*/


    );

    @FormUrlEncoded
    @POST(Constant.GETSUBJECTTESTSTART)
    Call<ResponseBody> GetSubjectTestStart(@Field(Constant.U_ID) String u_id,
                                           @Field(Constant.SUBJECT_ID) String subject_id
                                       /*  @Field(Constant.CHAPTER_ID) String chapter_id*/


    );

    @FormUrlEncoded
    @POST(Constant.GETSUBJECTLEADERBOARD)
    Call<ResponseBody> GetSubjectLeaderBoard(@Field(Constant.SUBJECT_ID) String subject_id


    );

    @FormUrlEncoded
    @POST(Constant.SETSTUDENTPASSWORD)
    Call<ResponseBody> SetStudentPassword(@Field(Constant.U_ID) String u_id,
                                          @Field(Constant.NEW_PASSWORD) String new_password


    );


    @Multipart
    @POST(Constant.SETSTUDENTIMAGE)
    Call<ResponseBody> SetStudentImage(@Part MultipartBody.Part file,
                                       @Header(Constant.IMAGE) MultipartBody.Part image,
                                       @Part(Constant.U_ID) RequestBody u_id);


   /* @FormUrlEncoded
    @POST(Constant.LOGIN)
    Call<ResponseBody> Login(@Field(Constant.USER_INPUT) String userinput,
                             @Field(Constant.PASSWORD) String password,
                             @Field(Constant.DEVICE_ID) String deviceid,
                             @Field(Constant.DEVICE_TYPE) String devicetype,
                             @Field(Constant.SOCIAL_ID) String socialid,
                             @Field(Constant.LOGIN_TYPE) String logintype,
                             @Field(Constant.PUSH_ID) String pushid


    );*/

   /* @FormUrlEncoded
    @POST(Constant.OTP)
    Call<ResponseBody> Otp(@Field(Constant.USER_ID) String user_id,
                           @Field(Constant.OTP_ID) String otp,
                           @Field(Constant.OTP_DEVICE_ID) String deviceid

    );

    @FormUrlEncoded
    @POST(Constant.LOGOUT)
    Call<ResponseBody> LogOut(@Field(Constant.LOGOUT_USER_ID) String user_id
    );*/

    /*@FormUrlEncoded
    @POST(Constant.GETCARVISITORDETAILS)
    Call<ResponseBody> GetCarVisitorDetails(@Field(Constant.CAR_ID) String car_id
    );

    @FormUrlEncoded
    @POST(Constant.DELETEUSEDCARIMAGE)
    Call<ResponseBody> DeleteUsedCarImage(@Field(Constant.IMAGE_ID) String image_id
    );

    @FormUrlEncoded
    @POST(Constant.GETUSEDCARIMAGES)
    Call<ResponseBody> GetUserCarImages(@Field(Constant.USED_CAR_ID) String used_car_id
    );*/

    /*@Multipart
    @POST(Constant.SETUSEDCARIMAGEUPLOAD)
    Call<ResponseBody> SetUsedCarImageUpload(@Part MultipartBody.Part file,
                                             @Header(Constant.FILE_TYPE) MultipartBody.Part image,
                                             @Part(Constant.CAR_ID) RequestBody car_id);*/



    /*@FormUrlEncoded
    @POST(Constant.FAMILY_MEMBER_LIST)
    Call<ResponseBody> Memberlist(@Query(Constant.ACCESS_TOKEN) String accessToken,
                                  @Field(Constant.USER_ID) String user_id


    );

    @FormUrlEncoded
    @POST(Constant.ADD_FAMILY_MEMBER)
    Call<ResponseBody> Addfamilymembers(@Query(Constant.ACCESS_TOKEN) String accessToken,
                                        @Field(Constant.NAME) String name,
                                        @Field(Constant.RELATION) String relation,
                                        @Field(Constant.MOBILE_NUMBER) String mobile_number,
                                        @Field(Constant.ADDRESS) String address,
                                        @Field(Constant.CITY) String city,
                                        @Field(Constant.ZIPCODE) String zipcode,
                                        @Field(Constant.COUNTRY) String country,
                                        @Field(Constant.GENDER) String gender,
                                        @Field(Constant.EMAIL) String email


    );


    @FormUrlEncoded
    @POST(Constant.MY_ACCOUNT)
    Call<ResponseBody> Myaccount(@Query(Constant.ACCESS_TOKEN) String accessToken,
                                 @Field(Constant.USER_ID) String user_id

    );

    @POST(Constant.LOG_OUT_API)
    Call<ResponseBody> logoutApi(@Query(Constant.ACCESS_TOKEN) String accessToken

    );

    @FormUrlEncoded
    @POST(Constant.LOG_OUT)
    Call<ResponseBody> logOut(@Query(Constant.ACCESS_TOKEN) String accessToken,
                              @Field(Constant.fcm_id) String fcm_id,
                              @Field(Constant.device_type) String device_type
    );

    @FormUrlEncoded
    @POST(Constant.SET_MEMBER_LAT_LNG)
    Call<ResponseBody> SetLatLong(@Query(Constant.ACCESS_TOKEN) String accessToken,
                                  @Field(Constant.LAT) String lat,
                                  @Field(Constant.LNG) String lng

    );


    @FormUrlEncoded
    @POST(Constant.COUNTRY_LIST)
    Call<ResponseBody> Contrylist(@Field(Constant.USER_ID) String user_id
    );

    @FormUrlEncoded
    @POST(Constant.VIEW_TIME_SLOT_LIST_ALL)
    Call<ResponseBody> Timeslotlist(@Query(Constant.ACCESS_TOKEN) String accessToken,
                                    @Field(Constant.USER_ID) String user_id


    );


    @FormUrlEncoded
    @POST(Constant.SINGLE_FRIEND_MSG)
    Call<ResponseBody> singleFriendMsg(@Query(Constant.ACCESS_TOKEN) String accessToken,
                                       @Field(Constant.FRIEND_ID) String friend_id);

    @FormUrlEncoded
    @POST(Constant.MARK_MSG_READ)
    Call<ResponseBody> markMsgRead(@Query(Constant.ACCESS_TOKEN) String accessToken,
                                   @Field(Constant.FROM_USER_ID) String from_user_id);

    @FormUrlEncoded
    @POST(Constant.SEND_MSG)
    Call<ResponseBody> sendMsg(@Query(Constant.ACCESS_TOKEN) String accessToken,
                               @Field(Constant.TO_USER_ID) String to_user_id,
                               @Field(Constant.message) String message
    );


    @POST(Constant.FETCH_MSG)
    Call<ResponseBody> fetchNewUnreadMsg(@Query(Constant.ACCESS_TOKEN) String accessToken);

*/

   /* @Multipart
    @POST(Constant.SETUSEDCARIMAGEUPLOAD)
    Call<ResponseBody> SetUsedCarImageUpload(@Part MultipartBody.Part file,
                                             @Header(Constant.FILE_TYPE) MultipartBody.Part image,
                                             @Part(Constant.LIST_ID) RequestBody list_id,
                                             @Part(Constant.SITE_ID) RequestBody site_id);*/
}

