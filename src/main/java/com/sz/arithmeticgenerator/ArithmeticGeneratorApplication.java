package com.sz.arithmeticgenerator;

import com.sz.arithmeticgenerator.service.AnswerEvaluator;
import com.sz.arithmeticgenerator.service.QuestionGenerator;
import com.sz.arithmeticgenerator.util.FileUtil;
import org.apache.commons.cli.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class ArithmeticGeneratorApplication {
    public static void main(String[] args) {
        SpringApplication.run(ArithmeticGeneratorApplication.class, args);

        Options options = new Options();
        options.addOption("n", true, "生成的题目数量");
        options.addOption("r", true, "生成题目的数字范围");
        options.addOption("e", true, "题目文件");
        options.addOption("a", true, "答案文件");

        // 创建命令行解析器
        CommandLineParser parser = new DefaultParser();
        try {
            // 解析命令行参数
            CommandLine cmd = parser.parse(options, args);

            // 如果指定了“n”和“r”选项，生成题目和答案
            if (cmd.hasOption("n") && cmd.hasOption("r")) {
                // 获取题目数量
                int numQuestions = Integer.parseInt(cmd.getOptionValue("n"));
                // 获取数字范围
                int range = Integer.parseInt(cmd.getOptionValue("r"));
                // 生成题目
                List<String> questions = QuestionGenerator.generateQuestions(numQuestions, range);
                // 评估题目的答案
                List<String> answers = AnswerEvaluator.evaluateQuestions(questions);
                // 将题目写入文件
                FileUtil.writeToFile("Exercises.txt", questions);
                // 将答案写入文件
                FileUtil.writeToFile("Answers.txt", answers);
                // 输出提示信息
                System.out.println("答案文件和题目文件生成成功");
            } else if (cmd.hasOption("e") && cmd.hasOption("a")) {
                // 如果指定了“e”和“a”选项，进行答案评分
                // 获取题目文件名
                String exerciseFile = cmd.getOptionValue("e");
                // 获取答案文件名
                String answerFile = cmd.getOptionValue("a");
                // 评分
                String result = AnswerEvaluator.gradeAnswers(exerciseFile, answerFile);
                // 将评分结果写入文件
                FileUtil.writeToFile("Grade.txt", result);
                System.out.println("评分完成");
            } else {
                // 如果命令行参数不正确，显示帮助信息
                // 创建命令行帮助格式化器
                HelpFormatter formatter = new HelpFormatter();
                // 打印帮助信息
                formatter.printHelp("数学生成器", options);
            }
        } catch (Exception ex) {
            System.err.println("处理命令行参数时出错: " + ex.getMessage());
        }
    }
}