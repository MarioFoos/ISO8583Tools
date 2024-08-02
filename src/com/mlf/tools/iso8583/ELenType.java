package com.mlf.tools.iso8583;

import java.util.ArrayList;

/**
 * Tipo de largo de campo
 * @author Mario
 */
public enum ELenType
{
	/** No definido */
	ND,
	/** Largo fijo predefinido */
	FL,
	/** Largo variable menor a 100 empacado BCD */
	L2B,
	/** Largo variable menor a 100 numérico*/
	L2N,
	/** Largo variable menor a 1000 empacado BCD */
	L3B,
	/** Largo variable menor a 1000 numérico*/
	L3N;

	/**
	 * Obtener el elmento del enum a partir del nombre
	 * @param name Nombre
	 * @return Elemento del enum
	 */
	public static ELenType fromName(String name)
	{
		for(ELenType type : ELenType.values())
		{
			if(type.name().equalsIgnoreCase(name))
			{
				return type;
			}
		}
		return ND;
	}

	/**
	 * Retornar un array con todos los elementos del enum excepto el elemento indefinido
	 * @return Array con todos los elementos del enum
	 */
	public static ArrayList<ELenType> getAll()
	{
		ArrayList<ELenType> elements = new ArrayList<ELenType>();
		for(ELenType element : ELenType.values())
		{
			if(!element.equals(ND))
			{
				elements.add(element);
			}
		}
		return elements;
	}
};
