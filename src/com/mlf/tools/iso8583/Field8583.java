package com.mlf.tools.iso8583;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.mlf.tools.EAlign;
import com.mlf.tools.Log;
import com.mlf.tools.StrUtils;

/**
 * Campo de un mensaje ISO-8583
 * @author Mario
 */
public class Field8583 implements Comparable<Field8583>
{
	private EField fieldId;			// Número de bit 0-63
	private EDataType dataType;		// Tipo de dato
	private EAlign align;			// Alineación de datos
	private ELenType lenType;		// Tipo de longitud de campo 
	private char padChar;			// Caracter de relleno
	private int maxLen;				// Máxima longitud del campo
	private int fieldLen;			// Longitud del campo informada
	private byte[] rawData;			// Datos si se pasan como buffer (BINL, BINR)

	/**
	 * Constructor
	 */
	public Field8583()
	{
		super();
		reset();
	}
	
	/**
	 * Constructor copia
	 * @param src Objeto de origen
	 */
	public Field8583(Field8583 src)
	{
		super();
		set(src);
	}

	/**
	 * Constuctor
	 * @param field Campo
	 * @param dataType Tipo de dato
	 * @param align Alineación
	 * @param lenType Tipo de largo
	 * @param maxLen Máximo largo
	 * @param fill Caracter de padeo
	 */
	public Field8583(EField field, EDataType dataType, EAlign align, ELenType lenType, int maxLen, char fill)
	{
		setConfig(field, dataType, align, lenType, maxLen, fill);
	}

	/**
	 * Constructor
	 * @param bit Número del campo
	 * @param dataType Tipo de campo
	 * @param align Alineación
	 * @param lenType Tipo de largo
	 * @param maxLen Máximo largo de campo
	 * @param fill Caracter de padeo
	 */
	public Field8583(int bit, EDataType dataType, EAlign align, ELenType lenType, int maxLen, char fill)
	{
		setConfig(bit, dataType, align, lenType, maxLen, fill);
	}

	/**
	 * Reset
	 */
	public void reset()
	{
		fieldId = EField.UNDEFINED;
		dataType = EDataType.UNDEFINED;
		align = EAlign.UNDEFINED;
		lenType = ELenType.ND;
		padChar = 0;
		maxLen = 0;
		fieldLen = 0;
		rawData = new byte[0];
	}
	
	/**
	 * Set
	 * @param src Objeto de origen
	 */
	public void set(Field8583 src)
	{
		fieldId = src.fieldId;
		dataType = src.dataType;
		align = src.align;
		lenType = src.lenType;
		padChar = src.padChar;
		maxLen = src.maxLen;
		if(src.hasData())
		{
			rawData = new byte[src.rawData.length];
			fieldLen = src.maxLen;
			StrUtils.copy(rawData, src.rawData);
		}
		else
		{
			clearData();
		}
	}
	
	/**
	 * Obtener json de la configuración del campo 
	 * @return Objeto json
	 */
	public JSONObject toJSON()
	{
		return toJSON(false);
	}

	/**
	 * Obtener json con la configuración y opcionalmente los datos del campo 
	 * @param includeData Incluir los datos del campo
	 * @return Objeto json
	 */
	public JSONObject toJSON(boolean includeData)
	{
		JSONObject obj = new JSONObject();
		try
		{
			obj.put("bit", fieldId.number());
			obj.put("type", dataType.name());
			obj.put("align", align.name());
			obj.put("lenType", lenType.name());
			obj.put("maxLen", maxLen);
			obj.put("padChar", (int) padChar);
			if(includeData)
			{
				obj.put("data", getData_str());
			}
		}
		catch(JSONException e)
		{
			e.printStackTrace();
		}
		return obj;
	}

	/**
	 * Obtener string de la configuración y opcionalmente datos del campo
	 * @param includeData Incluir o no datos del campo
	 * @return Información del campo
	 */
	public String toString(boolean includeData)
	{
		return toJSON(includeData).toString();
	}

	@Override
	public String toString()
	{
		return toString(false);
	}
	
	@Override
	public int compareTo(Field8583 o)
	{
		return fieldId.number() - o.fieldId.number();
	}
	
	/**
	 * Obtener la configuración del campo como una cadena json
	 * @return Cadena json
	 */
	public String getConfigAsString()
	{
		return toJSON(false).toString();
	}
	
	/**
	 * Comprobar la configuración del campo
	 * @return true/false
	 */
	private boolean checkConfig()
	{
		if(fieldId == EField.UNDEFINED)
		{
			Log.err("Bit num invalid: " + fieldId.number());
			return false;
		}
		if(dataType == EDataType.UNDEFINED)
		{
			Log.err("Bit " + fieldId.number() + " error. Data type Not Defined");
			return false;
		}
		if(maxLen < 1)
		{
			Log.err("Bit " + fieldId.number() + " error. Max len must be 1 or higher");
			return false;
		}
		switch(lenType)
		{
			case FL:
			case L2B:
			case L2N:
				if(maxLen > 99)
				{
					Log.err("Bit " + fieldId.number() + ". error. Len > 99");
					return false;
				}
				break;
			case L3B:
			case L3N:
				if(maxLen > 999)
				{
					Log.err("Bit " + fieldId.number() + ". error. Len > 999");
					return false;
				}
				break;
			case ND:
			default:
				Log.err("Bit " + fieldId.number() + ". error. Len type Not Defined");
				return false;
		}
		switch(dataType)
		{
			case TEXT:
			case TRACK:
				break;
			case NUMERIC:
			case BCD:
				if((padChar < '0') || (padChar > '9'))
				{
					Log.err("Bit " + fieldId.number() + " error. Type NUMERIC and  BCD need pad '0' to '9'");					
					return false;
				}				
				break;
			case BINARY:
			case ASC_HEX:
			case HEXSTRING:
				if((padChar >= 'a') && (padChar <= 'f'))
				{
					padChar = (char) ('A' - 'a' + padChar);
				}
				if((padChar >= '0') && (padChar <= '9'))
				{
					// Valid
				}
				else if((padChar >= 'A') && (padChar <= 'F'))
				{
					// Valid
				}
				else
				{
					Log.err("Bit " + fieldId.number() + " error. Type BINARY, HEXSTRING and HEX_STR need pad '0' to '9' or 'A' to 'F'");					
					return false;
				}
				break;
			case UNDEFINED:
			default:
				Log.err("Bit " + fieldId.number() + " error. Data type Not Defined");
				return false;
		}
		return true;
	}
	
	/**
	 * Obtener los datos del campo desde una array de bytes
	 * @param message Array de bytes
	 * @return Cantidad de bytes usados
	 */
	public int parseField(byte[] message)
	{
		if(!checkConfig())
		{
			return 0;
		}
		int sizeLen;	// Bytes que ocupa el campo de longitud
		int realLen;	// Bytes reales que ocupan los datos del campo
		int recLen;		// Longitud del campo declarada
		boolean bcdLed = false;
		switch(lenType)
		{
			case FL:
				sizeLen = 0;
				break;
			case L2B:
				sizeLen = 1;
				bcdLed = true;
				break;
			case L2N:
				sizeLen = 2;
				break;
			case L3B:
				sizeLen = 2;
				bcdLed = true;
				break;
			case L3N:
				sizeLen = 3;
				break;
			case ND:
			default:
				Log.err("Bit" + fieldId.number() + ": Len type not defined");
				return 0;
		}
		// Longitud informada en el campo
		byte[] bufLen;
		if(sizeLen == 0)
		{
			bufLen = new byte[0];
			recLen = maxLen;
		}
		else
		{
			bufLen = StrUtils.sub(message, 0, sizeLen);
			recLen = bcdLed ? StrUtils.BCD2Int(bufLen) : Integer.parseInt(new String(bufLen));
		}
		if(recLen < 0 || recLen > maxLen)
		{
			Log.err("Bit " + fieldId.number() + " error. Invalid length " + recLen);
			return 0;
		}
		// Longitud real del campo
		switch(dataType)
		{
			case BCD:
			case TRACK:
				realLen = (recLen + recLen%2)/2;
				break;
			case BINARY:
				realLen = recLen;
				break;
			case ASC_HEX:
				realLen = recLen*2;
				break;
			case NUMERIC:
			case TEXT:
			case HEXSTRING:
				realLen = recLen;
				break;
			default:
				Log.err("Bit " + fieldId.number() + " error: Data type not defined");
				return 0;
		}
		// Chequeo de largo por posibilidad
		if(message.length < sizeLen + realLen)
		{
			Log.err("Bit " + fieldId.number() + " error. Field to short " + message.length);
			return 0;
		}
		rawData = StrUtils.sub(message, sizeLen, sizeLen + realLen);
		fieldLen = recLen;
		
		logField(StrUtils.concatenate(bufLen, rawData));
		Log.info(getData_str());

		return sizeLen + realLen;
	}

	/**
	 * Obtener un array de bytes con los datos del campo ISO-8583
	 * @return Array de bytes con los datos del campo ISO-8583
	 */
	public byte[] buildField()
	{
		if(!hasData())
		{
			return new byte[0];
		}
		byte[] padded;
		switch(dataType)
		{
			case BCD:
			case BINARY:
			case TRACK:
			case ASC_HEX:
			case NUMERIC:
			case TEXT:
			case HEXSTRING:
				padded = rawData;
				break;
			default:
				Log.err("Data type not Defined");
				return new byte[0];
		}
		// Agrego el largo al principio si corresponde
		byte[] lenBuf;
		switch(lenType)
		{
			case FL:
				lenBuf = new byte[0];
				break;
			case L2B:
				lenBuf = StrUtils.toBCD(fieldLen, 1);
				break;
			case L2N:
				lenBuf =  StrUtils.toString(fieldLen, 2).getBytes();	
				break;
			case L3B:
				lenBuf = StrUtils.toBCD(fieldLen, 2);
				break;
			case L3N:
				lenBuf =  StrUtils.toString(fieldLen, 3).getBytes();
				break;
			default:
				lenBuf = new byte[0];
				Log.err("Len type undefined");
				break;
		}
		byte[] out = StrUtils.concatenate(lenBuf, padded);
		logField(out);
		return out;
	}
	
	@SuppressWarnings("unused")
	private void logField(byte[] out)
	{
		StringBuilder log = new StringBuilder();
		if(fieldId.index() < 1)
		{
			log.append(fieldId.name());	
		}
		else
		{
			log.append("Bit" + fieldId.number());
			log.append(" (" + fieldId.name() + ")");	
		}
		log.append(":\n");
		log.append(StrUtils.Buf2Log(out));
		Log.info(log.toString());
	}

	/**
	 * Saber si el campo tiene datos
	 * @return true/false
	 */
	public boolean hasData()
	{
		if((fieldLen < 1) || (rawData == null) || !checkConfig())
		{
			return false;
		}		
		return true;
	}

	/**
	 * Obtener los datos del campo
	 * @return Datos del campo
	 */
	public byte[] getData_raw()
	{
		return rawData;
	}

	/**
	 * Obtener los datos del campo como una cadena
	 * @return Cadena con datos del campo
	 */
	public String getData_str()
	{
		if(!hasData())
		{
			Log.err("Field " + fieldId + " empty");
			return "";
		}
		switch(dataType)
		{
			case TEXT:
			case NUMERIC:
			case HEXSTRING:
				return new String(rawData);
			case ASC_HEX:
				return new String(StrUtils.HexStr2Buf(new String(rawData)));
			case BCD:
				String str = StrUtils.toHexString(rawData);
				return StrUtils.adjust(str, fieldLen, align, padChar);
			case BINARY:
			case TRACK:
				return StrUtils.toHexString(rawData);
			default:
				Log.err("Unknow type " + fieldId + " dataType");
				break;
		}
		return "";
	}

	/**
	 * Establecer configuración
	 * @param field Campo (1 a 63) para los datos, 0 para el MTI, -1 para el TPDU 
	 * @param dataType Tipo de datos
	 * @param align Alineación
	 * @param lenType Tipo de largo
	 * @param maxLen Máximo largo
	 * @param fill Caracter de relleno
	 * @return Instancia
	 */
	public Field8583 setConfig(EField field, EDataType dataType, EAlign align, ELenType lenType, int maxLen, char fill)
	{
		this.fieldId = field;
		this.dataType = dataType;
		this.align = align;
		this.lenType = lenType;
		this.maxLen = maxLen;
		this.padChar = fill;
		return this;
	}

	/**
	 * Establecer configuración
	 * @param field Campo (1 a 63) para los datos, 0 para el MTI, -1 para el TPDU 
	 * @param dataType Tipo de datos
	 * @param align Alineación
	 * @param lenType Tipo de largo
	 * @param maxLen Máximo largo
	 * @param fill Caracter de relleno
	 * @return Instancia
	 */
	public Field8583 setConfig(int field, EDataType dataType, EAlign align, ELenType lenType, int maxLen, char fill)
	{
		return setConfig(EField.fromNumber(field), dataType, align, lenType, maxLen, fill);
	}

	/**
	 * Establecer la configuración del campo desde un campo de origen
	 * @param field Campo de origen
	 * @return Instancia
	 */
	public Field8583 setConfig(Field8583 field)
	{
		fieldId = field.fieldId;
		dataType = field.dataType;
		align = field.align;
		lenType = field.lenType;
		maxLen = field.maxLen;
		padChar = field.padChar;
		return this;
	}

	/**
	 * Establecer la configuración del campo desde una cadena json
	 * @param jstr Cadena json
	 * @return Instancia
	 */
	public Field8583 setConfig(String jstr)
	{
		if((jstr == null) || jstr.isEmpty())
		{
			return this;
		}
		try
		{
			JSONObject obj = new JSONObject(jstr);
			fieldId = EField.fromNumber(obj.optInt("bit"));
			dataType = EDataType.fromName(obj.optString("type"));
			align = EAlign.fromName(obj.optString("align"));
			lenType = ELenType.fromName(obj.optString("lenType"));
			maxLen = obj.optInt("maxLen");
			padChar = (char) obj.optInt("padChar");
			return this;
		}
		catch(JSONException e)
		{
			e.printStackTrace();
		}
		return this;
	}

	/**
	 * Borrar datos del campo
	 */
	public void clearData()
	{
		rawData = null;
		fieldLen = 0;
	}
	
	/*private void doSetData(byte[] data, int len)
	{
		int realLen = Math.min(Math.min(data.length, len), maxLen);
		rawData = new byte[realLen];
		if(data.length > rawData.length)
		{
			byte[] srcData = (align == EAlign.LEFT) ? data : StrUtils.sub(data, data.length - rawData.length);
			StrUtils.copy(rawData, srcData, realLen);
		}
		else
		{
			StrUtils.copy(rawData, data, realLen);
		}
		fieldLen = (lenType == ELenType.FL) ? maxLen : realLen;
	}*/

	/**
	 * Establecer los datos del campo
	 * @param data Datos del campo
	 */
	/*public void setData(byte[] data)
	{
		setData(data, (data != null) ? data.length : 0);
	}*/

	/**
	 * Establecer los datos del campo
	 * @param data Datos del campo
	 * @param len Longitud de datos
	 */
	/*public void setData(byte[] data, int len)
	{
		String sdata;
		switch(dataType)
		{
			case TEXT:
			case NUMERIC:
			case ASC_HEX:
				sdata = new String(data);
				if(sdata.length() > len)
				{
					sdata = (align == EAlign.LEFT) ? sdata.substring(0, len) : sdata.substring(sdata.length() - len);
				}
				setData(sdata);
				return;
			case HEXSTRING:
				sdata = StrUtils.Buf2HexStr(data, len);
				setData(sdata);
				break;
			default:
				break;
		}
		// Si no está configurado o está configurado para que no tenga contenido
		if(!checkConfig())
		{
			clearData();
			return;
		}
		// No hay datos
		if((data == null) || (data.length == 0) || (len < 1))
		{
			rawData = null;
			fieldLen = (lenType == ELenType.FL) ? maxLen : 0;
		}
		else
		{
			doSetData(data, len);
		}
	}*/

	/**
	 * Establecer los datos del campo
	 * @param data Datos del campo
	 */
	public void setData(String data)
	{
		if(!checkConfig())
		{
			clearData();
			return;
		}
		if((data == null) || data.isEmpty())
		{
			rawData = null;
			fieldLen = (lenType == ELenType.FL) ? (dataType == EDataType.ASC_HEX ? maxLen*2 : maxLen) : 0;
		}
		else
		{
			switch(dataType)
			{
				case BCD:
					fieldLen = (lenType == ELenType.FL) ? maxLen : Math.min(data.length(), maxLen);
					data = StrUtils.adjust(data, fieldLen, align, padChar);
					rawData = StrUtils.HexStr2Buf(data, padChar, align);
					break;
				case BINARY:
					fieldLen = (lenType == ELenType.FL) ? maxLen : Math.min((data.length() + data.length()%2)/2, maxLen);
					data = StrUtils.adjust(data, fieldLen*2, align, padChar);
					rawData = StrUtils.HexStr2Buf(data, padChar, align);
					break;
				case TRACK:
					fieldLen = (lenType == ELenType.FL) ? maxLen : Math.min(data.length(), maxLen);
					data = StrUtils.adjust(data, fieldLen, align, padChar);
					rawData = StrUtils.HexStr2Buf(data, padChar, align);
					break;
				case ASC_HEX:
					fieldLen = (lenType == ELenType.FL) ? maxLen : Math.min(data.length(), maxLen);
					data = StrUtils.adjust(data, fieldLen, align, padChar);
					rawData = StrUtils.toHexString(data.getBytes()).getBytes();
					break;
				case HEXSTRING:
					fieldLen = (lenType == ELenType.FL) ? maxLen : Math.min(data.length(), maxLen);
					data = StrUtils.adjust(data, fieldLen, align, padChar);
					rawData = data.getBytes();
					break;
				case NUMERIC:
				case TEXT:
					fieldLen = (lenType == ELenType.FL) ? maxLen : Math.min(data.length(), maxLen);
					data = StrUtils.adjust(data, fieldLen, align, padChar);
					rawData = data.getBytes();
					break;
				default:
					return;
			}
		}
	}

	/**
	 * Establecer en uno el bit indicado (para bitmap)
	 * @param bit Número de bit
	 */
	public void setBit(int bit)
	{
		if((rawData == null) || (bit < 1) || (bit > 63))
		{
			return;
		}
		int selByte = (bit - 1)/8;
		int selBit = 7 - (bit - 1)%8;
		rawData[selByte] |= (1 << selBit);
	}

	/**
	 * Obtener el valor del bit indicado (para bitmap)
	 * @param bitNumber Posición del bit
	 * @return true/false
	 */
	public boolean getBit(int bitNumber)
	{
		if((rawData == null) || (bitNumber < 1) || (bitNumber > 63))
		{
			return false;
		}
		int selByte = (bitNumber - 1)/8;
		int selBit = 7 - (bitNumber - 1)%8;
		return ((rawData[selByte] & (1 << selBit)) != 0);
	}
	
	/**
	 * Establecer el ID del campo que representa el objeto
	 * @param fieldId ID del campo
	 * @return Instancia
	 */
	public Field8583 setFieldId(EField fieldId)
	{
		this.fieldId = fieldId;
		return this;
	}
	
	/**
	 * Obtener el ID del campo que representa el objeto
	 * @return ID del campo
	 */
	public EField getFieldId()
	{
		return fieldId;
	}

	/**
	 * Establecer número de bit (-1 a 63 donde -1 y 0 son el TPDU y el MTI respectivamente)
	 * @param fieldNumber Número de bit
	 * @return Instancia
	 */
	public Field8583 setFieldNumber(int fieldNumber)
	{
		this.fieldId = EField.fromNumber(fieldNumber);
		return this;
	}
	
	/**
	 * Obtener el número de bit
	 * @return Número de bit
	 */
	public int getFieldNumber()
	{
		return fieldId.number();
	}
	
	/**
	 * Establecer el tipo de dato
	 * @param dataType Tipo de dato
	 * @return Instancia
	 */
	public Field8583 setDataType(EDataType dataType)
	{
		this.dataType = dataType;
		return this;
	}
	
	/**
	 * Obtener el tipo de dato
	 * @return Tipo de dato
	 */
	public EDataType getDataType()
	{
		return dataType;
	}
	
	/**
	 * Establecer la alineación de datos
	 * @param align Alineación de datos
	 * @return Instancia
	 */
	public Field8583 setAlign(EAlign align)
	{
		this.align = align;
		return this;
	}
	
	/**
	 * Obtener la alineación de datos
	 * @return Alineación de datos
	 */
	public EAlign getAlign()
	{
		return align;
	}
	
	/**
	 * Establecer el tipo de largo de campo
	 * @param eLenType Tipo de largo de campo
	 * @return Instancia
	 */
	public Field8583 setLenType(ELenType eLenType)
	{
		this.lenType = eLenType;
		return this;
	}
	
	/**
	 * Obtener el tipo de largo de campo
	 * @return Tipo de largo de campo
	 */
	public ELenType getLenType()
	{
		return lenType;
	}
	
	/**
	 * Establecer el máximo largo de campo
	 * @param maxLen Máximo largo de campo
	 * @return Instancia
	 */
	public Field8583 setMaxLen(int maxLen)
	{
		this.maxLen = maxLen;
		return this;
	}
	
	/**
	 * Obtener el máximo largo de campo
	 * @return Máximo largo de campo
	 */
	public int getMaxLen()
	{
		return maxLen;
	}

	/**
	 * Establecer el caracter de padeo
	 * @param padChar Caracter de padeo
	 * @return Instancia
	 */
	public Field8583 setPadChar(char padChar)
	{
		this.padChar = padChar;
		return this;
	}
	
	/**
	 * Obtener el caracter de padeo
	 * @return Caracter de padeo
	 */
	public char getPadChar()
	{
		return padChar;
	}
	
	/**
	 * Obtener el largo de campo
	 * @return Largo de campo
	 */
	public int getLen()
	{
		return fieldLen;
	}
}
