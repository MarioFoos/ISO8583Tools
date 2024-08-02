package com.mlf.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Utilidades de archivo
 * @author mario
 */
public class FileTools
{
	/**
	 * Leer l�neas de un archivo
	 * @param is Flujo de entrada
	 * @return L�neas le�das
	 */
	public static ArrayList<String> readLines(FileInputStream is)
	{
		return readLines(is, false);
	}

	/**
	 * Leer l�neas de un archivo
	 * @param file Archivo
	 * @param excludeEmpty Indica si omitir la l�neas vac�as
	 * @return L�neas le�das
	 */
	public static ArrayList<String> readLines(File file, boolean excludeEmpty)
	{
		FileInputStream is;
		try
		{
			is = new FileInputStream(file);
			return readLines(is, excludeEmpty);
		}
		catch(FileNotFoundException e)
		{
			e.printStackTrace();
		}
		return new ArrayList<String>();
	}

	/**
	 * Leer l�neas de un archivo
	 * @param is Flujo de archivo de entrada
	 * @param excludeEmpty Indica si omitir la l�neas vac�as
	 * @return Lista de l�neas
	 */
	public static ArrayList<String> readLines(FileInputStream is, boolean excludeEmpty)
	{
		ArrayList<String> lines = new ArrayList<String>();
        try
		{
        	BufferedReader br = new BufferedReader(new FileReader(is.getFD()));
    		String line;
	        while((line = br.readLine()) != null)
	        {
	        	if(line.isEmpty() && excludeEmpty)
	        	{
	        		continue;
	        	}
	        	lines.add(line);
	        }
	        br.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}		
        return lines;
	}
	
	/**
	 * Escribir array de strings en el stream de salida
	 * @param os Stream de salida
	 * @param lines L�neas
	 * @return L�neas escritas o -1
	 */
	public static int writeLines(OutputStream os, String[] lines)
	{
		return writeLines(os, lines, false);
	}

	/**
	 * Escribir array de strings en el stream de salida
	 * @param os Stream de salida
	 * @param lines L�neas
	 * @param excludeEmpty Excluir l�neas vac�as
	 * @return L�neas escritas o -1
	 */
	public static int writeLines(OutputStream os, String[] lines, boolean excludeEmpty)
	{
		ArrayList<String> arrLines = new ArrayList<>();
		arrLines.addAll(Arrays.asList(lines));
		return writeLines(os, arrLines, excludeEmpty);
	}

	/**
	 * Escribir array de strings en el stream de salida
	 * @param os Stream de salida
	 * @param lines L�neas
	 * @return L�neas escritas o -1
	 */
	public static int writeLines(OutputStream os, ArrayList<String> lines)
	{
		return writeLines(os, lines, false);
	}

	/**
	 * Escribir array de strings en el stream de salida
	 * @param os Stream de salida
	 * @param lines L�neas
	 * @param excludeEmpty Excluir l�neas vac�as
	 * @return L�neas escritas o -1
	 */
	public static int writeLines(OutputStream os, ArrayList<String> lines, boolean excludeEmpty)
	{
		StringBuilder sb = new StringBuilder();
		String eol = System.getProperty("line.separator");
		int count = 0;
		if(excludeEmpty)
		{
			for(String line : lines)
			{
				if(line.isEmpty())
				{
					continue;
				}
				sb.append(line).append(eol);
				++count;
			}
		}
		else
		{
			for(String line : lines)
			{
				sb.append(line).append(eol);
				++count;
			}
		}
		try
		{
			os.write(sb.toString().getBytes());
			return count;
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		return -1;
	}

}
