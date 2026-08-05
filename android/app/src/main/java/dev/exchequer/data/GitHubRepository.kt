package dev.exchequer.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class GitHubRepository(
    private val owner: String = "jain-Igtm",
    private val repository: String = "Exchequer",
    private val branch: String = "main",
) {
    suspend fun load(): RepositorySnapshot = withContext(Dispatchers.IO) {
        val latestBuild = loadWorkflow()
        val verifiedBuild = when {
            latestBuild?.conclusion == "success" -> latestBuild
            else -> loadWorkflow(status = "success")
        }

        val feed = verifiedBuild
            ?.let { loadFeed(it.headSha) }
            ?: FeedSnapshot(repository = "$owner/$repository", problems = emptyList())

        RepositorySnapshot(
            latestBuild = latestBuild,
            verifiedBuild = verifiedBuild,
            feed = feed,
            activity = loadActivity(),
        )
    }

    private fun loadWorkflow(status: String? = null): BuildSnapshot? {
        val statusQuery = status?.let { "&status=$it" }.orEmpty()
        val url = "$API/repos/$owner/$repository/actions/workflows/lean.yml/runs" +
            "?branch=$branch&per_page=1$statusQuery"
        val runs = JSONObject(get(url)).getJSONArray("workflow_runs")
        if (runs.length() == 0) return null

        val run = runs.getJSONObject(0)
        return BuildSnapshot(
            status = run.getString("status"),
            conclusion = run.optNullableString("conclusion"),
            headSha = run.getString("head_sha"),
            updatedAt = run.getString("updated_at"),
            htmlUrl = run.getString("html_url"),
        )
    }

    private fun loadFeed(sha: String): FeedSnapshot {
        val root = JSONObject(
            get("https://raw.githubusercontent.com/$owner/$repository/$sha/feed/index.json")
        )
        val problemsJson = root.getJSONArray("problems")
        val problems = buildList {
            for (index in 0 until problemsJson.length()) {
                add(problemsJson.getJSONObject(index).toProblem())
            }
        }
        return FeedSnapshot(
            repository = root.getString("repository"),
            problems = problems,
        )
    }

    private fun loadActivity(): List<ActivityItem> {
        val commits = JSONArray(
            get("$API/repos/$owner/$repository/commits?sha=$branch&per_page=8")
        )
        return buildList {
            for (index in 0 until commits.length()) {
                val item = commits.getJSONObject(index)
                val commit = item.getJSONObject("commit")
                val committer = commit.getJSONObject("committer")
                add(
                    ActivityItem(
                        sha = item.getString("sha"),
                        message = commit.getString("message").lineSequence().first(),
                        timestamp = committer.getString("date"),
                        htmlUrl = item.getString("html_url"),
                    )
                )
            }
        }
    }

    private fun JSONObject.toProblem(): ProblemSnapshot {
        val verifiedJson = getJSONArray("verified")
        val verified = buildList {
            for (index in 0 until verifiedJson.length()) {
                val item = verifiedJson.getJSONObject(index)
                add(
                    VerifiedItem(
                        name = item.getString("name"),
                        statement = item.getString("statement"),
                        source = item.getString("source"),
                    )
                )
            }
        }

        val obstructionsJson = getJSONArray("obstructions")
        val obstructions = buildList {
            for (index in 0 until obstructionsJson.length()) {
                val item = obstructionsJson.getJSONObject(index)
                add(
                    ObstructionItem(
                        title = item.getString("title"),
                        detail = item.getString("detail"),
                        source = item.optNullableString("source"),
                    )
                )
            }
        }

        val math = getJSONObject("mathematics")
        return ProblemSnapshot(
            id = getString("id"),
            title = getString("title"),
            verified = verified,
            obstructions = obstructions,
            mathematics = Mathematics(
                statement = math.getString("statement"),
                definitions = math.getString("definitions"),
                proof = math.getString("proof"),
                leanSource = math.getString("leanSource"),
            ),
        )
    }

    private fun get(url: String): String {
        val connection = (URL(url).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 12_000
            readTimeout = 12_000
            setRequestProperty("Accept", "application/vnd.github+json")
            setRequestProperty("X-GitHub-Api-Version", "2022-11-28")
            setRequestProperty("User-Agent", "Exchequer-Android")
        }

        return try {
            val code = connection.responseCode
            if (code !in 200..299) {
                throw IOException("HTTP $code · ${URL(url).path}")
            }
            connection.inputStream.bufferedReader().use { it.readText() }
        } finally {
            connection.disconnect()
        }
    }

    private fun JSONObject.optNullableString(key: String): String? {
        if (!has(key) || isNull(key)) return null
        return getString(key)
    }

    private companion object {
        const val API = "https://api.github.com"
    }
}
