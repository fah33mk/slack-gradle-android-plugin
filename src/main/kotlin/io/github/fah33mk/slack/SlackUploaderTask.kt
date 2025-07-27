package io.github.fah33mk.slack

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

open class SlackUploaderTask : DefaultTask() {

    @Input
    var channel = ""

    @Input
    var comment = ""

    @Input
    var filePath = ""

    @Input
    var token = ""

    @TaskAction
    fun doUpload() {
        println("🚧 Slack Build Share: Starting upload")

        try {
            val file = File(project.rootDir, filePath)
            println("📂 File: ${file.absolutePath}")

            if (!file.exists()) {
                println("❌ File not found at given path")
                return
            }

            val client = OkHttpClient.Builder()
                .callTimeout(3, TimeUnit.MINUTES)
                .readTimeout(3, TimeUnit.MINUTES)
                .build()

            println("📦 Preparing upload request...")

            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("channels", channel)
                .addFormDataPart("initial_comment", comment)
                .addFormDataPart(
                    "file",
                    file.name,
                    ProgressRequestBody(
                        file,
                        "application/octet-stream".toMediaTypeOrNull()
                    ) { percent ->
                        println("📤 Uploading build to Slack... $percent%")
                    }
                )
                .build()

            val request = Request.Builder()
                .url("https://slack.com/api/files.upload")
                .addHeader("Authorization", "Bearer $token")
                .post(requestBody)
                .build()

            println("🚀 Uploading to Slack...")

            client.newCall(request).execute().use { response ->
                val responseBody = response.body?.string()
                println("📬 Response: ${response.code}")
                println("📨 Body: $responseBody")

                if (response.isSuccessful) {
                    println("✅ Build uploaded successfully to Slack.")
                } else {
                    println("⚠️ Upload failed: check channel/token")
                }
            }

        } catch (_: SocketTimeoutException) {
            println("⏱️ Timeout: Slack didn't respond in 3 mins")
        } catch (e: Exception) {
            println("💥 Error: ${e.localizedMessage}")
        }
    }
}
