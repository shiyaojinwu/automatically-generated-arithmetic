package com.sz.arithmeticgenerator.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * 文件读写工具类
 * @author zyh
 * @version 1.0.0
 * @date 2025/3/16
 */
public class FileUtil {

    /**
     * 将内容写入文件
     * @param filename 文件名
     * @param content  内容
     * @author zyh
     * @date 2025/03/16
     */
    public static void writeToFile(String filename, List<String> content) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            // 遍历内容列表
            for (String line : content) {
                // 将当前行内容写入文件
                writer.write(line);
                // 在写入的内容后添加换行符
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("文件写错误: " + filename + e);
        }
    }

    /**
     * 将内容写入文件
     * @param filename 文件名
     * @param content  内容
     * @author zyh
     * @date 2025/03/16
     */
    public static void writeToFile(String filename, String content) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            // 将整个内容写入文件
            writer.write(content);
        } catch (IOException e) {
            System.out.println("文件写错误: " + filename + e);
        }
    }
}