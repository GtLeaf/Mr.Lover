package com.cmd.dream.mrcen.model

import com.google.gson.annotations.SerializedName

data class SettingModel (
    @SerializedName("vol_level")
    val volLevel: Int = 0,
    @SerializedName("phone_number")
    val phoneNumber: List<String> = listOf())