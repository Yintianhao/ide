package com.mine.ide.util;

import com.sun.org.apache.xpath.internal.operations.Bool;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.PrintStream;

/**
 * @author yintianhao
 * @createTime 20190521 23:34
 * @description PrintStream
 */
public class HackPrintStream extends PrintStream {
    //每个线程的输出流
    private ThreadLocal<ByteArrayOutputStream> out;
    //标注输出流写入过程是否出现异常
    private ThreadLocal<Boolean> trouble;
    public HackPrintStream(){
        super(new ByteArrayOutputStream());
        out = new ThreadLocal<>();
        trouble = new ThreadLocal<>();
    }
    @Override
    public String toString(){
        return out.get().toString();
    }
    /**
     * 检测当前线程有没有stream对象，没有就new一个,否则抛出异常
     * */
    private void ensureOpen() throws IOException{
        if(out.get()==null)
            out.set(new ByteArrayOutputStream());
    }
    /**
     * 刷新Stream，通过写入任意字节流到底层输出流然后刷新该流
     * */
    public void flush(){
        try{
            ensureOpen();
            out.get().flush();
        }catch (IOException e){
            trouble.set(true);
        }
    }
    /**
     * 关闭流，关闭失败抛出IOException
     * */
    public void close(){
        try{
            out.get().close();
        }catch (IOException e){
            trouble.set(false);
        }
    }
    /**
     *刷新流并且检查错误状态，当底层输出流抛出IOEXception而不是InterruptedIOException的时候，
     *内部错误状态被设置为true。当setError()方法被调用时，如果底层输出流上的一个操作抛出InterruptedIOException
     *PrintStream会通过Thread.currentThread().interrupt()将异常转化为中断
     */
    public boolean checkError(){
        if(out.get()!=null){
            try{
                out.get().flush();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return trouble.get()!=null?trouble.get():false;
    }
    /**
     * 设置错误状态，在clearError()调用之前，这个方法一直影响checkError()
     * */
    protected void setError(){
        trouble.set(true);
    }
    /**
     * 清除内部错误，这将会导致checkError()方法返回false，直到另一个写操作失败或者setError()设置true
     * */
    protected void clearError(){
        trouble.remove();
    }
    public void write(int b){
        try{
            ensureOpen();
            out.get().write(b);
            if(b=='\n'){
                out.get().flush();
            }
        }catch (InterruptedIOException e){
            Thread.currentThread().interrupt();
        }
        catch (IOException e){
            //待商讨，IOException和InterruptedIOException(继承于IOException)
            trouble.set(true);
            //Thread.currentThread().interrupt();
        }
    }
    public void write(byte[] buf,int off,int len){
        try{
            ensureOpen();
            out.get().write(buf,off,len);
        }catch (InterruptedIOException e){
            Thread.currentThread().interrupt();
        }catch (IOException e){
            trouble.set(true);
        }
    }
    private void write(char[] buf){
        try{
            ensureOpen();
            out.get().write(new String(buf).getBytes());
        }catch (InterruptedIOException e){
            Thread.currentThread().interrupt();
        }catch (IOException e){
            trouble.set(true);
        }

    }
    private void write(String buf){
        try{
            ensureOpen();
            out.get().write(buf.getBytes());
        }catch (InterruptedIOException e){
            Thread.currentThread().interrupt();
        }catch (IOException e){
            trouble.set(true);
        }

    }
    private void newLine(){
        try {
            ensureOpen();
            //用System.lineSeparator()；剔除平台无关性
            out.get().write(System.lineSeparator().getBytes());
        }catch (InterruptedIOException e){
            Thread.currentThread().interrupt();
        }catch (IOException e){
            trouble.set(true);
        }
    }
    public void print(boolean b){
        write(b?"true":"false");
    }
    public void print(char c){
        write(String.valueOf(c));
    }
    public void print(int i){
        write(String.valueOf(i));
    }
    public void print(long l){
        write(String.valueOf(l));
    }
    public void print(float f){
        write(String.valueOf(f));
    }
    public void print(double d){
        write(String.valueOf(d));
    }
    public void print(char[] s){
        write(String.valueOf(s));
    }
    public void print(String s){
        write(s==null?"null":s);
    }
    public void print(Object o){
        write(String.valueOf(o));
    }
    public void println(){
        newLine();
    }
    public void println(boolean x) {
        print(x);
        newLine();
    }

    public void println(char x) {
        print(x);
        newLine();
    }

    public void println(int x) {
        print(x);
        newLine();
    }

    public void println(long x) {
        print(x);
        newLine();
    }
    public void println(float x) {
        print(x);
        newLine();
    }

    public void println(double x) {
        print(x);
        newLine();
    }
    public void println(char x[]) {
        print(x);
        newLine();
    }
    public void println(String x) {
        print(x);
        newLine();
    }

    public void println(Object x) {
        String s = String.valueOf(x);
        print(s);
        newLine();
    }
}

