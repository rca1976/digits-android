/*
 * Copyright (C) 2015 Twitter, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.digits.sdk.android;

import android.util.Log;

import com.twitter.sdk.android.core.AuthenticatedClient;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Session;
import com.twitter.sdk.android.core.TwitterCore;

import java.util.concurrent.ExecutorService;

import javax.net.ssl.SSLSocketFactory;

import retrofit.RestAdapter;
import retrofit.android.MainThreadExecutor;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

class DigitsApiClient {
    private final SdkService service;
    private final Session session;
    private static final String NULL_SESSION_ERROR_LOG =
            "Attempting to connect to Digits API with null session. " +
                    "Please re-authenticate and try again";

    DigitsApiClient(Session session) {
        this(session, TwitterCore.getInstance(), TwitterCore.getInstance().getSSLSocketFactory(),
                Digits.getInstance()
                        .getExecutorService());
    }

    DigitsApiClient(Session session, TwitterCore twitterCore, SSLSocketFactory sslFactory,
                    ExecutorService executorService) {
        this(session, twitterCore, sslFactory, executorService, new DigitsUserAgent());
    }

    DigitsApiClient(Session session, TwitterCore twitterCore,
                    SSLSocketFactory sslFactory, ExecutorService executorService,
                    DigitsUserAgent userAgent) {
        if (session == null) {
            Log.e(Digits.TAG, NULL_SESSION_ERROR_LOG);
        }
        this.session = session;

        final RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(new DigitsApi().getBaseHostUrl())
                .setRequestInterceptor(new DigitsRequestInterceptor(userAgent))
                .setExecutors(executorService, new MainThreadExecutor())
                .setClient(
                        new AuthenticatedClient(twitterCore.getAuthConfig(), session, sslFactory))
                .build();
        this.service = restAdapter.create(SdkService.class);

    }

    public Session getSession() {
        return session;
    }

    public SdkService getService() {
        return service;
    }


    protected interface SdkService {
        @FormUrlEncoded
        @POST("/1.1/device/register.json")
        void register(@Field("raw_phone_number") String rawPhoneNumber,
                      @Field("text_key") String textKey,
                      @Field("send_numeric_pin") Boolean sendNumericPin,
                      @Field("lang") String lang,
                      @Field("client_identifier_string") String id,
                      @Field("verification_type") String verificationType,
                      Callback<DeviceRegistrationResponse> cb);

        @FormUrlEncoded
        @POST("/1.1/sdk/account.json")
        void account(@Field("phone_number") String phoneNumber,
                     @Field("numeric_pin") String numericPin,
                     Callback<DigitsUser> cb);

        @FormUrlEncoded
        @POST("/1/sdk/login")
        void auth(@Field("x_auth_phone_number") String phoneNumber,
                  @Field("verification_type") String verificationType,
                  @Field("lang") String lang,
                  Callback<AuthResponse> cb);

        @FormUrlEncoded
        @POST("/auth/1/xauth_challenge.json")
        void login(@Field("login_verification_request_id") String requestId,
                   @Field("login_verification_user_id") long userId,
                   @Field("login_verification_challenge_response") String code,
                   Callback<DigitsSessionResponse> cb);

        @FormUrlEncoded
        @POST("/auth/1/xauth_pin.json")
        void verifyPin(@Field("login_verification_request_id") String requestId,
                       @Field("login_verification_user_id") long userId,
                       @Field("pin") String pin,
                       Callback<DigitsSessionResponse> cb);

        @FormUrlEncoded
        @POST("/1.1/sdk/account/email")
        void email(@Field("email_address") String email, Callback<DigitsSessionResponse> cb);

        @GET("/1.1/sdk/account.json")
        void verifyAccount(Callback<VerifyAccountResponse> cb);

        @POST("/1.1/contacts/upload.json")
        UploadResponse upload(@Body Vcards vcards);

        @POST("/1.1/contacts/destroy/all.json")
        void deleteAll(Callback<Response> cb);

        @GET("/1.1/contacts/users_and_uploaded_by.json")
        void usersAndUploadedBy(@Query("next_cursor") String nextCursor,
                                @Query("count") Integer count, Callback<Contacts> cb);
    }
}
