package com.github.esabook.speechtx.APIService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIFactory {

    public static String __URL = "http://a.galactio.com/";

    /**
     * create retrofit object based
     *
     * @param t Class of T
     * @return
     */
    public <T> T create(Class<T> t) {
        return getRetrofit().create(t);
    }


    /**
     * Get retrofit build
     *
     * @return Retrofit
     */
    public Retrofit getRetrofit() {
        return getRetrofit(__URL);
    }

    /**
     * @param url
     * @return
     */
    public Retrofit getRetrofit(String url) {
        return new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

}
