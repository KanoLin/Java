package chater;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.border.MatteBorder;





class ClientThreads extends Thread
{
    private Socket socket=null;
    private BufferedReader ServerIn=null;
    private ClientGUI GUI=null;
    public ClientThreads (Socket s,ClientGUI f) throws IOException
    {
        socket=s;
        GUI=f;
        ServerIn = new BufferedReader(new InputStreamReader( socket.getInputStream(),"UTF-8"));
        start();
    }

    @Override
    public void run()
    {
        try
        {
            String sys="---.*---";
            while (true)//读入服务器端发送的信息并显示，若服务器离线则跳出循环
            {
                String str=ServerIn.readLine();
                if ((str==null)||socket.isClosed()){
                    break;
                }
                if (Pattern.matches(sys,str)){
                    JTextArea a=new JTextArea(1,35);
                    a.setLineWrap(true);
                    a.setText(str);
                    a.setEditable(false);
                    a.setAlignmentX(0.5f);
                    a.setBorder(new MatteBorder(2, 2, 2, 2, new Color(0, 0, 0)));
                    Box hbox=Box.createHorizontalBox();
                    hbox.add(a);
                   // hbox.add(Box.createHorizontalStrut(60));
                    GUI.box.add(hbox);

                }else {
                    JTextArea a = new JTextArea(1, 30);
                    a.setLineWrap(true);
                    a.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
                    a.setText(str);
                    a.setEditable(false);
                    a.setBorder(new MatteBorder(2, 2, 2, 2, new Color(65, 81, 192)));
                    Box hbox = Box.createHorizontalBox();
                    hbox.add(a);
                    hbox.add(Box.createHorizontalStrut(60));
                    GUI.box.add(hbox);

                }
                GUI.box.add(Box.createVerticalStrut(5));
                GUI.box.revalidate();
                //System.out.println(str);
            }
        }
        catch(IOException e){}
        finally{try{socket.close();}catch(IOException e){}}
    }
}


@SuppressWarnings("serial")
class ClientGUI extends JFrame
{
    //private static final long serialVersionUID = 1L;
    JPanel title=new JPanel();
    JPanel contentPane=new JPanel();
    JPanel bottom=new JPanel();
    public Box box=null;
    public String username=null;
    public JTextArea text0=null;
    public Socket socket=null;
    public PrintWriter ServerOut=null;
    public ClientGUI(Socket s,PrintWriter out)
    {
        socket=s;
        ServerOut=out;
        username=JOptionPane.showInputDialog(this,"用户名","登陆",JOptionPane.INFORMATION_MESSAGE);
        if (username==null)System.exit(0);

        this.setSize(400,600);
        this.setLocation(300,200);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setTitle("Client:"+username);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int answer=JOptionPane.showConfirmDialog(null,"确认关闭？","窗口消息",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
                if (answer==JOptionPane.YES_OPTION) System.exit(0);
            }
        });
        this.setResizable(false);
        this.setLayout(new BorderLayout());

        contentPane.setBorder(BorderFactory.createLineBorder(Color.red));


        text0=new JTextArea(5,30);


        box=Box.createVerticalBox();


        JScrollPane centerroll=new JScrollPane();
        centerroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        centerroll.setViewportView(contentPane);
        contentPane.add(box);
        centerroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);


        JButton btn=new JButton("发送");
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (socket.isClosed())JOptionPane.showMessageDialog(null, "您已离线", "通知", JOptionPane.INFORMATION_MESSAGE);
                if (text0.getText().equals("")||text0.getText().equals("/num"))return;
                ServerOut.println(text0.getText());

                JTextArea a=new JTextArea(1,30);
                a.setLineWrap(true);
                a.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                a.setText(text0.getText());
                a.setEditable(false);
                a.setBorder(BorderFactory.createLineBorder(Color.BLUE));
                a.setBorder(new MatteBorder(2, 2, 2, 2, new Color(192, 66, 119)));
                Box hbox=Box.createHorizontalBox();
                hbox.add(Box.createHorizontalStrut(60));
                hbox.add(a);
                box.add(hbox);
                box.add(Box.createVerticalStrut(5));
                box.revalidate();



                text0.setText(null);
            }
        });

        //text0.setLineWrap(true);
        JScrollPane inputAera=new JScrollPane(text0);
        inputAera.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        inputAera.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        bottom.add(inputAera);
        bottom.add(btn);

        add(centerroll,BorderLayout.CENTER);
        add(bottom,BorderLayout.SOUTH);

        this.setVisible(true);

    }

}

public class Client2 {
    static private String URL="localhost";
    static private int PORT=8080;
    static private Socket socket=null;
    static PrintWriter ServerOut=null;
    
    public static void main(String[] args) throws IOException
    {
        try{
            socket=new Socket(URL,PORT);//与服务器端建立连接
            ServerOut=new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),"UTF-8")),true);
            ClientGUI GUI=new ClientGUI(socket,ServerOut);
            

            ServerOut.println(GUI.username);
            new ClientThreads(socket,GUI);
            while (socket.isConnected()){}
            try{
                socket.close();
            }
            catch(IOException e){}

            System.exit(0);
        }
        catch(IOException e){
            JOptionPane.showMessageDialog(null, "无法连接服务器", "通知", JOptionPane.INFORMATION_MESSAGE);
            //e.printStackTrace();
        }
    }













}
