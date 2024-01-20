package com.storyteller_f.bi.network

import com.storyteller_f.bi.repository.BangumiPlayerRepository
import com.storyteller_f.bi.repository.BasePlayerRepository

fun bangumiPlayerRepository(
    v: com.storyteller_f.bi.entity.bangumi.EpisodeInfo, bangumiInfo: com.storyteller_f.bi.entity.bangumi.BangumiInfo
): BasePlayerRepository = BangumiPlayerRepository(
    sid = bangumiInfo.seasonId,
    epid = v.epId,
    aid = v.aid,
    id = v.cid,
    title = v.safeTitle,
    coverUrl = v.cover,
    ownerId = "",
    ownerName = bangumiInfo.seasonTitle
)