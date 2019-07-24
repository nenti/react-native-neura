package com.RNpublic.neuraintegration;

import android.os.Bundle;
import android.util.Log;

import com.neura.resources.authentication.AnonymousAuthenticationStateListener;
import com.neura.resources.authentication.AuthenticationState;
import com.neura.resources.user.UserDetails;
import com.neura.resources.user.UserDetailsCallbacks;
import com.neura.sdk.service.SimulateEventCallBack;
import com.neura.standalonesdk.service.NeuraApiClient;
import com.neura.standalonesdk.engagement.NeuraEngagements;

import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.modules.core.DeviceEventManagerModule;

public final class NeuraIntegrationSingleton {
    private static final NeuraIntegrationSingleton INSTANCE = new NeuraIntegrationSingleton();
    private NeuraEngagements NeuraEngagements;
    private NeuraApiClient NeuraApiClient;
    private ReactContext reactContext;

    public NeuraApiClient getNeuraApiClient() {
        return NeuraApiClient;
    }

    public NeuraEngagements getNeuraEngagements() {
        return NeuraEngagements;
    }

    public void setNeuraApiClient(NeuraApiClient mNeuraApiClient) {
        Log.i(getClass().getSimpleName(), "Singleton set apiClient");
        this.NeuraApiClient = mNeuraApiClient;
    }

    public ReactContext getreactContext() {
        return reactContext;
    }

    public void setreactContext(ReactContext reactContext) {
        Log.i(getClass().getSimpleName(), "Singleton set reactContext");
        this.reactContext = reactContext;
    }

    private NeuraIntegrationSingleton() {
        if (INSTANCE != null) {
            throw new IllegalStateException("Already instantiated");
        }
    }

    public static NeuraIntegrationSingleton getInstance() {
        return INSTANCE;
    }

    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Cannot clone instance of this class");
    }

    public void registerAuthStateListener() {
        Log.i(getClass().getSimpleName(), "Singleton registerAuthStateListener");
        this.NeuraApiClient.registerAuthStateListener(silentStateListener);
    }

    //
    public void kaki(){
        getNeuraApiClient().simulateAnEvent("userArrivedHomeByWalking", new SimulateEventCallBack() {
            @Override
            public void onSuccess(String dafuq) {
                Log.i(getClass().getSimpleName(), "dafuq: "  + dafuq);

            }

            @Override
            public void onFailure(String dafuqq, String dafuqqq) {
                Log.i(getClass().getSimpleName(), "dafuqq: "  + dafuqq);
            }
        });
    }

    public void onAuth() {
        final String token = getNeuraApiClient().getUserAccessToken();

        NeuraIntegrationSingleton.getInstance().getNeuraApiClient().getUserDetails(new UserDetailsCallbacks() {
            @Override
            public void onSuccess(UserDetails userDetails) {
                Log.i(getClass().getSimpleName(), "userId: "  + userDetails.getData().getNeuraId());
                WritableMap payload = Arguments.createMap();
                payload.putString("userToken", token);
                payload.putString("userId", userDetails.getData().getNeuraId());
                emitAuth(payload);
            }

            @Override
            public void onFailure(Bundle resultData, int errorCode) {

            }
        });
    }

    //Send the authPaylod to Android
    private void emitAuth(WritableMap payload) {
        this.reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit("neuraAuth", payload);
    }

    //create a call back to handle authentication stages.
    AnonymousAuthenticationStateListener silentStateListener = new AnonymousAuthenticationStateListener() {
        @Override
        public void onStateChanged(AuthenticationState state) {
            switch (state) {
                case AccessTokenRequested:
                    Log.i(getClass().getSimpleName(), "AnonymousAuthenticationStateListener: AccessTokenRequested");
                    break;
                case AuthenticatedAnonymously:
                    Log.i(getClass().getSimpleName(), "AnonymousAuthenticationStateListener: AuthenticatedAnonymously");
                    // successful authentication - I don't know what this does.
                    NeuraIntegrationSingleton.getInstance().getNeuraApiClient().unregisterAuthStateListener();
                    onAuth();
                    break;
                case NotAuthenticated:
                    Log.i(getClass().getSimpleName(), "AnonymousAuthenticationStateListener: NotAuthenticated");
                    break;
                case FailedReceivingAccessToken:
                    Log.i(getClass().getSimpleName(), "AnonymousAuthenticationStateListener: FailedReceivingAccessToken");

                    // Authentication failed indefinitely. a good opportunity to retry the authentication flow
                    NeuraIntegrationSingleton.getInstance().getNeuraApiClient().unregisterAuthStateListener();
                    break;
                default:
            }
        }
    };
}