import java.net.*;
import java.util.Scanner;
import java.io.*;

public class TCPClient
{
	public InetSocketAddress serverAddress = null; 
	public Socket clientSocket    = null;
	public ObjectOutputStream out = null;
	public ObjectInputStream in   = null;
	    
	public TCPClient(InetSocketAddress serverAddress)
	{
		this.serverAddress = serverAddress;
	}
	
	public void start() 
	{
		try
		{
			 this.clientSocket = new Socket();
		     this.clientSocket.connect(this.serverAddress);
		     
		     out = new ObjectOutputStream(clientSocket.getOutputStream());
			 out.flush();
			 
			 in = new ObjectInputStream(clientSocket.getInputStream());
			 System.out.println("Client ready to send messages!");
			 Thread reciever = new Thread (new sameer ());
			 reciever.start();
			 
			 while(true)
			 {
				 Scanner scanner = new Scanner(System.in);
				 String message = scanner.nextLine().trim();
				 
				 out.writeObject(message);
				
			 }			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(clientSocket != null)
			{	
				try
				{
					clientSocket.close();
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	private class sameer implements Runnable {

		@Override
		public void run() {
			
			try {
				while(true){
					
					System.out.println( in.readObject());
				}
			} catch (IOException | ClassNotFoundException e) {

				e.printStackTrace();
			}
		}
		
	}
	
	public static void main(String args[])
	{
		int port = 3001;
	    	    
	    System.out.println ("Enter IP: ");
	    Scanner getInfo = new Scanner(System.in);
	    String iP = getInfo.nextLine().trim();
	    InetSocketAddress serverAddress = new InetSocketAddress(iP,port);
	    
		TCPClient client = new TCPClient(serverAddress);
		client.start();
	}
}   
