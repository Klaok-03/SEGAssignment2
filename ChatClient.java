package edu.seg2105.client.backend;

import ocsf.client.*;
import java.io.*;
import edu.seg2105.client.common.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 */
public class ChatClient extends AbstractClient
{
  // Instance variables **********************************************
  
  /**
   * The interface type variable. It allows the implementation of 
   * the display method in the client.
   */
  private ChatIF clientUI; 
  private String loginId;
  
  // Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param loginId The login ID for the client.
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  public ChatClient(String loginId, String host, int port, ChatIF clientUI) 
    throws IOException 
  {
    super(host, port); // Call the superclass constructor
    this.clientUI = clientUI;
    this.loginId = loginId;
    openConnection(); // Open connection after setting loginId
  }
  
  public String getLoginId() {
    return loginId;
  }

  // Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message)
  {
    try
    {
      sendToServer(message);
    }
    catch(IOException e)
    {
      clientUI.display("Could not send message to server. Terminating client.");
      quit();
    }
  }
  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {
      clientUI.display("Error closing connection.");
    }
    System.exit(0);
  }
  
  @Override
  protected void connectionEstablished() {
    // Automatically send #login <loginId> message to the server upon connection
    try {
      sendToServer("#login " + loginId);
    } catch (IOException e) {
      clientUI.display("Error: Unable to send login message to server.");
    }
  }
  
  @Override
  protected void connectionClosed() {
    clientUI.display("The server has shut down. Closing connection.");
    System.exit(0); // Exit the client
  }

  @Override
  protected void connectionException(Exception exception) {
    clientUI.display("Connection error: The server has shut down.");
    System.exit(0); // Exit the client
  }
}
// End of ChatClient class