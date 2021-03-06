package com.repgate.doctor.http;

import com.repgate.doctor.data.AllMessageResponseData;
import com.repgate.doctor.data.AllRequestResponseData;
import com.repgate.doctor.data.CheckoutResponseData;
import com.repgate.doctor.data.DoctorProfileResponseData;
import com.repgate.doctor.data.DoctorResponseData;
import com.repgate.doctor.data.GetPaymentResponseData;
import com.repgate.doctor.data.JoinDoctorResponseData;
import com.repgate.doctor.data.LoginResponseData;
import com.repgate.doctor.data.MessageResponseData;
import com.repgate.doctor.data.MyProvidersResponseData;
import com.repgate.doctor.data.MyRepResponseData;
import com.repgate.doctor.data.RepProfileResponseData;
import com.repgate.doctor.data.RepResponseData;
import com.repgate.doctor.data.RequestResponseData;
import com.repgate.doctor.data.SendResponseData;
import com.repgate.doctor.data.SignupResponseData;
import com.repgate.doctor.data.SpecialtyResponseData;
import com.repgate.doctor.data.UploadResponseData;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * Created by developer on 3/18/16.
 */
public class HttpInterface {

    public interface LoginInterface {
        @FormUrlEncoded
        @POST("signin")
        Call<LoginResponseData> login(@Field("email") String email,
                                      @Field("password") String password,
                                      @Field("deviceId") String deviceId,
                                      @Field("deviceType") int deviceType,
                                      @Field("role") int role,
                                      @Field("appType") int appType);
    }

    public interface GetAllSpecialtiesInterface {
        @GET("getAllSpecialties")
        Call<SpecialtyResponseData> getSpecialties();
    }

    public interface GetMyProviderInterface {
        @GET("getJoinedUsers")
    Call<MyProvidersResponseData> getMyProviders(@Query("userId") int userId,
                                                 @Query("role") String roleId);
    }

    public interface GetProviderInterface {
        @GET("getUsers")
        Call<MyProvidersResponseData> getProviders(@Query("role") int roleId);
    }

    public interface UploadAvatarImageInterface {
        @Multipart
        @POST("uploadAvatar")
        Call<UploadResponseData> uploadImage(@Part("file\"; filename=\"image.png") RequestBody file,
                                             @Part("userId") int userId);
    }

    public interface UpdateProfileInterface {
        @FormUrlEncoded
        @POST("updateProfile")
        Call<DoctorProfileResponseData> updateProfile(@Field("userId") int userId,
                                                            @Field("displayName") String username,
                                                            @Field("phone") String phone,
                                                            @Field("officeAddr") String address,
                                                            @Field("pSpecialty") String pSpecialty,
                                                            @Field("cSpecialty") String cSpecialty,
                                                            @Field("education") String education,
                                                            @Field("certifications") String certifications,
                                                            @Field("awards") String awards,
                                                            @Field("block_allow_request") String block_allow_request,
                                                            @Field("block_allow_message") String block_allow_message,
                                                            @Field("area_of_interest") String area_of_interest);
    }

    public interface UploadFileInterface {
        @Multipart
        @POST("uploadFile")
        Call<UploadResponseData> upload(@Part MultipartBody.Part file);
    }

    public interface CreateMessageInterface {
        @FormUrlEncoded
        @POST("createMessage")
        Call<SendResponseData> createMessage(@Field("senderId") int senderId,
                                           @Field("receiverId") int receiverId,
                                           @Field("title") String title,
                                           @Field("attachs") String attachs,
                                           @Field("pdfLink") String pdfLink,
                                           @Field("videoLink") String videoLink,
                                           @Field("productInfo") String productInfo,
                                           @Field("content") String message);
    }

    public interface SendRequestInterface {
        @FormUrlEncoded
        @POST("createRequest")
        Call<SendResponseData> sendRequest(@Field("title") String title,
                                           @Field("content") String content,
                                           @Field("senderId") int senderId,
                                           @Field("receiverId") int receiverId,
                                           @Field("requestType") int type,
                                           @Field("requestDateTime") String date,
                                           @Field("handleStatus") int handleStatus,
                                           @Field("createdAt") String createdAt);
    }

    public interface GetRequestInterface {
        @GET("getRequest")
        Call<RequestResponseData> getRequest(@Query("userId") int userId, @Query("reqId") int requestId);
    }

    public interface HandleRequestInterface {
        @FormUrlEncoded
        @POST("handleRequest")
        Call<SendResponseData> handleRequest(@Field("reqId") int requestId,
                                             @Field("actionType") int action,
                                             @Field("requestDateTime") String reqDate,
                                             @Field("userId") int userId);
    }

    public interface DeleteMessageInterface {
        @FormUrlEncoded
        @POST("deleteMessage")
        Call<SendResponseData> deleteMessage(@Field("userId") int userId, @Field("msgId") int msgId);
    }

    public interface DeleteRequestInterface {
        @FormUrlEncoded
        @POST("deleteRequest")
        Call<SendResponseData> deleteRequest(@Field("userId") int userId, @Field("reqId") int reqId);
    }

    public interface GetSomeoneOwnMessageInterface {
        @GET("getAllMessages2Someone")
        Call<AllMessageResponseData> getSomeoneOwnMessages(@Query("userId") int userId, @Query("peerId") int peerId);
    }

    public interface GetSomeoneOwnRequestInterface {
        @GET("getAllRequests2Someone")
        Call<AllRequestResponseData> getSomeoneOwnRequests(@Query("userId") int userId, @Query("peerId") int peerId);
    }

    public interface GetAllMessageInterface {
        @GET("getAllMessages")
        Call<AllMessageResponseData> getAllMessages(@Query("userId") int userId);
    }

    public interface ChangeMessageReadStatus {
        @FormUrlEncoded
        @POST("readMessage")
        Call<SendResponseData> changeMessageReadStatus(@Field("msgId") int msgId);
    }

    public interface ChangeRequestReadStatus {
        @FormUrlEncoded
        @POST("readRequest")
        Call<SendResponseData> changeRequestReadStatus(@Field("reqId") int reqId);
    }

    public interface GetAllRequestsInterface {
        @GET("getAllRequests")
        Call<AllRequestResponseData> getAllRequests(@Query("userId") int userId, @Query("officeSchedule") int officeSchedule);
    }
}
