package main.java;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.io.Serializable;

import main.java.UtilityVariables;

public class VarsWriter
{
  private static UtilityVariables u;
  VarsWriter(UtilityVariables t)
  {
    u = t;
  }
  public static void write(String s)
  {
    String path = new File(s).getAbsolutePath();
		try {
			FileOutputStream fout = new FileOutputStream(path);
			ObjectOutputStream out = new ObjectOutputStream(fout);
			out.writeObject(u);
			out.close();
			fout.close();
		} catch(IOException i) {
			i.printStackTrace();
		}
  }
  public static UtilityVariables read(String s)
  {
  		String path = new File(s).getAbsolutePath();
  		UtilityVariables u = new UtilityVariables(0);
  		try
  		{
  			FileInputStream fin = new FileInputStream(s);
  			ObjectInputStream in = new ObjectInputStream(fin);
  			u = (UtilityVariables) in.readObject();
  			in.close();
  			fin.close();
  		}
  		catch(IOException i)
  		{
  			i.printStackTrace();
  			return u;
  		}
  		catch(ClassNotFoundException c)
  		{
  			System.out.println("Employee class not found");
  			c.printStackTrace();
  			return u;
  		}
  		return u;
  }
}
