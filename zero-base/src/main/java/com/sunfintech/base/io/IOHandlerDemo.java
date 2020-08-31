package com.sunfintech.base.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

/**
 * IO流操作
 * 1.IO流实现文件读写 
 *     1.1 FileInputStream{@link FileInputStream}与FileOutputStream {@link FileOutputStream} 
 *     1.2 FileWirter {@link FileWriter}与FileReader {@link FileReader} 
 *     1.3 流转换器InputStreamReader {@link InputStreamReader}与OutputStreamWriter
 *     1.4 缓冲流BufferedWirter,BufferInputStream,BufferedReader,BufferOutputStream {@link OutputStreamWriter} 
 *     1.5 装饰器模式下的IO流操作 
 * 
 * @author yangcj
 *
 */
public class IOHandlerDemo {

    public static void main(String[] args) {
        copyTextFile();

    }

    /**
     * 使用wirter和reader进行文本文件的复制
     * 
     * 为何Writer和reader相较于inputstream和outputstream差距不大,因为底层都是引用inputstream和outputstream
     * 将inputstream和outputstream通过流转换器转换成wirter和reader在进行读写
     * 
     * 特别说明,中文字符占两个字节,英文字符只占一个字节,所以会导致字符流可以正确读取带中文的文件而字节流却很困难
     * 一版UTF-8编码的文件没有感觉,一旦出现GBK或ISO此类的编码就原地爆炸
     * 
     * 缓冲流是相较于原有的要快
     * 值得一提就是,缓冲流是线程安全的,read方法是被修饰过的,但是我们自己操作本省是不安全的,所以还需要加同步块
     * 
     * 性能测试
     * 字符流::::::::文件复制消耗时间---------------------开始---------------------:0
     * 字符流::::::::文件复制消耗时间---------------------结束---------------------:129
     * 字节流::::::::文件复制消耗时间---------------------开始---------------------:0
     * 字节流::::::::文件复制消耗时间---------------------结束---------------------:77
     * 缓冲字符流::::::::文件复制消耗时间---------------------开始---------------------:0
     * 缓冲字符流::::::::文件复制消耗时间---------------------结束---------------------:30
     * 缓冲字节流::::::::文件复制消耗时间---------------------开始---------------------:0
     * 缓冲字节流::::::::文件复制消耗时间---------------------结束---------------------:14
     * 
     */
    private static void copyTextFile() {
        // 字符流
        copyTextReaderAndWriter();
        
        // 字节流
        copyTextInputAndOutput();
        
        //字符缓冲流
        copyTextBufferedReaderAndWriter();
        
        //字节缓冲流
        copyTextBufferedInputAndOutput();
    }

    
    private static void copyTextBufferedInputAndOutput() {
        String sourceFilePath = null;
        String targetFilePath = null;
        long start = System.currentTimeMillis();
        System.out.println("缓冲字节流::::::::文件复制消耗时间---------------------开始---------------------:" + 0);
        
        sourceFilePath = "D:\\workspace\\zero\\zero-base\\file\\input\\test2.txt";
        targetFilePath = "D:\\workspace\\zero\\zero-base\\file\\output\\testCopy2.txt";
        
        try(InputStream inputStream = new FileInputStream(sourceFilePath);
                OutputStream outputStream = new FileOutputStream(targetFilePath);
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream)) {
            
            byte[] bytes = new byte[1024];
            int length = 0;
            while ((length = bufferedInputStream.read(bytes)) > 0) {
                bufferedOutputStream.write(bytes, 0, length);
            }
            
            bufferedOutputStream.flush();
            outputStream.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        System.out.println("缓冲字节流::::::::文件复制消耗时间---------------------结束---------------------:" + (System.currentTimeMillis() - start));
    }

    private static void copyTextBufferedReaderAndWriter() {
        String sourceFilePath;
        String targetFilePath;
        long start = System.currentTimeMillis();
        System.out.println("缓冲字符流::::::::文件复制消耗时间---------------------开始---------------------:" + 0);

        sourceFilePath = "D:\\workspace\\zero\\zero-base\\file\\reader\\test2.txt";
        targetFilePath = "D:\\workspace\\zero\\zero-base\\file\\writer\\testCopy2.txt";

        // 流创建要开启,使用完成要关闭,否则会内存溢出
        try (Reader reader = new FileReader(sourceFilePath); 
                Writer writer = new FileWriter(targetFilePath);
                BufferedReader bufferedReader = new BufferedReader(reader); 
                BufferedWriter bufferedWriter = new BufferedWriter(writer)) {
            char[] chars = new char[1024];
            int length = 0;
            while ((length = bufferedReader.read(chars)) != -1) {
                bufferedWriter.
                write(chars, 0, length);
            }
            
            // 清空缓冲区,不清空会导致外部内存使用的引用疯涨直至吃尽所有内存
            bufferedWriter.flush();
            writer.flush();
        } catch (FileNotFoundException e) {
            // 未发现文件异常
            e.printStackTrace();
        } catch (IOException e) {
            // IO流统一异常
            e.printStackTrace();
        }
        System.out.println("缓冲字符流::::::::文件复制消耗时间---------------------结束---------------------:" + (System.currentTimeMillis() - start));
    }

    /**
     * 
     */
    private static void copyTextInputAndOutput() {
        String sourceFilePath = null;
        String targetFilePath = null;
        long start = System.currentTimeMillis();
        System.out.println("字节流::::::::文件复制消耗时间---------------------开始---------------------:" + 0);
        
        sourceFilePath = "D:\\workspace\\zero\\zero-base\\file\\input\\test1.txt";
        targetFilePath = "D:\\workspace\\zero\\zero-base\\file\\output\\testCopy1.txt";
        
        try(InputStream inputStream = new FileInputStream(sourceFilePath);OutputStream outputStream = new FileOutputStream(targetFilePath);) {
            
            byte[] bytes = new byte[1024];
            int length = 0;
            while ((length = inputStream.read(bytes)) > 0) {
                outputStream.write(bytes, 0, length);
            }
            
            outputStream.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        System.out.println("字节流::::::::文件复制消耗时间---------------------结束---------------------:" + (System.currentTimeMillis() - start));
    }

    private static void copyTextReaderAndWriter() {
        String sourceFilePath;
        String targetFilePath;
        long start = System.currentTimeMillis();
        System.out.println("字符流::::::::文件复制消耗时间---------------------开始---------------------:" + 0);

        sourceFilePath = "D:\\workspace\\zero\\zero-base\\file\\reader\\test1.txt";
        targetFilePath = "D:\\workspace\\zero\\zero-base\\file\\writer\\testCopy1.txt";

        // 流创建要开启,使用完成要关闭,否则会内存溢出
        try (Reader reader = new FileReader(sourceFilePath); Writer writer = new FileWriter(targetFilePath);) {
            char[] chars = new char[1024];
            int length = 0;
            while ((length = reader.read(chars)) != -1) {
                writer.write(chars, 0, length);
            }
            
            // 清空缓冲区,不清空会导致外部内存使用的引用疯涨直至吃尽所有内存
            writer.flush();
        } catch (FileNotFoundException e) {
            // 未发现文件异常
            e.printStackTrace();
        } catch (IOException e) {
            // IO流统一异常
            e.printStackTrace();
        }
        System.out.println("字符流::::::::文件复制消耗时间---------------------结束---------------------:" + (System.currentTimeMillis() - start));
    }

    
}
