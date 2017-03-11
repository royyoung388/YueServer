package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
	
    public static final int PORT = 8888;//监听的端口号   
    private List<Socket> mClientList = new ArrayList<Socket>(); 
    private List<String> ipadress = new ArrayList<>();
    
    public static void main(String[] args) {
        System.out.println("服务器启动...\n");
        Server server = new Server();
        server.init();
    }


    //初始化同时进行链接处理
    public void init() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            while (true) {
                // 一旦有堵塞, 则表示服务器与客户端获得了连接  
                Socket client = serverSocket.accept();
                
                mClientList.add(client);  
                ipadress.add(client.getInetAddress().toString());
                
                System.out.println("新的设备" + client.getInetAddress().toString());
                System.out.println("当前设备数目" + mClientList.size());
                
                // 处理这次连接  
                new HandlerThread(client);
            }
        } catch (Exception e) {
            System.out.println("服务器异常: " + e.getMessage());
        }
    }

    //具体的链接处理
    private class HandlerThread implements Runnable {
    	
        private Socket socket;
        
        public HandlerThread(Socket client) {
            socket = client;
            new Thread(this).start();
            }

        public void run() {
            try {
                // 读取客户端数据  
                DataInputStream input = new DataInputStream(socket.getInputStream());
                
                //这里要注意和客户端输出流的写方法对应,否则会抛 EOFException
                String clientInputStr = input.readUTF();
                
                //获取ip地址
                String toip = clientInputStr.substring(0, clientInputStr.indexOf("#"));
                
                //将数据写进文件
                dealwritefile(clientInputStr);
                
                // 处理客户端数据  
                System.out.println("客户端发过来的内容:\n" + clientInputStr);
                System.out.println(socket.getInetAddress());
                
                System.out.println("完毕");
                
                //寻找
                int index = 0;
                while (toip.equals(ipadress.get(index))) {
                	if (index == mClientList.size() - 1) {
                		break;
                	}
                	index++;
                }
                
                //判断是否完成有该ip
                if (ipadress.get(index).toString().equals(toip)) {
                	System.out.println("ip已找到");
                	
                    DataOutputStream out = new DataOutputStream(mClientList.get(index).getOutputStream());

                	String s = clientInputStr.substring(clientInputStr.indexOf("#") + 1);
                    out.writeUTF(s);
                    out.close();
                } else {
					System.out.println("ip没找到");
				}

                // 向客户端回复信息  
                
               
                // 发送键盘输入的一行  
                //String s = new BufferedReader(new InputStreamReader(System.in)).readLine();
                
               
                //String s = socket.getInetAddress().toString()+ "    " + socket.getLocalAddress().toString();
                

                input.close();
            } catch (Exception e) {
                System.out.println("服务器 run 异常: " + e.getMessage());
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (Exception e) {
                        socket = null;
                        System.out.println("服务端 finally 异常:" + e.getMessage());
                    }
                }
            }
        }
        
        //实现文件写
        private void dealwritefile(String str) {	  
        	try  
    	    {  
    	    //使用BufferedReader和BufferedWriter进行文件复制（操作的是字符,以行为单位读入字符）  
    	 
    	
    	    BufferedWriter bw=new BufferedWriter(new FileWriter("data.txt", true));   
    	    bw.write(str);  
    	    //由于BufferedReader的rendLIne()是不读入换行符的，所以写入换行时须用newLine()方法  
    	    bw.newLine();  
        	bw.close();  
    	    }  
        	catch (IOException e)  
        	{  
        		e.printStackTrace();  
        	}  	
        }
        
        
        //实现文件读
        private String dealreadfile() {
        	
        	String s = "";
        	
        	 try  
             {  
             //使用BufferedReader和BufferedWriter进行文件复制（操作的是字符,以行为单位读入字符）  
             BufferedReader br=new BufferedReader(new FileReader("data.txt"));  
             
             String str = null;

             while ((str = br.readLine()) != null) {
                 s += str;
             }
             br.close();  
             }  
        	 catch (IOException e)  
        	 {  
        		 e.printStackTrace();  
             }
    		return s;  
        }
        
    }
}  
