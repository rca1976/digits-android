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

import com.twitter.sdk.android.core.Session;
import com.twitter.sdk.android.core.TwitterCore;

import java.util.concurrent.ExecutorService;

import javax.net.ssl.SSLSocketFactory;

public class MockDigitsApiClient extends DigitsApiClient {
    Session activeSession;

    public MockDigitsApiClient(Session session, TwitterCore twitterCore,
                         SSLSocketFactory sslFactory, ExecutorService executorService,
                         DigitsUserAgent userAgent){
        super(session, twitterCore,
                sslFactory,  executorService,
                userAgent);
        activeSession = session;
    }

    @Override
    public Session getSession() {
        return activeSession;
    }
}
