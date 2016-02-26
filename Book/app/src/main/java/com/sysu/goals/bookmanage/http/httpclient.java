package com.sysu.goals.bookmanage.http;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import android.util.Log;

import com.sysu.goals.bookmanage.ScanFragment;

/**
 * Created by goals on 2015/10/30.
 */
public class httpclient {
    static private String PATH="http://172.18.219.71:8088/cgi-bin/mysql.exe";

      static public  void connect_isbn(String isbn,Handler handler){
          try {
              URL url= new URL(PATH);
              URLConnection connection = url.openConnection();
              connection.setConnectTimeout(30000);
              HttpURLConnection httpUrlConnection = (HttpURLConnection) connection;
              httpUrlConnection.setDoOutput(true);
              httpUrlConnection.setDoInput(true);
              httpUrlConnection.setUseCaches(false);
              httpUrlConnection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
              httpUrlConnection.setRequestMethod("POST");
              //httpUrlConnection.setRequestProperty("Connection","Keep-Alive");

              PrintWriter out = new PrintWriter(httpUrlConnection.getOutputStream());
              String sql="{\"tag\":1,\"ISBN\":\"";
              sql=sql+isbn;
              sql=sql+"\"}";
              out.println(sql);

              try{
                  Thread.sleep(1000);
              }
              catch(Exception e){}
              //out.println(sql);

              out.close();
              //Log.v("jj_isbn",sql);
              InputStream inStrm = httpUrlConnection.getInputStream();// 调用HttpURLConnection连接对象的getInputStream()函数,
              // 将内存缓冲区中封装好的完整的HTTP请求电文发送到服务端。
              InputStreamReader isr= new InputStreamReader(inStrm);
              BufferedReader bufferedReader = new BufferedReader(isr);
              String result="";
              result=bufferedReader.readLine();
              Message msg=new Message();
              msg.what=1;
              msg.obj=result;

              Log.v("jj_isbn", "get");
              handler.sendMessage(msg);

              inStrm.close();
          } catch (MalformedURLException e) {
              e.printStackTrace();
          }  catch (IOException e) {
              e.printStackTrace();
          }



      }



    static public  void connect_all(String sql1,Handler mhandler,int count){
        try {
            URL url= new URL(PATH);
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(30000);
            HttpURLConnection httpUrlConnection = (HttpURLConnection) connection;
            httpUrlConnection.setDoOutput(true);
            httpUrlConnection.setDoInput(true);
            httpUrlConnection.setUseCaches(false);
            httpUrlConnection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
            httpUrlConnection.setRequestMethod("POST");
            //httpUrlConnection.setRequestProperty("Connection", "Keep-Alive");

            PrintWriter out = new PrintWriter(httpUrlConnection.getOutputStream());
            String sql="{\"tag\":2,\"SQL\":\"";
            sql=sql+sql1;
            sql=sql+"\"}";
            //Log.v("jj_all", sql);
            out.println(sql);
            try{Thread.sleep(1000);}
            catch(Exception e){}
            //out.println(sql);
            out.close();


            InputStream inStrm = httpUrlConnection.getInputStream();// 调用HttpURLConnection连接对象的getInputStream()函数,
            // 将内存缓冲区中封装好的完整的HTTP请求电文发送到服务端。
            InputStreamReader isr= new InputStreamReader(inStrm);
            //BufferedReader bufferedReader = new BufferedReader(isr,10*1024*1024);
            BufferedReader bufferedReader = new BufferedReader(isr);
            String inputLine="";
            String result="";
            /*while ( (inputLine=bufferedReader.readLine())!=null){
                result += inputLine;  //保存书籍信息
            }*/
            result=bufferedReader.readLine();//可以接收完整数据

            Message msg=new Message();
            msg.what=count;
            msg.obj=result;
            mhandler.sendMessage(msg);
            Log.v("jj_all", "get");

            inStrm.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }  catch (IOException e) {
            e.printStackTrace();
        }
    }


    static public  void connect_book(String info,String sql1,Handler mhandler){
        try {
            URL url= new URL(PATH);
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(30000);
            HttpURLConnection httpUrlConnection = (HttpURLConnection) connection;
            httpUrlConnection.setDoOutput(true);
            httpUrlConnection.setDoInput(true);
            httpUrlConnection.setUseCaches(false);
            httpUrlConnection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
            httpUrlConnection.setRequestMethod("POST");
            //httpUrlConnection.setRequestProperty("Connection", "Keep-Alive");

            PrintWriter out = new PrintWriter(httpUrlConnection.getOutputStream());
            String sql="{\"tag\":2,\"SQL\":\"";
            sql=sql+sql1;
            sql=sql+"\"}";
            //Log.v("jj_book", sql);
            out.println(sql);
            try{Thread.sleep(1000);}
            catch(Exception e){}
            //out.println(sql);

            out.close();//不能放最后，否则发送不了

            InputStream inStrm = httpUrlConnection.getInputStream();// 调用HttpURLConnection连接对象的getInputStream()函数,
            // 将内存缓冲区中封装好的完整的HTTP请求电文发送到服务端。
            InputStreamReader isr= new InputStreamReader(inStrm);
            //BufferedReader bufferedReader = new BufferedReader(isr,10*1024*1024);
            BufferedReader bufferedReader = new BufferedReader(isr);
            String inputLine="";
            String result="";
            /*while ( (inputLine=bufferedReader.readLine())!=null){
                result += inputLine;  //保存书籍信息
            }*/
            result=bufferedReader.readLine();//可以接收完整数据

            Message msg=new Message();
            //Message msg2=new Message();
            msg.what=2;
            Bundle bundle=new Bundle();
            bundle.putString("2",result);
            bundle.putString("1",info);
            msg.obj=bundle;
            mhandler.sendMessage(msg);
            Log.v("jj_book", "get");

            inStrm.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }  catch (IOException e) {
            e.printStackTrace();
        }
    }


    static public  String connect_normal(String sql1){
        try {
            URL url= new URL(PATH);
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(30000);
            HttpURLConnection httpUrlConnection = (HttpURLConnection) connection;
            httpUrlConnection.setDoOutput(true);
            httpUrlConnection.setDoInput(true);
            httpUrlConnection.setUseCaches(false);
            httpUrlConnection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
            httpUrlConnection.setRequestMethod("POST");
            //httpUrlConnection.setRequestProperty("Connection", "Keep-Alive");

            PrintWriter out = new PrintWriter(httpUrlConnection.getOutputStream());
            String sql="{\"tag\":2,\"SQL\":\"";
            sql=sql+sql1;
            sql=sql+"\"}";
            //Log.v("jj_normal",sql);
            out.println(sql);
            try{Thread.sleep(1000);}
            catch(Exception e){}
           // out.println(sql);


            out.close();
            InputStream inStrm = httpUrlConnection.getInputStream();// 调用HttpURLConnection连接对象的getInputStream()函数,
            // 将内存缓冲区中封装好的完整的HTTP请求电文发送到服务端。
            InputStreamReader isr= new InputStreamReader(inStrm);
            //BufferedReader bufferedReader = new BufferedReader(isr,10*1024*1024);
            BufferedReader bufferedReader = new BufferedReader(isr);
            String inputLine="";
            String result="";
            result=bufferedReader.readLine();//可以接收完整数据

           // Message msg=new Message();
            //msg.obj=result;
            //mhandler.sendMessage(msg);
            Log.v("jj_normal", result);

            inStrm.close();
            return result;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }  catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    static public  void connect_insert(String sql1){
        try {
            URL url= new URL(PATH);
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(10000);
            HttpURLConnection httpUrlConnection = (HttpURLConnection) connection;
            httpUrlConnection.setDoOutput(true);
            httpUrlConnection.setDoInput(true);
            httpUrlConnection.setUseCaches(false);
            httpUrlConnection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
            httpUrlConnection.setRequestMethod("POST");
            //httpUrlConnection.setRequestProperty("Connection","Keep-Alive");

            String sql="{\"tag\":2,\"SQL\":\"";
            sql=sql+sql1;
            sql=sql+"\"}";

            OutputStream outputStream = httpUrlConnection.getOutputStream();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);

            outputStreamWriter.write(sql);
            outputStreamWriter.flush();




            /*PrintWriter out = new PrintWriter(httpUrlConnection.getOutputStream());
            //Log.v("jj_insert", sql);
            out.println(sql);
            try{Thread.sleep(1000);}
            catch(Exception e){}
            out.close();*/

            InputStream inStrm = httpUrlConnection.getInputStream();// 调用HttpURLConnection连接对象的getInputStream()函数,
            // 将内存缓冲区中封装好的完整的HTTP请求电文发送到服务端。
            //InputStreamReader isr= new InputStreamReader(inStrm);
            //BufferedReader bufferedReader = new BufferedReader(isr);
            Log.v("jj_insert", "get");
            outputStreamWriter.close();
            inStrm.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }  catch (IOException e) {
            e.printStackTrace();
        }
    }


    static public  void connect_recommend(String isbn){
        try {
            URL url= new URL(PATH);
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(30000);
            HttpURLConnection httpUrlConnection = (HttpURLConnection) connection;
            httpUrlConnection.setDoOutput(true);
            httpUrlConnection.setDoInput(true);
            httpUrlConnection.setUseCaches(false);
            httpUrlConnection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
            httpUrlConnection.setRequestMethod("POST");
            //httpUrlConnection.setRequestProperty("Connection","Keep-Alive");

            PrintWriter out = new PrintWriter(httpUrlConnection.getOutputStream());
            String sql="{\"tag\":3,\"ISBN\":\"";
            sql=sql+isbn;
            sql=sql+"\"}";
            out.println(sql);
            out.close();


            InputStream inStrm = httpUrlConnection.getInputStream();// 调用HttpURLConnection连接对象的getInputStream()函数,
            // 将内存缓冲区中封装好的完整的HTTP请求电文发送到服务端。
            InputStreamReader isr= new InputStreamReader(inStrm);
            BufferedReader bufferedReader = new BufferedReader(isr);
            Log.v("jj_recommend", "get");

            inStrm.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }  catch (IOException e) {
            e.printStackTrace();
        }
    }

    static public  void connect_all_cut(String sql1,int cut,Bundle bundle){

        try {
            URL url= new URL(PATH);
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(30000);
            HttpURLConnection httpUrlConnection = (HttpURLConnection) connection;
            httpUrlConnection.setDoOutput(true);
            httpUrlConnection.setDoInput(true);
            httpUrlConnection.setUseCaches(false);
            httpUrlConnection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
            httpUrlConnection.setRequestMethod("POST");

            PrintWriter out = new PrintWriter(httpUrlConnection.getOutputStream());
            String sql="{\"tag\":2,\"SQL\":\"";
            sql=sql+sql1;

            sql=sql+String.valueOf(cut*20);
            sql=sql+",20\"}";
            //sql=sql+"\"";
            Log.v("jj_all_cut", sql);
            Log.v("jj_all_cut", String.valueOf(cut));
            Log.v("jj_all_cut", String.valueOf(cut));
            out.println(sql);
            try{Thread.sleep(1000);}
            catch(Exception e){}
            //out.println(sql);
            out.flush();
            //out.close();


            InputStream inStrm = httpUrlConnection.getInputStream();// 调用HttpURLConnection连接对象的getInputStream()函数,
            // 将内存缓冲区中封装好的完整的HTTP请求电文发送到服务端。
            InputStreamReader isr= new InputStreamReader(inStrm);
            //BufferedReader bufferedReader = new BufferedReader(isr,10*1024*1024);
            BufferedReader bufferedReader = new BufferedReader(isr);
            String result="";
            result=bufferedReader.readLine();//可以接收完整数据

            bundle.putString(String.valueOf(cut), result);
            Log.v("jj_all_cut1", "get1");

            out.close();
            inStrm.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }  catch (IOException e) {
            e.printStackTrace();
        }


    }

    static public  void connect_cut(String sql1,Handler mhandler,int count,int cut) {
        Bundle bundle=new Bundle();
        for(int a=0;a<=cut;a++){
            connect_all_cut(sql1,a,bundle);
        }
        Message msg=new Message();
        msg.what=count;
        msg.obj=bundle;
        mhandler.sendMessage(msg);
    }

    }




