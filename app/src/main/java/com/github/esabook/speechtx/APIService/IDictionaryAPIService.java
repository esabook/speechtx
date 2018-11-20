package com.github.esabook.speechtx.APIService;

import com.github.esabook.speechtx.models.DictionaryDTO;
import retrofit2.Call;
import retrofit2.http.GET;

import java.util.ArrayList;

public interface IDictionaryAPIService {

    @GET("/interview/dictionary-v2.json")
    Call<Dict> getDictionary();

    class Dict {
        public ArrayList<DictionaryDTO> dictionary;
    }
}
