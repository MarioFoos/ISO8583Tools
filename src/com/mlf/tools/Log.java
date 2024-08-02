package com.mlf.tools;

import java.util.logging.Level;

/**
 * Log global
 * @author Mario
 */
public class Log
{
	private static LogTools log = null;
	
	/**
	 * Log info
	 * @param message Mensaje
	 */
	public static void info(String message)
	{
		if(log != null)
		{
			Log.log.info(message);
		}
		else
		{
			System.out.println(message);
		}
	}

	/**
	 * Log warning
	 * @param message Mensaje
	 */
	public static void war(String message)
	{
		if(log != null)
		{
			Log.log.war(message);
		}
		else
		{
			System.out.println(message);
		}
	}
	
	/**
	 * Log error
	 * @param message Mensaje
	 */
	public static void err(String message)
	{
		if(log != null)
		{
			Log.log.err(message);
		}
		else
		{
			System.out.println(message);
		}
	}

	/**
	 * Establecer el objeto de log
	 * @param log Log
	 */
	public static void setLog(LogTools log)
	{
		Log.log = log;
	}
	/**
	 * Establecer el objeto de log
	 * @param file Nombre del archivo
	 */
	public static void setLog(String file)
	{
		Log.log = new LogTools("./" + file, Level.ALL);
	}
}
