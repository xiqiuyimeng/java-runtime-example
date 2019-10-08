package com.example.demo.jython;

import java.nio.file.NoSuchFileException;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 提供获取当前运行环境下pyCmd和py入口文件地址的方法。
 * 为提升性能，对pyCmd及pyPath进行缓存。
 * pyCmd：运行python脚本的前置程序别名（python、python3等），目前只检测这两种。
 * pyPath：要运行的py脚本入口文件，该文件实现路由分发功能。由于当前脚本在jar包内，
 *      无法直接在runtime下运行，故拷贝到指定目录后运行。
 * @author luwt
 * @date 2019/9/7.
 */
public class PyEnvironment {

    // 当前环境必须为python3
    private static final String PYTHON3_PATTERN = "^Python 3\\.*";

    private static final String[] PYTHON_COMMANDS = {"python", "python3"};

    private static String PYTHON3_COMMAND;

    private static String[] WINDOWS_CMD = {"cmd", "/c"};

    private static String[] LINUX_SHELL = {"/bin/sh", "-c"};


    String getPyCmd(){
        return PyCache.pyCommand;
    }

    String getPyPath() {
        return PyCache.pyPath;
    }


    /**
     *  检测py环境，处理并缓存以备用
     * @author luwt
     * @date
     * @param
     * @return
     */
    private static class PyCache {

        static final String pyCommand;

        static final String pyPath;

        static {
            try {
                // 检测py版本，确定pyCmd并缓存之
                checkPythonVersion();
                System.out.println("检测后的pyCmd：" + PYTHON3_COMMAND);
            } catch (Exception e){
                if (e instanceof NoSuchFileException) {
                    // 可能要再进一步处理
                    System.out.println(e.getMessage());
                } else {
                    System.out.println(e.getMessage());
                }
            }
            pyCommand = PYTHON3_COMMAND;

            // 检测py文件是否存在，不存在则拷贝，并返回py入口文件地址，缓存之
            PyFile pyFile = new PyFile();
            String path = pyFile.copyFiles(PyFile.JY_PATH);
            System.out.println("复制的文件路径为：" + path);
            pyPath = path;
        }

    }


    // 验证python版本，只有满足3才可以，如果输入python -V，运行成功，分析结果，
    // 匹配是否为3.x，否则尝试python3 -V，若不满足抛异常
    private static void checkPythonVersion() throws Exception {
        for (String pyCommand : PYTHON_COMMANDS) {
            String[] command = getPyVersionCommand();
            Map<String, String> execRes = ExecutePythonScript.executePyScript(new String[]{
                    command[0], command[1], pyCommand + " -V"
            }, false);
            if (execRes.get("code").equals(String.valueOf(0))) {
                // 获取执行结果判断
                String pyVersionRes = execRes.get("data");
                if (Pattern.compile(PYTHON3_PATTERN).matcher(pyVersionRes).lookingAt()) {
                    PYTHON3_COMMAND = pyCommand;
                    break;
                }
            }
        }
        if (PYTHON3_COMMAND == null) {
            throw new NoSuchFileException("未检测到合适的python环境");
        }
    }

    private static String[] getPyVersionCommand(){
        String os = System.getProperty("os.name");
        if (os.toLowerCase().startsWith("win")) {
            return WINDOWS_CMD;
        } else {
            return LINUX_SHELL;
        }
    }


}
