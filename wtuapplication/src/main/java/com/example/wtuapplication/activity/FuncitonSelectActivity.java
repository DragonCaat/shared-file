package com.example.wtuapplication.activity;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import java.net.SocketAddress;
import java.net.SocketTimeoutException;

import static org.apache.commons.lang3.StringUtils.substring;

public class FuncitonSelectActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int PORT = 8001;//服务器端口号
    private Button btn1 = null;
    private Button btn2 = null;
    private Button btn3 = null;
    private Button btn4 = null;
    private Button btn5 = null;
    private Button btn6 = null;
    private Button btn7 = null;
    private Button btn8 = null;
    private String mIp;
    private Toolbar toolbar;
    private  Socket socket = null;
    private PrintWriter out;
    private String mstr;
    private BufferedReader br;
    private String mesg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads().detectDiskWrites().detectNetwork()
                .penaltyLog().build());

        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath()
                .build());
        setContentView(R.layout.activity_function_select);
        initView();
        initData();
        Intent intent = getIntent();
        mIp = intent.getStringExtra("ip");
        toolbar.setTitleMarginStart(250);
        setSupportActionBar(toolbar);
        //actionBar添加按钮
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.back);
        }
    }
    public void initView()
    {
        btn1 = (Button) findViewById(R.id.btn_SubtitlesPlay);//用于给服务器端发送指令
        btn2 = (Button) findViewById(R.id.btn_SubtitlesClose);//用于给服务器端发送指令
        btn3 = (Button) findViewById(R.id.btn_PIP_Play);//用于给服务器端发送指令
        btn4 = (Button) findViewById(R.id.btn_PIP_Close);//用于给服务器端发送指令
        btn5 = (Button) findViewById(R.id.btn_video_close);//用于给服务器端发送指令
        btn6 = (Button) findViewById(R.id.btn_picture_close);//用于给服务器端发送指令
        btn7 = (Button) findViewById(R.id.btn_background_close);//用于给服务器端发送指令
        btn8 = (Button) findViewById(R.id.btn_mainProcess_close);//用于给服务器端发送指令
        toolbar = (Toolbar) findViewById(R.id.toolbar);
    }
    public void initData()
    {
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btn5.setOnClickListener(this);
        btn6.setOnClickListener(this);
        btn7.setOnClickListener(this);
        btn8.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_SubtitlesPlay:
                subtitlesPlay();
                break;
            case R.id.btn_SubtitlesClose:
                subtitlesClose();
                break;
            case R.id.btn_PIP_Play:
                pipPlay();
                break;
            case R.id.btn_PIP_Close:
                pipClose();
                break;
            case R.id.btn_video_close:
                videoClose();
                break;
            case R.id.btn_picture_close:
                pictureClose();
                break;
            case R.id.btn_background_close:
                backgroundClose();
                break;
            case R.id.btn_mainProcess_close:
                mainProcessClose();
                break;
        }

    }
    public void subtitlesPlay(){
        Intent intent = new Intent(this,SendMesg.class);
        intent.putExtra("ip",mIp);
        startActivity(intent);

    }
    public void subtitlesClose(){
        mesg ="4 "+ "\r\n";
        try {
            //创建一个流套接字并将其连接到指定主机上的指定端口号
            Log.v("ddddd", mesg);
            //socket = new Socket( "218.199.5.67",8008);//指定服务器地址和端口号
            // socket = new Socket("218.199.6.178", 8002);//指定服务器地址和端口号
            socket = new Socket();//指定服务器地址和端口号
            socket.connect(new InetSocketAddress(mIp,PORT), 1000);//设置连接请求超时时间1s
            socket.setSoTimeout(5000);//设置读操作超时时间3s，到了超时的时间还没有连接就会抛出异常, 此处阻塞，连接服务器并设置连接服务器超时时间
            //向服务器端发送数据
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        //给服务器发送消息
                        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                        out.println(mesg);
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
            } else {
                Toast.makeText(getApplicationContext(),"连接网关超时，请确认服务器是否开启",Toast.LENGTH_SHORT).show();
                Log.i("sendmesg", "连接网关超时!");
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

    }
    public void pipPlay(){
        mesg ="6 "+ "\r\n";
        try {
            //创建一个流套接字并将其连接到指定主机上的指定端口号
            Log.v("ddddd", mesg);
            //socket = new Socket( "218.199.5.67",8008);//指定服务器地址和端口号
            // socket = new Socket("218.199.6.178", 8002);//指定服务器地址和端口号
            socket = new Socket();//指定服务器地址和端口号
            socket.connect(new InetSocketAddress(mIp,PORT), 1000);//设置连接请求超时时间1s
            socket.setSoTimeout(5000);//设置读操作超时时间3s，到了超时的时间还没有连接就会抛出异常, 此处阻塞，连接服务器并设置连接服务器超时时间
            //向服务器端发送数据
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        //给服务器发送消息
                        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                        out.println(mesg);
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
            } else {
                Toast.makeText(getApplicationContext(),"连接网关超时，请确认服务器是否开启",Toast.LENGTH_SHORT).show();
                Log.i("sendmesg", "连接网关超时!");
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


    }
    public void pipClose(){
        mesg ="5 "+ "\r\n";
        try {
            //创建一个流套接字并将其连接到指定主机上的指定端口号
            Log.v("ddddd", mesg);
            //socket = new Socket( "218.199.5.67",8008);//指定服务器地址和端口号
            // socket = new Socket("218.199.6.178", 8002);//指定服务器地址和端口号
            socket = new Socket();//指定服务器地址和端口号
            socket.connect(new InetSocketAddress(mIp,PORT), 1000);//设置连接请求超时时间1s
            socket.setSoTimeout(5000);//设置读操作超时时间3s，到了超时的时间还没有连接就会抛出异常, 此处阻塞，连接服务器并设置连接服务器超时时间
            //向服务器端发送数据
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        //给服务器发送消息
                        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                        out.println(mesg);
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
            } else {
                Toast.makeText(getApplicationContext(),"连接网关超时，请确认服务器是否开启",Toast.LENGTH_SHORT).show();
                Log.i("sendmesg", "连接网关超时!");
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

    }
    public void videoClose(){
        mesg ="7 "+ "\r\n";
        try {
            //创建一个流套接字并将其连接到指定主机上的指定端口号
            Log.v("ddddd", mesg);
            //socket = new Socket( "218.199.5.67",8008);//指定服务器地址和端口号
            // socket = new Socket("218.199.6.178", 8002);//指定服务器地址和端口号
            socket = new Socket();//指定服务器地址和端口号
            socket.connect(new InetSocketAddress(mIp,PORT), 1000);//设置连接请求超时时间1s
            socket.setSoTimeout(5000);//设置读操作超时时间3s，到了超时的时间还没有连接就会抛出异常, 此处阻塞，连接服务器并设置连接服务器超时时间
            //向服务器端发送数据
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        //给服务器发送消息
                        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                        out.println(mesg);
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
            } else {
                Toast.makeText(getApplicationContext(),"连接网关超时，请确认服务器是否开启",Toast.LENGTH_SHORT).show();
                Log.i("sendmesg", "连接网关超时!");
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

    }
    public void pictureClose(){
        mesg ="8 "+ "\r\n";
        try {
            //创建一个流套接字并将其连接到指定主机上的指定端口号
            Log.v("ddddd", mesg);
            //socket = new Socket( "218.199.5.67",8008);//指定服务器地址和端口号
            // socket = new Socket("218.199.6.178", 8002);//指定服务器地址和端口号
            socket = new Socket();//指定服务器地址和端口号
            socket.connect(new InetSocketAddress(mIp,PORT), 1000);//设置连接请求超时时间1s
            socket.setSoTimeout(5000);//设置读操作超时时间3s，到了超时的时间还没有连接就会抛出异常, 此处阻塞，连接服务器并设置连接服务器超时时间
            //向服务器端发送数据
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        //给服务器发送消息
                        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                        out.println(mesg);
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
            } else {
                Toast.makeText(getApplicationContext(),"连接网关超时，请确认服务器是否开启",Toast.LENGTH_SHORT).show();
                Log.i("sendmesg", "连接网关超时!");
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

    }
    public  void backgroundClose(){
        mesg ="9 "+ "\r\n";
        try {
            //创建一个流套接字并将其连接到指定主机上的指定端口号
            Log.v("ddddd", mesg);
            //socket = new Socket( "218.199.5.67",8008);//指定服务器地址和端口号
            // socket = new Socket("218.199.6.178", 8002);//指定服务器地址和端口号
            socket = new Socket();//指定服务器地址和端口号
            socket.connect(new InetSocketAddress(mIp,PORT), 1000);//设置连接请求超时时间1s
            socket.setSoTimeout(5000);//设置读操作超时时间3s，到了超时的时间还没有连接就会抛出异常, 此处阻塞，连接服务器并设置连接服务器超时时间
            //向服务器端发送数据
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        //给服务器发送消息
                        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                        out.println(mesg);
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
            } else {
                Toast.makeText(getApplicationContext(),"连接网关超时，请确认服务器是否开启",Toast.LENGTH_SHORT).show();
                Log.i("sendmesg", "连接网关超时!");
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

    }
    public void mainProcessClose(){
        mesg ="0 "+ "\r\n";
        try {
            //创建一个流套接字并将其连接到指定主机上的指定端口号
            Log.v("ddddd", mesg);
            //socket = new Socket( "218.199.5.67",8008);//指定服务器地址和端口号
            // socket = new Socket("218.199.6.178", 8002);//指定服务器地址和端口号
            socket = new Socket();//指定服务器地址和端口号
            socket.connect(new InetSocketAddress(mIp,PORT), 1000);//设置连接请求超时时间1s
            socket.setSoTimeout(5000);//设置读操作超时时间3s，到了超时的时间还没有连接就会抛出异常, 此处阻塞，连接服务器并设置连接服务器超时时间
            //向服务器端发送数据
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        //给服务器发送消息
                        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                        out.println(mesg);
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
            } else {
                Toast.makeText(getApplicationContext(),"连接网关超时，请确认服务器是否开启",Toast.LENGTH_SHORT).show();
                Log.i("sendmesg", "连接网关超时!");
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

    }
    /**
     * menu中控件的点击事件
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
        }
        return true;
    }
}
