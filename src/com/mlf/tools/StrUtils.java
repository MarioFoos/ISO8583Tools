package com.mlf.tools;

/**
 * Clase con funciones de manejo de cadenas
 * Las funciones de conversión siguen los nombres:
 * xxx2yyy donde xxx y yyy pueden valer:
 * Num: Entero (p.e: 1234)
 * Str: String (p.e: "1234")
 * HexStr: String con datos hexa (p.e: "01AF03")
 * Buf: Array de bytes (p.e: { 0x05, 0x1F, 0x24, 0xF4, 0xA5, 0x5B } )
 * Log: String con formato de log de buffer
 * @author Mario
 *
 */
public class StrUtils
{
	private static final int LOG_COLUMNS = 16;
	private static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	/**
	 * Formatear un monto en centavos como una moneda 
	 * @param symbol Moneda
	 * @param value Monto en centavos
	 * @return Cadena formateada
	 */
	public static String formatAmount(String symbol, long value)
	{
		String formatted;
		if(value < 100)
		{
			formatted = symbol + String.format("0.%02d", value);
			return formatted;
		}
		formatted = symbol + String.format("%d.%02d", value/100, value%100);
		return formatted;
	}
	
	/**
	 * Establecer el valor del caracter en una cadena
	 * @param src Cadena de origen
	 * @param pos Posición a cambiar
	 * @param car Caracter
	 * @return Cadena modificada
	 */
	public static String setCharAt(String src, int pos, char car)
	{
		if(pos < 0 || pos >= src.length())
		{
			return src;
		}
		String out = src.substring(0, pos) + car + src.substring(pos);
		return out;
	}

	/**
	 * Logitud de la cadena como array de bytes
	 * @param src Cadena de entrada
	 * @return Longitud
	 */
	public static int len(byte[] src)
	{
		for(int i = 0; i < src.length; ++i)
		{
			if(src[i] == 0)
			{
				return i;
			}
		}
		return src.length;
	}
	
	//--------------------------- Subarray --------------------------------------------------------

	/**
	 * Obtener un sub array de bytes
	 * @param in Array de entrada
	 * @param from Índice de inicio
	 * @return Sub array
	 */
	public static byte[] sub(byte[] in, int from)
	{
		int len = in.length - from;
		byte[] out = new byte[len];

		for(int i = 0; i < len; ++i)
		{
			if(from + i < in.length)
			{
				out[i] = in[from + i];
			}
			else
			{
				break;
			}
		}
		return out;
	}
	/**
	 * Obtener un sub array de bytes
	 * @param in Array de entrada
	 * @param from Índice de inicio
	 * @param to índice final
	 * @return Sub array
	 */
	public static byte[] sub(byte[] in, int from, int to)
	{
		int len = to - from;
		byte[] out = new byte[len];

		for(int i = 0; i < len; ++i)
		{
			if(from + i < in.length)
			{
				out[i] = in[from + i];
			}
			else
			{
				break;
			}
		}
		return out;
	}
	//----- Generate ------------------------------------------------------------------------------
	
	/**
	 * Obtener un array de bytes con el valor dado
	 * @param car Caracter del array a generar
	 * @param len Longitud
	 * @return Array de bytes
	 */
	public static byte[] arrOf(int car, int len)
	{
		if(len <= 0)
		{
			return new byte[0];
		}
		byte[] out = new byte[len];
		for(int i = 0; i < len ; ++i)
		{
			out[i] = (byte) car;
		}
		return out;
	}

	/**
	 * Obtener una cadena con el valor dado
	 * @param car Caracter del array a generar
	 * @param len Longitud
	 * @return Cadena
	 */
	public static String strOf(int car, int len)
	{
		StringBuilder out = new StringBuilder();
		for(int i = 0; i < len ; ++i)
		{
			out.append((char) car);
		}
		return out.toString();
	}
	
	//---------------- Conversión -----------------------------------------------------------------

	/**
	 * Valor numérico a cadena que lo representa (p.e. 123 -> "123")
	 * @param value Valor entero
	 * @param len Longitud de la cadena de salida, se rellena con ceros a la izquierda o se recorta para ajustar
	 * @return Cadena que representa el valor numérico
	 */
	public static String toString(int value, int len)
	{
		return toString((long) value, len);
	}

	/**
	 * Valor numérico a cadena que lo representa (p.e. 123 -> "123")
	 * @param value Valor entero
	 * @param len Longitud de la cadena de salida, se rellena con ceros a la izquierda o se recorta para ajustar
	 * @return Cadena que representa el valor numérico
	 */
	public static String toString(long value, int len)
	{
		return adjNum(Long.toString(value), len);
	}

	/**
	 * Valor numérico a cadena hexa que lo representa 1234 (= 4D2) -> "4D2"
	 * @param value Valor entero
	 * @param len Longitud de la cadena de salida, se rellena con ceros a la izquierda o se recorta para ajustar
	 * @return Cadena hexadecimal que representa el valor numérico
	 */
	public static String toHexString(int value, int len)
	{
		return toHexString((long) value, len);
	}

	/**
	 * Valor numérico a cadena hexa que lo representa 1234 (= 4D2) -> "4D2"
	 * @param value Valor entero
	 * @param len Longitud de la cadena de salida, se rellena con ceros a la izquierda o se recorta para ajustar
	 * @return Cadena hexadecimal que representa el valor numérico
	 */
	public static String toHexString(long value, int len)
	{
		return adjNum(Long.toHexString(value).toUpperCase(), len);
	}
	
	/**
	 * Convertir un array de bytes a una cadena hexadecimal (p.e. [24 A0 F1] -> "24A0F1")
	 * @param buf Array de bytes
	 * @return Cadena hexa
	 */
	public static String toHexString(byte[] buf)
	{
		return toHexString(buf, (buf == null) ? 0 : buf.length);
	}

	/**
	 * Convertir un array de bytes a una cadena hexadecimal (p.e. [24 A0 F1] -> "24A0F1")
	 * @param buf Array de bytes
	 * @param len Longitud del buffer de entrada
	 * @return Cadena hexa
	 */
	public static String toHexString(byte[] buf, int len)
	{
		if((buf == null) || (buf.length == 0) || (len == 0))
		{
			return strOf('0', 2*len);
		}
		int hight, low, i;
		StringBuilder out = new StringBuilder();
		if(buf.length < len)
		{
			int lenToAdd = (len - buf.length)*2;
			for(i = 0; i < lenToAdd; ++i)
			{
				out.append('0');
			}
			for(i = 0; i < buf.length; ++i)
			{
				hight = (buf[i] >> 4) & 0x0F;
				low = buf[i] & 0x0F;
				out.append(HEX_DIGITS[hight]);
				out.append(HEX_DIGITS[low]);
			}
		}
		else
		{
			for(i = buf.length - len; i < buf.length; ++i)
			{
				hight = (buf[i] >> 4) & 0x0F;
				low = buf[i] & 0x0F;
				out.append(HEX_DIGITS[hight]);
				out.append(HEX_DIGITS[low]);
			}
		}
		return out.toString();
	}

	/**
	 * Convertir un número a buffer BCD 12345 -> [01 23 45]
	 * @param value Número
	 * @param digits Dígitos del número BCD
	 * @return Array de bytes BCD
	 */
	public static byte[] toBCD(int value, int digits)
	{
		return toBCD((long)value, digits);
	}

	/**
	 * Convertir un número a buffer BCD 12345 -> [01 23 45]
	 * @param value Número
	 * @param lenout Largo del buffer de salida
	 * @return Array de bytes BCD
	 */
	public static byte[] toBCD(long value, int lenout)
	{
		return HexStr2Buf(toString(value, 2*lenout));
	}
	
	/**
	 * Obtener el valor entero de un BCD de un byte
	 * @param in Valor BCD
	 * @return Valor entero
	 */
	public static int BCD2Int(byte in)
	{
		return BCD2Int(new byte[] {in});
	}
	
	/**
	 * Obtener el valor entero de un BCD
	 * @param in Array BCD
	 * @return Valor entero
	 */
	public static int BCD2Int(byte[] in)
	{
		return Integer.parseInt(toHexString(in));
	}

	/**
	 * Obtener el valor entero de un BCD de un byte
	 * @param in Valor BCD
	 * @return Valor entero
	 */
	public static long BCD2Long(byte in)
	{
		return BCD2Long(new byte[] {in});
	}
	
	/**
	 * Obtener el valor entero de un BCD
	 * @param in Array BCD
	 * @return Valor entero
	 */
	public static long BCD2Long(byte[] in)
	{
		return Long.parseLong(toHexString(in));
	}

	/**
	 * Convertir una cadena hexa a un array de bytes
	 * @param hexString Cadena hexa
	 * @param lenout Largo del array de salida
	 * @return Array de bytes
	 */
	public static byte[] HexStr2Buf(String hexString, int lenout)
	{
		return HexStr2Buf(adjNum(hexString, lenout*2));
	}

	/**
	 * Convertir una cadena hexa a un array de bytes
	 * @param hexString Cadena hexa
	 * @return Array de bytes
	 */
	public static byte[] HexStr2Buf(String hexString)
	{
		return HexStr2Buf(hexString, '0', EAlign.RIGHT);
	}

	/**
	 * Convertir una cadena hexa a un array de bytes
	 * @param hexString Cadena hexa
	 * @param padCar Caracter de padeo
	 * @param align Tipo de alineación
	 * @return Array de bytes
	 */
	public static byte[] HexStr2Buf(byte[] hexString, char padCar, EAlign align)
	{
		if(hexString == null)
		{
			return new byte[0];
		}
		boolean pad = (hexString.length%2 == 1);
		int high, low;
		int lenin, lenout;
		byte[] in, out;
		if(pad)
		{
			lenin = hexString.length + 1;
			in = (align == EAlign.LEFT) ? padRight(hexString, lenin, padCar) : padLeft(hexString, lenin, padCar);
		}
		else
		{
			lenin = hexString.length;
			in = hexString;
		}
		lenout = lenin/2;
		out = new byte[lenout];
		for(int i = 0; i < lenout; ++i)
		{
			high = HexChar2Buf(in[  2*i ]);
			low  = HexChar2Buf(in[2*i + 1]);
			out[i] = (byte) (((high << 4) & 0xF0) + (low & 0x0F));
		}
		return out;
	}

	/**
	 * Convertir una cadena hexa a un array de bytes
	 * @param hex Cadena hexa
	 * @param padCar Caracter de padeo
	 * @param align Tipo de alineación
	 * @return Array de bytes
	 */
	public static byte[] HexStr2Buf(String hex, char padCar, EAlign align)
	{
		hex = padToEven(hex, padCar, align);
		int lenout = hex.length()/2;
		byte[] out = new byte[lenout];
		int high, low;
		for(int i = 0; i < lenout; ++i)
		{
			high = HexChar2Buf(hex.charAt(2*i));
			low = HexChar2Buf(hex.charAt(2*i + 1));
			out[i] = (byte) (((high << 4) & 0xF0) + (low & 0x0F));
		}
		return out;
	}

	/**
	 * Determinal si un caracter es un dígito hexadecimal ('1'...'9', 'A'/'a'...'F'/'f')
	 * @param car Caracter
	 * @return true/false
	 */
	public static boolean isHexaDigit(int car)
	{
		if(car >= '0' && car <= '9')
		{
			return true;	
		}
		if(car >= 'A' && car <= 'F')
		{
			return true;
		}
		if(car >= 'a' && car <= 'f')
		{
			return true;
		}
		return false;
	}

	/**
	 * Convertir un dígito hexadecimal (0 a 15) en un caracter representativo
	 * @param digit Dígito hexadecimal
	 * @return Caracter
	 */
	public static char HexDigit2Char(int digit)
	{
		if((digit < 0) || (digit > 15))
		{
			return '0';
		}
		return HEX_DIGITS[digit];
	}

	/**
	 * Convertir un caracter hexadecimal en su valor
	 * @param car Caracter
	 * @return Valor del caracter dado
	 */
	public static int HexChar2Buf(int car)
	{
		if(car >= '0' && car <= '9')
		{
			return (car - '0');	
		}
		if(car >= 'A' && car <= 'F')
		{
			return (10 + car - 'A');
		}
		if(car >= 'a' && car <= 'f')
		{
			return (10 + car - 'a');
		}
		return (car & 0x0f);
	}

	/**
	 * Pasar un número a un array de byte 5874 (= 0x16F2) -> [00 00 16 F2] (len = 4)
	 * @param value Número
	 * @param len Longitud en bytes del buffer de salida
	 * @return Buffer con el número
	 */
	public static byte[] Num2Buf(int value, int len)
	{
		return HexStr2Buf(toHexString(value, len*2));
	}

	/**
	 * Convertir un array de bytes en una cadena que se pueda logear
	 * @param data Array de bytes
	 * @param len Longitud del array
	 * @return Cadena apta para log
	 */
	public static String Buf2Log(byte[] data, int len)
	{
		if((data == null) || data.length == 0 || len == 0)
		{
			return "";
		}
		byte[] buf = new byte[len];
		StrUtils.copy(buf, data, len);
		return HexStrToLog(toHexString(buf));
	}

	/**
	 * Convertir un array de bytes en una cadena que se pueda logear
	 * @param data Array de bytes
	 * @return Cadena apta para log
	 */
	public static String Buf2Log(byte[] data)
	{
		return Buf2Log(data, (data == null) ? 0 : data.length);
	}

	/**
	 * Convertir una cadena hexadecimal en otra apta para log
	 * @param str Cadena hexadecimal
	 * @return Cadena apta para log
	 */
	public static String HexStrToLog(String str)
	{
		if(str == null || str.length() == 0)
		{
			return "";
		}
		int inputLen = str.length();
		int lines = inputLen/(2*LOG_COLUMNS);
		String output = "";
		
		if(inputLen%(2*LOG_COLUMNS) > 0)
		{
			lines += 1;
		}
		for(int i = 0; i < lines; ++i)
		{
			output += HexStrToLogLine(str.substring(i*2*LOG_COLUMNS));
		}
		return output;		
	}

	private static String HexStrToLogLine(String str)
	{
		int inputLen = str.length();
		String output = "";
		boolean hasEOL = (str.length() > 2*LOG_COLUMNS);
		for(int i = 0; i < LOG_COLUMNS; ++i)
		{
			if(2*i < inputLen)
			{
				output += str.substring(2*i, 2*(i + 1));
			}
			else
			{
				output += "  ";
			}
			output += " ";
		}
		for(int i = 0; i < LOG_COLUMNS; ++i)
		{
			if(2*i < inputLen)
			{
				String strByte = str.substring(2*i, 2*(i + 1));
				int value = Integer.parseInt(strByte, 16);
				if((value < 32) || (value > 127))
				{
					output += ".";
				}
				else
				{
					output += ((char) value);
				}
			}
			else
			{
				break;
			}
		}
		if(hasEOL)
		{
			output += '\n';
		}
		return output;
	}

	//----- Adjust --------------------------------------------------------------------------------
	
	/**
	 * Ajustar una cadena al largo dado rellenando o recortando
	 * @param str Cadena
	 * @param newlen Nuevo largo
	 * @param align Alineación
	 * @param car Caracter de relleno
	 * @return Cadena ajustada
	 */
	public static String adjust(String str, int newlen, EAlign align, char car)
	{
		if(str.length() > newlen)
		{
			return (align == EAlign.LEFT) ? firsts(str, newlen) : lasts(str, newlen);
		}
		else if(str.length() < newlen)
		{
			return (align == EAlign.LEFT) ? padRight(str, newlen, car) : padLeft(str, newlen, car);
		}
		return str;
	}

	/**
	 * Ajustar una cadena al largo dado, agregando espacio a la derecha o recortando los primero caracteres
	 * @param str Cadena
	 * @param newlen Nuevo largo
	 * @return Cadena ajustada
	 */
	public static String adjStr(String str, int newlen)
	{
		if(str.length() > newlen)
		{
			return firsts(str, newlen);
		}
		else if(str.length() < newlen)
		{
			return padRight(str, newlen, ' ');
		}
		return str;
	}

	/**
	 * Ajustar una cadena de números al largo dado, agregando ceros a la izquierda o recortando los últimos caracteres
	 * @param str Cadena
	 * @param newlen Nuevo largo
	 * @return Cadena ajustada
	 */
	public static String adjNum(String str, int newlen)
	{
		if(str == null)
		{
			return strOf('0', newlen);
		}
		if(str.length() > newlen)
		{
			return lasts(str, newlen);
		}
		else if(str.length() < newlen)
		{
			return padLeft(str, newlen, '0');
		}
		return str;
	}

	/**
	 * Primeros caracteres de la cadena
	 * @param str Cadena
	 * @param len Cantidad de caracteres
	 * @return Cadena recortada
	 */
	public static String firsts(String str, int len)
	{
		if(str.length() > len)
		{
			return str.substring(0, len);
		}
		return str;
	}
	
	/**
	 * Últimos caracteres de la cadena
	 * @param str Cadena
	 * @param len Cantidad de caracteres
	 * @return Cadena recortada
	 */
	public static String lasts(String str, int len)
	{
		if(str.length() > len)
		{
			return str.substring(str.length() - len);
		}
		return str;
	}

	/**
	 * Padear una cadena a una cantidad par de caracteres
	 * @param str Cadena
	 * @param padCar Caracter de padeo
	 * @param align Alineación
	 * @return Cadena padeada
	 */
	public static String padToEven(String str, char padCar, EAlign align)
	{
		if(str.length()%2 == 1)
		{
			return (align.equals(EAlign.RIGHT)) ? (padCar + str) : (str + padCar);
		}
		return str;
	}

	/**
	 * Obtener una versión padeada a la derecha del array pasado 
	 * @param in Array original
	 * @param newlen Nueva longitud en bytes
	 * @param car Caracter de padeo
	 * @return Versión padeada del array de origen
	 */
	public static String padRight(String in, int newlen, char car)
	{
		if(in == null)
		{
			return strOf(car, newlen);
		}
		return in + strOf(car, newlen - in.length());
	}

	/**
	 * Obtener una versión padeada a la derecha del array pasado 
	 * @param in Array original
	 * @param newlen Nueva longitud en bytes
	 * @param car Caracter de padeo
	 * @return Versión padeada del array de origen
	 */
	public static byte[] padRight(byte[] in, int newlen, int car)
	{
		if(in == null)
		{
			return arrOf(car, newlen);
		}
		if(newlen <= 0)
		{
			return new byte[0];
		}		
		byte[] out = new byte[newlen];
		int i, inLen = (in == null) ? 0 : in.length;
		// Hay algo que rellenar
		if(inLen < newlen)
		{
			for(i = 0; i < inLen; ++i)
			{
				out[i] = in[i];	
			}
			for(i = inLen; i < newlen; ++i)
			{
				out[i] = (byte) car;
			}
		}
		// El nuevo largo en más corto, copio hasta donde entre
		else
		{
			for(i = 0; i < newlen; ++i)
			{
				out[i] = in[i];	
			}
		}
		return out;
	}

	/**
	 * Obtener una versión padeada a la derecha del array pasado 
	 * @param in Array original
	 * @param newlen Nueva longitud en bytes
	 * @param car Caracter de padeo
	 * @return Versión padeada del array de origen
	 */
	public static String padLeft(String in, int newlen, char car)
	{
		if(in == null)
		{
			return strOf(car, newlen);
		}
		return strOf(car, newlen - in.length()) + in;
	}

	/**
	 * Obtener una versión padeada a la derecha del array pasado 
	 * @param in Array original
	 * @param newlen Nueva longitud en bytes
	 * @param car Caracter de padeo
	 * @return Versión padeada del array de origen
	 */
	public static byte[] padLeft(byte[] in, int newlen, int car)
	{
		if(in == null)
		{
			return arrOf(car, newlen);
		}
		if(newlen <= 0)
		{
			return new byte[0];
		}
		byte[] out = new byte[newlen];
		int i, offset, inLen = (in == null) ? 0 : in.length;
		// Hay algo que rellenar
		if(inLen < newlen)
		{
			offset = newlen - inLen;
			for(i = 0; i < offset; ++i)
			{
				out[i] = (byte) car;
			}		
			for(i = offset; i < newlen; ++i)
			{
				out[i] = in[i - offset];
			}		
		}
		else
		{
			offset = inLen - newlen;
			for(i = offset; i < newlen + offset; ++i)
			{
				out[i - offset] = in[i];	
			}
		}
		return out;
	}

	//----- Clear / fill --------------------------------------------------------------------------

	/**
	 * Limpiar array de bytes
	 * @param dest Array de bytes
	 * @return Cantidad de bytes escritos
	 */
	public static int clear(byte[] dest)
	{
		return fill(dest, 0, (byte)0, dest.length);
	}

	/**
	 * Rellenar array con un caracter
	 * @param arr Array de bytes
	 * @param car Caracter de relleno
	 * @return Cantidad de caracteres escritos
	 */
	public static int fill(byte[] arr, int car)
	{
		return fill(arr, 0, car, arr.length);
	}

	/**
	 * Rellenar array con un caracter
	 * @param arr Array de bytes
	 * @param offset Offset
	 * @param car Caracter de relleno
	 * @return Cantidad de caracteres escritos
	 */
	public static int fill(byte[] arr, int offset, int car)
	{
		return fill(arr, offset, car, arr.length - offset);
	}

	/**
	 * Rellenar array con un caracter
	 * @param arr Array de bytes
	 * @param offset Offset
	 * @param car Caracter de relleno
	 * @param len Cantidad a rellenar
	 * @return Cantidad de caracteres escritos
	 */
	public static int fill(byte[] arr, int offset, int car, int len)
	{
		if((arr == null) || (arr.length == 0) || (len == 0) || (offset >= arr.length))
		{
			return 0;
		}
		int realLen = Math.min(arr.length - offset, len);
		for(int i = 0; i < realLen; ++i)
		{
			arr[i + offset] = (byte) car;
		}
		return realLen;
	}

	//----- Compare -------------------------------------------------------------------------------
	
	/**
	 * Comprobar si el array de bytes representa una cadena vacía
	 * @param str Array de bytes
	 * @return true/false
	 */
	public static boolean isEmpty(byte[] str)
	{
		return isEmpty(str, str.length);
	}

	/**
	 * Comprobar si el array de bytes representa una cadena vacía
	 * @param str Array de bytes
	 * @param len Largo a comprobar
	 * @return true/false
	 */
	public static boolean isEmpty(byte[] str, int len)
	{
		return ((str == null) || (str.length == 0) || (len == 0) || (str[0] == 0));
	}

	/**
	 * Comprar dos array de bytes hasta el largo dado
	 * @param str1 Primero array
	 * @param str2 Segundo array
	 * @param len Largo a comprar
	 * @return true/false
	 */
	public static boolean equal(byte[] str1, byte[] str2, int len)
	{
		for(int i = 0; i < len; ++i)
		{
			if(str1[i] != str2[i])
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * Comparar dos arrays de bytes
	 * @param str1 Primer array
	 * @param str2 Segundo array
	 * @return Resultado de la comparación
	 */
	public static int cmp(byte[] str1, byte[] str2)
	{
		int dif = 0;
		int lencmp = Math.min(str1.length, str2.length);
		for(int i = 0; i < lencmp; ++i)
		{
			dif += (int)(str1[i] - str2[i]);
		}
		return dif;
	}

	/**
	 * Comparar dos arrays de bytes
	 * @param str1 Primer array
	 * @param str2 Segundo array
	 * @param len Longitud a comprar
	 * @return Resultado de la comparación
	 */
	public static int cmp(byte[] str1, byte[] str2, int len)
	{
		int dif = 0;
		int lencmp = Math.min(Math.min(str1.length, str2.length), len);
		for(int i = 0; i < lencmp; ++i)
		{
			dif += (int)(str1[i] - str2[i]);
		}
		return dif;
	}

	//----- Concatenar ----------------------------------------------------------------------------

	/**
	 * Concatenar dos arrays
	 * @param str1 Primer array
	 * @param str2 Segundo array
	 * @param len Largo del segundo array
	 * @return Nuevo array concatenado
	 */
	public static byte[] concatenate(byte[] str1, byte[] str2, int len)
	{
		int len1 = ((str1 == null) ? 0 : str1.length);
		int len2 = ((str2 == null) ? 0 : Math.min(str2.length, len));
		byte[] out = new byte[len1 + len2];
		int i;
		for(i = 0; i < len1; ++i)
		{
			out[i] = str1[i];
		}
		for(i = 0; i < len2; ++i)
		{
			out[i + len1] = str2[i];
		}
		return out;
	}

	/**
	 * Concatenar dos arrays
	 * @param str1 Primer array
	 * @param str2 Segundo array
	 * @return Nuevo array concatenado
	 */
	public static byte[] concatenate(byte[] str1, byte[] str2)
	{
		int len1 = ((str1 == null) ? 0 : str1.length);
		int len2 = ((str2 == null) ? 0 : str2.length);
		byte[] out = new byte[len1 + len2];
		int i;
		for(i = 0; i < len1; ++i)
		{
			out[i] = str1[i];
		}
		for(i = 0; i < len2; ++i)
		{
			out[i + len1] = str2[i];
		}
		return out;
	}

	/**
	 * Concatenar al array pasado
	 * @param dest Destino
	 * @param src Cadena a agregar
	 * @return Cantidad de bytes copiados
	 */
	public static int cat(byte[] dest, String src)
	{
		return cat(dest, (src == null) ? null : src.getBytes());
	}

	/**
	 * Concatenar al array pasado
	 * @param dest Destino
	 * @param src Caracter a agregar
	 * @return Cantidad de bytes copiados
	 */
	public static int cat(byte[] dest, int src)
	{
		return cat(dest, new byte[] { (byte) src });
	}

	/**
	 * Concatenar al array pasado
	 * @param dest Destino
	 * @param src Array a agregar
	 * @return Cantidad de bytes copiados
	 */
	public static int cat(byte[] dest, byte[] src)
	{
		if((dest == null) || (dest.length < 1) || (src == null) || (src.length < 1))
		{
			return 0;
		}
		for(int offset = 0; offset < dest.length; ++offset)
		{
			if(dest[offset] == 0)
			{
				return copy(dest, offset, src);
			}
		}
		return 0;
	}

	//----- Copy from String ----------------------------------------------------------------------

	/**
	 * Copiar una cadena a un array con desplazamiento y una cantidad de caracteres dados
	 * @param dest Array de destino
	 * @param src Cadena
	 * @return Cantidad de bytes copiados
	 */
	public static int copy(byte[] dest, String src)
	{
		return copy(dest, 0, (src == null) ? new byte[0] : src.getBytes(), (src == null) ? 0 : src.length());
	}

	/**
	 * Copiar una cadena a un array con desplazamiento y una cantidad de caracteres dados
	 * @param dest Array de destino
	 * @param src Cadena
	 * @param len Cantidad de bytes a copiar
	 * @return Cantidad de bytes copiados
	 */
	public static int copy(byte[] dest, String src, int len)
	{
		return copy(dest, 0, (src == null) ? new byte[0] : src.getBytes(), len);
	}	
	
	/**
	 * Copiar una cadena a un array con desplazamiento y una cantidad de caracteres dados
	 * @param dest Array de destino
	 * @param offset Desplazamiento de copia
	 * @param src Cadena
	 * @return Cantidad de bytes copiados
	 */
	public static int copy(byte[] dest, int offset, String src)
	{
		return copy(dest, offset, (src == null) ? new byte[0] : src.getBytes(), (src == null) ? 0 : src.length());
	}
	
	/**
	 * Copiar una cadena a un array con desplazamiento y una cantidad de caracteres dados
	 * @param dest Array de destino
	 * @param offset Desplazamiento de copia
	 * @param src Cadena
	 * @param len Cantidad de bytes a copiar
	 * @return Cantidad de bytes copiados
	 */
	public static int copy(byte[] dest, int offset, String src, int len)
	{
		return copy(dest, offset, (src == null) ? new byte[0] : src.getBytes(), len);
	}
	
	/**************** Copiar desde byte **********************************************************/

	/**
	 * Copiar un byte a un array con un desplazamiento dado
	 * @param dest Array de destino
	 * @param src Array de origen
	 * @return Cantidad de bytes copiados
	 */
	public static int copy(byte[] dest, int src)
	{
		return copy(dest, 0, src);
	}	

	/**
	 * Copiar un byte a un array con un desplazamiento dado
	 * @param dest Array de destino
	 * @param offset Desplazamiento de copia
	 * @param src Array de origen
	 * @return Cantidad de bytes copiados
	 */
	public static int copy(byte[] dest, int offset, int src)
	{
		if((dest == null) || (offset >= dest.length))
		{
			return 0;
		}
		dest[offset] = (byte) src;
		return 1;
	}	

	//-----  Copiar desde array de bytes ----------------------------------------------------------

	/**
	 * Copiar un array a otro con un desplazamiento y una cantidad de caracteres dados
	 * @param dest Array de destino
	 * @param src Array de origen
	 * @return Cantidad de bytes copiados
	 */
	public static int copy(byte[] dest, byte[] src)
	{
		return copy(dest, 0, src, (src == null) ? 0 : src.length);
	}

	/**
	 * Copiar un array a otro con un desplazamiento y una cantidad de caracteres dados
	 * @param dest Array de destino
	 * @param src Array de origen
	 * @param len Cantidad de bytes a copia
	 * @return Cantidad de bytes copiados
	 */
	public static int copy(byte[] dest, byte[] src, int len)
	{
		return copy(dest, 0, src, len);
	}

	/**
	 * Copiar un array a otro con un desplazamiento y una cantidad de caracteres dados
	 * @param dest Array de destino
	 * @param offset Desplazamiento de copia
	 * @param src Array de origen
	 * @return Cantidad de bytes copiados
	 */
	public static int copy(byte[] dest, int offset, byte[] src)
	{
		return copy(dest, offset, src, (src == null) ? 0 : src.length);
	}

	/**
	 * Copiar un array a otro con un desplazamiento y una cantidad de caracteres dados
	 * @param dest Array de destino
	 * @param offset Desplazamiento de copia
	 * @param src Array de origen
	 * @param len Cantidad de bytes a copia
	 * @return Cantidad de bytes copiados
	 */
	public static int copy(byte[] dest, int offset, byte[] src, int len)
	{
		if((src == null) || (src.length < 1) || (len < 1) || (dest == null) || (offset >= dest.length))
		{
			return 0;
		}
		int count = 0, srclen = Math.min(len, src.length);
		for(int i = 0; i < srclen; ++i)
		{
			if(i + offset < dest.length)
			{
				dest[i + offset] = src[i];
				++count;
			}
			else
			{
				break;
			}
		}
		return count;
	}
}
