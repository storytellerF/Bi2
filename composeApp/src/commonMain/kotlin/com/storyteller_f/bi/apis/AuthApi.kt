package com.storyteller_f.bi.apis

import com.storyteller_f.bi.entity.QRLoginScheme
import com.storyteller_f.bi.entity.QrLoginInfo
import com.storyteller_f.bi.entity.ResultInfo
import com.storyteller_f.bi.entity.UserInfo
import com.storyteller_f.bi.gs.getOrCreateBuvidId
import de.jensklingenberg.ktorfit.http.Field
import de.jensklingenberg.ktorfit.http.FormUrlEncoded
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST

interface AccountApi {
    @GET("x/v2/account/mine")
    suspend fun account(): ResultInfo<UserInfo>
}

interface AuthApi {
    /**
     * 获取登录二维码
     */
    @FormUrlEncoded
    @POST("x/passport-tv-login/qrcode/auth_code")
    suspend fun qrCode(
        @Field("local_id") localId: String = getOrCreateBuvidId()
    ): ResultInfo<QRLoginScheme>

    /**
     *
     */
    @FormUrlEncoded
    @POST("x/passport-tv-login/qrcode/poll")
    suspend fun checkQrCode(
        @Field("auth_code") authCode: String,
        @Field("local_id") localId: String = getOrCreateBuvidId(),
    ): ResultInfo<QrLoginInfo>
}
