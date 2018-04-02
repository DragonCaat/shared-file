package com.example.wtuapplication.activity;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class SendComan {
	public static final String IP_ADDR = "122.204.82.100";
	public static final int PORT = 8001;
	
    public static void main(String[] args) {
        	Socket socket = null;
        	try {
	        	//socket = new Socket(IP_ADDR, PORT);
				socket = new Socket("218.199.6.178", PORT);
				//socket = new Socket(" localhost",8008);
				DataInputStream input = new DataInputStream(socket.getInputStream());
	            DataOutputStream out = new DataOutputStream(socket.getOutputStream());  
	            System.out.print("向服务器发送消息:");
	            String str = new BufferedReader(new InputStreamReader(System.in)).readLine();  
	            out.writeUTF(str);  
	            socket.shutdownOutput();
	            InputStream is=socket.getInputStream();
	            BufferedReader in = new BufferedReader(new InputStreamReader(is));
	            //Thread.sleep(500); 
	            String info=null;
	            byte[] buf = new byte[20];
	            int ret = is.read(buf);
	            info = new String(buf);
	            System.out.println(info + ret);
	            out.close();
	            input.close();
        	} catch (Exception e) {
        		System.out.println("客户端异常:" + e.getMessage());
        	} finally {
        		if (socket != null) {
        			try {
						socket.close();
					} catch (IOException e) {
						socket = null; 
						System.out.println("客户端 finally 异常:" + e.getMessage());
					}
        		}
        	}
        }
}  