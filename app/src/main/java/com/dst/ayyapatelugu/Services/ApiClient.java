package com.dst.ayyapatelugu.Services;

import static com.dst.ayyapatelugu.Services.UnsafeTrustManager.createTrustAllSslSocketFactory;
import static com.dst.ayyapatelugu.Services.UnsafeTrustManager.createTrustAllTrustManager;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import okhttp3.OkHttpClient;

public class ApiClient {
    public static OkHttpClient getUnsafeOkHttpClient() {
        return new OkHttpClient.Builder()
                .sslSocketFactory(UnsafeTrustManager.createTrustAllSslSocketFactory(), UnsafeTrustManager.createTrustAllTrustManager())
                .hostnameVerifier((hostname, session) -> true) // Bypasses hostname verification
                .build();
    }
}
