package ru.xrtz.whichstate.feature.results.share

import android.net.Uri
import ru.xrtz.whichstate.core.domain.model.RoundSummary

interface ShareCardRenderer {
    suspend fun render(summary: RoundSummary): Uri
}
