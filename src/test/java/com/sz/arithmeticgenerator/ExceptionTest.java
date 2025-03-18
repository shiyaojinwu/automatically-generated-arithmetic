package com.sz.arithmeticgenerator;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author zyh
 * @version 1.0.0
 * @date 2025/3/18
 */
@SpringBootTest
public class ExceptionTest {

    /**
     * 范围参数不合法
     * @author zyh
     * @date 2025/03/18
     */
    @Test
    public void testInvalidRangeParameter() {
        String[] args = {"-n", "10", "-r", "0"};
        ArithmeticGeneratorApplication.main(args);

        // 检查控制台输出是否包含错误信息
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));

        ArithmeticGeneratorApplication.main(args);
        // 验证错误信息
        String expectedErrorMessage = "处理命令行参数时出错: bound must be positive";
        assertTrue(errContent.toString().contains(expectedErrorMessage));
    }

    /**
     * 文件不存在
     * @author zyh
     * @date 2025/03/18
     */
    @Test
    public void testFileNotExist() {
        String[] args = {"-e", "NotExist.txt", "-a", "NotExist.txt"};
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent)); // 重定向标准错误流

        // 执行程序
        ArithmeticGeneratorApplication.main(args);

        // 验证错误信息
        String expectedErrorMessage = "Error reading files: NotExist.txt (系统找不到指定的文件。)";
        assertTrue(errContent.toString().contains(expectedErrorMessage));
    }

    /**
     * 参数格式错误
     * @author zyh
     * @date 2025/03/18
     */
    @Test
    public void testInvalidParameterFormat() {
        String[] args = {"-n", "abc", "-r", "10"};
        ArithmeticGeneratorApplication.main(args);

        // 检查控制台输出是否包含错误信息
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));

        ArithmeticGeneratorApplication.main(args);

        System.out.println(errContent);
    }

    /**
     * 参数缺失
     * @author zyh
     * @date 2025/03/18
     */
    @Test
    public void testMissingRequiredParameter() {
        String[] args = {"-n", "10"};
        ArithmeticGeneratorApplication.main(args);

        // 检查控制台输出是否包含帮助信息
        // 可以通过重定向 System.out 来捕获输出
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        ArithmeticGeneratorApplication.main(args);

        assertTrue(outContent.toString().contains("数学生成器"));
        assertTrue(outContent.toString().contains("生成的题目数量"));
        assertTrue(outContent.toString().contains("生成题目的数字范围"));
    }
}
