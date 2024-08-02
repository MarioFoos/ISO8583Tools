package com.mlf.tools.iso8583;

import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import com.mlf.tools.EAlign;
import com.mlf.tools.FileTools;
import com.mlf.tools.StrUtils;

/**
 * Procesador de datos 8583
 * @author Mario
 */
public class Proc8583
{
	private static final int FIELDS	= 65;

	private Field8583[] fields;

	/**
	 * Constructor
	 */
	public Proc8583()
	{
		super();
		reset();
	}

	/**
	 * Constructor copia
	 * @param src Objeto a copiar
	 */
	public Proc8583(Proc8583 src)
	{
		super();
		set(src);
	}

	/**
	 * Reset
	 */
	public void reset()
	{
		fields = new Field8583[FIELDS];
		for(int i = 0; i < FIELDS; ++i)
		{
			fields[i] = new Field8583();
		}
	}
	
	/**
	 * Set
	 * @param src Objeto de origen
	 */
	public void set(Proc8583 src)
	{
		reset();
		for(int i = 0; i < FIELDS; ++i)
		{
			fields[i].set(src.fields[i]);
		}
	}

	/**
	 * Obtener los datos de los campo desde un mensaje ISO-8583
	 * @param message Mensaje ISO-8583
	 * @return Array con los campos del mensaje ISO-8583
	 */
	public Field8583[] ParseISO8583(byte[] message)
	{
		byte[] rest = Arrays.copyOf(message, message.length);
		int processLen;
		boolean[] present = new boolean[FIELDS];
		
		// TPDU
		if(fields[0].getMaxLen() > 0)
		{
			processLen = fields[0].parseField(rest);
			present[0] = processLen > 0;
			rest = StrUtils.sub(rest, processLen);
		}
		// MTI
		processLen = fields[1].parseField(rest);
		present[1] = processLen > 0;
		rest = StrUtils.sub(rest, processLen);
		
		// Bitmap
		processLen = fields[2].parseField(rest);
		present[2] = processLen > 0;
		rest = StrUtils.sub(rest, processLen);
		
		// Recorro desde el bit 2 (pan) hasta el 63
		for(int i = 3; i < FIELDS; ++i)
		{
			present[i] = fields[2].getBit(fields[i].getFieldNumber());
		}
		for(int i = 3; i < FIELDS; ++i)
		{
			if(present[i])
			{
				processLen = fields[i].parseField(rest);
				rest = StrUtils.sub(rest, processLen);
			}
		}
		return fields;
	}

	/**
	 * Construir el mensaje ISO-8583 a partir de los datos de los campos
	 * @return Mensaje ISO-8583
	 */
	public byte[] BuilISO8583()
	{
		//	Armar mapa de bits
		fields[2].setData(StrUtils.toHexString(new byte[8]));
		for(int i = 3; i <  FIELDS; ++i)
		{
			if(fields[i].getLen() > 0)
			{
				fields[2].setBit(fields[i].getFieldNumber());
			}
		}
		byte[] message = new byte[0];
		for(int i = 0; i < FIELDS; ++i)
		{
			message = StrUtils.concatenate(message, fields[i].buildField());
		}
		return message;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		String eol = System.getProperty("line.separator");
		for(int i = 0; i < FIELDS; ++i)
		{
			sb.append(fields[i].toJSON(true).toString()).append(eol);
		}
		return sb.toString();
	}

	/**
	 * Establecer configuración
	 * @param fieldId Campo según el enum EField
	 * @param dataType Tipo de datos
	 * @param align Alineación
	 * @param lenType Tipo de largo
	 * @param maxLen Máximo largo
	 * @param fill Caracter de relleno
	 * @return Instancia
	 */
	public Proc8583 setConfig(EField fieldId, EDataType dataType, EAlign align, ELenType lenType, int maxLen, char fill)
	{
		Field8583 field = getField(fieldId);
		if(field != null)
		{
			field.setConfig(fieldId, dataType, align, lenType, maxLen, fill);	
		}
		return this;
	}

	/**
	 * Establecer configuración de un campo
	 * @param fieldNumber Campo (1 a 63) para los datos, 0 para el MTI, -1 para el TPDU 
	 * @param dataType Tipo de datos
	 * @param align Alineación
	 * @param lenType Tipo de largo
	 * @param maxLen Máximo largo
	 * @param fill Caracter de relleno
	 * @return Instancia
	 */
	public Proc8583 setConfig(int fieldNumber, EDataType dataType, EAlign align, ELenType lenType, int maxLen, char fill)
	{
		return setConfig(EField.fromNumber(fieldNumber), dataType, align, lenType, maxLen, fill);
	}

	/**
	 * Obtener una cadena con la configuración de campos
	 * @return Cadena con la configuración de campos
	 */
	public String getConfigAsString()
	{
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < FIELDS; ++i)
		{
			sb.append(fields[i].getConfigAsString()).append("\n");
		}
		return sb.toString();
	}
	
	/**
	 * Eliminar los datos de los campos
	 */
	public void clearData()
	{
		for(int i = 0; i < FIELDS; ++i)
		{
			fields[i].clearData();
		}
	}
	
	/**
	 * Establecer los datos de un campo
	 * @param fieldId ID del campo
	 * @param data Datos
	 * @return Instancia
	 */
	/*public Proc8583 setData(EField fieldId, byte[] data)
	{
		Field8583 field = getField(fieldId);
		if(field != null)
		{
			field.setData(data);
		}
		return this;
	}*/

	/**
	 * Establecer los datos de un campo
	 * @param fieldNumber Número de campo
	 * @param data Datos
	 * @return Instancia
	 */
	/*public Proc8583 setData(int fieldNumber, byte[] data)
	{
		return setData(EField.fromNumber(fieldNumber), data);
	}*/

	/**
	 * Establecer los datos de un campo
	 * @param fieldId Id del campo
	 * @param data Datos
	 * @param len Longitud de datos
	 * @return Instancia
	 */
	/*public Proc8583 setData(EField fieldId, byte[] data, int len)
	{
		Field8583 field = getField(fieldId);
		if(field != null)
		{
			field.setData(data, len);
		}
		return this;
	}*/

	/**
	 * Establecer los datos de un campo
	 * @param fieldNumber Número del campo
	 * @param data Datos
	 * @param len Longitud de datos
	 * @return Instancia
	 */
	/*public Proc8583 setData(int fieldNumber, byte[] data, int len)
	{
		return setData(EField.fromNumber(fieldNumber), data, len);
	}*/

	/**
	 * Establecer los datos de un campo
	 * @param fieldId Id del campo
	 * @param data Datos
	 * @return Instancia
	 */
	public Proc8583 setData(EField fieldId, String data)
	{
		Field8583 field = getField(fieldId);
		if(field != null)
		{
			field.setData(data);
		}
		return this;
	}

	/**
	 * Establecer los datos de un campo
	 * @param fieldNumber Número del campo
	 * @param data Datos
	 * @return Instancia
	 */
	public Proc8583 setData(int fieldNumber, String data)
	{
		return setData(EField.fromNumber(fieldNumber), data);
	}

	/**
	 * Guardar la configuración a un archivo
	 * @param os Flujo de salida
	 */
	public void saveConfig(OutputStream os)
	{
		ArrayList<String> lines = new ArrayList<>();
		for(int i = 0; i < FIELDS; ++i)
		{
			lines.add(fields[i].getConfigAsString());
		}
		FileTools.writeLines(os, lines);
	}

	/**
	 * Cargar configuración desde archivo
	 * @param is Flujo de entrada de archivo
	 */
	public void loadConfig(FileInputStream is)
	{
		ArrayList<String> lines = FileTools.readLines(is, true);
		for(int i = 0; i < FIELDS && i < lines.size(); ++i)
		{
			fields[i].setConfig(lines.get(i));
		}
		Arrays.sort(fields);
	}

	/**
	 * Determinar si el campo tiene datos
	 * @param fieldId Campo
	 * @return true/false
	 */
	public boolean hasData(EField fieldId)
	{
		Field8583 field = getField(fieldId);
		if(field != null)
		{
			return field.hasData();
		}
		return false;
	}
	
	/**
	 * Determinar si el campo tiene datos
	 * @param fieldNumber Número de campo
	 * @return true/false
	 */
	public boolean hasData(int fieldNumber)
	{
		return hasData(EField.fromNumber(fieldNumber));
	}

	/**
	 * Obtener objeto de campo
	 * @param fieldId Tipo de campo
	 * @return Objeto de campo
	 */
	public Field8583 getField(EField fieldId)
	{
		if(fieldId != EField.UNDEFINED)
		{
			return fields[fieldId.number() + 1];
		}
		return null;
	}

	/**
	 * Obtener objeto de campo
	 * @param fieldNumber Número del campo
	 * @return Objeto de campo
	 */
	public Field8583 getField(int fieldNumber)
	{
		return getField(EField.fromNumber(fieldNumber));
	}
	
	/**
	 * Obtener una cadena con los datos del campo
	 * @param fieldId ID del campo
	 * @return Cadena con los datos del campo
	 */
	public String getData_str(EField fieldId)
	{
		Field8583 field = getField(fieldId);
		if(field != null)
		{
			return field.getData_str();
		}
		return null;
	}

	/**
	 * Obtener una cadena con los datos del campo
	 * @param fieldNumber Número del campo
	 * @return Cadena con los datos del campo
	 */
	public String getData_str(int fieldNumber)
	{
		return getData_str(EField.fromNumber(fieldNumber));
	}

	/**
	 * Obtener los datos del campo
	 * @param fieldId Id del campo
	 * @return Datos del campo
	 */
	public byte[] getData_raw(EField fieldId)
	{
		Field8583 field = getField(fieldId);
		if(field != null)
		{
			return field.getData_raw();
		}
		return null;
	}

	/**
	 * Obtener los datos del campo
	 * @param fieldNumber Número del campo
	 * @return Datos del campo
	 */
	public byte[] getData_raw(int fieldNumber)
	{
		return getData_raw(EField.fromNumber(fieldNumber));
	}
	
	//----- Seteo de bits por definición ----------------------------------------------------------
	
	/**
	 * Establecer el TPDU del mensaje
	 * @param tpdu TPDU
	 * @return Instancia
	 */
	public Proc8583 setTPDU(String tpdu)
	{
		return setData(EField.TPDU, tpdu);
	}

	/**
	 * Establecer el TPDU del mensaje
	 * @param tpdu TPDU
	 * @return Instancia
	 */
	/*public Proc8583 setTPDU(byte[] tpdu)
	{
		return setData(EField.TPDU, tpdu);
	}*/

	/**
	 * Obtener el TPDU del mensaje
	 * @return TPDU
	 */
	public byte[] getTPDU()
	{
		return getData_raw(EField.TPDU);
	}

	/**
	 * Establecer el MTI (Tipo de mensaje)
	 * @param mti MTI (Tipo de mensaje)
	 * @return Instancia
	 */
	/*public Proc8583 setMTI(byte[] mti)
	{
		return setData(EField.MTI, mti);
	}*/

	/**
	 * Establecer el MTI (Tipo de mensaje)
	 * @param mti MTI (Tipo de mensaje)
	 * @return Instancia
	 */
	public Proc8583 setMTI(String mti)
	{
		return setData(EField.MTI, mti);
	}

	/**
	 * Obtener el MTI (Tipo de mensaje)
	 * @return MTI (Tipo de mensaje)
	 */
	public byte[] getMTI()
	{
		return getData_raw(EField.MTI);
	}

	/**
	 * Establecer el PAN (Primary Account Number)
	 * @param pan PAN (Primary Account Number)
	 * @return Instancia
	 */
	public Proc8583 setPAN(String pan)
	{
		return setData(EField.PAN, pan);
	}

	/**
	 * Obtener el PAN (Primary Account Number)
	 * @return PAN (Primary Account Number)
	 */
	public byte[] getPAN()
	{
		return getData_raw(EField.PAN);
	}

	/**
	 * Establecer el código de proceso
	 * @param prcode Código de proceso
	 * @return Instancia
	 */
	public Proc8583 setPRCode(String prcode)
	{
		return setData(EField.PRCODE, prcode);
	}

	/**
	 * Obtener el código de proceso
	 * @return Código de proceso
	 */
	public byte[] getPRCode()
	{
		return getData_raw(EField.PRCODE);
	}

	/**
	 * Establecer monto
	 * @param amount Monto
	 * @return Instancia
	 */
	public Proc8583 setAmount(String amount)
	{
		return setData(EField.AMOUNT_TRX, amount);
	}

	/**
	 * Establecer monto
	 * @param amount Monto
	 * @return Instancia
	 */
	public Proc8583 setAmount(double amount)
	{
		return setData(EField.AMOUNT_TRX, Long.toString(Math.round(amount*100)));
	}
	
	/**
	 * Obtener monto
	 * @return amount Monto
	 */
	public byte[] getAmount()
	{
		return getData_raw(EField.AMOUNT_TRX);
	}

	/**
	 * Establecer la fecha y hora de transmisión 
	 * @param datetime Fecha y hora de transmisión
	 * @return Instancia
	 */
	public Proc8583 setDateTime(String datetime)
	{
		return setData(EField.DATETIME_TX, datetime);
	}

	/**
	 * Obtener la fecha y hora de transmisión 
	 * @return Fecha y hora de transmisión
	 */
	public byte[] getDateTime()
	{
		return getData_raw(EField.DATETIME_TX);
	}

	/**
	 * Establecer el número de seguimiento 
	 * @param traceNumber Número de seguimiento
	 * @return Instancia
	 */
	public Proc8583 setTraceNumber(String traceNumber)
	{
		return setData(EField.TRACE_NUMBER, traceNumber);
	}

	/**
	 * Obtener el número de seguimiento 
	 * @return Número de seguimiento
	 */
	public byte[] getTraceNumber()
	{
		return getData_raw(EField.TRACE_NUMBER);
	}

	/**
	 * Establecer la hora local
	 * @param time Hora local
	 * @return Instancia
	 */
	public Proc8583 setLocalTime(String time)
	{
		return setData(EField.TIME_LOCAL, time);
	}

	/**
	 * Obtener la hora local
	 * @return Hora local
	 */
	public byte[] getLocalTime()
	{
		return getData_raw(EField.TIME_LOCAL);
	}

	/**
	 * Establecer la fecha local
	 * @param date Fecha local
	 * @return Instancia
	 */
	public Proc8583 setLocalDate(String date)
	{
		return setData(EField.DATE_LOCAL, date);
	}

	/**
	 * Obtener la fecha local
	 * @return Fecha local
	 */
	public byte[] getLocalDate()
	{
		return getData_raw(EField.DATE_LOCAL);
	}

	/**
	 * Establecer la fecha de expiración de la tarjeta
	 * @param date Fecha de expiración de la tarjeta
	 * @return Instancia
	 */
	public Proc8583 setExpirationDate(String date)
	{
		return setData(EField.DATE_EXPIRATION, date);
	}

	/**
	 * Obtener la fecha de expiración de la tarjeta
	 * @return Fecha de expiración de la tarjeta
	 */
	public byte[] getExpirationDate()
	{
		return getData_raw(EField.DATE_EXPIRATION);
	}

	/**
	 * Establecer la fecha de liquidación
	 * @param date Fecha de liquidación
	 * @return Instancia
	 */
	public Proc8583 setSettlementDate(String date)
	{
		return setData(EField.DATE_SETTLEMENT, date);
	}

	/**
	 * Obtener la fecha de liquidación
	 * @return Fecha de liquidación
	 */
	public byte[] getSettlementDate()
	{
		return getData_raw(EField.DATE_SETTLEMENT);
	}

	/**
	 * Establecer la fecha de captura
	 * @param date Fecha de captura
	 * @return Instancia
	 */
	public Proc8583 setCaptureDate(String date)
	{
		return setData(EField.DATE_CAPTURE, date);
	}

	/**
	 * Obtener la fecha de captura
	 * @return Fecha de captura
	 */
	public byte[] getCaptureDate()
	{
		return getData_raw(EField.DATE_CAPTURE);
	}

	/**
	 * Establecer el código de país
	 * @param code Código de país
	 * @return Instancia
	 */
	public Proc8583 setCountryCode(String code)
	{
		return setData(EField.COUNTRY_CODE, code);
	}

	/**
	 * Obtener el código de país
	 * @return Código de país
	 */
	public byte[] getCountryCode()
	{
		return getData_raw(EField.COUNTRY_CODE);
	}

	/**
	 * Establecer el modo de entrada del punto de servicio 
	 * @param mode Modo de entrada del punto de servicio
	 * @return Instancia
	 */
	public Proc8583 setEntryMode(String mode)
	{
		return setData(EField.ENTRY_MODE, mode);
	}

	/**
	 * Obtener el modo de entrada del punto de servicio 
	 * @return Modo de entrada del punto de servicio
	 */
	public byte[] getEntryMode()
	{
		return getData_raw(EField.ENTRY_MODE);
	}
	
	/**
	 * Establecer el número de PAN de la aplicación / Número de secuencia de la tarjeta
	 * @param value Número de PAN de la aplicación / Número de secuencia de la tarjeta
	 * @return Instancia
	 */
	public Proc8583 setCardSecuenceNumber(String value)
	{
		return setData(EField.CARD_SEQ_NUMBER, value);
	}

	/**
	 * Obtener el número de PAN de la aplicación / Número de secuencia de la tarjeta 
	 * @return Número de PAN de la aplicación
	 */
	public byte[] getCardSecuenceNumber()
	{
		return getData_raw(EField.CARD_SEQ_NUMBER);
	}

	/**
	 * Establecer el NII
	 * @param nii NII
	 * @return Instancia
	 */
	public Proc8583 setNII(String nii)
	{
		return setData(EField.NII, nii);
	}

	/**
	 * Obtener el NII
	 * @return NII
	 */
	public byte[] getNII()
	{
		return getData_raw(EField.NII);
	}

	/**
	 * Establecer el código de condición del POS
	 * @param code Código de condición del POS
	 * @return Instancia
	 */
	public Proc8583 setPOSConditionCode(String code)
	{
		return setData(EField.POS_CONDITION_CODE, code);
	}

	/**
	 * Obtener el código de condición del POS
	 * @return code
	 */
	public byte[] getPOSConditionCode()
	{
		return getData_raw(EField.POS_CONDITION_CODE);
	}

	/**
	 * Establecer el número de cuenta primaria extendida / CVV  
	 * @param value Número de cuenta primaria extendida / CVV
	 * @return Instancia
	 */
	public Proc8583 setCVV(String value)
	{
		return setData(EField.CVV, value);
	}

	/**
	 * Obtener el número de cuenta primaria extendida / CVV  
	 * @return Número de cuenta primaria extendida / CVV
	 */
	public byte[] getCVV()
	{
		return getData_raw(EField.CVV);
	}

	/**
	 * Establecer el track 2
	 * @param track Track 2
	 * @return Instancia
	 */
	public Proc8583 setTrack2(String track)
	{
		return setData(EField.TRACK2, track);
	}

	/**
	 * Obtener el track 2
	 * @return Track 2
	 */
	public byte[] getTrack2()
	{
		return getData_raw(EField.TRACK2);
	}
	
	/**
	 * Establecer el track 3
	 * @param track Track 3
	 * @return Instancia
	 */
	public Proc8583 setTrack3(String track)
	{
		return setData(EField.TRACK3, track);
	}
	
	/**
	 * Obtener el track 3
	 * @return Track 3
	 */
	public byte[] getTrack3()
	{
		return getData_raw(EField.TRACK3);
	}	

	/**
	 * Establecer el RRN (Retrieval Reference Number) 
	 * @param rrn RRN (Retrieval Reference Number)
	 * @return Instancia
	 */
	public Proc8583 setRRN(String rrn)
	{
		return setData(EField.RRN, rrn);
	}

	/**
	 * Obtener el RRN (Retrieval Reference Number) 
	 * @return RRN (Retrieval Reference Number)
	 */
	public byte[] getRRN()
	{
		return getData_raw(EField.RRN);
	}	

	/**
	 * Establecer la respuesta de identificación de autorización 
	 * @param value Respuesta de identificación de autorización
	 * @return Instancia
	 */
	public Proc8583 setAuthorizationID(String value)
	{
		return setData(EField.AUTHORIZATION_ID, value);
	}

	/**
	 * Obtener la respuesta de identificación de autorización 
	 * @return Respuesta de identificación de autorización
	 */
	public byte[] getAuthorizationID()
	{
		return getData_raw(EField.AUTHORIZATION_ID);
	}	

	/**
	 * Establecer el código de respuesta
	 * @param code Código de respuesta
	 * @return Instancia
	 */
	public Proc8583 setResponseCode(String code)
	{
		return setData(EField.RESPONSE_CODE, code);
	}

	/**
	 * Obtener el código de respuesta
	 * @return Código de respuesta
	 */
	public byte[] getResponseCode()
	{
		return getData_raw(EField.RESPONSE_CODE);
	}	

	/**
	 * Establecer el ID de terminal
	 * @param id ID de terminal
	 * @return Instancia
	 */
	public Proc8583 setTerminalID(String id)
	{
		return setData(EField.TERMINAL_ID, id);
	}

	/**
	 * Obtener el ID de terminal
	 * @return ID de terminal
	 */
	public byte[] getTerminalID()
	{
		return getData_raw(EField.TERMINAL_ID);
	}	
	
	/**
	 * Establecer el número/código de comercio
	 * @param id Número/código de comercio
	 * @return Instancia
	 */
	public Proc8583 setCommerceCode(String id)
	{
		return setData(EField.COMMERCE_CODE, id);
	}
	
	/**
	 * Obtener el número/código de comercio
	 * @return Número/código de comercio
	 */
	public byte[] getCommerceCode()
	{
		return getData_raw(EField.COMMERCE_CODE);
	}

	/**
	 * Establecer el nombre del comercio
	 * @param name Nombre del comercio
	 * @return Instancia
	 */
	public Proc8583 setCommerceName(String name)
	{
		return setData(EField.COMMERCE_NAME, name);
	}

	/**
	 * Obtener el nombre del comercio
	 * @return Nombre del comercio
	 */
	public byte[] getCommerceName()
	{
		return getData_raw(EField.COMMERCE_NAME);
	}

	/**
	 * Establecer los datos adicionales que llegan en el campo 44
	 * @param data Datos adicionales que llegan en el campo 44
	 * @return Instancia
	 */
	/*public Proc8583 setAdditionalData44(byte[] data)
	{
		return setData(EField.ADDITIONAL_DATA_44, data);
	}*/

	/**
	 * Establecer los datos adicionales que llegan en el campo 44
	 * @param data Datos adicionales que llegan en el campo 44
	 * @return Instancia
	 */
	public Proc8583 setAdditionalData44(String data)
	{
		return setData(EField.ADDITIONAL_DATA_44, data);
	}

	/**
	 * Obtener los datos adicionales que llegan en el campo 44
	 * @return Datos adicionales que llegan en el campo 44
	 */
	public byte[] getAdditionalData44()
	{
		return getData_raw(EField.ADDITIONAL_DATA_44);
	}
	
	/**
	 * Establecer el track1
	 * @param track Track1
	 * @return Instancia
	 */
	public Proc8583 setTrack1(String track)
	{
		return setData(EField.TRACK1, track);
	}

	/**
	 * Obtener el track1
	 * @return Track1
	 */
	public byte[] getTrack1()
	{
		return getData_raw(EField.TRACK1);
	}

	/**
	 * Establecer los datos adicionales del campo 46
	 * @param data Datos adicionales del campo 46
	 * @return Instancia
	 */
	/*public Proc8583 setAdditionalData46(byte[] data)
	{
		return setData(EField.ADDITIONAL_DATA_46, data);
	}*/

	/**
	 * Establecer los datos adicionales del campo 46
	 * @param data Datos adicionales del campo 46
	 * @return Instancia
	 */
	public Proc8583 setAdditionalData46(String data)
	{
		return setData(EField.ADDITIONAL_DATA_46, data);
	}

	/**
	 * Obtener los datos adicionales del campo 46
	 * @return Datos adicionales del campo 46
	 */
	public byte[] getAdditionalData46()
	{
		return getData_raw(EField.ADDITIONAL_DATA_46);
	}

	/**
	 * Establecer los datos adicionales del campo 47
	 * @param data Datos adicionales del campo 47
	 * @return Instancia
	 */
	/*public Proc8583 setAdditionalData47(byte[] data)
	{
		return setData(EField.ADDITIONAL_DATA_47, data);
	}*/

	/**
	 * Establecer los datos adicionales del campo 47
	 * @param data Datos adicionales del campo 47
	 * @return Instancia
	 */
	public Proc8583 setAdditionalData47(String data)
	{
		return setData(EField.ADDITIONAL_DATA_47, data);

	}

	/**
	 * Obtener los datos adicionales del campo 47
	 * @return Datos adicionales del campo 47
	 */
	public byte[] getAdditionalData47()
	{
		return getData_raw(EField.ADDITIONAL_DATA_47);
	}
	
	/**
	 * Establecer los datos adicionales del campo 48
	 * @param data Datos adicionales del campo 48
	 * @return Instancia
	 */
	/*public Proc8583 setAdditionalData48(byte[] data)
	{
		return setData(EField.ADDITIONAL_DATA_48, data);
	}*/

	/**
	 * Establecer los datos adicionales del campo 48
	 * @param data Datos adicionales del campo 48
	 * @return Instancia
	 */
	public Proc8583 setAdditionalData48(String data)
	{
		return setData(EField.ADDITIONAL_DATA_48, data);
	}

	/**
	 * Obtener los datos adicionales del campo 48
	 * @return Datos adicionales del campo 48
	 */
	public byte[] getAdditionalData48()
	{
		return getData_raw(EField.ADDITIONAL_DATA_48);
	}

	/**
	 * Establecer el código de la moneda de la transacción
	 * @param currency Código de la moneda de la transacción
	 * @return Instancia
	 */
	public Proc8583 setCurrency(String currency)
	{
		return setData(EField.CURRENCY, currency);
	}

	/**
	 * Obtener el código de la moneda de la transacción
	 * @return Código de la moneda de la transacción
	 */
	/*public byte[] getCurrency()
	{
		return getData(EField.CURRENCY);
	}*/

	/**
	 * Establecer los datos del PIN (Personal Identification Number)
	 * @param pindata Datos del PIN (Personal Identification Number)
	 * @return Instancia
	 */
	public Proc8583 setPinData(String pindata)
	{
		return setData(EField.PIN_BLOCK, pindata);
	}
	
	/**
	 * Obtener los datos del PIN (Personal Identification Number)
	 * @return Datos del PIN (Personal Identification Number)
	 */
	public byte[] getPinData()
	{
		return getData_raw(EField.PIN_BLOCK);
	}

	/**
	 * Establecer los montos adicionales
	 * @param amount Montos adicionales
	 * @return Intancia
	 */
	public Proc8583 setSecondAmount(String amount)
	{
		return setData(EField.SECOND_AMOUNT, amount);
	}

	/**
	 * Obtener valor de los montos adicionales
	 * @return Montos adicionales
	 */
	public byte[] getSecondAmount()
	{
		return getData_raw(EField.SECOND_AMOUNT);
	}

	/**
	 * Establecer los datos del campo 55
	 * @param data Datos del campo 55
	 * @return Instancia
	 */
	/*public Proc8583 setDataField55(byte[] data)
	{
		return setData(EField.FIELD_55, data);
	}*/

	/**
	 * Establecer los datos del campo 55
	 * @param data Datos del campo 55
	 * @return Instancia
	 */
	public Proc8583 setDataField55(String data)
	{
		return setData(EField.FIELD_55, data);
	}

	/**
	 * Obtener los datos del campo 55
	 * @return Datos del campo 55
	 */
	public byte[] getDataField55()
	{
		return getData_raw(EField.FIELD_55);
	}

	/**
	 * Establecer los datos del campo 56
	 * @param data Datos del campo 56
	 * @return Instancia
	 */
	/*public Proc8583 setDataField56(byte[] data)
	{
		return setData(EField.FIELD_56, data);
	}*/

	/**
	 * Establecer los datos del campo 56
	 * @param data Datos del campo 56
	 * @return Instancia
	 */
	public Proc8583 setDataField56(String data)
	{
		return setData(EField.FIELD_56, data);
	}
	
	/**
	 * Obtener los datos del campo 56
	 * @return Datos del campo 56
	 */
	public byte[] getDataField56()
	{
		return getData_raw(EField.FIELD_56);
	}
	
	/**
	 * Establecer los datos del campo 57
	 * @param data Datos del campo 57
	 * @return Instancia
	 */
	/*public Proc8583 setDataField57(byte[] data)
	{
		return setData(EField.FIELD_57, data);
	}*/

	/**
	 * Establecer los datos del campo 57
	 * @param data Datos del campo 57
	 * @return Instancia
	 */
	public Proc8583 setDataField57(String data)
	{
		return setData(EField.FIELD_57, data);
	}
	
	/**
	 * Obtener los datos del campo 57
	 * @return Datos del campo 57
	 */
	public byte[] getDataField57()
	{
		return getData_raw(EField.FIELD_57);
	}
	
	/**
	 * Establecer los datos del campo 58
	 * @param data Datos del campo 58
	 * @return Instancia
	 */
	/*public Proc8583 setDataField58(byte[] data)
	{
		return setData(EField.FIELD_58, data);
	}*/

	/**
	 * Establecer los datos del campo 58
	 * @param data Datos del campo 58
	 * @return Instancia
	 */
	public Proc8583 setDataField58(String data)
	{
		return setData(EField.FIELD_58, data);
	}
	
	/**
	 * Obtener los datos del campo 58
	 * @return Datos del campo 58
	 */
	public byte[] getDataField58()
	{
		return getData_raw(EField.FIELD_58);
	}
	
	/**
	 * Establecer los datos del campo 59
	 * @param data Datos del campo 59
	 * @return Instancia
	 */	
	/*public Proc8583 setDataField59(byte[] data)
	{
		return setData(EField.FIELD_59, data);
	}*/

	/**
	 * Establecer los datos del campo 59
	 * @param data Datos del campo 59
	 * @return Instancia
	 */	
	public Proc8583 setDataField59(String data)
	{
		return setData(EField.FIELD_59, data);
	}

	/**
	 * Obtener los datos del campo 59
	 * @return Datos del campo 59
	 */
	public byte[] getDataField59()
	{
		return getData_raw(EField.FIELD_59);
	}
	
	/**
	 * Establecer los datos del campo 60
	 * @param data Datos del campo 60
	 * @return Instancia
	 */	
	/*public Proc8583 setDataField60(byte[] data)
	{
		return setData(EField.FIELD_60, data);
	}*/

	/**
	 * Establecer los datos del campo 60
	 * @param data Datos del campo 60
	 * @return Instancia
	 */	
	public Proc8583 setDataField60(String data)
	{
		return setData(EField.FIELD_60, data);
	}

	/**
	 * Obtener los datos del campo 60
	 * @return Datos del campo 60
	 */
	public byte[] getDataField60()
	{
		return getData_raw(EField.FIELD_60);
	}

	/**
	 * Establecer los datos del campo 61
	 * @param data Datos del campo 61
	 * @return Instancia
	 */	
	/*public Proc8583 setDataField61(byte[] data)
	{
		return setData(EField.FIELD_61, data);
	}*/

	/**
	 * Establecer los datos del campo 61
	 * @param data Datos del campo 61
	 * @return Instancia
	 */	
	public Proc8583 setDataField61(String data)
	{
		return setData(EField.FIELD_61, data);
	}

	/**
	 * Obtener los datos del campo 61
	 * @return Datos del campo 61
	 */
	public byte[] getDataField61()
	{
		return getData_raw(EField.FIELD_61);
	}
	
	/**
	 * Establecer los datos del campo 62
	 * @param data Datos del campo 62
	 * @return Instancia
	 */	
	/*public Proc8583 setDataField62(byte[] data)
	{
		return setData(EField.FIELD_62, data);
	}*/

	/**
	 * Establecer los datos del campo 62
	 * @param data Datos del campo 62
	 * @return Instancia
	 */	
	public Proc8583 setDataField62(String data)
	{
		return setData(EField.FIELD_62, data);
	}

	/**
	 * Obtener los datos del campo 62
	 * @return Datos del campo 62
	 */
	public byte[] getDataField62()
	{
		return getData_raw(EField.FIELD_62);
	}
	
	/**
	 * Establecer los datos del campo 63
	 * @param data Datos del campo 63
	 * @return Instancia
	 */	
	/*public Proc8583 setDataField63(byte[] data)
	{
		return setData(EField.FIELD_63, data);
	}*/

	/**
	 * Establecer los datos del campo 63
	 * @param data Datos del campo 63
	 * @return Instancia
	 */	
	public Proc8583 setDataField63(String data)
	{
		return setData(EField.FIELD_63, data);
	}

	/**
	 * Obtener los datos del campo 63
	 * @return Datos del campo 63
	 */
	public byte[] getDataField63()
	{
		return getData_raw(EField.FIELD_63);
	}
}
