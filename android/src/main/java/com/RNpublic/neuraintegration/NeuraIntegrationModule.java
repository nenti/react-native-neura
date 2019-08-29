package com.RNpublic.neuraintegration;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.InstanceIdResult;
import com.neura.sdk.service.SimulateEventCallBack;
import com.neura.sdk.service.SubscriptionRequestCallbacks;
import com.neura.standalonesdk.engagement.EngagementFeatureAction;
import com.neura.standalonesdk.util.SDKUtils;
import com.neura.resources.authentication.AnonymousAuthenticateCallBack;
import com.neura.resources.authentication.AnonymousAuthenticateData;
import com.neura.sdk.object.AnonymousAuthenticationRequest;
import com.google.firebase.iid.FirebaseInstanceId;
import com.neura.resources.user.UserDetailsCallbacks;
import com.neura.resources.user.UserDetails;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;
import android.os.Bundle;

import static com.android.volley.VolleyLog.TAG;
import static com.neura.standalonesdk.engagement.EngagementFeatureAction.CLOSE;
import static com.neura.standalonesdk.engagement.EngagementFeatureAction.OPT_OUT;
import static com.neura.standalonesdk.engagement.EngagementFeatureAction.REJECT;
import static com.neura.standalonesdk.engagement.EngagementFeatureAction.SNOOZE;
import static com.neura.standalonesdk.engagement.EngagementFeatureAction.SUCCESS;

import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;


public class NeuraIntegrationModule extends ReactContextBaseJavaModule {
    private Callback success;
    private Callback error;
    private String appUid = "";
    private String appSecret = "";
    private ReactApplicationContext mReactApplicationContext;
    private String LoginSuccessMessage = "Successfully requested authentication with neura. ";

    public NeuraIntegrationModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mReactApplicationContext = reactContext;
        NeuraIntegrationSingleton.getInstance().setreactContext(mReactApplicationContext);
    }

    @Override
    public String getName() {
        return "RNNeuraIntegration";
    }

    /**
     * public static int tagEngagementFeature(android.content.Context context,
     *                                        java.lang.String featureName,
     *                                        java.lang.String instanceId,
     *                                        EngagementFeatureAction action,
     *                                        java.lang.String value)
     * Method to tag an action triggered by the user that will be monitored in the engagement dashboard.
     * Parameters:
     * context - application context Context.getApplicationContext()
     * featureName - Mandatory. The name that will be displayed in the insights dashboard, the feature to monitor.
     * Can contain only alphanumeric (a-z, A-Z, 0-9) and ‘_’, ‘-’ characters and must be not longer than 32 characters.
     * instanceId - Optional. Describes specific instance of engagement
     * in case there is need to bind between several engagement instances
     * For example:
     * two engagements which need to be bound should have same value for instanceId. API
     * action - from closed list: EngagementFeatureAction
     * value - Optional. Describes specific value of feature.
     * For example
     * featureName = "button_pressed", value = "red"
     * featureName = "button_pressed", value = "green"
     * Returns:
     * error code, if success SUCCESS, otherwise ERROR_MANDATORY_PARAM , ERROR_CLIENT_NOT_LOGGED_IN ERROR_CLIENT_INVALID_FEATURE_NAME_PARAM, ERROR_CLIENT_INVALID_VALUE_PARAM, ERROR_CLIENT_ENGAGEMENT_DISABLED
     *
     *  @param featureName
     * @param instanceId
     * @param value
     */
    @ReactMethod
    private void tagEngagementFeature(String featureName, @Nullable String instanceId, @Nullable String actionString,  @Nullable String value, final Promise promise) {
        EngagementFeatureAction action;
        switch (actionString) {
            case "CLOSE":
                action = CLOSE;
                break;
            case "REJECT":
                action = REJECT;
                break;
            case "OPT_OUT":
                action = OPT_OUT;
                break;
            case "SNOOZE":
                action = SNOOZE;
                break;
            default:
                action = SUCCESS;
                break;
        }

        int response =  NeuraIntegrationSingleton.getInstance().getNeuraEngagements().tagEngagementFeature(mReactApplicationContext.getApplicationContext(), featureName, instanceId, action, value);

        if (response == 0) {
            String successMessage = "Neura Tag Success";
            Log.i(getClass().getSimpleName(), successMessage);
            promise.resolve(successMessage);
        } else {
            String errorMessage = "Neura Tag Fail: ";
            Log.i(getClass().getSimpleName(), errorMessage);
            promise.reject(SDKUtils.errorCodeToString(response), errorMessage);
        }

    }

    /**
     * public static int tagEngagementAttempt(Context context,
     *                                        String featureName,
     *                                        String instanceId,
     *                                        String value)
     * Method to tag an action triggered by the user that will be monitored in the engagement dashboard.
     * Parameters:
     * context - application context Context.getApplicationContext()
     * featureName - Mandatory. The name that will be displayed in the insights dashboard, the feature to monitor. Can contain only alphanumeric (a-z, A-Z, 0-9) and â€˜_â€™, â€˜-â€™ characters and must be not longer than 32 characters.
     * instanceId - Optional. Describes specific instance of engagement incase there is need to bind between several engagement instances
     * For example:
     * two engagements which need to be bound should have same value for instanceId. API
     * value - Optional. Describes specific value of feature.
     * For example
     * featureName = "button_pressed", value = "red"
     * featureName = "button_pressed", value = "green"
     * Returns:
     * error code, if success SUCCESS, otherwise ERROR_MANDATORY_PARAM , ERROR_CLIENT_NOT_LOGGED_IN ERROR_CLIENT_INVALID_FEATURE_NAME_PARAM, ERROR_CLIENT_INVALID_VALUE_PARAM, ERROR_CLIENT_ENGAGEMENT_DISABLED
     * @param featureName
     * @param promise
     */
    @ReactMethod
    private void tagEngagementAttempt(String featureName,  @Nullable String instanceId, @Nullable String value, final Promise promise) {
        int response =  NeuraIntegrationSingleton.getInstance().getNeuraEngagements().tagEngagementAttempt(mReactApplicationContext.getApplicationContext(), featureName, instanceId, value);

        if (response == 0) {
            String successMessage = "Neura Tag Success";
            Log.i(getClass().getSimpleName(), successMessage);
            promise.resolve(successMessage);
        } else {
            String errorMessage = "Neura Tag Fail: ";
            Log.i(getClass().getSimpleName(), errorMessage);
            promise.reject(SDKUtils.errorCodeToString(response), errorMessage);
        }

    }


    @ReactMethod
    private void authenticateAnon(final Promise promise) {
        Log.i(getClass().getSimpleName(), "Anon auth starting");

        if (NeuraIntegrationSingleton.getInstance().getNeuraApiClient().isLoggedIn()) {
            Log.i(getClass().getSimpleName(), "Already Logged In");
            NeuraIntegrationSingleton.getInstance().onAuth();
            promise.resolve(LoginSuccessMessage);
        } else {
            Log.i(getClass().getSimpleName(), "Will attempt to log in");

            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "getInstanceId failed", task.getException());
                                return;
                            }

                            // Get new Instance ID token
                            if (task.getResult() != null) {
                                String pushToken = task.getResult().getToken();

                                Log.i(getClass().getSimpleName(), "Neura Push Token:" + pushToken);

                                //Instantiate AnonymousAuthenticationRequest instance.
                                AnonymousAuthenticationRequest request = new AnonymousAuthenticationRequest(pushToken);

                                //Pass the AnonymousAuthenticationRequest instance and register a call back for success and failure events.
                                NeuraIntegrationSingleton.getInstance().getNeuraApiClient().authenticate(request, new AnonymousAuthenticateCallBack() {
                                    @Override
                                    public void onSuccess(AnonymousAuthenticateData authenticateData) {
                                        NeuraIntegrationSingleton.getInstance().registerAuthStateListener();
                                        String debug = LoginSuccessMessage + " Neura Id: "  + authenticateData.getNeuraUserId();
                                        Log.i(getClass().getSimpleName(), debug);
                                        promise.resolve(debug);
                                    }

                                    @Override
                                    public void onFailure(int errorCode) {
                                        String errorMessage = "Failed to authenticate with neura. Reason" +  SDKUtils.errorCodeToString(errorCode);
                                        Log.e(getClass().getSimpleName(), errorMessage);
                                        promise.reject(SDKUtils.errorCodeToString(errorCode), errorMessage);
                                    }
                                });

                                Log.i(getClass().getSimpleName(), "Attempted log in");
                            }
                        }

                    });
        }
    }

    @ReactMethod
    public void isAuthenticated(Promise promise) {
        Boolean isLoggedIn = NeuraIntegrationSingleton.getInstance().getNeuraApiClient().isLoggedIn();
        Log.i(getClass().getSimpleName(), "isAuthenticated: "  + isLoggedIn);
        promise.resolve(isLoggedIn);
    }

    @ReactMethod
    public void getUserAccessToken(Promise promise) {
        if ( NeuraIntegrationSingleton.getInstance().getNeuraApiClient().isLoggedIn()) {
            String token = NeuraIntegrationSingleton.getInstance().getNeuraApiClient().getUserAccessToken();
            Log.i(getClass().getSimpleName(), "getUserAccessToken: "  + token);
            promise.resolve(token);
        } else {
            Log.i(getClass().getSimpleName(), "getUserAccessToken Not logged in");
            promise.reject("Not logged in");
        }
    }
    
    @ReactMethod
    public void subscribeToEvent(String eventName, String eventID, String webhookID, final Promise promise) {
        NeuraIntegrationSingleton.getInstance().getNeuraApiClient().subscribeToEvent(eventName, eventID, webhookID, new SubscriptionRequestCallbacks(){
            @Override
            public void onSuccess(String eventName, Bundle resultData, String identifier) {
                promise.resolve("Successfully subscribed to event");
            }
            
            @Override
            public void onFailure(String eventName, Bundle resultData, int errorCode) {
                String errorMessage = "Failed to subscribe to event. Reason: " +  SDKUtils.errorCodeToString(errorCode);
                Log.e(getClass().getSimpleName(), errorMessage);
                promise.reject(SDKUtils.errorCodeToString(errorCode), errorMessage);
            }
        });
    }

    @ReactMethod
    public void getUserId(final Promise promise) {
        NeuraIntegrationSingleton.getInstance().getNeuraApiClient().getUserDetails(new UserDetailsCallbacks() {
            @Override
            public void onSuccess(UserDetails userDetails) {
                promise.resolve(userDetails.getData().getNeuraId());
            }

            @Override
            public void onFailure(Bundle resultData, int errorCode) {
                promise.reject(new Error());
            }
        });
    }

    @ReactMethod
    public void setExternalId(String externalId) {
        NeuraIntegrationSingleton.getInstance().getNeuraApiClient().setExternalId(externalId);
    }

    @ReactMethod
    public void simulateAnEvent(String eventName, final Promise promise) {
        NeuraIntegrationSingleton.getInstance().getNeuraApiClient().simulateAnEvent(eventName, new SimulateEventCallBack(){
            @Override
            public void onSuccess(String s) {
                String debug = "Fired, check server: ";
                Log.i(getClass().getSimpleName(), debug);
                promise.resolve(debug);
            }

            @Override
            public void onFailure(String s, String s1) {
                String errorMessage = "Failed, boo";
                Log.e(getClass().getSimpleName(), errorMessage);
                promise.reject(errorMessage);
            }
        });
    }

    @ReactMethod
    public void neuraLogout() {
        NeuraIntegrationSingleton.getInstance().getNeuraApiClient().forgetMe(null);
    }
}