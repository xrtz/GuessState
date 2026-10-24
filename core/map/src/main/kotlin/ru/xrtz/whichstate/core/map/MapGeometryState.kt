package ru.xrtz.whichstate.core.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.xrtz.whichstate.core.map.asset.MapAssetDataSource
import ru.xrtz.whichstate.core.map.geometry.MapGeometry
import ru.xrtz.whichstate.core.map.geometry.toPath

internal data class MapGeometryAndPaths(val geometry: MapGeometry, val paths: Map<String, Path>)

@Composable
internal fun rememberMapGeometry(): MapGeometryAndPaths? {
    val context = LocalContext.current
    val dataSource = remember(context) { MapAssetDataSource(context) }
    var result by remember { mutableStateOf<MapGeometryAndPaths?>(null) }

    LaunchedEffect(dataSource) {
        val geometry = dataSource.geometry()
        val paths = withContext(Dispatchers.Default) {
            geometry.states.associate { it.code to it.toPath() }
        }
        result = MapGeometryAndPaths(geometry, paths)
    }

    return result
}
