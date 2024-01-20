package com.storyteller_f.bi.network

import com.storyteller_f.bi.entity.BangumiInfo
import com.storyteller_f.bi.entity.EpisodeInfo
import com.storyteller_f.bi.repository.BangumiPlayerRepository
import com.storyteller_f.bi.repository.BasePlayerRepository

fun bangumiPlayerRepository(
    v: EpisodeInfo,
    bangumiInfo: BangumiInfo
): BasePlayerRepository = BangumiPlayerRepository(
    sid = bangumiInfo.seasonId,
    epid = v.epId,
    aid = v.aid,
    id = v.cid,
    title = v.safeTitle,
    coverUrl = v.cover
)
