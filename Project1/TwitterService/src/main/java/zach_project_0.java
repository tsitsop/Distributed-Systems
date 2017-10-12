//package main.java;
//
//// ECHO SERVER FOR PROJECT 0
//import java.io.*;
//import java.net.*;
//import java.util.*;
//
//class addPair{
//  public String ip;
//  public int port;
//  public addPair(String i, int x)
//  {
//    this.ip = i;
//    this.port = x;
//  }
//
//}
//
//
//
//class echoServer implements Runnable{
//  private Thread t;
//  private String threadName;
//  private addPair client;
//
//  echoServer(String name,addPair c)
//  {
//    threadName = name;
//    client = c;
//    //System.out.println("Creating "+ threadName);
//  }
//
//
//
//  public void run()
//  {
//    //System.out.println("Running " +  threadName );
//      try
//      {
//        ServerSocket echoSocket = new ServerSocket(client.port);
//        String s1;
//        String s2;
//
//
//
//        while (true)
//        {
//          //System.out.println("Echo Server waiting for client");
//          Socket connectionSocket = echoSocket.accept();
//          //System.out.println("Echo Server accepted client");
//          BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
//          DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
//          //System.out.println("Echo Server Waiting for message");
//          s1 = inFromClient.readLine();
//          System.out.println("recieved: " + s1);
//          s2 = threadName+" reply: "+s1;
//          outToClient.writeBytes(s2+"\n");
//          //System.out.println("Echo Server sent: " + s2);
//          //echoSocket.close();
//        }
//      }catch (IOException e)
//      {
//        System.out.println(e);
//      }
//
//      //
//      //System.out.println("Thread " +  threadName + " exiting.");
//  }
//
//  public void start()
//  {
//    //System.out.println("Starting " +  threadName+". Listening on port: "+client.port);
//      if (t == null) {
//         t = new Thread (this, threadName);
//         t.start ();
//      }
//  }
//
//}
//
//
//class requestServer implements Runnable{
//  private Thread t;
//  private String threadName;
//  private addPair client;
//  private String message;
//
//  requestServer (String name,addPair c)
//  {
//    threadName = name;
//    client = c;
//    //System.out.println("Creating "+ threadName);
//  }
//
//
//
//  public void run()
//  {
//    //System.out.println("Running " +  threadName );
//      try
//      {
//        Socket clientSocket = new Socket(client.ip,client.port);
//        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
//        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//
//        //System.out.println("Request Server Writing Message to port: "+client.port);
//        outToServer.writeBytes(message+"\n");
//        //System.out.println("Request Server wrote Message to port: "+client.port);
//        String s1 = inFromServer.readLine();
//        //System.out.println("Request Server got Message from port: "+client.port);
//        System.out.println(s1);
//        clientSocket.close();
//
//      }catch (IOException e)
//      {
//        System.out.println(e);
//      }
//
//      //
//      //System.out.println("Thread " +  threadName + " exiting.");
//  }
//
//  public void start(String m)
//  {
//    message = m;
//    //System.out.println("Starting " +  threadName);
//      if (t == null) {
//         t = new Thread (this, threadName);
//         t.start ();
//      }
//  }
//
//}
//
//
//
//class Server{
//
//  public static void main(String args[]) throws Exception{
//
//    int id = 0;
//    ArrayList<addPair> addrs = new ArrayList<addPair>();
//    ArrayList<requestServer> servers = new ArrayList<requestServer>();
//    echoServer echo;
//
//    try{
//
//      id = Integer.parseInt(args[0]);
//
//    }catch(NumberFormatException e)
//    {
//      System.err.println("Argument" + args[0] + " must be an integer.");
//       System.exit(1);
//    }
//
//    addrs.add(new addPair("localhost",7000));
//    addrs.add(new addPair("localhost",7001));
//    addrs.add(new addPair("localhost",7002));
//
//    echo = new echoServer("Server "+id,addrs.get(id));
//
//    echo.start();
//    //client
//
//    String sentence;
//    BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
//    //
//    //
//    while(true)
//    {
//      //
//      servers.clear();
//      for (int i=0;i<3;i++)
//      {
//        if (i==id)
//        {
//          continue;
//        }
//        servers.add(new requestServer("Request: "+i,addrs.get(i)));
//      }
//      //
//      sentence = inFromUser.readLine();
//      for (int i =0;i<servers.size();i++)
//      {
//        servers.get(i).start(sentence);
//      }
//
//      //
//    }
//
//  }
//
//}
