//Client.java
package chater;

import java.io.*;
import java.net.*;


class ClientThread extends Thread
{
    private Socket socket=null;
    private BufferedReader ServerIn=null;
    public ClientThread (Socket s) throws IOException
    {
        socket=s;
        ServerIn = new BufferedReader(new InputStreamReader( socket.getInputStream(),"UTF-8"));
        start();
    }
    
    @Override
    public void run()
    {
        try
        {
            while (true)//读入服务器端发送的信息并显示，若服务器离线则跳出循环
            {
                String str=ServerIn.readLine();
                if ((str==null)||socket.isClosed()){ System.out.println("------服务器已离线------");break;}
                System.out.println(str);
            } 
        }
        catch(IOException e){}
        finally{try{socket.close();}catch(IOException e){}}
    }
}



public class Client
{
    public static void main(String[] args) throws IOException
    {
        System.out.print("请输入你的用户名：");
        BufferedReader cin=new BufferedReader(new InputStreamReader(System.in));
        String name=cin.readLine();
        try{
            Socket socket=new Socket("localhost",8080);//与服务器端建立连接
            //BufferedReader ServerIn=new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
            PrintWriter ServerOut=new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),"UTF-8")),true);
            ServerOut.println(name);
            new ClientThread(socket);
            while (!socket.isClosed())//读入客户端信息并发送给服务器，输入EXIT退出客户端
            {
                String str=cin.readLine();
                if (str.equals(""))continue;
                if ((str==null)||(str.equals("EXIT")))break;
                ServerOut.println(str);              
            } 
            if(socket.isClosed()) {System.out.println("------服务器已离线------");}
            System.out.println("您已离线");
            try{
                socket.close();
            }
            catch(IOException e){};
        }
        catch(IOException e){e.printStackTrace();}
    }
}