package com.example.youmanage.data.remote.authentication

import com.google.gson.annotations.SerializedName

data class ChangePasswordRequest(
    @SerializedName("new_password")
    val newPassword: String,
    @SerializedName("confirm_password")
    val confirmPassword: String,
    @SerializedName("reset_token")
    val resetToken: String
)
