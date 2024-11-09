package edu.seg2105.edu.server.backend;

import ocsf.server.*;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 */
public class EchoServer extends AbstractServer 
{
  final public static int DEFAULT_PORT = 5555;

  public EchoServer(int port) 
  {
    super(port);
  }

  @Override
  public void handleMessageFromClient(Object msg, ConnectionToClient client)
  {
    String message = msg.toString();

    try {
    	if(client.getInfo("loginId") == null) {
    		if(message.startsWith("#login ")) {
    			String loginId = message.substring(7).trim();
                client.setInfo("loginId", loginId);
                System.out.println("Client logged in with ID: " + loginId);
            } else {
                client.sendToClient("Error: First message must be #login <loginid>");
                client.close();
                return;
            }
        } else {
            String loginId = client.getInfo("loginId").toString();
            String prefixedMessage = loginId + ": " + message;
            System.out.println("Message received: " + prefixedMessage + " from " + client);
            super.sendToAllClients(prefixedMessage);
        }
    } catch (Exception e) {
        System.out.println("Error handling message from client: " + e.getMessage());
    }
  }

  private void handleCommand(String command) {
      try {
          if(command.equalsIgnoreCase("#quit")) {
            System.out.println("Server quitting...");
            System.exit(0);
          } else if(command.equalsIgnoreCase("#stop")) {
              this.stopListening();
              System.out.println("Server stopped listening for new clients.");
          } else if(command.equalsIgnoreCase("#close")) {
              this.close();
              System.out.println("Server closed and all clients disconnected.");
          } else if(command.startsWith("#setport")) {
              if(!this.isListening()) {
                String[] parts = command.split(" ");
                if(parts.length == 2) {
                    int port = Integer.parseInt(parts[1]);
                    this.setPort(port);
                    System.out.println("Port set to: " + port);
                } else {
                    System.out.println("Invalid usage. Use: #setport <port>");
                }
              } else {
                  System.out.println("Error: Cannot set port while server is listening.");
              }
          } else if(command.equalsIgnoreCase("#start")) {
              if(!this.isListening()) {
                this.listen();
                System.out.println("Server started listening for new clients.");
              } else {
                  System.out.println("Server is already listening.");
              }
          } else if(command.equalsIgnoreCase("#getport")) {
              System.out.println("Current port: " + this.getPort());
          } else {
              System.out.println("Unknown command.");
          }
      } catch (Exception e) {
          System.out.println("Command error: " + e.getMessage());
      }
  }

  @Override
  protected void serverStarted()
  {
    System.out.println("Server listening for connections on port " + getPort());
  }

  @Override
  protected void serverStopped()
  {
    System.out.println("Server has stopped listening for connections.");
  }

  @Override
  protected void clientConnected(ConnectionToClient client) {
      System.out.println("Client connected: " + client.getInetAddress().getHostAddress());
  }

  @Override
  protected void clientDisconnected(ConnectionToClient client) {
      System.out.println("Client disconnected: " + client.getInetAddress().getHostAddress());
  }

  @Override
  protected void clientException(ConnectionToClient client, Throwable exception) {
      System.out.println("Client disconnected due to error: " + client.getInetAddress().getHostAddress());
  }

  public static void main(String[] args) 
  {
    int port = DEFAULT_PORT;

    try {
      port = Integer.parseInt(args[0]);
    } catch(Throwable t) {
      port = DEFAULT_PORT;
    }
    
    EchoServer sv = new EchoServer(port);
    
    try {
      sv.listen();
    } catch (Exception ex) {
      System.out.println("ERROR - Could not listen for clients!");
    }
  }
}
// End of EchoServer class
