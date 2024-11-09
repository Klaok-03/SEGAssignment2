package edu.seg2105.client.ui;

import java.io.*;
import java.util.Scanner;

import edu.seg2105.client.backend.ChatClient;
import edu.seg2105.client.common.*;

/**
 * This class constructs the UI for a chat client. It implements the
 * chat interface in order to activate the display() method.
 */
public class ClientConsole implements ChatIF 
{
  final public static int DEFAULT_PORT = 5555;

  ChatClient client;
  Scanner fromConsole; 

  /**
   * Constructs an instance of the ClientConsole UI.
   *
   * @param loginId The login ID for the client.
   * @param host The host to connect to.
   * @param port The port to connect on.
   */
  public ClientConsole(String loginId, String host, int port) 
  {
    try 
    {
      client = new ChatClient(loginId, host, port, this);
    } 
    catch(IOException exception) 
    {
      System.out.println("Error: Can't setup connection! Terminating client.");
      System.exit(1);
    }
    
    fromConsole = new Scanner(System.in); 
  }

  public void accept() 
  {
    try
    {
      String message;

      while (true) 
      {
        message = fromConsole.nextLine();
        
        if(message.startsWith("#")) {
          handleCommand(message);
        } else {
          client.sendToServer(message);
        }
      }
    } 
    catch (Exception ex) 
    {
      System.out.println("Unexpected error while reading from console!");
    }
  }
  
  private void handleCommand(String command) {
    try {
      if(command.equalsIgnoreCase("#quit")) {
        client.closeConnection();
        System.exit(0);
      } else if(command.equalsIgnoreCase("#logoff")) {
    	  client.closeConnection();
      } else if(command.startsWith("#sethost")) {
    	  if(!client.isConnected()) {
    		String[] parts = command.split(" ");
    		if(parts.length == 2) {
    		  client.setHost(parts[1]);
    		  System.out.println("Host set to: " + parts[1]);
    		} else {
    			System.out.println("Invalid usage. Use: #sethost <host>");
    		}
    	  } else {
    		  System.out.println("Error: Cannot set host while connected.");
    	  }
      } else if(command.startsWith("#setport")) {
    	  if(!client.isConnected()) {
    		String[] parts = command.split(" ");
    		if(parts.length == 2) {
    		  int port = Integer.parseInt(parts[1]);
    		  client.setPort(port);
    		  System.out.println("Port set to: " + port);
    		} else {
    			System.out.println("Invalid usage. Use: #setport <port>");
    		}
    	  } else {
    		  System.out.println("Error: Cannot set port while connected.");
    	  }
      } else if(command.equalsIgnoreCase("#login")) {
    	  if(!client.isConnected()) {
    		client.openConnection();
    	  } else {
    		  System.out.println("Error: Already connected.");
    	  }
      } else if(command.equalsIgnoreCase("#gethost")) {
    	  System.out.println("Current host: " + client.getHost());
      } else if(command.equalsIgnoreCase("#getport")) {
    	  System.out.println("Current port: " + client.getPort());
      } else {
    	  System.out.println("Unknown command.");
      }
    } catch (Exception e) {
    	System.out.println("Command error: " + e.getMessage());
    }
  }

  @Override
  public void display(String message) 
  {
    System.out.println("> " + message);
  }

  public static void main(String[] args) 
  {
    if (args.length < 1) {
      System.out.println("Error: Login ID is mandatory.");
      System.out.println("Usage: java ClientConsole <login id> [host] [port]");
      System.exit(1);
    }

    String loginId = args[0];
    String host = (args.length > 1) ? args[1] : "localhost";
    int port = (args.length > 2) ? Integer.parseInt(args[2]) : DEFAULT_PORT;

    ClientConsole chat = new ClientConsole(loginId, host, port);
    chat.accept();
  }
}
// End of ClientConsole class