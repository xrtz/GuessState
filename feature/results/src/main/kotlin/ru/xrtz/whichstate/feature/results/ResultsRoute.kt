package ru.xrtz.whichstate.feature.results

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import ru.xrtz.whichstate.core.domain.model.Level
import ru.xrtz.whichstate.core.domain.model.RoundId

@Composable
fun ResultsRoute(
    roundId: RoundId,
    onPlayAgain: (Level) -> Unit,
    onChangeLevel: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ResultsViewModel = hiltViewModel<ResultsViewModel, ResultsViewModel.Factory>(
        key = roundId.value.toString(),
    ) { it.create(roundId) },
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is ResultsEffect.PlayAgain -> onPlayAgain(effect.level)
                ResultsEffect.ChangeLevel -> onChangeLevel()
                is ResultsEffect.ShareImage -> shareImage(context, effect.uri)
            }
        }
    }

    ResultsScreen(
        state = state,
        onAction = viewModel::onAction,
        modifier = modifier,
    )
}

private fun shareImage(context: Context, uri: Uri) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "image/png"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, null))
}
