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
	
    public static final int PORT = 8888;//�����Ķ˿ں�   
    private List<Socket> mClientList = new ArrayList<Socket>(); 
    private List<String> ipadress = new ArrayList<>();
    
    public static void main(String[] args) {
        System.out.println("����������...\n");
        Server server = new Server();
        server.init();
    }


    //��ʼ��ͬʱ�������Ӵ���
    public void init() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            while (true) {
                // һ���ж���, ���ʾ��������ͻ��˻��������  
                Socket client = serverSocket.accept();
                
                mClientList.add(client);  
                ipadress.add(client.getInetAddress().toString());
                
                System.out.println("�µ��豸" + client.getInetAddress().toString());
                System.out.println("��ǰ�豸��Ŀ" + mClientList.size());
                
                // �����������  
                new HandlerThread(client);
            }
        } catch (Exception e) {
            System.out.println("�������쳣: " + e.getMessage());
        }
    }

    //��������Ӵ���
    private class HandlerThread implements Runnable {
    	
        private Socket socket;
        
        public HandlerThread(Socket client) {
            socket = client;
            new Thread(this).start();
            }

        public void run() {
            try {
                // ��ȡ�ͻ�������  
                DataInputStream input = new DataInputStream(socket.getInputStream());
                
                //����Ҫע��Ϳͻ����������д������Ӧ,������� EOFException
                String clientInputStr = input.readUTF();
                
                //��ȡip��ַ
                String toip = clientInputStr.substring(0, clientInputStr.indexOf("#"));
                
                //������д���ļ�
                dealwritefile(clientInputStr);
                
                // ����ͻ�������  
                System.out.println("�ͻ��˷�����������:\n" + clientInputStr);
                System.out.println(socket.getInetAddress());
                
                System.out.println("���");
                
                //Ѱ��
                int index = 0;
                while (toip.equals(ipadress.get(index))) {
                	if (index == mClientList.size() - 1) {
                		break;
                	}
                	index++;
                }
                
                //�ж��Ƿ�����и�ip
                if (ipadress.get(index).toString().equals(toip)) {
                	System.out.println("ip���ҵ�");
                	
                    DataOutputStream out = new DataOutputStream(mClientList.get(index).getOutputStream());

                	String s = clientInputStr.substring(clientInputStr.indexOf("#") + 1);
                    out.writeUTF(s);
                    out.close();
                } else {
					System.out.println("ipû�ҵ�");
				}

                // ��ͻ��˻ظ���Ϣ  
                
               
                // ���ͼ��������һ��  
                //String s = new BufferedReader(new InputStreamReader(System.in)).readLine();
                
               
                //String s = socket.getInetAddress().toString()+ "    " + socket.getLocalAddress().toString();
                

                input.close();
            } catch (Exception e) {
                System.out.println("������ run �쳣: " + e.getMessage());
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (Exception e) {
                        socket = null;
                        System.out.println("����� finally �쳣:" + e.getMessage());
                    }
                }
            }
        }
        
        //ʵ���ļ�д
        private void dealwritefile(String str) {	  
        	try  
    	    {  
    	    //ʹ��BufferedReader��BufferedWriter�����ļ����ƣ����������ַ�,����Ϊ��λ�����ַ���  
    	 
    	
    	    BufferedWriter bw=new BufferedWriter(new FileWriter("data.txt", true));   
    	    bw.write(str);  
    	    //����BufferedReader��rendLIne()�ǲ����뻻�з��ģ�����д�뻻��ʱ����newLine()����  
    	    bw.newLine();  
        	bw.close();  
    	    }  
        	catch (IOException e)  
        	{  
        		e.printStackTrace();  
        	}  	
        }
        
        
        //ʵ���ļ���
        private String dealreadfile() {
        	
        	String s = "";
        	
        	 try  
             {  
             //ʹ��BufferedReader��BufferedWriter�����ļ����ƣ����������ַ�,����Ϊ��λ�����ַ���  
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
