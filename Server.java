import java.net.*;
import java.io.*;

/**
 * Peer to peer java Server
 * Server will show "gochat [--server <IP>:<Port>]" to the CLI
 * Client connect to the server using the following ip address and port number
 * Server receive and send messages from and to Client
 * If user type exit to the input then the socket connection will be closed
 */
public class Server
{
	//initialize socketConnection and inputStream stream
	private Socket socketConnection = null;
	private ServerSocket serverSocket = null;
	private DataInputStream input = null;
	
	private DataInputStream inputStream = null;
	private DataOutputStream outputStream = null;
	
	// constructor with port
	public Server(int port)
	{
		// starts serverSocket and waits for a connection
		try
		{
			serverSocket = new ServerSocket(port);
			System.out.println("gochat [--server 127.0.0.1:5000]");
			System.out.println("Waiting for connection");
			
			socketConnection = serverSocket.accept();
			System.out.println("Client accepted");
			
			// takes inputStream from terminal
			inputStream = new DataInputStream(System.in);
			
			// sends output to the socketConnection
			outputStream = new DataOutputStream(socketConnection.getOutputStream());
			
			// takes inputStream from the client socketConnection
			input = new DataInputStream(
					new BufferedInputStream(socketConnection.getInputStream()));
			
			new Thread(() -> {
				try {
					while (true) {
						String receivedMessage = input.readUTF();
						System.out.println("Received from client: " + receivedMessage);
						
						if ( receivedMessage.equals("exit")) {
							System.exit(127);
						}
					}
				}catch(Exception ex) {}
			}).start();
			
			// reads message from client until "exit" is sent
			String message = "";
			while (!message.equals("exit"))
			{
				System.out.println("Enter your message: ");
				message = inputStream.readLine();
				// fix broken pipe error
				outputStream.writeUTF(message);
				
			}
			System.out.println("Closing connection");
			
		}
		catch(IOException i)
		{
			System.out.println(i);
		}
		finally {
			try {
				// close connection
				input.close();
				outputStream.close();
				socketConnection.close();
			} catch (Exception e) {
				System.out.println("Exception while closing the socket: "+e.getMessage());
			}
		}
	}
	
	public static void main(String args[])
	{
		Server server = new Server(5000);
	}
}

