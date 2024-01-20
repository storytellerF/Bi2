package com.storyteller_f.bi.network

import bilibili.metadata.Metadata
import bilibili.metadata.device.Device
import bilibili.metadata.fawkes.FawkesReq
import bilibili.metadata.locale.Locale
import bilibili.metadata.locale.LocaleIds
import bilibili.metadata.network.Network
import bilibili.metadata.network.NetworkType
import bilibili.metadata.restriction.Restriction
import com.storyteller_f.bi.gs.getOrCreateBuvidId
import io.ktor.util.*

object GrpcBinary {

    /**
     * 频道.
     */
    private const val CHANNEL = "bilibili140"

    /**
     * 未知.
     */
    private const val NETWORK_OID = "46007"

    /**
     * 未知.
     */
    val buvid get() = getOrCreateBuvidId()

    /**
     * 应用类型.
     */
    const val MOBILE_APP = "android_hd"

    /**
     * 移动平台.
     */
    private const val PLATFORM = "android_hd"

    /**
     * 产品环境.
     */
    const val ENVIRONMENT = "prod"

    /**
     * 应用Id.
     */
    private const val APP_ID = 1

    /**
     * 国家或地区.
     */
    private const val REGION = "CN"

    /**
     * 语言.
     */
    private const val LANGUAGE = "zh"

    /**
     * 获取客户端在Fawkes系统中的信息标头.
     */
    fun getFawkesreqBin(): String {
        val msg = FawkesReq(appkey = MOBILE_APP, env = ENVIRONMENT)
        return msg.encode().toBase64()
    }

    /**
     * 获取元数据标头.
     */
    fun getMetadataBin(accessToken: String): String {
        val msg = Metadata(
            access_key = accessToken,
            mobi_app = MOBILE_APP,
            build = BUILD_VERSION,
            channel = CHANNEL,
            buvid = buvid,
            platform = PLATFORM
        )
        return msg.encode().toBase64()
    }

    /**
     * 获取设备标头.
     */
    fun getDeviceBin(): String {
        val msg = Device(
            app_id = APP_ID,
            mobi_app = MOBILE_APP,
            build = BUILD_VERSION,
            channel = CHANNEL,
            buvid = buvid,
            platform = PLATFORM,
            brand = "Build.BRAND",
            model = "Build.MODEL",
            osver = "Build.VERSION.RELEASE"
        )
        return msg.encode().toBase64()
    }

    /**
     * 获取网络标头.
     */
    fun getNetworkBin(): String {
        val msg = Network(type = NetworkType.WIFI, oid = NETWORK_OID)
        return msg.encode().toBase64()
    }

    /**
     * 获取限制标头.
     */
    fun getRestrictionBin(): String {
        val msg = Restriction()
        return msg.encode().toBase64()
    }

    /**
     * 获取本地化标头.
     */
    fun getLocaleBin(): String {
        val cLocale = LocaleIds(language = LANGUAGE, region = REGION)
        val sLocale = LocaleIds(language = LANGUAGE, region = REGION)
        val msg = Locale(c_locale = cLocale, s_locale = sLocale)
        return msg.encode().toBase64()
    }

    /**
     * 将数据转换为Base64字符串.
     */
    private fun ByteArray.toBase64() = encodeBase64()
}
