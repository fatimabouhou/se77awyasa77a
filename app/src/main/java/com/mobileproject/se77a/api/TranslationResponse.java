package com.mobileproject.se77a.api;

import com.google.gson.annotations.SerializedName;

public class TranslationResponse {
    @SerializedName("responseData")
    private ResponseData responseData;

    public String getTranslatedText() {
        return responseData != null ? responseData.translatedText : "";
    }

    public static class ResponseData {
        @SerializedName("translatedText")
        public String translatedText;
    }
}
