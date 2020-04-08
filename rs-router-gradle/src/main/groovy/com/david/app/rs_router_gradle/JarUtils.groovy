package com.david.app.rs_router_gradle;


import java.util.jar.JarEntry;
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry;

public class JarUtils {

    /**
     * 将Jar解压到指定目录
     * @param jarPath jar的绝对路径
     * @param destDirPath jar包解压后的保存路径
     */
    public static void unzipJar(String jarPath,String destDirPath){
        if(jarPath.endsWith(".jar")){
                JarFile jarFile = new JarFile(jarPath)
                Enumeration<JarEntry> jarEntries = jarFile.entries()
                while (jarEntries.hasMoreElements()){
                    JarEntry entry = jarEntries.nextElement()
                    if(entry.directory){
                        continue
                    }
                    String entryName = entry.getName()
                    String outFileName = destDirPath+"/"+entryName;
                    File outFile = new File(outFileName)
                    outFile.getParentFile().mkdirs()
                    InputStream inputStream = jarFile.getInputStream(entry)
                    FileOutputStream fileOutputStream = new FileOutputStream(outFile)
                    fileOutputStream << inputStream
                    fileOutputStream.close()
                    inputStream.close()
                }
            jarFile.close()

        }
    }

    /**
     * 重新打包
     * @param packagePath 将这个目录下面所有文件打包jar
     * @param destPath 打包好的jar的绝对路径
     */
     static void zipJar(String packagePath,String destPath){
        File file = new File(packagePath)
         JarOutputStream outputStream = new JarOutputStream(new FileOutputStream(destPath))
         file.eachFileRecurse {File f->
             String entryName = f.getAbsolutePath().substring(file.absolutePath.length()+1)
             outputStream.putNextEntry(new ZipEntry(entryName))
             if(!f.directory){
                 InputStream inputStream = new FileInputStream(f)
                 outputStream << inputStream
                 inputStream.close()
             }
         }
         outputStream.close()
    }
}
