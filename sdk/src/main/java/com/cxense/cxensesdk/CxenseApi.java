package com.cxense.cxensesdk;

import com.cxense.cxensesdk.model.BaseUserIdentity;
import com.cxense.cxensesdk.model.CxenseUserIdentity;
import com.cxense.cxensesdk.model.EventDataRequest;
import com.cxense.cxensesdk.model.SegmentsResponse;
import com.cxense.cxensesdk.model.User;
import com.cxense.cxensesdk.model.UserDataRequest;
import com.cxense.cxensesdk.model.UserExternalData;
import com.cxense.cxensesdk.model.UserExternalDataResponse;
import com.cxense.cxensesdk.model.UserIdentity;
import com.cxense.cxensesdk.model.UserSegmentRequest;
import com.cxense.cxensesdk.model.WidgetRequest;
import com.cxense.cxensesdk.model.WidgetResponse;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-07-03).
 */

public interface CxenseApi {
    @POST("profile/user/segment")
    Call<SegmentsResponse> getUserSegments(@Body UserSegmentRequest request);

    @POST("profile/user")
    Call<User> getUser(@Body UserDataRequest request);

    @POST("profile/user/external/read")
    Call<UserExternalDataResponse> getUserExternalData(@Body BaseUserIdentity identity);

    @POST("profile/user/external/update")
    Call<Void> updateUserExternalData(@Body UserExternalData externalData);

    @POST("profile/user/external/delete")
    Call<Void> deleteExternalUserData(@Body UserIdentity identity);

    @POST("profile/user/external/link")
    Call<UserIdentity> getUserExternalLink(@Body CxenseUserIdentity identity);

    @POST("profile/user/external/link/update")
    Call<UserIdentity> updateUserExternalLink(@Body CxenseUserIdentity identity);

    @POST("dmp/push")
    Call<Void> pushEvents(@Body EventDataRequest request);

    @GET("https://scomcluster.cxense.com/Repo/rep.gif")
    Call<ResponseBody> track(@QueryMap Map<String, String> options);

    @POST("public/widget/data")
    Call<WidgetResponse> getWidgetData(@Body WidgetRequest request);

    @GET
    Call<Void> trackUrlClick(@Url String url);
}
