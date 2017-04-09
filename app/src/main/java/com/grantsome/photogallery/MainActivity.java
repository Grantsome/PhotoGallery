package com.grantsome.photogallery;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
public class MainActivity extends Activity {

	private TextView tv;
	private EditText et;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		tv = (TextView) findViewById(R.id.tv);
		et = (EditText) findViewById(R.id.et);
	}
	public void gethand(View v){
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					//initSSL();
					initSSLALL();
				} catch (Exception e) {
					Log.e("HTTPS TEST", e.getMessage());
				}
			}
		}).start();
	}
	

	/**
	 * HttpUrlConnection 方式，支持指定load-der.crt证书验证，此种方式Android官方建议
	 * 
	 * @throws CertificateException
	 * @throws IOException
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 */
	public void initSSL() throws CertificateException, IOException, KeyStoreException,
			NoSuchAlgorithmException, KeyManagementException {
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		InputStream in = getAssets().open("baidu.cer");
		Certificate ca = cf.generateCertificate(in);

		KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
		keystore.load(null, null);
		keystore.setCertificateEntry("ca", ca);

		String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
		TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
		tmf.init(keystore);

		// Create an SSLContext that uses our TrustManager
		SSLContext context = SSLContext.getInstance("SSL");
		context.init(null, tmf.getTrustManagers(), null);
		String urlStr = "https://api.flickr.com/services/rest/?method=flickr.photos.getRecent&api_key=89b13c41adafc85b80edbadc4a655b60&format=json&nojsoncallback=1&extras=url_s";
		URL url = new URL(urlStr);
		//忽略对证书的合法性检测
//		HttpsURLConnection.setDefaultHostnameVerifier( new HostnameVerifier(){  
//		    public boolean verify(String string,SSLSession ssls) {  
//		            return true; 
//		        }  
//		});
		HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
		urlConnection.setSSLSocketFactory(context.getSocketFactory());
		InputStream input = urlConnection.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
		final StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = reader.readLine()) != null) {
			result.append(line);
		}
		System.out.println("https返回数据："+result.toString());
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				tv.setText(result.toString());
			}
		});
		Log.e("TTTT", result.toString());
	}

	/**
	 * HttpUrlConnection支持所有Https免验证，不建议使用
	 * 
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 */
	private String result = null;
	public void initSSLALL() throws NoSuchProviderException {
		try {
			URL url = new URL("https://api.flickr.com/services/rest/?method=flickr.photos.getRecent&api_key=89b13c41adafc85b80edbadc4a655b60&format=json&nojsoncallback=1&extras=url_s");
			SSLContext context = SSLContext.getInstance("SSL");
			context.init(null, new TrustManager[] { new TrustAnyTrustManager() }, null);
			HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory()); 
			HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
				@Override
				public boolean verify(String arg0, SSLSession arg1) {
					return true;
				}
			});
			HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.setDoOutput(false);
			connection.setRequestMethod("GET");
			//connection.connect();
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			InputStream in = connection.getInputStream();
			int bytesRead = 0;
			byte[] buffer = new byte[1024];
			while ((bytesRead = in.read(buffer)) > 0) {
				out.write(buffer, 0, bytesRead);
			}
			result =  out.toByteArray().toString();
			out.close();
			in.close();
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		System.out.println("result:::" + result.toString());
		Log.e("TTTT", result.toString());
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				tv.setText(result.toString());
			}
		});
		
	}
	private static class TrustAnyTrustManager implements X509TrustManager {

		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException
		{
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException
		{
		}

		public X509Certificate[] getAcceptedIssuers()
		{
			return new X509Certificate[]{};
		}
	}

	private static class TrustAnyHostnameVerifier implements HostnameVerifier
	{
		public boolean verify(String hostname, SSLSession session)
		{
			return true;
		}
	}

	public static String connect(String url) throws Exception
	{
		InputStream in = null;
		OutputStream out = null;
		byte[] buffer = new byte[4096];
		String str_return = "";
		try
		{
//            URL console = new URL(url);
			URL console = new URL(new String(url.getBytes("utf-8")));

			HttpURLConnection conn = (HttpURLConnection) console.openConnection();
			//如果是https
			if (conn instanceof HttpsURLConnection)
			{
				SSLContext sc = SSLContext.getInstance("SSL");
				sc.init(null, new TrustManager[]{new TrustAnyTrustManager()}, new java.security.SecureRandom());
				((HttpsURLConnection) conn).setSSLSocketFactory(sc.getSocketFactory());
				((HttpsURLConnection) conn).setHostnameVerifier(new TrustAnyHostnameVerifier());
			}
//            conn.setRequestProperty("Content-type", "text/html");
//            conn.setRequestProperty("Accept-Charset", "GBK");
//            conn.setRequestProperty("contentType", "GBK");
//            conn.setRequestMethod("POST");
//            conn.setDoOutput(true);
//            conn.setRequestProperty("User-Agent", "directclient");
//            PrintWriter outdate = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(),"utf-8"));
//            outdate.println(url);
//            outdate.close();
			conn.connect();
			InputStream is = conn.getInputStream();
			DataInputStream indata = new DataInputStream(is);
			String ret = "";

			while (ret != null)
			{
				ret = indata.readLine();
				if (ret != null && !ret.trim().equals(""))
				{
					str_return = str_return + new String(ret.getBytes("ISO-8859-1"), "utf-8");
				}
			}
			conn.disconnect();
		} catch (Exception e)
		{
			throw e;
		} finally
		{
			try
			{
				in.close();
			} catch (Exception e)
			{
			}
			try
			{
				out.close();
			} catch (Exception e)
			{
			}
		}
		return str_return;
	}

}