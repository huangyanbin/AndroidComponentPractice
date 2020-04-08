package com.david.app.rs_router_gradle

import com.android.build.api.transform.JarInput
import javassist.ClassPool
import javassist.CtClass
import javassist.CtMethod
import org.apache.commons.io.FileUtils
import org.gradle.api.Project

import java.util.jar.JarEntry
import java.util.jar.JarFile

class RouterInject {

    private final static ClassPool pool = ClassPool.getDefault()
    private static List componentList = []
    private static final String CC_PACKAGE = 'com.david.app.cc'
    private static final String CC_MANAGER = "CCManager"
    private static final String I_COMPONENT = "IComponent"
    public static final String SPOT = "."

    /**
     * 查找是否有IComponent接口
     */
    static boolean findIComponentInterface(String path){
        pool.appendClassPath(path)
        try{
            CtClass ctClass = pool.get("${CC_PACKAGE}${SPOT}${I_COMPONENT}")
            return  ctClass != null
        }catch(Exception ignored){
            return false
        }

    }

     static boolean inject(Project project, String path,String prefix){
        pool.appendClassPath(path)
        pool.appendClassPath(project.android.bootClasspath[0].toString())
        File dir = new File(path)
        //是否包含 CCManager
        boolean isContainCCManager = false
        if(dir.directory){
            //递归遍历
            dir.eachFileRecurse {file->
                String filePath = file.absolutePath
                println(filePath)
                def fileName = file.getName()
                if(fileName.endsWith(".class")){
                    if(fileName.contains("android") ||fileName == "R.class" || fileName == "BuildConfig.class" ){
                        return
                    }
                    int begin = filePath.indexOf(prefix) + prefix.length()
                    String className = null
                    if(begin >0){
                        className = filePath.substring(begin + 1,filePath.length()-6)
                        className = className.replaceAll("\\\\",".")
                    }
                    if(className != null && className != "" && !className.contains("\$") && !className.contains("android")){
                        println(className)
                        if(className == "${CC_PACKAGE}${SPOT}${CC_MANAGER}"){
                            isContainCCManager = true
                        }
                        CtClass ctClass = pool.getCtClass(className)
                        //解冻class
                        if(ctClass.isFrozen()){
                            ctClass.defrost()
                        }
                        try{
                            CtClass[] interfaces = ctClass.getInterfaces()
                            interfaces.each {interfaceClass->
                                if(interfaceClass.name ==  "${CC_PACKAGE}${SPOT}${I_COMPONENT}"){
                                    println("find className->${className}")
                                    componentList.add(className)
                                }
                            }
                            ctClass.detach()
                        }catch(Exception e){

                        }
                    }
                }
            }
        }
        return isContainCCManager
    }


    static regComponent(Project project, String path){
        pool.appendClassPath(path)
        pool.appendClassPath(project.android.bootClasspath[0].toString())
        CtClass ctClass = pool.getCtClass( "${CC_PACKAGE}${SPOT}${CC_MANAGER}")
        CtMethod initMethod = ctClass.getDeclaredMethod("init")
        StringBuilder builder = new StringBuilder()
        int position = 0
        componentList.each {component->
            builder.append("${CC_PACKAGE}${SPOT}${I_COMPONENT} component${position} = new ${component}();\n")
            builder.append(" this.componentMap.put(component${position}.getName(),component${position});\n")
            println("insert ->${component}")
            position++
        }

        initMethod.insertBefore(builder.toString())
        ctClass.writeFile(path)
        ctClass.detach()
    }
}