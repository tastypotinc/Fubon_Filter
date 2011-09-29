package org.Fubon.Server;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.io.BufferedInputStream;
//import java.io.BufferedOutputStream;
//import java.io.DataInputStream;
 
public class ClientTest implements Runnable  {
    private String address = "127.0.0.1";// 連線的ip
    private int port = 81;// 連線的port
    Socket client;
    InetSocketAddress isa;
    
    public ClientTest() {
    	client = new Socket();
        isa = new InetSocketAddress(this.address, this.port);
    }
    public void run () {
        try {
            client.connect(isa, 10000);
            System.out.println("Client connec to Server"+client.getInetAddress());
            BufferedInputStream in = new java.io.BufferedInputStream(client.getInputStream());
            byte[] b = new byte[1024];
            String data = "";
            int length;
            while ((length = in.read(b)) > 0)// <=0的話就是結束了
            {
                data = new String(b, 0, length);
                System.out.println("我取得的值:" + data);
            }

            
            in.close();
            in = null;
            client.close();
 
        } catch (java.io.IOException e) {
            System.out.println("Socket連線有問題 !");
            System.out.println("IOException :" + e.toString());
        }
    }
 

}