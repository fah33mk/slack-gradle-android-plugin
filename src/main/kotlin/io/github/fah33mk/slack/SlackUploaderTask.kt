package io.github.fah33mk.slack

import com.slack.api.Slack
import com.slack.api.methods.SlackApiException
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.io.IOException

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
        val slack = Slack.getInstance()
        val methods = slack.methods(token)

        val file = File(project.rootDir, filePath)
        println("📂 Preparing to upload file: ${file.absolutePath}")

        if (!file.exists()) {
            println("❌ Error: File not found at path: ${file.absolutePath}")
            return
        }

        try {
            println("🚀 Uploading file to Slack channel: #$channel")

            val response = methods.filesUploadV2 { builder ->
                builder
                    .channel(channel)
                    .file(file)
                    .filename(file.name)
                    .initialComment(comment)
            }

            if (response.isOk) {
                println("✅ File uploaded successfully.")
                println("📁 File ID: ${response.file?.id}")
                println("🔗 File URL: ${response.file?.permalink}")
            } else {
                println("⚠️ Slack API responded with error:")
                println("   ➤ Error Code: ${response.error}")
                println("   ➤ Needed: ${response.needed}")
                println("   ➤ Provided: ${response.provided}")
            }

        } catch (e: SlackApiException) {
            println("💥 Slack API Exception occurred:")
            println("   ➤ Message: ${e.message}")
            println("   ➤ Response Code: ${e.response?.code}")
            println("   ➤ Body: ${e.response?.body?.string()}")
        } catch (e: IOException) {
            println("💥 I/O Exception occurred during upload:")
            println("   ➤ Message: ${e.message}")
        } catch (e: Exception) {
            println("💥 Unexpected error occurred:")
            println("   ➤ Type: ${e::class.java.simpleName}")
            println("   ➤ Message: ${e.message}")
        }
    }
}
