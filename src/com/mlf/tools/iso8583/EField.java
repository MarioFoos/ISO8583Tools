package com.mlf.tools.iso8583;

/**
 * Campos del mensaje
 * @author Mario
 */
public enum EField
{
	/** No definido */
	UNDEFINED 			(-2),
	/** TPDU */
	TPDU				(-1),
	/** Message Type ID */
	MTI					(0),
	/** BitMap */
	BITMAP				(1),
	/** Primary account number (PAN) */
	PAN					(2),
	/** Processing code */
	PRCODE				(3),
	/** Amount, transaction */
	AMOUNT_TRX			(4),
	/** Amount, Settlement */
	AMOUNT_SET			(5),
	/** Amount, cardholder billing */
	AMOUNT_BILL			(6),
	/** Transmission date and time */
	DATETIME_TX			(7),
	/** Amount, Cardholder billing fee */
	AMOUNT_BILLFEE		(8),
	/** Conversion rate, Settlement */
	CONV_RATE_SET		(9),
	/** Conversion rate, cardholder billing  */
	CONV_RATE_BILL		(10),
	/** Systems trace audit number   */
	TRACE_NUMBER		(11),
	/** Time, Local transaction    */
	TIME_LOCAL			(12),
	/** Date, Local transaction (MMdd) */
	DATE_LOCAL			(13),
	/** Date, Expiration */
	DATE_EXPIRATION 	(14),
	/** Date, Settlement */
	DATE_SETTLEMENT		(15),
	/** Date, conversion */
	DATE_CONVERSION		(16),
	/** Date, capture */
	DATE_CAPTURE		(17),
	/** Merchant type */
	MERCHANT_TYPE		(18),
	/** Country code */
	COUNTRY_CODE		(19),
	/** PAN Extended, country code */
	PAN_COUNTRY_CODE	(20),
	/** Forwarding institution. country code */
	FWD_COUNTRY_CODE	(21),
	/** Point of service entry mode */
	ENTRY_MODE			(22),
	/** Application PAN number / Card sequence number */
	CARD_SEQ_NUMBER		(23),
	/** Network International identifier, NII */
	NII 				(24),
	/** Point of service condition code */
	POS_CONDITION_CODE	(25),
	/** Point of service capture code */
	POS_CAPTURE_CODE	(26),
	/** Authorizing identification response length */
	AUTH_RESP_LEN		(27),
	/** Amount, transaction fee */
	AMOUNT_TRX_FEE		(28),
	/** Amount. settlement fee */
	AMOUNT_SET_FEE		(29),
	/** Amount, transaction processing fee */
	AMOUNT_TRX_PR_FEE	(30),
	/** Amount, settlement processing fee */
	AMOUNT_SET_PR_FEE	(31),
	/** Acquiring institution identification code */
	ACQ_ID_CODE			(32),
	/** Forwarding institution identification code */
	FWD_ID_CODE			(33),
	/** Primary account number, extended, CVV */
	CVV 				(34),
	/** Track 2 data */
	TRACK2 				(35),
	/** Track 3 data */
	TRACK3 				(36),
	/** Retrieval reference number, RRN */
	RRN					(37),
	/** Authorization identification response */
	AUTHORIZATION_ID	(38),
	/** Response code */
	RESPONSE_CODE		(39),
	/** Service restriction code */
	SERV_RESTRIC_CODE	(40),
	/** Card acceptor terminal identification */
	TERMINAL_ID	(41),
	/** Card acceptor identification code */
	COMMERCE_CODE		(42),
	/** Card acceptor name/location */
	COMMERCE_NAME		(43),
	/** Additional response data */
	ADDITIONAL_DATA_44	(44),
	/** Track 1 Data */
	TRACK1 				(45),
	/** Additional data - ISO */
	ADDITIONAL_DATA_46	(46),
	/** Additional data - National, encripted tracks */
	ADDITIONAL_DATA_47	(47),
	/** Additional data - Private */
	ADDITIONAL_DATA_48	(48),
	/** Currency code, transaction */
	CURRENCY			(49),
	/** Currency code, settlement */
	CURRENCY_SETTLEMENT	(50),
	/** Currency code, cardholder billing */
	CURRENCY_BILL		(51),
	/** Personal Identification number data, pin block */
	PIN_BLOCK 			(52),
	/** Security related control information */
	SECURITY_INFO 		(53),
	/** Additional amounts, secound amount */
	SECOND_AMOUNT		(54),
	/** Reserved ISO, EMV data */
	FIELD_55			(55),
	/** Reserved ISO */
	FIELD_56			(56),
	/** Reserved National */
	FIELD_57			(57),
	/** Reserved National */
	FIELD_58			(58),
	/** Reserved for national use, products */
	FIELD_59			(59),
	/** Advice/reason code (private reserved), versión, trace, terminal id, etc */
	FIELD_60			(60),
	/** Reserved Private, fecha posdatado/monto */
	FIELD_61			(61),
	/** Reserved Private, roc number */
	FIELD_62			(62),
	/** Reserved Private, datos cierre de lote */
	FIELD_63			(63);

	int number;
	EField(int number)
	{
		this.number = number;
	}
	
	/**
	 * Obtener el Elemento del enum desde el nombre
	 * @param name Nombre
	 * @return Elemento del enum
	 */
	public static EField fromName(String name)
	{
		for(EField type : EField.values())
		{
			if(type.name().equalsIgnoreCase(name))
			{
				return type;
			}
		}
		return UNDEFINED;
	}

	/**
	 * Obtener el elemento del enum a partir de número del campo
	 * @param number Número de campo (0 a 63)
	 * @return Elemento del enum
	 */
	public static EField fromNumber(int number)
	{
		for(EField type : EField.values())
		{
			if(type.number == number)
			{
				return type;
			}
		}
		return UNDEFINED;
	}

	/**
	 * Obtener el número de campo
	 * @return Número de campo
	 */
	public int number()
	{
		return number;
	}
	
	/**
	 * Obtener el índice del campo
	 * @return Índice del campo
	 */
	public int index()
	{
		return (number + 1);
	}
};
