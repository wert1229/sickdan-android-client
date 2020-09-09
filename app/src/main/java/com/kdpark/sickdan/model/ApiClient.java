package com.kdpark.sickdan.model;

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

    public static final String BASE_URL = "http://222.111.195.42:8080";
//    public static final String BASE_URL = "http://10.0.2.2:8080";
    private static Retrofit retrofit;

    private ApiClient() {};

    private static Retrofit getInstance() {
        if (retrofit == null) {
            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
            OkHttpClient client= clientBuilder.addInterceptor(chain -> {
                String token = SharedDataUtil.getData(SharedDataUtil.JWT_TOKEN, false);

                Request request;

                if (!token.isEmpty()) {
                    request = chain.request().newBuilder().addHeader("X-AUTH-TOKEN", token).build();
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

        return retrofit;
    }

    public static ErrorResponse getErrorResponse(Response response) {
        Converter<ResponseBody, ErrorResponse> errorConverter =
                retrofit.responseBodyConverter(ErrorResponse.class, new Annotation[0]);

        ErrorResponse errorResponse;

        try {
            errorResponse = errorConverter.convert(response.errorBody());
        } catch (IOException e) {
            errorResponse = new ErrorResponse();
            e.printStackTrace();
        }

        return errorResponse;
    }

    public static <T> T getService(Class<T> nestedClass) {
        return getInstance().create(nestedClass);
    }

}
