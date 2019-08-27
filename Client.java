import java.net.*;
import java.io.*;

/**
 * Peer to peer java Client
 * Client constructor accept ip address and port number from the main class
 * Client connect to the server using ip address and port number
 * Client receive and send messages from and to Server
 * If user type exit to the input then the socket connection will be closed
 */

public class Client
{
	// initialize socketConnection and inputDataStream output streams
	private Socket socketConnection = null;
	private DataInputStream inputDataStream = null;
	private DataOutputStream outputDataStream = null;
	private DataInputStream in = null;
	
	
	// constructor to put ip address and port
	public Client(String address, int port)
	{
		// establish a connection
		try
		{
			
			socketConnection = new Socket(address, port);
			System.out.println("Connected");

			// takes inputDataStream from terminal
			inputDataStream = new DataInputStream(System.in);
			
			// sends output to the socketConnection
			outputDataStream = new DataOutputStream(socketConnection.getOutputStream());
			
			// input from the server
			in = new DataInputStream(new BufferedInputStream(socketConnection.getInputStream()));
			
			// string to read message from inputDataStream
			String messageString = "";
			
			
			new Thread(() -> {
				try {
					while (true) {
						String messageResponse = in.readUTF();
						System.out.println("Server response: " + messageResponse);
						
						if ( messageResponse.equals("exit")) {
							System.exit(127);
						}
					}
				} catch (Exception ex) {
					System.out.println("Exception "+ex.getMessage());
				}
			}).start();
			
			// keep reading until "exit" is inputDataStream
			while (!messageString.equals("exit"))
			{
				System.out.println("Enter your message: ");
				messageString = inputDataStream.readLine();
				outputDataStream.writeUTF(messageString);
			}
			
		}
		catch(UnknownHostException u)
		{
			System.out.println(u);
		}
		catch(IOException i)
		{
			System.out.println(i);
		}
		finally {
			// close the connection
			try
			{
				inputDataStream.close();
				outputDataStream.close();
				socketConnection.close();
			}
			catch(IOException i)
			{
				System.out.println(i);
			}
		}
		
	}
	
	public static void main(String args[])
	{
		String host = null;
		int port = 0;
		
		try {
			if (args.length < 2) {
				throw new IllegalArgumentException("Insufficient arguments. Usage: client --server host:port");
			}
			
			String hostport[] = args[1].trim().split(":");
			if ( hostport.length == 2 ) {
				host = hostport[0];
				port = Integer.parseInt(hostport[1]);
			} else {
				throw new IllegalArgumentException("Insufficient arguments. Usage: client --server host:port");
			}
			
			Client client = new Client(host, port);
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
	}
}

