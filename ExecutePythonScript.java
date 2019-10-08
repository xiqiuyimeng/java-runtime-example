package com.example.demo.jython;

import org.apache.tomcat.jni.Time;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 执行py脚本
 * 在第一次执行时，会做一些环境准备工作，尽可能的保证py代码运行成功。
 * @author luwt
 * @date 2019/9/4.
 */
public class ExecutePythonScript {


    public Map<String, String> executePyScript(String[] params){
        // 执行脚本前准备工作：先进行py版本检测，只有py3满足，
        // 将检测成功的py命令（python、python3）缓存常量，为了之后每次执行命令保证正确
        // 同时检测指定目录下py文件是否存在，不存在就复制一份
        // 真正执行脚本
        Map<String, String> result = new HashMap<>();
        PyEnvironment pyEnvironment = new PyEnvironment();
        String[] pyCmd = reorganizeArray(new String[]{pyEnvironment.getPyCmd(), pyEnvironment.getPyPath()}, params);
        try {
            result = executePyScript(pyCmd, true);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        return result;
    }

    /**
     * 数组重组
     * @author luwt
     * @date
     * @param
     * @return
     */
    private String[] reorganizeArray (String[] pys, String... params){
        // 新数组：可变参数(系统检测的pyCmd和文件路径)在前，原数组内容（运行参数）悉数在后
        String[] newArray = new String[pys.length + params.length];
        System.arraycopy(pys, 0, newArray, 0, pys.length);
        System.arraycopy(params, 0, newArray, pys.length, params.length);
        return newArray;
    }


    /**
     *  执行python脚本，并获取标准输出
     *  开启线程接收异常输出
     * @author luwt
     * @date
     * @param
     * @return
     */
    public static Map<String, String> executePyScript(String[] cmd, Boolean catchErrorStreamEx) throws Exception{
        Map<String, String> execResult = new HashMap<>();
        BufferedReader bufferedReader;
        Process process = Runtime.getRuntime().exec(cmd);
        // 在当前方法获取标准输出流
        BufferedInputStream bis = new BufferedInputStream(process.getInputStream());
        bufferedReader = new BufferedReader(new InputStreamReader(bis, StandardCharsets.UTF_8));
        // 读取结果，暂时先放在字符串中，后续如果有问题再修改
        String result = getExecResult(bufferedReader);
        // 执行结果 0成功
        int res = process.waitFor();
        execResult.put("code", String.valueOf(res));
        execResult.put("data", result);
        bufferedReader.close();
        if (catchErrorStreamEx) {
            // 为异常输出流单独开线程读取，否则可能会造成标准输出流的阻塞
//            Thread t = new Thread(new InputStreamRunnable(process.getErrorStream()));
//            t.start();
            // 暂时先不开启线程，由于可能存在线程执行顺序导致process先关闭，子线程获取不到流报错
            new InputStreamRunnable(process.getErrorStream()).run();
        }
        process.destroy();
        return execResult;
    }


    /**
     * 接收执行结果
     * @author luwt
     * @date
     * @param
     * @return
     */
    private static String getExecResult(BufferedReader bufferedReader) throws IOException {
        StringBuffer buffer = new StringBuffer();
        String line;

        while ((line = bufferedReader.readLine()) != null) {
            buffer.append(line);
            buffer.append("\n");
        }
        return buffer.toString();
    }

}
