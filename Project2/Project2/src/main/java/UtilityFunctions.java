package main.java;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * This class contains utility functions that don't belong in any specific class.
 *
 * @author tsitsg,maurez
 *
 */
public class UtilityFunctions {
	private static final String varFilePath = "./src/main/resources/test";

	/**
	 * Checks to see if system is recovering from failure or starting fresh.
	 *
	 * @param name	The name of the user at this site. Assuming usernames unique, this file is unique
	 * @return true if no file is found, false otherwise
	 */
	public static boolean freshStart(String name) {
		File f = new File(varFilePath+name + ".dat").getAbsoluteFile();
		try {
			return f.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	//Checks if the file is empty
	public static boolean isEmpty(String name)
	{
		File f = new File(varFilePath+name + ".dat").getAbsoluteFile();
		if (f.length()>0)
		{
			return false;
		}
		return true;
	}
	/**
	   * Write log, dictionary, local clock, matrix clock to disk
	   *
	   * @param		location of the file to write to
	   */
	  public static void writeVars(SiteVariables vars) {
		  try {
			FileOutputStream fout = new FileOutputStream(varFilePath + vars.getMySite().getName() + ".dat");
			ObjectOutputStream out = new ObjectOutputStream(fout);
			out.writeObject(vars);
			out.close();
			fout.close();
		  }catch(FileNotFoundException ex){
				return;
			}catch(IOException i) {
			i.printStackTrace();
		  }
	  }


	  /**
	   * Read log, dictionary, local clock, matrix clock from disk
	   *
	   * @param		location of the file to write to
	   * @return	A UtilityVariables object containing the information
	   */
	  public static SiteVariables readVars(Site mySite) {
	  	SiteVariables v = null;

		try {
			FileInputStream fin = new FileInputStream(varFilePath + mySite.getName() + ".dat");
			ObjectInputStream in = new ObjectInputStream(fin);
			v = (SiteVariables) in.readObject();
			in.close();
			fin.close();

		}
		catch(FileNotFoundException ex)
		{
			return null;
		}
		catch(IOException i) {
			i.printStackTrace();
			return null;
		} catch(ClassNotFoundException c) {
			c.printStackTrace();
			return null;
		}

		return v;
	  }
}
