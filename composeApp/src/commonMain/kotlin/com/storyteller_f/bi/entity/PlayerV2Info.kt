package com.storyteller_f.bi.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlayerV2Info(
    val aid: Long,
    @SerialName("allow_bp") val allowBp: Boolean,
    @SerialName("answer_status") val answerStatus: Int,
    @SerialName("block_time") val blockTime: Int,
    val bvid: String,
    val cid: Long,
    val fawkes: Fawkes,
    @SerialName("has_next") val hasNext: Boolean,
    @SerialName("ip_info") val ipInfo: IpInfo,
    @SerialName("is_owner") val isOwner: Boolean,
    @SerialName("is_ugc_pay_preview") val isUgcPayPreview: Boolean,
    @SerialName("last_play_cid") val lastPlayCid: Long,
    @SerialName("last_play_time") val lastPlayTime: Int,
    @SerialName("level_info") val levelInfo: LevelInfo,
    @SerialName("login_mid") val loginMid: Int,
    @SerialName("login_mid_hash") val loginMidHash: String,
    @SerialName("max_limit") val maxLimit: Int,
    val name: String,
    @SerialName("no_share") val noShare: Boolean,
    @SerialName("now_time") val nowTime: Int,
    @SerialName("online_count") val onlineCount: Int,
    @SerialName("online_switch") val onlineSwitch: OnlineSwitch,
    val options: Options,
    @SerialName("page_no") val pageNo: Int,
    val permission: String,
    @SerialName("preview_toast") val previewToast: String,
    val role: String,
    @SerialName("show_switch") val showSwitch: ShowSwitch,
    val subtitle: Subtitle,
    @SerialName("toast_block") val toastBlock: Boolean,
    val vip: Vip,
) {

    @Serializable
    data class Dash(
        val labels: Labels
    )

    @Serializable
    data class Fawkes(
        @SerialName("config_version") val configVersion: Int,
        @SerialName("ff_version") val ffVersion: Int
    )

    @Serializable
    data class Flv(
        val labels: Labels
    )

    @Serializable
    data class IpInfo(
        val city: String,
        val country: String,
        val ip: String,
        val province: String,
        @SerialName("zone_id") val zoneId: Int,
        @SerialName("zone_ip") val zoneIp: String
    )

    @Serializable
    data class Label(
        @SerialName("bg_color") val bgColor: String,
        @SerialName("bg_style") val bgStyle: Int,
        @SerialName("border_color") val borderColor: String,
        @SerialName("img_label_uri_hans") val imgLabelUriHans: String,
        @SerialName("img_label_uri_hans_static") val imgLabelUriHansStatic: String,
        @SerialName("img_label_uri_hant") val imgLabelUriHant: String,
        @SerialName("img_label_uri_hant_static") val imgLabelUriHantStatic: String,
        @SerialName("label_theme") val labelTheme: String,
        val path: String,
        val text: String,
        @SerialName("text_color") val textColor: String,
        @SerialName("use_img_label") val useImgLabel: Boolean
    )

    @Serializable
    data class Labels(
        @SerialName("pcdn_group") val pcdnGroup: String,
        @SerialName("pcdn_stage") val pcdnStage: String,
        @SerialName("pcdn_vendor") val pcdnVendor: String,
        @SerialName("pcdn_version") val pcdnVersion: String,
        @SerialName("pcdn_video_type") val pcdnVideoType: String
    )

    @Serializable
    data class LevelInfo(
        @SerialName("current_exp") val currentExp: Int,
        @SerialName("current_level") val currentLevel: Int,
        @SerialName("current_min") val currentMin: Int,
        @SerialName("level_up") val levelUp: Long,
        @SerialName("next_exp") val nextExp: Long
    )

    @Serializable
    data class OnlineSwitch(
        @SerialName("enable_gray_dash_playback") val enableGrayDashPlayback: String,
        @SerialName("new_broadcast") val newBroadcast: String,
        @SerialName("realtime_dm") val realtimeDm: String,
        @SerialName("subtitle_submit_switch") val subtitleSubmitSwitch: String
    )

    @Serializable
    data class Options(
        @SerialName("is_360") val is360: Boolean,
        @SerialName("without_vip") val withoutVip: Boolean
    )

    @Serializable
    data class PcdnLoader(
        val dash: Dash,
        val flv: Flv
    )

    @Serializable
    data class ShowSwitch(
        @SerialName("long_progress") val longProgress: Boolean
    )

    @Serializable
    data class Subtitle(
        @SerialName("allow_submit") val allowSubmit: Boolean,
        val lan: String,
        @SerialName("lan_doc") val lanDoc: String,
        val subtitles: List<SubtitleItem>
    )

    @Serializable
    data class SubtitleItem(
        @SerialName("ai_status") val aiStatus: Int,
        @SerialName("ai_type") val aiType: Int,
        val id: Long,
        @SerialName("id_str") val idStr: String,
        @SerialName("is_lock") val isLock: Boolean,
        val lan: String,
        @SerialName("lan_doc") val lanDoc: String,
        @SerialName("subtitle_url") val subtitleUrl: String,
        val type: Int
    )

    @Serializable
    data class Vip(
        @SerialName("avatar_subscript") val avatarSubscript: Int,
        @SerialName("avatar_subscript_url") val avatarSubscriptUrl: String,
        @SerialName("due_date") val dueDate: Long,
        val label: Label,
        @SerialName("nickname_color") val nicknameColor: String,
        val role: Int,
        val status: Int,
        @SerialName("theme_type") val themeType: Int,
        @SerialName("tv_vip_pay_type") val tvVipPayType: Int,
        @SerialName("tv_vip_status") val tvVipStatus: Int,
        val type: Int,
        @SerialName("vip_pay_type") val vipPayType: Int
    )
}

@Serializable
data class SubtitleJsonInfo(
    val stroke: String? = null,
    @SerialName("background_alpha") val backgroundAlpha: Float,
    @SerialName("background_color") val backgroundColor: String,
    val body: List<ItemInfo>,
    @SerialName("font_color") val fontColor: String,
    @SerialName("font_size") val fontSize: Float,
) {
    @Serializable
    data class ItemInfo(
        val from: Double,
        val to: Double,
        val location: Int,
        val content: String,
    )
}
