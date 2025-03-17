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
            // 使用索引遍历内容列表
            for (int i = 0; i < content.size(); i++) {
                // 构造带序号的行
                String lineWithNumber = (i + 1) + ". " + content.get(i);
                // 将带序号的行写入文件
                writer.write(lineWithNumber);
                // 添加换行符
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("文件写错误: " + filename + ": " + e.getMessage());
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