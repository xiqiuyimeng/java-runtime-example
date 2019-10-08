package com.example.demo.jython;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * @author luwt
 * @date 2019/9/4.
 */
public class InputStreamRunnable implements Runnable {
    private static Logger logger = LoggerFactory.getLogger(InputStreamRunnable.class);

    private BufferedReader bufferedReader = null;

    public InputStreamRunnable(InputStream is) {
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(
                    new BufferedInputStream(is), StandardCharsets.UTF_8
            ));
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        String line;
        int num = 0;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                logger.error("==>" + String.format("%2d", num ++) + " " + line);
            }
            bufferedReader.close();
        } catch (Exception e) {
            logger.error(e.toString());
        }
    }
}
