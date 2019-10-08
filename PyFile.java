package com.example.demo.jython;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * @author luwt
 * @date 2019/9/3.
 */
public class PyFile {

    // py文件路径集合
    static final String[] JY_PATH = {"com/example/demo/python/jy_test.py"};


    /**
     * 检测指定目录是否有需要的py文件：若无，悉数拷贝，并返回主入口文件地址(目前暂定，为数组第一个)
     * @author luwt
     * @date
     * @param
     * @return
     */
    public String copyFiles(String... paths){
        String[] mainPath = new String[paths.length];
        for (int i = 0; i < paths.length; i++) {
            mainPath[i] = copyFile(paths[i]);
        }
        return mainPath[0];
    }


    /**
     * 检测指定目录是否有需要的py文件：若无，拷贝；若有，返回文件路径
     * @author luwt
     * @date
     * @param
     * @return
     */
    private String copyFile(String path){
        String fileName = new File(path).getName();
        String newPath = getNewPath(fileName);
        if (new File(fileName).exists())
            return newPath;
        try {
            BufferedReader in = new BufferedReader(getInputStreamReader(path));
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(newPath), StandardCharsets.UTF_8)
            );
            String line = "";
            while ((line = in.readLine()) != null) {
                bufferedWriter.append(line);
                bufferedWriter.newLine();
            }
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e){
            e.printStackTrace();
        }
        return newPath;
    }

    /**
     * 拼接新路径
     * @author luwt
     * @date
     * @param
     * @return
     */
    private String getNewPath(String fileName){
        // 获取当前工作路径
        String dir = System.getProperty("user.dir");
        return dir + File.separator + fileName;
    }

    /**
     *  打开文件输入流
     * @author luwt
     * @date
     * @param
     * @return
     */
    private InputStreamReader getInputStreamReader(String path) throws IOException{
        URL url = this.getClass().getClassLoader().getResource(path);
        if (url == null) {
            throw new FileNotFoundException("文件路径找不到");
        }
        return new InputStreamReader(url.openStream(), StandardCharsets.UTF_8);
    }


}
