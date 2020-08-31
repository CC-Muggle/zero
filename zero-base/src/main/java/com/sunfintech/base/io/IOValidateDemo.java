package com.sunfintech.base.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * IO流操作
 * 2.IO流底层原理验证 
 *     2.1IO流是将本地JNI进行封装(Windows下是使用C进行操作),
 *     2.2 IO流使用装饰器模式,使得原有的IO接口能配合其他的特殊属性使用.
 *     2.3IO流线程不安全,因为所有线程使用的是统一内存区域,且会破坏单个文件的完整性 
 *     2.4 不能使用volatile关键字修饰,因为无法保证其原子性
 *     
 * @author coddl
 *
 */
public class IOValidateDemo {

    public static void main(String[] args) {
        validateIOThreadSafe();
    }

    /**
     * 验证IO流是否线程安全 结论:IO并非线程安全(并非真正意义上的)
     * 原因:由于IO流操作的是同一块内存缓冲区,在执行flush时,会将缓冲区清空,则会把其他文件读写的内容也从私有的工作空间提交到主内存
     * 
     */
    private static void validateIOThreadSafe() {
        new Thread(() -> {
            try (InputStream fileInput = new FileInputStream(new File("D:/征信报文0612(1).csv"));
                    OutputStream fileOutput = new FileOutputStream(new File("D:/征信报文0612--副本.csv"));) {
                byte[] bytes = new byte[1024];
                StringBuffer stringBuffer = new StringBuffer();
                while (fileInput.read(bytes) != -1) {
                    stringBuffer.append(new String(bytes));
                    fileOutput.write(bytes);
                }
                fileOutput.flush();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }).start();;

        new Thread(() -> {
            try (InputStream fileInput = new FileInputStream(new File("D:/json样本(1).csv"));
                    OutputStream fileOutput = new FileOutputStream(new File("D:/json样本(1)--副本.csv"));) {
                byte[] bytes = new byte[1024];
                StringBuffer stringBuffer = new StringBuffer();
                while (fileInput.read(bytes) != -1) {
                    stringBuffer.append(new String(bytes));
                    fileOutput.write(bytes);
                }
                fileOutput.flush();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }).start();;
    }
}
