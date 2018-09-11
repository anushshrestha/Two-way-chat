import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author ans7740
 */
public class Talk {
  static class TalkClient {
    private static void runClient(String hostName, String portNumber) throws Exception {
      // Create socket connection
      // System.out.println("Starting TalkClient");
      // The Socket class constructor takes two parameters – a string, the IP address
      // of the server and an integer,
      // the port number on the server which the client would like to connect
      String serverName = hostName;
      // "linux1.ens.utulsa.edu";
      int serverPortNumber = Integer.parseInt(portNumber);
      String message = null;
      try {
        Socket socket = new Socket(serverName, serverPortNumber);
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        while (true) {
          message = in.readLine();
          out.println(message);
        }
      } catch (UnknownHostException e) {
        System.out.println("Unknown host:" + serverName);
        System.exit(1);
      } catch (IOException e) {
        System.out.println(e);
        System.out.println("No I/O");
        System.exit(1);
      }
    }
  }

  public static void runServer(String portNumber) {
    System.out.println("Starting TalkServer");
    BufferedReader in = null;
    int serverPortNumber = Integer.parseInt(portNumber);
    String message = null;
    Socket client = null;
    ServerSocket server = null;
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
          System.out.println(message);
        }
      }
    } catch (IOException e) {
      System.out.println("Read failed");
      System.exit(-1);
    }
  }

  public static void main(String[] args) {
    if (args.length == 0) {
      System.out.println("Missing operation mode. Check at Talk -help");
    } else {
      String selectedOption = args[0];
      String[] options = { "-h", "-s", "-a", "-help" };
      List<String> optionsList = Arrays.asList(options);
      if (optionsList.contains(selectedOption)) {
        System.out.println(selectedOption + " -h");
        if (selectedOption == "-h") {
          System.out.println("Sdfasdf");
          // TalkClient newTalkClient = new TalkClient();
          try {
            TalkClient.runClient(args[1], args[2]);
          } catch (Exception e) {
            System.out.println(e);
            System.out.println("Client unable to communicate with server");
            System.exit(1);
          }
        } else if (selectedOption == "-s") {
          runServer(args[1]);
        } else if (selectedOption == "-a") {
          // TalkClient newTalkClient = new TalkClient();
          try {
            TalkClient.runClient(args[1], args[2]);
          } catch (Exception e) {
            runServer(args[2]);
          }
          
        } else if (selectedOption == "-help") {
          System.out.print("Anush Shrestha \n" + "Instruciton to use program: \n"
          + "Talk –h [hostname | IPaddress] [–p portnumber]\n"
          + "The program behaves as a client connecting to [hostname | IPaddress] on port portnumber. "
          + "If a server is not available your program should exit with the message “Client unable to "
          + "communicate with server”. Note: portnumber in this case refers to the server and not to the "
          + "client.\n" + "Talk –s [–p portnumber]\n"
          + "The program behaves as a server listening for connections on port portnumber. If the port is"
          + "not available for use, your program should exit with the message “Server unable to listen on "
          + "specified port”.\n" + "Talk –a [hostname|IPaddress] [–p portnumber]\n"
          + "The program enters ``auto’’ mode. When in auto mode, your program should start as a client "
          + "attempting to communicate with hostname|IPaddress on port portnumber. If a server is not found,"
          + " your program should detect this condition and start behaving as a server listening for "
          + "connections on port portnumber.\n" + "Talk –help\n"
          + "The program prints your name and instructions on how to use your program.\n");
        } else {
          System.out.println("am i here");
          System.out.println("Invalid Invocation");
        }
      } else {
        System.out.println("Invalid operation mode. Check at Talk -help");
      }

    }
  }
}