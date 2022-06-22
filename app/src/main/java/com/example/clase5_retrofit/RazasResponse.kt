package com.example.clase5_retrofit

import com.google.gson.annotations.SerializedName

data class RazasResponse(
    @SerializedName("message") var imagenes: List<String>,
    @SerializedName("status") var estado: String
)