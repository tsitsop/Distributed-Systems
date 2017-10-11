//http://tutorials.jenkov.com/java-concurrency/synchronized.html
//Do the synchronized counter thing


import java.io.*;
import java.net.*;
import java.util.*;

class addPair{
  public String ip;
  public int port;
  public addPair(){};
  public addPair(String i, int x)
  {
    this.ip = i;
    this.port = x;
  }
}

class Pair<X,Y>{
 public final X x;
 public final Y y;
 public Pair(X x, Y y) {
   this.x = x;
   this.y = y;
 }
}

class event{
  public String type;
  public String p1;
  public String p2;
  public UUID id;
  event(UUID i,String t, String x,String y)
  {
    id = i;
    type = t;
    p1 = x;
    p2 = y;
  }
  public String toString()
  {
    return "Event: "+type+": "+p1+":"+p2;
  }
}


class tweetLog{
  tweetLog()
  {

  }
}

class tweetDict{

  tweetDict()
  {

  }
}


class getTweets implements Runnable{
  private Thread t;
  private String threadName;
  private addPair loc;

  getTweets(String name,addPair p)
  {
    threadName = name;
    loc = p;
    System.out.println("Creating "+ threadName);
  }

  public void run()
  {
    System.out.println("Running " +  threadName );
      try
      {
        ServerSocket sSocket = new ServerSocket(loc.port);
        String s1;
        String s2;



        while (true)
        {
          System.out.println("Server waiting for client");
          Socket cSocket = sSocket.accept();
          System.out.println("Server accepted client");
          BufferedReader inFromClient = new BufferedReader(new InputStreamReader(cSocket.getInputStream()));
          DataOutputStream outToClient = new DataOutputStream(cSocket.getOutputStream());
          System.out.println("Server Waiting for message");
          s1 = inFromClient.readLine();
          System.out.println("recieved: " + s1);
          //Send reply tweets

        }
      }catch (IOException e)
      {
        System.out.println(e);
      }

      //
      System.out.println("Thread " +  threadName + " exiting.");
  }

  public void start()
  {
    System.out.println("Starting " +  threadName+". Listening on port: "+loc.port);
      if (t == null) {
         t = new Thread (this, threadName);
         t.start ();
      }
  }
}

class p1{

  public static boolean isBlocked(String s1,String s2,Map<UUID,event> dict)
  {
    for ( event e : dict.values())
    {
      if (e.type.equals("block") && e.p1.equals(s1) && e.p2.equals(s2))
      {
        return true;
      }
    }
    return false;
  }


  public static void main(String args[]) throws Exception{

    int id = 0;
    String username = "";
    ArrayList<addPair> addrs = new ArrayList<addPair>();
    addPair myAddr = new addPair();
    getTweets tServer;
    event e;

    try
    {
      id = Integer.parseInt(args[0]);
    }
    catch(NumberFormatException ex)
    {
      System.err.println("Argument" + args[0] + " must be an integer.");
      System.exit(1);
    }

    BufferedReader br;
    String fLine;
    String[] lParts;
    int lineCount = 0;
    try {
            br = new BufferedReader( new FileReader("hosts.txt"));
            while((fLine = br.readLine()) != null)
            {
                lParts = fLine.split(" ");

                if (lineCount == id)
                {
                  myAddr.ip =lParts[0];
                  myAddr.port =Integer.parseInt(lParts[1]);
                  username = lParts[2];
                }
                else
                {
                  addrs.add(new addPair(lParts[0],Integer.parseInt(lParts[1])));
                }
                lineCount++;
            }
        }
        catch (Exception ex)
        {
            System.err.println(ex);
        }
    //testing
    tServer = new getTweets("Tweet Server",myAddr);


    ArrayList<event> exampleLog = new ArrayList<event>();

    Map<UUID,event> blockDict = new HashMap<>();

    /*exampleLog.add(new event(UUID.randomUUID(),"tweet","zach","testing code, LOL!"));
    exampleLog.add(new event(UUID.randomUUID(),"tweet","george","dude im trying to sleep"));
    exampleLog.add(new event(UUID.randomUUID(),"tweet","zach","I dont care"));
    e = new event(UUID.randomUUID(),"block","george","zach");
    exampleLog.add(e);
    blockDict.put(UUID.randomUUID(),e);
    exampleLog.add(new event(UUID.randomUUID(),"tweet","george","just blocked zach, time to sleep!"));
    exampleLog.add(new event(UUID.randomUUID(),"tweet","shane","you both are idiots"));*/

  //


    BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
    String in;
    tServer.start();
    while(true)
    {
      in = inFromUser.readLine();
      if (in.equals("tweet"))
      {

      }
      if (in.equals("view"))
      {
        for (int i=0;i<exampleLog.size();i++)
        {
          e = exampleLog.get(i);
          if (e.type.equals("tweet")&&(!isBlocked(e.p1,username,blockDict)))
          {
            System.out.println(e);
          }
        }
      }
    }


  }
}
