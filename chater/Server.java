//Server.java
package chater;

import java.io.*;
import java.net.*;
import java.util.*;



class ServerThread extends Thread
{
    private Socket socket=null;
    private BufferedReader ClientIn=null;
    private PrintWriter ClientOut=null;
    private static List<PrintWriter> allLink=new ArrayList<PrintWriter>();//存储已连接的客户端输出流对象
    public ServerThread (Socket s) throws IOException
    {
        socket=s;
        ClientIn = new BufferedReader(new InputStreamReader( socket.getInputStream(),"UTF-8"));
        ClientOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),"UTF-8")),true);
        start();
    }
    
    @Override
    public void run()
    {
    	String name=null;
        try
        {
        	name=ClientIn.readLine(); 
        	System.out.println("---用户："+name+" 已连接："+socket);
            ClientOut.println("------服务器连接成功！------");
            if (!allLink.isEmpty())
                for(PrintWriter out:allLink)//对已连接的客户端进行上线消息广播
                    out.println("---系统："+name+"已上线---");
            //ClientOut.flush();
            allLink.add(ClientOut);
            System.out.println("---当前在线人数"+allLink.size()+"---");//返回给客户端的信息
            ClientOut.println("---当前在线人数"+allLink.size()+"---");//服务器端显示信息
            while (true)
            {
                String Cstr=ClientIn.readLine();
                if ((Cstr==null)||socket.isClosed())break;
                if (Cstr.equals("/num")){ClientOut.println("---当前在线人数"+allLink.size()+"---");continue;}//输入"/num"可以查看当前在线人数
                System.out.println(name+"："+Cstr);
                for(PrintWriter out:allLink)//对已连接的客户端进行消息广播
                    if (out!=ClientOut)out.println(name+"："+Cstr);
            } 
        }
        catch(IOException e){}
        finally{
            allLink.remove(ClientOut);
            System.out.println("---用户："+name+"已离线"+socket);
                    for(PrintWriter out:allLink)//对已连接的客户端进行下线消息广播
                        out.println("---系统："+name+"已下线---");     
            System.out.println("---当前在线人数"+allLink.size()+"---");
            try{socket.close();}catch(IOException e){}
        }
    }
}



public class Server 
{
    public static final int PORT=8080;
    public static void main(String[] args) throws IOException
    {
        ServerSocket server= new ServerSocket(PORT);
        System.out.println("--------服务器已开启---------");
        try{
            while (true)
            {
                Socket socket=server.accept();//服务器端收到客户端请求后即开始新进程
                try{
                    new ServerThread(socket);
                }
                catch(IOException e){socket.close();}
            }
        }
        finally{
            server.close();
        }
    }
}
