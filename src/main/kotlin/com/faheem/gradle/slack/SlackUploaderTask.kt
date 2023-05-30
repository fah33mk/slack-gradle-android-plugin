package com.faheem.gradle.slack

import allbegray.slack.webapi.SlackWebApiClientImpl
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.nio.file.Paths
import javax.inject.Inject

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

        val file = Paths.get(project.rootDir.absolutePath, filePath).toFile()
        val slack = SlackWebApiClientImpl(token)

        try {
            slack.uploadFile(
                file,
                file.extension,
                file.name,
                file.name,
                comment,
                channel
            )
        } catch (e: Exception) {
            throw Exception(e.cause)
        }
    }
}
