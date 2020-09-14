package com.kdpark.sickdan.model;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.kdpark.sickdan.util.SharedDataUtil;
import com.kdpark.sickdan.viewmodel.common.ErrorResponse;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;

import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ApiClient {

//    public static final String BASE_URL = "http://222.111.195.42:8080";   // prod
    public static final String BASE_URL = "http://121.160.25.204:8080";     // dev
//    public static final String BASE_URL = "http://10.0.2.2:8080";         // simulator

    public static final String ACCESS_TOKEN_HEADER = "Authorization";
    public static final String REFRESH_TOKEN_HEADER = "Refresh";

    private static Retrofit retrofit;

    private ApiClient() {}

    public static void init(Application application) {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        OkHttpClient client = clientBuilder.addInterceptor(chain -> {

            SharedPreferences sp = application.getSharedPreferences(SharedDataUtil.JWT_INFO, Context.MODE_PRIVATE);
            String accessToken = sp.getString(SharedDataUtil.JWT_ACCESS_TOKEN, "");

            Request request;

            if (accessToken != null && !accessToken.equals("")) {
                request = chain.request().newBuilder().addHeader(ACCESS_TOKEN_HEADER, accessToken).build();
            } else {
                request = chain.request();
            }

            return chain.proceed(request);
        }).build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(client)
                .build();
    }

    public static ErrorResponse getErrorResponse(Response<?> response) {
        Converter<ResponseBody, ErrorResponse> errorConverter =
                retrofit.responseBodyConverter(ErrorResponse.class, new Annotation[0]);

        ErrorResponse errorResponse;

        if (response.errorBody() == null) return new ErrorResponse();

        try {
            errorResponse = errorConverter.convert(response.errorBody());
        } catch (IOException e) {
            errorResponse = new ErrorResponse();
            e.printStackTrace();
        }

        return errorResponse;
    }

    public static <T> T getService(Class<T> nestedClass) {
        return retrofit.create(nestedClass);
    }

}
