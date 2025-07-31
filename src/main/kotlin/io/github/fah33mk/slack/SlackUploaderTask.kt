package io.github.fah33mk.slack

import com.slack.api.Slack
import com.slack.api.methods.SlackApiException
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
import java.io.IOException

abstract class SlackUploaderTask : DefaultTask() {

    @get:Input
    abstract val channel: Property<String>

    @get:Input
    abstract val comment: Property<String>

    @get:InputFile
    abstract val inputFile: RegularFileProperty

    @get:Input
    abstract val token: Property<String>

    @TaskAction
    fun doUpload() {
        val slack = Slack.getInstance()
        val methods = slack.methods(token.get())

        val file = inputFile.get().asFile
        logger.lifecycle("📂 Preparing to upload file: ${file.absolutePath}")

        if (!file.exists()) {
            logger.error("❌ Error: File not found at path: ${file.absolutePath}")
            return
        }

        try {
            logger.lifecycle("🚀 Uploading file to Slack channel: #${channel.get()}")

            val response = methods.filesUploadV2 { builder ->
                builder
                    .channel(channel.get())
                    .file(file)
                    .filename(file.name)
                    .initialComment(comment.get())
            }

            if (response.isOk) {
                logger.lifecycle("✅ File uploaded successfully.")
                logger.lifecycle("📁 File ID: ${response.file?.id}")
                logger.lifecycle("🔗 File URL: ${response.file?.permalink}")
            } else {
                logger.error("⚠️ Slack API responded with error: ${response.error}")
            }

        } catch (e: SlackApiException) {
            logger.error("💥 Slack API Exception: ${e.message}", e)
        } catch (e: IOException) {
            logger.error("💥 I/O Exception during upload: ${e.message}", e)
        } catch (e: Exception) {
            logger.error("💥 Unexpected error: ${e.message}", e)
        }
    }
}
