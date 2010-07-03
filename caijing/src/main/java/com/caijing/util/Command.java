/*
 * Created on 2004-10-17
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */

package com.caijing.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Writer;

/**
 * @author Ayou
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class Command
{
	public static boolean run(String command, Writer out)
	{
		Command commandobj = new Command();
		try
		{
			return commandobj.process(command, out);
		}
		finally
		{
			command = null;
		}
	}

	public boolean process(String command, Writer out)
	{
		try
		{
			Runtime runtime = Runtime.getRuntime();
			Process process = runtime.exec(command);
			InputStreamReader rd = new InputStreamReader(process.getInputStream());
			BufferedReader br = new BufferedReader(rd);
			String line = null;
			while ((line = br.readLine()) != null)
			{
				if (out != null)
				{
					out.write(line);
					out.write('\n');
				}
			}
			process.destroy();
			br.close();
			rd.close();
			br = null;
			rd = null;
			process = null;
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}

	public boolean copy(String input, String output)
	{
		return process("/bin/cp -f " + input + " " + output, null);
	}

	public boolean mv(String input, String output)
	{
		return process("/bin/mv -f " + input + " " + output, null);
	}
}