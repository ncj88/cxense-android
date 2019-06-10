package com.cxense.cxensesdk;

import com.cxense.cxensesdk.model.BaseUserIdentity;
import com.cxense.cxensesdk.model.CxenseUserIdentity;
import com.cxense.cxensesdk.model.EventDataRequest;
import com.cxense.cxensesdk.model.PerformanceEvent;
import com.cxense.cxensesdk.model.SegmentsResponse;
import com.cxense.cxensesdk.model.User;
import com.cxense.cxensesdk.model.UserDataRequest;
import com.cxense.cxensesdk.model.UserExternalData;
import com.cxense.cxensesdk.model.UserExternalDataResponse;
import com.cxense.cxensesdk.model.UserIdentity;
import com.cxense.cxensesdk.model.UserSegmentRequest;
import com.cxense.cxensesdk.model.WidgetRequest;
import com.cxense.cxensesdk.model.WidgetResponse;
import com.cxense.cxensesdk.model.WidgetVisibilityReport;

import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-07-03).
 */

interface CxenseApi {

    @POST(CxenseConstants.ENDPOINT_USER_SEGMENTS)
    Call<SegmentsResponse> getUserSegments(@Body UserSegmentRequest request);

    @POST(CxenseConstants.ENDPOINT_USER_PROFILE)
    Call<User> getUser(@Body UserDataRequest request);

    @POST(CxenseConstants.ENDPOINT_READ_USER_EXTERNAL_DATA)
    Call<UserExternalDataResponse> getUserExternalData(@Body BaseUserIdentity identity);

    @POST(CxenseConstants.ENDPOINT_UPDATE_USER_EXTERNAL_DATA)
    Call<Void> updateUserExternalData(@Body UserExternalData externalData);

    @POST(CxenseConstants.ENDPOINT_DELETE_USER_EXTERNAL_DATA)
    Call<Void> deleteExternalUserData(@Body UserIdentity identity);

    @POST(CxenseConstants.ENDPOINT_READ_USER_EXTERNAL_LINK)
    Call<UserIdentity> getUserExternalLink(@Body CxenseUserIdentity identity);

    @POST(CxenseConstants.ENDPOINT_UPDATE_USER_EXTERNAL_LINK)
    Call<UserIdentity> updateUserExternalLink(@Body CxenseUserIdentity identity);

    @POST(CxenseConstants.ENDPOINT_PUSH_DMP_EVENTS)
    Call<Void> pushEvents(@Body EventDataRequest request);

    @GET("https://scomcluster.cxense.com/Repo/rep.gif")
    Call<ResponseBody> trackInsightEvent(@QueryMap Map<String, String> options);

    @GET("https://scomcluster.cxense.com/dmp/push.gif")
    Call<ResponseBody> trackDmpEvent(@Query("persisted") String persistentId,
                                     @Query(PerformanceEvent.SEGMENT_IDS) List<String> segments,
                                     @QueryMap Map<String, String> options);

    @POST("https://comcluster.cxense.com/cce/push?experimental=true")
    Call<Void> pushConversionEvents(@Body EventDataRequest request);

    @POST("public/widget/data")
    Call<WidgetResponse> getWidgetData(@Body WidgetRequest request);

    @POST("/public/widget/visibility")
    Call<ResponseBody> reportWidgetVisibility(@Body WidgetVisibilityReport request);

    @GET
    Call<Void> trackUrlClick(@Url String url);

    @GET
    Call<ResponseBody> getPersisted(@Url String url, @Query("persisted") String persistentId);

    @POST
    Call<ResponseBody> postPersisted(@Url String url, @Query("persisted") String persistentId, @Body Object data);
}
