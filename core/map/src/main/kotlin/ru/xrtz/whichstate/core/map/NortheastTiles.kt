package ru.xrtz.whichstate.core.map

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.xrtz.whichstate.core.designsystem.components.StateTile
import ru.xrtz.whichstate.core.designsystem.components.StateTileStatus
import ru.xrtz.whichstate.core.domain.model.UsState

private val NE_ROWS = listOf(
    UsState.NH to UsState.VT,
    UsState.MA to UsState.RI,
    UsState.CT to UsState.NJ,
    UsState.DE to UsState.MD,
)

@Composable
fun NortheastTiles(
    onStateTapped: (UsState) -> Unit,
    modifier: Modifier = Modifier,
    highlight: MapHighlight? = null,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        NE_ROWS.forEach { (left, right) ->
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                NortheastTile(left, highlight, onStateTapped)
                NortheastTile(right, highlight, onStateTapped)
            }
        }
    }
}

@Composable
private fun NortheastTile(state: UsState, highlight: MapHighlight?, onStateTapped: (UsState) -> Unit) {
    StateTile(
        code = state.name,
        status = highlight?.statusOf(state) ?: StateTileStatus.DEFAULT,
        onClick = if (highlight == null) ({ onStateTapped(state) }) else null,
        contentDescription = state.fullName,
    )
}
