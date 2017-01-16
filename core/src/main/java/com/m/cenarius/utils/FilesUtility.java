package com.m.cenarius.utils;

import android.content.Context;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by M on 16/9/9.
 * <p/>
 * 文件系统工具类
 */
public class FilesUtility {

    /**
     * 删除
     *
     * @param pathToFileOrDirectory 文件/目录的路径
     */
    public static void delete(String pathToFileOrDirectory) {
        delete(new File(pathToFileOrDirectory));
    }

    /**
     * 删除
     *
     * @param fileOrDirectory 文件/目录 对象
     */
    public static void delete(File fileOrDirectory) {
        if (!fileOrDirectory.exists()) {
            return;
        }

        if (fileOrDirectory.isDirectory()) {
            File[] filesList = fileOrDirectory.listFiles();
            for (File child : filesList) {
                delete(child);
            }
        }

        final File to = new File(fileOrDirectory.getAbsolutePath() + System.currentTimeMillis());
        fileOrDirectory.renameTo(to);
        to.delete();

        //fileOrDirectory.delete();
    }

    /**
     * 创建文件夹
     *
     * @param dirPath 路径
     */
    public static void ensureDirectoryExists(String dirPath) {
        ensureDirectoryExists(new File(dirPath));
    }

    /**
     * 创建文件夹
     *
     * @param dir 文件对象
     */
    public static void ensureDirectoryExists(File dir) {
        if (dir != null && (!dir.exists() || !dir.isDirectory())) {
            dir.mkdirs();
        }
    }

    /**
     * 拷贝
     *
     * @param src 源路径
     * @param dst 目的路径
     * @throws IOException
     */
    public static void copy(String src, String dst) throws IOException {
        copy(new File(src), new File(dst));
    }

    /**
     * 拷贝
     *
     * @param src 源文件
     * @param dst 目标文件
     * @throws IOException
     */
    public static void copy(File src, File dst) throws IOException {
        if (src.isDirectory()) {
            ensureDirectoryExists(dst);

            String[] filesList = src.list();
            for (String file : filesList) {
                File srcFile = new File(src, file);
                File destFile = new File(dst, file);

                copy(srcFile, destFile);
            }
        } else {
            copyFile(src, dst);
        }
    }

    private static void copyFile(File fromFile, File toFile) throws IOException {
        InputStream in = new BufferedInputStream(new FileInputStream(fromFile));
        OutputStream out = new BufferedOutputStream(new FileOutputStream(toFile));

        // Transfer bytes from in to out
        byte[] buf = new byte[8192];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }

        in.close();
        out.close();
    }

    /**
     * 读取
     *
     * @param filePath 文件路径
     * @return data from file
     * @throws IOException
     */
    public static String readFromFile(String filePath) throws IOException {
        return readFromFile(new File(filePath));
    }

    /**
     * 读取
     *
     * @param file 文件
     * @return data from file
     * @throws IOException
     */
    public static String readFromFile(File file) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

        StringBuilder content = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            content.append(line).append("\n");
        }

        bufferedReader.close();

        return content.toString().trim();
    }

    /**
     * 保存
     *
     * @param content  需要保存的数据
     * @param filePath 文件路径
     * @throws IOException
     */
    public static void writeToFile(String content, String filePath) throws IOException {
        writeToFile(content, new File(filePath));
    }

    /**
     * 保存
     *
     * @param content 需要保存的数据
     * @param dstFile 目的
     * @throws IOException
     */
    public static void writeToFile(String content, File dstFile) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(dstFile, false));

        bufferedWriter.write(content);

        bufferedWriter.close();
    }

    /**
     * 计算 MD5 hash
     *
     * @param filePath 文件路径
     * @return calculated hash
     * @throws Exception
     * @see MD5
     */
    public static String calculateFileHash(String filePath) throws Exception {
        return calculateFileHash(new File(filePath));
    }

    /**
     * 计算 MD5 hash
     *
     * @param file 文件
     * @return hash
     * @throws Exception
     * @see MD5
     */
    public static String calculateFileHash(File file) throws Exception {
        MD5 md5 = new MD5();
        InputStream in = new BufferedInputStream(new FileInputStream(file));

        int len;
        byte[] buff = new byte[8192];
        while ((len = in.read(buff)) > 0) {
            md5.write(buff, len);
        }

        return md5.calculateHash();
    }

    /**
     * 解压assets的zip压缩文件到指定目录
     * @param context 上下文对象
     * @param assetName 压缩文件名
     * @param outputDirectory 输出目录
     * @param isReWrite 是否覆盖
     * @throws IOException
     */
    public static void unZip(Context context, String assetName,
                             String outputDirectory,boolean isReWrite) throws IOException {
        //创建解压目标目录
        File file = new File(outputDirectory);
        //如果目标目录不存在，则创建
        if (!file.exists()) {
            file.mkdirs();
        }
        //打开压缩文件
        InputStream inputStream = context.getAssets().open(assetName);
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        //读取一个进入点
        ZipEntry zipEntry = zipInputStream.getNextEntry();
        //使用1Mbuffer
        byte[] buffer = new byte[1024 * 1024];
        //解压时字节计数
        int count = 0;
        //如果进入点为空说明已经遍历完所有压缩包中文件和目录
        while (zipEntry != null) {
            //如果是一个目录
            if (zipEntry.isDirectory()) {
                file = new File(outputDirectory + File.separator + zipEntry.getName());
                //文件需要覆盖或者是文件不存在
                if(isReWrite || !file.exists()){
                    file.mkdir();
                }
            } else {
                //如果是文件
                file = new File(outputDirectory + File.separator
                        + zipEntry.getName());
                //文件需要覆盖或者文件不存在，则解压文件
                if(isReWrite || !file.exists()){
                    file.createNewFile();
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    while ((count = zipInputStream.read(buffer)) > 0) {
                        fileOutputStream.write(buffer, 0, count);
                    }
                    fileOutputStream.close();
                }
            }
            //定位到下一个文件入口
            zipEntry = zipInputStream.getNextEntry();
        }
        zipInputStream.close();
    }

}