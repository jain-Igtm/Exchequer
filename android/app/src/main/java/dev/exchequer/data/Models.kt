package dev.exchequer.data

data class BuildSnapshot(
    val status: String,
    val conclusion: String?,
    val headSha: String,
    val updatedAt: String,
    val htmlUrl: String,
)

data class VerifiedItem(
    val name: String,
    val statement: String,
    val source: String,
)

data class ObstructionItem(
    val title: String,
    val detail: String,
    val source: String?,
)

data class Mathematics(
    val statement: String,
    val definitions: String,
    val proof: String,
    val leanSource: String,
)

data class ProblemSnapshot(
    val id: String,
    val title: String,
    val verified: List<VerifiedItem>,
    val obstructions: List<ObstructionItem>,
    val mathematics: Mathematics,
)

data class FeedSnapshot(
    val repository: String,
    val problems: List<ProblemSnapshot>,
)

data class ActivityItem(
    val sha: String,
    val message: String,
    val timestamp: String,
    val htmlUrl: String,
)

data class RepositorySnapshot(
    val latestBuild: BuildSnapshot?,
    val verifiedBuild: BuildSnapshot?,
    val feed: FeedSnapshot,
    val activity: List<ActivityItem>,
)
