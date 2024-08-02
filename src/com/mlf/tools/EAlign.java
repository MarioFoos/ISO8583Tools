package com.mlf.tools;

/**
 * Alineación
 * @author Mario
 */
public enum EAlign
{
	/** No definido */
	UNDEFINED,
	/** Derecha */
	RIGHT,
	/** Izquierda */
	LEFT;

	/**
	 * Obtener elemento del enum a partir del nombre
	 * @param name Nombre del elemento
	 * @return Elemento del enum
	 */
	public static EAlign fromName(String name)
	{
		for(EAlign type : EAlign.values())
		{
			if(type.name().equalsIgnoreCase(name))
			{
				return type;
			}
		}
		return UNDEFINED;
	}
};
