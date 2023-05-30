package com.faheem.gradle.slack

import org.gradle.api.Plugin
import org.gradle.api.Project

class SlackUploaderPlugin : Plugin<Project> {

    @Suppress("UnstableApiUsage") // create() is incubating
    override fun apply(project: Project) {
        project.tasks.create("uploadFileToSlack", SlackUploaderTask::class.java)
    }
}
