package com.mlf.tools.iso8583;

/**
 * Tipo de dato
*/
public enum EDataType
{
	/** No definido */
	UNDEFINED,
	/** Numerico empaquetado BCD, por ejemplo: "12345" -> [12 34 50] ó [01 23 45] (len = 5) */
	BCD,
	/** Datos binarios, por ejemplo "C1A118" -> [C1 A1 18] (len = 3) */
	BINARY,
	/** Track, por ejemplo, "4761739001010119D241220117589472" -> [47 61 73 90 01 01 01 19 D2 41 22 01 17 58 94 72] (len = 32) */
	TRACK,
	/** Datos como cadena hexadecimal que representa un texto, por ejemplo "123" -> "313233" (len = 3) */
	ASC_HEX,
	/** Numerico, por ejemplo "123456" -> "123456"(len = 6) */
	NUMERIC,
	/** Texto ASCII, por ejemplo: "Text 123" -> "Text 123" (len = 8) */
	TEXT,
	/** Texto con caracteres hexadecimales, por ejemplo: "546D123F" -> "546D123F" (len = 8) */
	HEXSTRING;
	
	/**
	 * Obtener el elmento del enum a partir del nombre
	 * @param name Nombre
	 * @return Elemento del enum
	 */
	public static EDataType fromName(String name)
	{
		for(EDataType type : EDataType.values())
		{
			if(type.name().equalsIgnoreCase(name))
			{
				return type;
			}
		}
		return UNDEFINED;
	}	
};
