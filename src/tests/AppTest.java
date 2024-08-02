package tests;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.auth.x500.X500Principal;

import com.mlf.tools.EAlign;
import com.mlf.tools.Log;
import com.mlf.tools.LogTools;
import com.mlf.tools.StrUtils;
import com.mlf.tools.iso8583.EDataType;
import com.mlf.tools.iso8583.EField;
import com.mlf.tools.iso8583.ELenType;
import com.mlf.tools.iso8583.Proc8583;

/**
 * Test
 * @author Mario
 */
@SuppressWarnings("unused")
public class AppTest
{
	private static final String DATE_FORMAT2 = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
	//2023-03-28T20:37:55.177Z
	/**
	 * Main
	 * @param args Argumentos
	 */
	public static void main(String[] args)
	{

		
	}
}
