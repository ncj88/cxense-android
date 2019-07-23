package com.cxense.cxensesdk;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

/**
 * Api Error answer
 *
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-05).
 */

final class ApiError {
    @Nullable
    @SerializedName("error")
    String error;
}
