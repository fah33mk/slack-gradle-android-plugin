package io.github.fah33mk.slack

import org.gradle.api.Plugin
import org.gradle.api.Project

class SlackUploaderPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.tasks.register("uploadFileToSlack", SlackUploaderTask::class.java)
    }
}
