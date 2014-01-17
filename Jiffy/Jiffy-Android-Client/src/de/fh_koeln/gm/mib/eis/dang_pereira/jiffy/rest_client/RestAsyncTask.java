package de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.rest_client;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.Config;
import de.fh_koeln.gm.mib.eis.dang_pereira.jiffy.R;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

public class RestAsyncTask extends AsyncTask<Object, Void, Bundle> {
	
	private static DefaultHttpClient _httpClient;
	private static Context appCtx;
	
	private HTTPCallback callback;
	
	@Override
	protected Bundle doInBackground(Object... params) {
		
		if(params.length < 4)
			return null;
		
		appCtx = (Context)params[0];
		String op = (String)params[1];
		String path = (String)params[2];
		callback = (HTTPCallback)params[3];
		
		HashMap headers = new HashMap();
		if(params.length == 5 && params[4] != null && params[4] instanceof HashMap) {
			headers = (HashMap)params[4];
		}
		
		HttpEntity givenEntity = null;
		if(params.length == 6 && params[5] instanceof HttpEntity) {
			givenEntity = (HttpEntity)params[5];
		}

		
		Config cfg = Config.getInstance();
		
		String host = cfg.rest_endpoint.host + ":" + cfg.rest_endpoint.port;
		
		HttpUriRequest req = null;
		
		if(op.equalsIgnoreCase("get")) {
			req = new HttpGet(host + path);
		}
		else if(op.equalsIgnoreCase("put")) {
			req = new HttpPut(host + path);
			((HttpPut)req).setEntity(givenEntity);
		}
		else if(op.equalsIgnoreCase("post")) {
			req = new HttpPost(host + path);
			((HttpPost)req).setEntity(givenEntity);
		}
		else if(op.equalsIgnoreCase("delete")) {
			req = new HttpDelete(host + path);
		}
		else {
			Log.e(Config.TAG, "Unbekannter HTTP-Verb!");
			return null;
		}
		
		HttpClient client = getClient();
		
		/* Header setzen */
		for(Object key: headers.keySet()) {
			req.setHeader((String)key, (String)headers.get(key));
		}
		boolean endpointAvailable = false;
		Bundle resp = new Bundle();
		
		HttpResponse httpResp;
		try {
			httpResp = client.execute(req);
			resp.putInt("statuscode", httpResp.getStatusLine().getStatusCode());
			
			byte[] data = null;
			InputStream is = httpResp.getEntity().getContent();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			int size = 1024;
			int len;
			byte[] buf = new byte[size];
			while((len = is.read(buf, 0, size)) != -1) {
				bos.write(buf, 0, len);
			}
			data = bos.toByteArray();
			
			if(data != null)
				resp.putByteArray("data", data);
			endpointAvailable = true;
			
		} catch (IOException e) {
			Log.d(Config.TAG, "Konnte die Verbindung nicht herstellen: " + e.getMessage());
		}
		
		resp.putBoolean("available", endpointAvailable);
		
	    return resp;
	}

	
	private static DefaultHttpClient getClient() {
		if(_httpClient == null) {
			
			SchemeRegistry schemeRegistry = new SchemeRegistry();
			
			Config cfg = Config.getInstance();
			
			SocketFactory sf = null;
			try {
				sf = HttpSSLSocketFactory.getSSLSocketFactory(appCtx.getResources().openRawResource(R.raw.broker), cfg.ssl.pass_ks);
			} catch (Exception e) {
				Log.e(Config.TAG, "KeyStore konnte nicht eingebunden werden: " + e.getMessage());
			}
			
			schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			schemeRegistry.register(new Scheme("https", sf, 443));
			HttpParams httpparams = new BasicHttpParams();
			SingleClientConnManager scm = new SingleClientConnManager(httpparams, schemeRegistry);

			int timeoutConnection = 10000;
	        int timeoutSocket = 10000;
	        HttpConnectionParams.setConnectionTimeout(httpparams, timeoutConnection);
	        HttpConnectionParams.setSoTimeout(httpparams, timeoutSocket);

			_httpClient = new DefaultHttpClient(scm, httpparams);
		}
		
		return _httpClient;
	}
	

	protected void onPostExecute(Bundle resp) {
		callback.handle(resp);
	}
	
}
