package dev.exchequer.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.exchequer.data.ActivityItem
import dev.exchequer.data.BuildSnapshot
import dev.exchequer.data.GitHubRepository
import dev.exchequer.data.Mathematics
import dev.exchequer.data.ObstructionItem
import dev.exchequer.data.ProblemSnapshot
import dev.exchequer.data.RepositorySnapshot
import dev.exchequer.data.VerifiedItem
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private sealed interface ScreenState {
    data object Loading : ScreenState
    data class Ready(val snapshot: RepositorySnapshot) : ScreenState
    data class Failed(val message: String) : ScreenState
}

private enum class MathTab {
    Mathematics,
    Lean,
}

@Composable
fun ExchequerApp() {
    val repository = remember { GitHubRepository() }
    var refreshKey by remember { mutableIntStateOf(0) }
    var state by remember { mutableStateOf<ScreenState>(ScreenState.Loading) }

    LaunchedEffect(refreshKey) {
        state = ScreenState.Loading
        state = runCatching { repository.load() }
            .fold(
                onSuccess = { ScreenState.Ready(it) },
                onFailure = { ScreenState.Failed(it.message ?: it::class.java.simpleName) },
            )
    }

    GlossBackground {
        when (val current = state) {
            ScreenState.Loading -> LoadingScreen()
            is ScreenState.Failed -> FailureScreen(
                message = current.message,
                onRefresh = { refreshKey++ },
            )
            is ScreenState.Ready -> Dashboard(
                snapshot = current.snapshot,
                onRefresh = { refreshKey++ },
            )
        }
    }
}

@Composable
private fun GlossBackground(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF08080D),
                        Color(0xFF0A0910),
                        Color(0xFF050507),
                    )
                )
            )
    ) {
        Canvas(Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF8E63FF).copy(alpha = 0.22f),
                        Color.Transparent,
                    ),
                    center = Offset(size.width * 0.18f, size.height * 0.08f),
                    radius = size.width * 0.72f,
                ),
                radius = size.width * 0.72f,
                center = Offset(size.width * 0.18f, size.height * 0.08f),
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF1BC7E8).copy(alpha = 0.12f),
                        Color.Transparent,
                    ),
                    center = Offset(size.width * 0.9f, size.height * 0.52f),
                    radius = size.width * 0.65f,
                ),
                radius = size.width * 0.65f,
                center = Offset(size.width * 0.9f, size.height * 0.52f),
            )
        }
        content()
    }
}

@Composable
private fun LoadingScreen() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            modifier = Modifier.size(34.dp),
            strokeWidth = 2.dp,
            color = MaterialTheme.colorScheme.primary,
            trackColor = Color.White.copy(alpha = 0.08f),
        )
    }
}

@Composable
private fun FailureScreen(message: String, onRefresh: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 20.dp,
                start = 20.dp,
                end = 20.dp,
            ),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Header(onRefresh)
        GlossCard {
            Text(
                text = "SYNC FAILED",
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp,
            )
            Spacer(Modifier.height(10.dp))
            SelectionContainer {
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                )
            }
        }
    }
}

@Composable
private fun Dashboard(snapshot: RepositorySnapshot, onRefresh: () -> Unit) {
    var selectedProblemId by remember(snapshot.feed.problems) {
        mutableStateOf(snapshot.feed.problems.firstOrNull()?.id)
    }
    val selectedProblem = snapshot.feed.problems.firstOrNull { it.id == selectedProblemId }
        ?: snapshot.feed.problems.firstOrNull()

    val topPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val bottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 18.dp,
            end = 18.dp,
            top = topPadding + 12.dp,
            bottom = bottomPadding + 28.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item { Header(onRefresh) }
        snapshot.latestBuild?.let { build ->
            item {
                BuildCard(
                    latestBuild = build,
                    verifiedBuild = snapshot.verifiedBuild,
                )
            }
        }

        if (snapshot.feed.problems.size > 1) {
            item {
                ProblemSelector(
                    problems = snapshot.feed.problems,
                    selectedId = selectedProblem?.id,
                    onSelected = { selectedProblemId = it },
                )
            }
        }

        selectedProblem?.let { problem ->
            item { ProblemTitle(problem.title) }
            if (problem.verified.isNotEmpty()) {
                item {
                    VerifiedSection(
                        items = problem.verified,
                        verifiedSha = snapshot.verifiedBuild?.headSha,
                    )
                }
            }
            if (problem.obstructions.isNotEmpty()) {
                item { ObstructionsSection(problem.obstructions) }
            }
            item { MathematicsSection(problem.mathematics) }
        }

        if (snapshot.activity.isNotEmpty()) {
            item { SectionLabel("Latest activity") }
            items(snapshot.activity, key = { it.sha }) { activity ->
                ActivityRow(activity)
            }
        }
    }
}

@Composable
private fun Header(onRefresh: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = "Exchequer",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 30.sp,
            lineHeight = 34.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = (-0.7).sp,
        )
        IconButton(onClick = onRefresh) {
            Icon(
                imageVector = Icons.Rounded.Refresh,
                contentDescription = "Refresh",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun BuildCard(latestBuild: BuildSnapshot, verifiedBuild: BuildSnapshot?) {
    val result = latestBuild.conclusion ?: latestBuild.status
    val statusColor = when (result) {
        "success" -> Color(0xFF7BF0B5)
        "failure", "cancelled", "timed_out" -> Color(0xFFFF8E96)
        else -> Color(0xFFFFD27A)
    }

    GlossCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                SectionLabel("Lean build")
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Box(
                        Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(statusColor)
                    )
                    Text(
                        text = result.uppercase(),
                        color = statusColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.9.sp,
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = latestBuild.headSha.take(8),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                )
                Text(
                    text = formatTimestamp(latestBuild.updatedAt),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                )
            }
        }

        if (verifiedBuild != null && verifiedBuild.headSha != latestBuild.headSha) {
            Spacer(Modifier.height(16.dp))
            Hairline()
            Spacer(Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "Verified commit",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                )
                Text(
                    text = verifiedBuild.headSha.take(8),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                )
            }
        }
    }
}

@Composable
private fun ProblemSelector(
    problems: List<ProblemSnapshot>,
    selectedId: String?,
    onSelected: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        problems.forEach { problem ->
            FilterChip(
                selected = problem.id == selectedId,
                onClick = { onSelected(problem.id) },
                label = { Text(problem.title) },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = Color.White.copy(alpha = 0.04f),
                    selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                )
            )
        }
    }
}

@Composable
private fun ProblemTitle(title: String) {
    Text(
        text = title,
        color = MaterialTheme.colorScheme.onBackground,
        fontSize = 21.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = (-0.25).sp,
        modifier = Modifier.padding(top = 4.dp, start = 2.dp),
    )
}

@Composable
private fun VerifiedSection(items: List<VerifiedItem>, verifiedSha: String?) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionLabel(
            if (verifiedSha == null) "Verified" else "Verified · ${verifiedSha.take(8)}"
        )
        items.forEach { item ->
            GlossCard(compact = true) {
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF7BF0B5),
                        modifier = Modifier.size(18.dp),
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        Text(
                            text = item.name,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = item.statement,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                        )
                        Text(
                            text = item.source,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.82f),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ObstructionsSection(items: List<ObstructionItem>) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionLabel("Obstructions")
        items.forEach { item ->
            GlossCard(compact = true) {
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Warning,
                        contentDescription = null,
                        tint = Color(0xFFFFD27A),
                        modifier = Modifier.size(18.dp),
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        Text(
                            text = item.title,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                        SelectionContainer {
                            Text(
                                text = item.detail,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                lineHeight = 18.sp,
                            )
                        }
                        item.source?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.82f),
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MathematicsSection(mathematics: Mathematics) {
    var expanded by remember { mutableStateOf(false) }
    var tab by remember { mutableStateOf(MathTab.Mathematics) }

    GlossCard(
        modifier = Modifier.animateContentSize(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SectionLabel("Mathematics")
            Icon(
                imageVector = Icons.Rounded.ExpandMore,
                contentDescription = if (expanded) "Collapse" else "Expand",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.rotate(if (expanded) 180f else 0f),
            )
        }

        AnimatedVisibility(visible = expanded) {
            Column(
                modifier = Modifier.padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Hairline()
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MathTab.entries.forEach { entry ->
                        FilterChip(
                            selected = tab == entry,
                            onClick = { tab = entry },
                            label = {
                                Text(if (entry == MathTab.Mathematics) "Math" else "Lean")
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = Color.White.copy(alpha = 0.04f),
                                selectedContainerColor =
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                            )
                        )
                    }
                }

                when (tab) {
                    MathTab.Mathematics -> {
                        MathBlock("Statement", mathematics.statement)
                        MathBlock("Definitions", mathematics.definitions)
                        MathBlock("Proof", mathematics.proof)
                    }
                    MathTab.Lean -> CodeBlock(mathematics.leanSource)
                }
            }
        }
    }
}

@Composable
private fun MathBlock(label: String, body: String) {
    Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
        Text(
            text = label.uppercase(),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.82f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.0.sp,
        )
        SelectionContainer {
            Text(
                text = body,
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = FontFamily.Serif,
                fontSize = 16.sp,
                lineHeight = 25.sp,
            )
        }
    }
}

@Composable
private fun CodeBlock(code: String) {
    val horizontal = rememberScrollState()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color.Black.copy(alpha = 0.32f))
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.07f),
                shape = RoundedCornerShape(18.dp),
            )
            .horizontalScroll(horizontal)
            .padding(16.dp)
    ) {
        SelectionContainer {
            Text(
                text = code,
                color = Color(0xFFE8E3F2),
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                lineHeight = 18.sp,
            )
        }
    }
}

@Composable
private fun ActivityRow(item: ActivityItem) {
    GlossCard(compact = true) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Text(
                text = item.sha.take(7),
                color = MaterialTheme.colorScheme.primary,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = item.message,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp,
                    lineHeight = 19.sp,
                )
                Text(
                    text = formatTimestamp(item.timestamp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp,
                )
            }
        }
    }
}

@Composable
private fun GlossCard(
    modifier: Modifier = Modifier,
    compact: Boolean = false,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = RoundedCornerShape(if (compact) 22.dp else 28.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.095f),
                        Color.White.copy(alpha = 0.035f),
                        Color(0xFF8E63FF).copy(alpha = 0.035f),
                    ),
                    start = Offset.Zero,
                    end = Offset(1000f, 1000f),
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    listOf(
                        Color.White.copy(alpha = 0.20f),
                        Color.White.copy(alpha = 0.045f),
                    )
                ),
                shape = shape,
            )
            .padding(if (compact) 16.dp else 20.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp),
        content = content,
    )
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.15.sp,
    )
}

@Composable
private fun Hairline() {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
    ) {
        drawLine(
            brush = Brush.horizontalGradient(
                listOf(
                    Color.Transparent,
                    Color.White.copy(alpha = 0.14f),
                    Color.Transparent,
                )
            ),
            start = Offset.Zero,
            end = Offset(size.width, 0f),
            strokeWidth = 1.dp.toPx(),
            cap = StrokeCap.Round,
        )
    }
}

private fun formatTimestamp(value: String): String {
    return runCatching {
        DISPLAY_TIME.format(Instant.parse(value))
    }.getOrElse { value }
}

private val DISPLAY_TIME: DateTimeFormatter = DateTimeFormatter
    .ofPattern("MMM d · h:mm a")
    .withZone(ZoneId.systemDefault())
