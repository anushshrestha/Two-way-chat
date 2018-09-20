import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author ans7740
 */
public class Talk {
  public static final int DEFAULT_PORT = 1287;
  public static final String DEFAULT_HOSTNAME = "localhost";

  static class TalkClient {
    private static void runClient(String hostName, int portNumber) throws Exception {
      // Create socket connection
      // System.out.println("Starting TalkClient");
      // The Socket class constructor takes two parameters – a string, the IP address
      // of the server and an integer,
      // the port number on the server which the client would like to connect
      String serverName = hostName;
      ServerSocket fromServer = null;
      // "linux1.ens.utulsa.edu";
      int serverPortNumber = portNumber;
      String message = null;

      // Send To server
      try {
        // Variables from server to client
        Socket socket = new Socket(serverName, serverPortNumber);
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        // Variables from client to server
        BufferedReader inFromServer = null;
        Socket socketFromServer = null;
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        String messageFromServer = null;
        while (true) {
          // From Client to Server Starts
          message = in.readLine();
          out.println(message);
          // From client to Server Ends

          // From Server to client Starts

          fromServer = new ServerSocket(serverPortNumber);
          try {
            socketFromServer = fromServer.accept();
            System.out.println("Client accepted connection from Server" + fromServer.getInetAddress());
          } catch (IOException e) {
            System.out.println("Accept from Server failed on port " + serverPortNumber);
            System.exit(-1);
          }
          try {
            inFromServer = new BufferedReader(new InputStreamReader(socketFromServer.getInputStream()));
          } catch (IOException e) {
            System.out.println("Couldn't get an inputStream from the server");
            System.exit(-1);
          }

          try {
            while (true) {
              if (inFromServer.ready()) {
                messageFromServer = inFromServer.readLine();
                System.out.println("[remote]" + messageFromServer);
              }
            }
          } catch (IOException e) {
            System.out.println("Read from server failed");
            System.exit(-1);
          }

          // From Server to client ends
        }
      } catch (UnknownHostException e) {
        System.out.println("Unknown host:" + serverName);
        System.exit(1);
      } catch (IOException e) {
        throw e;
        // System.out.println(e);
        // System.out.println("No I/O");
        // System.exit(1);
      }
    }
  }

  public static void runServer(int portNumber) {
    System.out.println("Starting TalkServer");

    // Varibles for client to server
    BufferedReader in = null;
    int serverPortNumber = portNumber;
    String message = null;
    Socket client = null;
    ServerSocket server = null;

    // Variable for server to client
    Socket socketToClient = null;
    String messageToClient = null;
    BufferedReader outToClient;

    // From client to server starts
    try {
      server = new ServerSocket(serverPortNumber);
      System.out.println("Server listening on port " + serverPortNumber);
    } catch (IOException e) {
      System.out.println("Could not listen on port " + serverPortNumber);
      System.exit(-1);
    }
    try {
      client = server.accept();
      System.out.println("Server accepted connection from " + client.getInetAddress());
    } catch (IOException e) {
      System.out.println("Accept failed on port " + serverPortNumber);
      System.exit(-1);
    }
    try {
      in = new BufferedReader(new InputStreamReader(client.getInputStream()));
    } catch (IOException e) {
      System.out.println("Couldn't get an inputStream from the client");
      System.exit(-1);
    }

    try {
      while (true) {
        if (in.ready()) {
          message = in.readLine();
          System.out.println("[remote]" + message);
        }
        // From client to server ends

        // From server to client starts

        // socketToClient = new Socket(client.getInetAddress(), client.getPort());
        // outToClient = new BufferedReader(new InputStreamReader(System.in));
        // PrintWriter out = new PrintWriter(socketToClient.getOutputStream(), true);
        // messageToClient = outToClient.readLine();
        // out.println(messageToClient);
      }
    } catch (IOException e) {
      System.out.println(e);
      System.out.println("Read from client failed");
      System.exit(-1);
    }
    // From server to client ends
  }

  public static void main(String[] args) {
    if (args.length == 0) {
      System.out.println("Missing operation mode. Check at Talk -help");
    } else {
      String selectedOption = args[0];
      // System.out.println("Args " + args.length);
      String[] options = { "-h", "-s", "-a", "-help" };
      List<String> optionsList = Arrays.asList(options);
      if (optionsList.contains(selectedOption)) {

        if (selectedOption.equals("-h")) {
          // System.out.println("Sdfasdf");
          // TalkClient newTalkClient = new TalkClient();
          String hostName;
          int portNumber;
          if (args.length > 1) {
            hostName = (args[1] != null) ? args[1] : DEFAULT_HOSTNAME;
            if (args.length > 2) {
              if (args[2].equals("-p")) {
                if (args.length > 3) {
                  if (args[3] != null) {
                    portNumber = Integer.parseInt(args[3]);
                    try {
                      TalkClient.runClient(hostName, portNumber);
                    } catch (Exception e) {
                      System.out.println(e);
                      System.out.println("Client unable to communicate with server");
                      System.exit(1);
                    }
                  } else {
                    System.out.println("Invalid Invocation");
                  }
                } else {
                  System.out.println("Invalid Invocation");
                }
              } else {
                System.out.println("Invalid Invocation");
              }
            } else {
              portNumber = DEFAULT_PORT;
              try {
                TalkClient.runClient(hostName, portNumber);
              } catch (Exception e) {
                System.out.println(e);
                System.out.println("Client unable to communicate with server");
                System.exit(1);
              }
            }
          } else {
            hostName = DEFAULT_HOSTNAME;
            portNumber = DEFAULT_PORT;
            try {
              TalkClient.runClient(hostName, portNumber);
            } catch (Exception e) {
              System.out.println(e);
              System.out.println("Client unable to communicate with server");
              System.exit(1);
            }

            // int portNumber = (args.length < 3 || args[3] != null) ? DEFAULT_PORT :
            // Integer.parseInt(args[3]);
          }
        } else if (selectedOption.equals("-s")) {
          int portNumber;
          if (args.length > 1) {
            if (args[1].equals("-p")) {
              if (args.length > 2) {
                if (args[2] != null) {
                  portNumber = Integer.parseInt(args[2]);
                  runServer(portNumber);
                } else {
                  System.out.println("Invalid Invocation");
                }
              } else {
                System.out.println("Invalid Invocation");
              }
            } else {
              System.out.println("Invalid Invocation");
            }
          } else {
            portNumber = DEFAULT_PORT;
            runServer(portNumber);
          }
        } else if (selectedOption.equals("-a")) {
          String hostName;
          int portNumber;
          if (args.length > 1) {
            hostName = (args[1] != null) ? args[1] : DEFAULT_HOSTNAME;
            if (args.length > 2) {
              if (args[2].equals("-p")) {
                if (args.length > 3) {
                  System.out.println(args[0] + args[1] + args[2]);
                  if (args[3] != null) {
                    portNumber = Integer.parseInt(args[3]);
                    try {
                      TalkClient.runClient(hostName, portNumber);
                    } catch (Exception e) {
                      System.out.println(e);
                      runServer(portNumber);
                    }
                  } else {
                    System.out.println("Invalid Invocation");
                  }
                } else {
                  System.out.println("Invalid Invocation");
                }
              } else {
                System.out.println("Invalid Invocation");
              }
            } else {
              portNumber = DEFAULT_PORT;
              try {
                TalkClient.runClient(hostName, portNumber);
              } catch (Exception e) {
                System.out.println(e);
                runServer(portNumber);
              }
            }
          } else {
            hostName = DEFAULT_HOSTNAME;
            portNumber = DEFAULT_PORT;
            try {
              TalkClient.runClient(hostName, portNumber);
            } catch (Exception e) {
              System.out.println(e);
              runServer(portNumber);
            }
          }
        } else if (selectedOption.equals("-help")) {
          System.out.print("Anush Shrestha \n" + "Instruciton to use program: \n"
              + "\nTalk -h [hostname | IPaddress] [-p portnumber]\n"
              + "The program behaves as a client connecting to [hostname | IPaddress] on port portnumber. "
              + "If a server is not available your program should exit with the message \"Client unable to "
              + "communicate with server\". Note: portnumber in this case refers to the server and not to the "
              + "client.\n" + "\nTalk -s [-p portnumber]\n"
              + "The program behaves as a server listening for connections on port portnumber. If the port is"
              + "not available for use, your program should exit with the message \"Server unable to listen on "
              + "specified port\".\n" + "\nTalk -a [hostname|IPaddress] [-p portnumber]\n"
              + "The program enters \"auto\" mode. When in auto mode, your program should start as a client "
              + "attempting to communicate with hostname|IPaddress on port portnumber. If a server is not found,"
              + " your program should detect this condition and start behaving as a server listening for "
              + "connections on port portnumber.\n" + "\nTalk -help\n"
              + "The program prints your name and instructions on how to use your program.\n");
        } else {
          System.out.println("Invalid Invocation");
        }
      } else {
        System.out.println("Invalid operation mode. Check at Talk -help");
      }

    }
  }
}