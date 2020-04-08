package com.david.app.rs_router_gradle

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

 class RouterPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        println("SmartRouterPluginGroovy start")
        def android = project.extensions.getByType(AppExtension)
        def classTransform = new RouterTransform(project)
        android.registerTransform(classTransform)
    }
}