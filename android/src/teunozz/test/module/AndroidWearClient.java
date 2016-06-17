package teunozz.test.module;

import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.util.TiActivityResultHandler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

public class AndroidWearClient extends Object implements	
	TiActivityResultHandler,
	GoogleApiClient.ConnectionCallbacks,
	GoogleApiClient.OnConnectionFailedListener
{
	private final static String TAG = "AndroidWearClient";

    private static final int REQUEST_OAUTH = 1;
    private GoogleApiClient client = null;
    private TitestModule module = null;
    private boolean authInProgress = false;
    
    private void buildClient() {
    	client = new GoogleApiClient.Builder(
				TiApplication.getAppCurrentActivity())
		    	.addApi(Wearable.API)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.build();
    }
    
    public void start(TitestModule moduleArg) {
        module = moduleArg;
        buildClient();
        client.connect();
    }
    
    public void stop() {
    }
    
	public void onError(Activity activity, int requestCode, Exception e) {
	}

	public void onResult(Activity activity, int requestCode, int resultCode, Intent data) {		
		if (requestCode == REQUEST_OAUTH) {
			authInProgress = false;
            if (resultCode == Activity.RESULT_OK) {
                if (!client.isConnecting() && !client.isConnected()) {
                    client.connect();
                }
            }
        }
	}
	
	public void onConnectionFailed(ConnectionResult result) {
	}

	public void onConnected(Bundle connectionHint) {
		// connected to Google Wearable API
		module.fireEvent("connected", null);
	}
    
	public void onConnectionSuspended(int cause) {
	}
    
    public void sendData() {
		// Construct a DataRequest and send over the data layer
		PutDataMapRequest putDMR = PutDataMapRequest.create("/test");
		putDMR.getDataMap().putString("key", "test");
		PutDataRequest request = putDMR.asPutDataRequest();
		DataApi.DataItemResult result = Wearable.DataApi.putDataItem(client, request).await();
		
		if (result.getStatus().isSuccess()) {
    		// Do something
			Log.e(TAG, "SUCCESS");
		} else {
			// Do something
		}
    }    
}
