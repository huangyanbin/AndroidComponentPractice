package com.david.app.rs_router_gradle


import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.Format
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.gradle.api.Project

class RouterTransform extends Transform{

    private Project mProject


    RouterTransform(Project project){
        this.mProject = project
    }

    @Override
    String getName() {
        return 'SmartRouterTransform'
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        long time = System.currentTimeMillis()
        transformInvocation.outputProvider.deleteAll()
        //遍历input
        DirectoryInput containCCDirInput = null
        JarInput containCCJarInput = null
        String containCCJarDestPath = null
        transformInvocation.inputs.each { input ->
            //遍历文件夹
            input.directoryInputs.each { dirInput->
                 RouterInject.findIComponentInterface(dirInput.file.absolutePath)
            }
            //遍历jar
            int index = 0
            input.jarInputs.each {jarInput->
                String destPath = "${jarInput.file.parent}/tmp/${index}"
                new File(destPath).deleteOnExit()
                JarUtils.unzipJar(jarInput.file.absolutePath,destPath)
                File destFile = new File(destPath)
                RouterInject.findIComponentInterface(destFile.absolutePath)
                index++
            }
        }
        transformInvocation.inputs.each { input ->
           //遍历文件夹
           input.directoryInputs.each { dirInput->
               println("dir->${dirInput.name}")
                boolean isContainCCManager =  RouterInject.inject(mProject,dirInput.file.absolutePath,"classes")
               if(!isContainCCManager){
                   newOutputDirectory(transformInvocation, dirInput)
               }else{
                   containCCDirInput = dirInput
               }
           }
            //遍历jar
            int position = 0
           input.jarInputs.each {jarInput->
               println("jar->${jarInput.file.absolutePath}")
               String destPath = "${jarInput.file.parent}/tmp/${position}"
               File destFile = new File(destPath)
               boolean isContainCCManager =  RouterInject.inject(mProject,destFile.absolutePath,destPath+"/")
               if(!isContainCCManager){
                   String zipJarPath = destPath+".jar"
                   JarUtils.zipJar(destPath,zipJarPath)
                   File newJarFile = new File(zipJarPath)
                   newOutputJar(transformInvocation, jarInput,newJarFile)
               }else{
                   containCCJarDestPath = destPath
                   containCCJarInput = jarInput
               }
               position++
           }
       }
        //如果存在CCManager dir最后修改CCManager
        if(containCCDirInput != null){
            RouterInject.regComponent(mProject,containCCDirInput.file.absolutePath)
            newOutputDirectory(transformInvocation,containCCDirInput)
        }
        //如果存在CCManager class jar最后修改CCManager
        if(containCCJarInput != null){
            File containCCJarDestFile = new File(containCCJarDestPath)
            RouterInject.regComponent(mProject,containCCJarDestFile.absolutePath)
            String zipJarPath = containCCJarDestPath+".jar"
            JarUtils.zipJar(containCCJarDestPath,zipJarPath)
            File newJarFile = new File(zipJarPath)
            newOutputJar(transformInvocation, containCCJarInput,newJarFile)
        }
        long lastTime = System.currentTimeMillis()
        println("the smartRouter transform cost time ${(lastTime-time)/1000}s")
    }

    private void newOutputJar( TransformInvocation transformInvocation,JarInput jarInput,File jarFile) {
        def jarName = jarInput.name
        def md5Name = DigestUtils.md5Hex(jarInput.file.absolutePath)
        if (jarName.endsWith(".jar")) {
            jarName = jarName.substring(0, jarName.length() - 4)
        }
        def dest = transformInvocation.outputProvider.getContentLocation(jarName + md5Name,
                jarInput.contentTypes, jarInput.scopes, Format.JAR)
        FileUtils.copyFile(jarFile, dest)
    }

    private void newOutputDirectory(TransformInvocation transformInvocation, DirectoryInput dirInput) {
        def dest = transformInvocation.outputProvider.getContentLocation(dirInput.name
                , dirInput.contentTypes, dirInput.scopes, Format.DIRECTORY)
        FileUtils.copyDirectory(dirInput.file, dest)
    }
}