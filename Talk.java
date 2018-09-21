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

  public static void runServer(int portNumber) {
    // Varibles for client to server
    int serverPortNumber = portNumber;
    String messageFromClient = null;
    Socket clientSocket = null;
    ServerSocket serverSocket = null;

    // From server to client starts
    String messageForClient = "";
    try {
      serverSocket = new ServerSocket(serverPortNumber);
      System.out.println("Server listening on port " + serverPortNumber);
    } catch (IOException e) {
      System.out.println("Could not listen on port " + serverPortNumber);
      System.exit(0);
    }
    try {
      clientSocket = serverSocket.accept();
      System.out.println("Server accepted connection from " + clientSocket.getInetAddress());
    } catch (IOException e) {
      System.out.println("Accept failed on port " + serverPortNumber);
      System.exit(0);
    }
    try {
      // To and fro continuous communication
      BufferedReader messageFromClientReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      ServerReaderThread readFromClientThread = new ServerReaderThread(messageFromClientReader);
      Thread fromClientThread = new Thread(readFromClientThread);
      fromClientThread.start();

      // From server to client
      BufferedReader outToClient = new BufferedReader(new InputStreamReader(System.in));
      PrintWriter writeToClient = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

      try {
        while (true) {
          messageForClient = outToClient.readLine();
          if (messageForClient.equals("STATUS")) {
            System.out.println(
                "Server connected to client at " + "IP address:" + clientSocket.getInetAddress().getHostAddress()
                    + "at Port:" + clientSocket.getPort() + " Server Port: " + clientSocket.getLocalPort());
          } else {
            writeToClient.println(messageForClient);
            writeToClient.flush();
          }
        }
      } catch (IOException ie) {
      }

      clientSocket.close();
      serverSocket.close();
      outToClient.close();

    } catch (IOException e) {
      // System.out.println(e);
      System.out.println("Read from client failed. Please Try Later");
      // e.printStackTrace();
      System.exit(0);
    }
    // From server to client ends
  }

  static class TalkClient {
    private static void runClient(String hostName, int portNumber) throws Exception {
      String serverName = hostName;
      ServerSocket fromServer = null;
      int serverPortNumber = portNumber;
      String messageToServer = null;
      String messageFromServer = null;

      try {
        Socket socket = new Socket(serverName, serverPortNumber);
        if (socket.isConnected()) {
          System.out.println(" Client is connected to Server ");
        }

        BufferedReader messageToServerReader = new BufferedReader(new InputStreamReader(System.in));
        BufferedReader messsageFromServerReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

        // From server to client
        ClientReaderThread readFromServerThread = new ClientReaderThread(messsageFromServerReader);
        Thread fromServerThread = new Thread(readFromServerThread);
        fromServerThread.start();

        // From Client to Server Starts
        try {
          while (true) {
            messageToServer = messageToServerReader.readLine();
            if (messageToServer.equals("STATUS")) {
              System.out.println(
                  "Client is connected to the server at" + "IP address : " + socket.getInetAddress().getHostAddress()
                      + " and port " + socket.getPort() + " Client Port Number : " + socket.getLocalPort());
            } else {
              out.println(messageToServer);
              out.flush();
            }
          }
        } catch (IOException e) {
          // e.printStackTrace();
        }

        socket.close();
      } catch (UnknownHostException e) {
        System.out.println(" Unknown host:" + serverName + " Please try later.");
        System.exit(0);
      } catch (IOException e) {
        throw e;
        // System.out.println(e);
        // System.out.println("No I/O");
        // System.exit(1);
      }
    }

  }

  public static void main(String[] args) {
    if (args.length == 0) {
      System.out.println(" Missing operation mode. Check at Talk -help");
    } else {
      String selectedOption = args[0];
      String[] options = { "-h", "-s", "-a", "-help" };
      List<String> optionsList = Arrays.asList(options);
      if (optionsList.contains(selectedOption)) {

        if (selectedOption.equals("-h")) {
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
                      // System.out.println(e);
                      System.out.println("Client unable to communicate with server. Please try later.");
                      System.exit(0);
                    }
                  } else {
                    System.out.println("Invalid Invocation. Please check Talk -help");
                  }
                } else {
                  System.out.println("Invalid Invocation. Please check Talk -help");
                }
              } else {
                System.out.println("Invalid Invocation. Please check Talk -help");
              }
            } else {
              portNumber = DEFAULT_PORT;
              try {
                TalkClient.runClient(hostName, portNumber);
              } catch (Exception e) {
                // System.out.println(e);
                System.out.println("Client unable to communicate with server. Please try later.");
                System.exit(0);
              }
            }
          } else {
            hostName = DEFAULT_HOSTNAME;
            portNumber = DEFAULT_PORT;
            try {
              TalkClient.runClient(hostName, portNumber);
            } catch (Exception e) {
              // System.out.println(e);
              System.out.println("Client unable to communicate with server. Please try later.");
              System.exit(0);
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
                  System.out.println("Invalid Invocation. Please check Talk -help");
                }
              } else {
                System.out.println("Invalid Invocation. Please check Talk -help");
              }
            } else {
              System.out.println("Invalid Invocation. Please check Talk -help");
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
                  if (args[3] != null) {
                    portNumber = Integer.parseInt(args[3]);
                    try {
                      TalkClient.runClient(hostName, portNumber);
                    } catch (Exception e) {
                      // System.out.println(e);
                      runServer(portNumber);
                    }
                  } else {
                    System.out.println("Invalid Invocation. Please check Talk -help");
                  }
                } else {
                  System.out.println("Invalid Invocation. Please check Talk -help");
                }
              } else {
                System.out.println("Invalid Invocation. Please check Talk -help");
              }
            } else {
              portNumber = DEFAULT_PORT;
              try {
                TalkClient.runClient(hostName, portNumber);
              } catch (Exception e) {
                // System.out.println(e);
                runServer(portNumber);
              }
            }
          } else {
            hostName = DEFAULT_HOSTNAME;
            portNumber = DEFAULT_PORT;
            try {
              TalkClient.runClient(hostName, portNumber);
            } catch (Exception e) {
              // System.out.println(e);
              runServer(portNumber);
            }
          }
        } else if (selectedOption.equals("-help")) {
          System.out.print("Anush Shrestha \n" + "Instruction to use program: \n"
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
          System.out.println("Invalid Invocation. Please check Talk -help");
        }
      } else {
        System.out.println("Invalid operation mode. Please check Talk -help");
      }

    }
  }

}

class ReaderThread implements Runnable {
  BufferedReader bufferReader = null;
  String message = "";

  ReaderThread(BufferedReader fromBufferReader) {
    this.bufferReader = fromBufferReader;
  }

  public void run() {
    try {
      // For continous to and fro communication
      while (true) {
        String message = bufferReader.readLine();
        System.out.println("[Remote]: " + message);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}