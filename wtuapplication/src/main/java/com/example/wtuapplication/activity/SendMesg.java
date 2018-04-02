package com.example.wtuapplication.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.example.wtuapplication.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Created by 贾焰华 on 2017/12/11.
 */

public class SendMesg extends Activity implements View.OnClickListener {
    private EditText editText;
    private String ip;
    private String mesg;
    private Socket socket;
    private  static int PORT = 8001;
    private String mstr;
    private PrintWriter out;
    private BufferedReader br;
    private ProgressDialog mDialog;
    public  boolean flag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);	//去掉标题栏
        setContentView(R.layout.send_mesg);
        init();
        Intent intent = getIntent();
        ip = intent.getStringExtra("ip");
    }
    public void init(){
        findViewById(R.id.send).setOnClickListener(this);
        findViewById(R.id.cancel).setOnClickListener(this);
        editText = (EditText) findViewById(R.id.editText);

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.send:
                sendMesg();
                break;
            case R.id.cancel:
                cancel();
                break;
        }
    }
    public void sendMesg(){
        mDialog = new ProgressDialog(this);
        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置进度条的形式为圆形转动的进度条
        mDialog.setCancelable(true);// 设置是否可以通过点击Back键取消
        mDialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
        mDialog.setIcon(R.mipmap.error);//
        mDialog.setTitle("提示");
        mDialog.setMessage("正在发送...");
        // dismiss监听
        mDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mDialog.isShowing()) {
                            dialog.dismiss();
                        }
                        return;
                    }
                });
        mDialog.show();
        mesg ="1 "+ editText.getText().toString() + "\r\n";
        try {
            //创建一个流套接字并将其连接到指定主机上的指定端口号
            Log.v("ddddd", mesg);
            //socket = new Socket( "218.199.5.67",8008);//指定服务器地址和端口号
            // socket = new Socket("218.199.6.178", 8002);//指定服务器地址和端口号
            socket = new Socket();//指定服务器地址和端口号
            socket.connect(new InetSocketAddress(ip,PORT), 1000);//设置连接请求超时时间1s
            socket.setSoTimeout(5000);//设置读操作超时时间5s，到了超时的时间还没有连接就会抛出异常, 此处阻塞，连接服务器并设置连接服务器超时时间
            //向服务器端发送数据
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //给服务器发送消息
                            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                            out.println(mesg);
                            mDialog.dismiss();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                mstr = br.readLine();
                if (mstr != null) {
                    Toast.makeText(getApplicationContext(),"消息已发送",Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getApplicationContext(),"接收消息为空",Toast.LENGTH_SHORT).show();
                }
                out.close();
                br.close();
        } catch(SocketTimeoutException e) {
            if (!socket.isClosed() && socket.isConnected()) {
                Toast.makeText(getApplicationContext(),"读取网关数据超时!",Toast.LENGTH_SHORT).show();
                Log.i("sendmesg", "读取网关数据超时!");
                mDialog.dismiss();
            } else {
                Toast.makeText(getApplicationContext(),"连接网关超时，请确认服务器是否开启",Toast.LENGTH_SHORT).show();
                Log.i("sendmesg", "连接网关超时!");
                mDialog.dismiss();
            }
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        finish();
    }
    public void cancel()
    {
        finish();

    }
}
