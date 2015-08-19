
import java.net.*;
import java.util.ArrayList;
import java.io.*;

public class TCPServer {
	
	public ServerSocket listenSocket = null;
    public InetSocketAddress address = null;
    
    private ArrayList<Service> ips = new ArrayList<Service> ();

    public TCPServer(InetSocketAddress address)
    {
    	    this.address = address;
    }
        
    public void start()
    {
        try 
        {	
        	this.listenSocket = new ServerSocket();
        	this.listenSocket.bind(address);
        	
        	System.out.println("TCP Server started!");
        	
        	while(true)
        	{
        			Socket serviceSocket = listenSocket.accept();
        			
        			Service service = new Service (serviceSocket);
        			ips.add(service);
        			
        			for (int i =0 ; i<ips.size();i++){

						ips.get(i).out.flush();
						String send = 
								serviceSocket.getInetAddress().getHostAddress()
								+", has joined the room.";
						ips.get(i).out.writeObject(send);
        			}
        			Thread newServiceThread = new Thread(service);
        			newServiceThread.start();
        	} 
        }
        	
        catch (Exception e) 
        {	
			e.printStackTrace();
		}
        	
    }
        
	private class Service implements Runnable
	{
		Socket serviceSocket = null;
	    ObjectInputStream in = null;
		ObjectOutputStream out = null;
		
		private Service(Socket serviceSocket)
		{					
			try
			{		
				this.serviceSocket = serviceSocket;
				this.in = new ObjectInputStream(this.serviceSocket.getInputStream());
				this.out = new ObjectOutputStream(this.serviceSocket.getOutputStream());
				this.out.flush();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		public void run()
		{
			try
			{
				while(true)
				{
					/* Receive message from the client */
					String message = (String)in.readObject();

					System.out.println("Server Received:" + message);

					/* Echo the message back to the client */
					for (int i =0 ; i<ips.size();i++){

						ips.get(i).out.flush();
						String send = 
								this.serviceSocket.getInetAddress().getHostAddress()
								+": "
								+message;
						ips.get(i).out.writeObject(send);
					}
				}
			}
			
			catch(Exception e)
			{
				//e.printStackTrace();
				for (int i =0 ; i<ips.size();i++){

					try {
						ips.get(i).out.flush();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						//e1.printStackTrace();
					}
					String send = 
							serviceSocket.getInetAddress().getHostAddress()
							+", has left the room.";
					try {
						ips.get(i).out.writeObject(send);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						//e1.printStackTrace();
					}
    			}
				ips.remove(this);
			}
			
			finally
			{
				if(serviceSocket != null)
					
				try
				{
					serviceSocket.close();
				}
				
				catch(Exception e)
				{
						e.printStackTrace();
				}	
			}
			
		}
	}
	
	 public static void main(String args[]) 
	 {
		    int port = 3001;
		    InetSocketAddress serverAddress = new InetSocketAddress(port);
		    
		    TCPServer server = new TCPServer(serverAddress);
		    server.start();
	 }
}	
		
