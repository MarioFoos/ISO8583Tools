package com.mlf.tools;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Clase sencilla para el manejo de logs. Ofrece enviar log a la terminal y a un archivo si se ha indicado una ruta válida
 * Tiene métodos para logear en varios niveles: información (log_info), advertencia (log_war) y error (log_err). Se le puede
 * dar un nivel de log, incluso apagado y crea dos archivos uno actual y uno anterior. El tamaño del log también puede ser establecido. 
 * @author Mario Foos
 */
public class LogTools
{
	private static final int DEF_MAX_LINE_LEN = -1;
	
	private class MyFormatter extends Formatter
	{
	    private DateFormat df;
		private int maxLineLen;

		public MyFormatter()
		{
			super();
			df = new SimpleDateFormat("HH:mm:ss");
			maxLineLen = DEF_MAX_LINE_LEN;
		}
		@Override
		public String format(LogRecord record)
		{
			StringBuffer builder = new StringBuffer();
			String line = formatMessage(record);
			if((maxLineLen > 0) && (line.length() > maxLineLen))
			{
				line = line.substring(0, maxLineLen);
			}
	        builder.append("[").append(df.format(new Date(record.getMillis()))).append("]");
	        builder.append("[").append(record.getLevel()).append("] ");
	        builder.append(line);
	        builder.append(System.getProperty("line.separator"));
        	return builder.toString();
	    }
		public void setMaxLineLen(int maxLineLen)
		{
			this.maxLineLen = maxLineLen;
		}
	}
	private static final long MIN_LOG_SIZE = 1024;
	private static final long DEF_LOG_SIZE = 1024*1024*20;
	private Logger LOGGER;
	private String logFile;
	private String logFileOld;
	private FileHandler fileHandler;
	private ConsoleHandler consoleHandler;
	private MyFormatter formatter;
	private Level logLevel;
	private long logSize;

	/**
	 * Contructor. El nivel de log se pone en Level.SEVERE por defecto por lo
	 * que solo se logean los errores. El tamaño de log por defecto se deja en 5MB
	 * @param logFile Ruta completa del archivo de log
	 */
	public LogTools(String logFile)
	{
		this(logFile, Level.SEVERE);
	}

	/**
	 * Constructor. 
	 * @param logFile Ruta completa del archivo de log
	 * @param logLevel Nivel de log
	 * Level.ALL: Todo
	 * Level.INFO: Hasta nivel de información
	 * Level.WARNING: hasta nivel de advertencias
	 * Level.SEVERE: Solo logear errores
	 * Level.OFF: Desactivar el log
	 */
	public LogTools(String logFile, Level logLevel)
	{
		this(logFile, logLevel, DEF_LOG_SIZE);
	}

	/**
	 * Constructor. El tamaño de log por defecto se deja en 5MB
	 * @param logFile Ruta completa del archivo de log
	 * @param logLevel Nivel de log
	 * Level.ALL: Todo
	 * Level.INFO: Hasta nivel de información
	 * Level.WARNING: hasta nivel de advertencias
	 * Level.SEVERE: Solo logear errores
	 * Level.OFF: Desactivar el log
	 * @param logSize Tamaño del log
	 */
	public LogTools(String logFile, Level logLevel, long logSize)
	{
		this(logFile, logLevel, logSize, DEF_MAX_LINE_LEN);
	}
	
	/**
	 * Constructor. El tamaño de log por defecto se deja en 5MB
	 * @param logFile Ruta completa del archivo de log
	 * @param logLevel Nivel de log
	 * Level.ALL: Todo
	 * Level.INFO: Hasta nivel de información
	 * Level.WARNING: hasta nivel de advertencias
	 * Level.SEVERE: Solo logear errores
	 * Level.OFF: Desactivar el log
	 * @param logSize Tamaño del log
	 * @param lineSize Tamaño de la línea del log
	 */
	public LogTools(String logFile, Level logLevel, long logSize, int lineSize)
	{
		formatter = new MyFormatter();
		formatter.setMaxLineLen(lineSize);
    	String[] tokens = logFile.split("[\\\\|/]");
    	String filename = tokens[tokens.length - 1];
		LOGGER = Logger.getLogger(LogTools.class.getName()+ "." + filename);
		setLogFile(logFile);
		setLogSize(logSize);
		this.logLevel = logLevel;
    	
	}

	private void TestLogSize()
	{
		File fileLog = new File(logFile);
		if(fileLog.length() > logSize)
		{
			// Remuevo el handler del archivo
			fileHandler.flush();
			fileHandler.close();
			LOGGER.removeHandler(fileHandler);
			
			// Borro el viejo
			File fileOld = new File(logFileOld);
			if(fileOld.isFile() && fileOld.exists())
			{
				fileOld.delete();
			}
			fileLog.renameTo(fileOld);

			// Agrego el handler nuevo
			try
			{
				fileHandler = new FileHandler(logFile, true);
				fileHandler.setFormatter(formatter);
				LOGGER.addHandler(fileHandler);
			}
			catch(SecurityException e)
			{
				e.printStackTrace();
				return;
			}
			catch(IOException e)
			{
				e.printStackTrace();
				return;
			}
		}
	}

	private void setLogFile(String logFile)
	{
		if(logFile == null)
		{
			Log.err("invalid log file name: " + logFile);
			return;
		}
		// Remover todos los handler
		if(LOGGER.getParent() != null)
		{
			for(Handler h : LOGGER.getParent().getHandlers())
			{
				LOGGER.getParent().removeHandler(h);
			}
		}
		for(Handler h : LOGGER.getHandlers())
		{
			LOGGER.removeHandler(h);
		}
		// Crear handler de file
		this.logFile = logFile;
		this.logFileOld = this.logFile + ".old" ;
		try
		{
			fileHandler = new FileHandler(this.logFile, true);
			fileHandler.setFormatter(formatter);
		}
		catch(SecurityException e)
		{
			e.printStackTrace();
			return;
		}
		catch(IOException e)
		{
			e.printStackTrace();
			return;
		}
		// Crear handler de consola
		consoleHandler = new ConsoleHandler();
		consoleHandler.setFormatter(formatter);
		
		// Agregar los handler
		LOGGER.addHandler(consoleHandler);
		LOGGER.addHandler(fileHandler);
		LOGGER.setLevel(Level.INFO);
	}

	private static String stackTraceToString(Throwable e)
	{
	    StringBuilder sb = new StringBuilder();
		if(e != null)
		{
			StackTraceElement[] elements = e.getStackTrace();
			if(elements != null)
			{
			    for(StackTraceElement element : e.getStackTrace())
			    {
			        sb.append(element.toString());
			        sb.append("\n");
			    }
			}
			else
			{
				sb.append("elements == null");
			}
		}
		else
		{
			sb.append("e == null");
		}
	    return sb.toString();
	}	

	/**
	 * Elimina los archivos de log
	 */
	public void RemoveLogs()
	{
		// Remuevo el handler del archivo
		fileHandler.flush();
		fileHandler.close();
		LOGGER.removeHandler(fileHandler);
		
		// Borro el viejo
		File fileOld = new File(logFileOld);
		if(fileOld.isFile() && fileOld.exists())
		{
			fileOld.delete();
		}
		// Borro el actual
		File fileLog = new File(logFile);
		fileLog.delete();
		
		// Agrego el handler nuevo
		try
		{
			fileHandler = new FileHandler(logFile, true);
			fileHandler.setFormatter(formatter);
			LOGGER.addHandler(fileHandler);
		}
		catch(SecurityException e)
		{
			e.printStackTrace();
			return;
		}		
		catch(IOException e)
		{
			e.printStackTrace();
			return;
		}		
	}

	/**
	 * Obtener la ruta completa del archivo de log
	 * @return Ruta completa del archivo de log
	 */
	public String getLogFile()
	{
		return logFile;
	}
	
	/**
	 * Obtener la ruta completa del archivo de log viejo
	 * @return Ruta completa del archivo de log viejo
	 */
	public String getLogFileOld()
	{
		return logFileOld;
	}
	
	/**
	 * Establecer el tamaño del archivo de log máximo (al superarse se guarda como viejo y se empieza con otro los)
	 * @param logSize Tamaño en bytes del log (mínimo permitido 1kb)
	 */
	public void setLogSize(long logSize)
	{
		this.logSize = (logSize < MIN_LOG_SIZE) ? MIN_LOG_SIZE : logSize;
	}
	
	/**
	 * Obtener el tamaño máximo en bytes del archivo de log
	 * @return Tamaño máximo del log
	 */
	public long getLogSize()
	{
		return logSize;
	}

	/**
	 * Establecer la máxima longitud de línes del log
	 * @param maxLineLen Establecer la máxima longitus de línea del log
	 */
	public void setMaxLineLen(int maxLineLen)
	{
		formatter.setMaxLineLen(maxLineLen);
	}

	/**
	 * Escribir en el log una cadena en la categoría de información
	 * @param str Cadena a logear
	 */
	public void info(String str)
	{
		if(str == null || str.isEmpty())
		{
			return;
		}
		if(!logFile.isEmpty() && logLevel.intValue() <= Level.INFO.intValue())
		{
			TestLogSize();
			LOGGER.log(Level.INFO, str);
		}
	}

	/**
	 * Escribir en el log una cadena en la categoría de advertencia
	 * @param str Cadena a logear
	 */
	public void war(String str)
	{
		if(str == null || str.isEmpty())
		{
			return;
		}
		if(!logFile.isEmpty() && logLevel.intValue() <= Level.WARNING.intValue())
		{
			TestLogSize();
			LOGGER.log(Level.WARNING, str);
		}
	}

	/**
	 * Escribir en el log una cadena en la categoría de error
	 * @param str Cadena a logear
	 */
	public void err(String str)
	{
		if(str == null || str.isEmpty())
		{
			return;
		}
		if(!logFile.isEmpty() && logLevel.intValue() <= Level.SEVERE.intValue())
		{
			TestLogSize();
			LOGGER.log(Level.SEVERE, str);
		}
	}

	/**
	 * Escribir en el log una cadena en la categoría de error
	 * @param e Error a logear
	 */
	public void err(Throwable e)
	{
		if(!logFile.isEmpty() && logLevel.intValue() <= Level.SEVERE.intValue())
		{
			TestLogSize();
			LOGGER.log(Level.SEVERE, stackTraceToString(e));
		}
	}
	
	/**
	 * Establecer el nivel de log
	 * Level.ALL: Todo
	 * Level.INFO: Hasta nivel de información
	 * Level.WARNING: hasta nivel de advertencias
	 * Level.SEVERE: Solo logear errores
	 * Level.OFF: Desactivar el log
	 * @param logLevel Nivel del log
	 */
	public void setLogLevel(Level logLevel)
	{
		RemoveLogs();
		this.logLevel = logLevel;
	}
	
	/**
	 * Obtener el nivel del log
	 * @return Nivel del log
	 */
	public Level getLogLevel()
	{
		return logLevel;
	}
}
