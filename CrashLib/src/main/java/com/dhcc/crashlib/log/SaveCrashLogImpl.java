package com.dhcc.crashlib.log;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author jasoncool
 * 将异常信息保存至本地文件的实现类
 */
public class SaveCrashLogImpl {

    @NonNull
    private  Handler handler;

     SaveCrashLogImpl(@NonNull Handler handler) {
        this.handler = checkNotNull(handler);
    }

    /**
     * 存储崩溃信息
     * @param level ObtainMessage的msg.what
     * @param message 崩溃信息
     * @param iFileCloseListener 文件写入完毕的回调接口
     */
    public void saveCrashLog(int level, @NonNull String message,IFileCloseListener iFileCloseListener) {
        checkNotNull(message);
        CrashLogBean logBean=new CrashLogBean();
        logBean.setMessage(message);
        logBean.setiFileCloseListener(iFileCloseListener);
        handler.sendMessage(handler.obtainMessage(level, logBean));
    }

    /**
     * 防止内存泄漏的静态handler
     */
    static class WriteHandler extends Handler  {
        private final String folder;
        private final int maxFileSize;
        IFileCloseListener iFileCloseListener;

        /**
         * 构造方法
         * @param looper 传入的looper
         * @param folder 文件夹名
         * @param maxFileSize 单个崩溃日志文件大小
         */
        WriteHandler(@NonNull Looper looper, @NonNull String folder, int maxFileSize) {
            super(checkNotNull(looper));
            this.folder = checkNotNull(folder);
            this.maxFileSize = maxFileSize;
        }
        @Override
        public void handleMessage(Message msg) {
            CrashLogBean logBean=(CrashLogBean) msg.obj;
            String content = logBean.getMessage();
            iFileCloseListener=logBean.getiFileCloseListener();
            FileWriter fileWriter = null;
            File logFile = getLogFile(folder, CrashLogConfig.LOG_FILE_NAME);

            try {
                fileWriter = new FileWriter(logFile, true);
                writeLogFile(fileWriter, content);
                fileWriter.flush();
                fileWriter.close();

            } catch (IOException e) {
                if (fileWriter != null) {
                    try {
                        fileWriter.flush();
                        fileWriter.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }finally {
                iFileCloseListener.onFileClose(content,logFile);
            }
        }

        /**
         * 写入日志文件
         * @param fileWriter 文件写入
         * @param content    文件内容
         */
        void writeLogFile(@NonNull FileWriter fileWriter, @NonNull String content){
            checkNotNull(fileWriter);
            checkNotNull(content);
            try{
                fileWriter.append("\n").append(content);
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        /**
         * 获取崩溃日志文件
         * @param floderName 文件夹名
         * @param fileName 文件名
         * @return 文件实例
         */
        File getLogFile(@NonNull String floderName, @NonNull String fileName) {

            File folder = new File(floderName);
            if (!folder.exists()) {
                if (!folder.mkdirs()) {
                    Log.println(Log.ERROR, "saveLog", "文件未创建成功，可能是读写权限没给");
                }
            }
            int newFileCount = 0;
            File newFile;
            File existingFile = null;

            newFile = new File(folder, String.format("%s_%s.txt", fileName, newFileCount));
            while (newFile.exists()) {
                existingFile = newFile;
                newFileCount++;
                newFile = new File(folder, String.format("%s_%s.txt", fileName, newFileCount));
            }

            if (existingFile != null) {
                if (existingFile.length() >= maxFileSize) {
                    return newFile;
                }
                return existingFile;
            }

            return newFile;
        }

    }

    /**
     * 检查对象是否为空
     * @param value 传入对象
     * @param <T> 返回对象类型本身
     * @return 返回值
     */
    private static <T>T checkNotNull(T value) {
        if (value == null) {
            throw new NullPointerException();
        }
        return value;
    }

    /**
     * 异常信息写入完毕的回调接口
     */
    public interface  IFileCloseListener{
        /**
         * @param content 异常信息
         * @param file 异常文件
         */
        void onFileClose(String content, File file);
    }

}
