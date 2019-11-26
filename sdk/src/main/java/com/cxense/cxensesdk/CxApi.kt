package com.cxense.cxensesdk

import com.cxense.cxensesdk.model.EventDataRequest
import com.cxense.cxensesdk.model.PerformanceEvent
import com.cxense.cxensesdk.model.SegmentsResponse
import com.cxense.cxensesdk.model.User
import com.cxense.cxensesdk.model.UserDataRequest
import com.cxense.cxensesdk.model.UserExternalData
import com.cxense.cxensesdk.model.UserExternalDataRequest
import com.cxense.cxensesdk.model.UserExternalDataResponse
import com.cxense.cxensesdk.model.UserIdentity
import com.cxense.cxensesdk.model.UserIdentityMappingRequest
import com.cxense.cxensesdk.model.UserSegmentRequest
import com.cxense.cxensesdk.model.WidgetRequest
import com.cxense.cxensesdk.model.WidgetResponse
import com.cxense.cxensesdk.model.WidgetVisibilityReport
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.QueryMap
import retrofit2.http.Url

interface CxApi {
    @Authorized
    @POST(ENDPOINT_USER_SEGMENTS)
    fun getUserSegments(@Body request: UserSegmentRequest): Call<SegmentsResponse>

    @Authorized
    @POST(ENDPOINT_USER_PROFILE)
    fun getUser(@Body request: UserDataRequest): Call<User>

    @Authorized
    @POST(ENDPOINT_READ_USER_EXTERNAL_DATA)
    fun getUserExternalData(@Body request: UserExternalDataRequest): Call<UserExternalDataResponse>

    @Authorized
    @POST(ENDPOINT_UPDATE_USER_EXTERNAL_DATA)
    fun setUserExternalData(@Body externalData: UserExternalData): Call<Void>

    @Authorized
    @POST(ENDPOINT_DELETE_USER_EXTERNAL_DATA)
    fun deleteExternalUserData(@Body identity: UserIdentity): Call<Void>

    @Authorized
    @POST(ENDPOINT_READ_USER_EXTERNAL_LINK)
    fun getUserExternalLink(@Body request: UserIdentityMappingRequest): Call<UserIdentity>

    @Authorized
    @POST(ENDPOINT_UPDATE_USER_EXTERNAL_LINK)
    fun addUserExternalLink(@Body request: UserIdentityMappingRequest): Call<UserIdentity>

    @Authorized
    @POST(ENDPOINT_PUSH_DMP_EVENTS)
    fun pushEvents(@Body request: EventDataRequest): Call<Void>

    @GET("https://scomcluster.cxense.com/Repo/rep.gif")
    fun trackInsightEvent(@QueryMap options: Map<String, String>): Call<ResponseBody>

    @GET("https://scomcluster.cxense.com/dmp/push.gif")
    fun trackDmpEvent(
        @Query("persisted") persistentId: String,
        @Query(PerformanceEvent.SEGMENT_IDS) segments: List<String>,
        @QueryMap options: Map<String, String>
    ): Call<ResponseBody>

    @POST("https://comcluster.cxense.com/cce/push?experimental=true")
    fun pushConversionEvents(@Body request: EventDataRequest): Call<Void>

    @POST("public/widget/data")
    fun getWidgetData(@Body request: WidgetRequest): Call<WidgetResponse>

    @POST("/public/widget/visibility")
    fun reportWidgetVisibility(@Body request: WidgetVisibilityReport): Call<Void>

    @GET
    fun trackUrlClick(@Url url: String): Call<Void>

    @GET
    fun getPersisted(
        @Url url: String,
        @Query("persisted") persistentId: String
    ): Call<ResponseBody>

    @POST
    fun postPersisted(
        @Url url: String,
        @Query("persisted") persistentId: String,
        @Body data: Any
    ): Call<ResponseBody>
}