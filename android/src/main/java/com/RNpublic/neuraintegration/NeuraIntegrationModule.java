package com.RNpublic.neuraintegration;

import com.neura.resources.authentication.AuthenticateCallback;
import com.neura.sdk.object.AuthenticationRequest;
import com.neura.sdk.object.Permission;
import com.neura.sdk.service.SimulateEventCallBack;
import com.neura.standalonesdk.engagement.EngagementFeatureAction;
import com.neura.standalonesdk.util.SDKUtils;
import com.neura.standalonesdk.events.NeuraEventCallBack;
import com.neura.standalonesdk.events.NeuraEvent;
import com.neura.standalonesdk.events.NeuraPushCommandFactory;
import com.neura.resources.authentication.AnonymousAuthenticateCallBack;
import com.neura.resources.authentication.AnonymousAuthenticateData;
import com.neura.sdk.object.AnonymousAuthenticationRequest;
import com.neura.resources.authentication.AuthenticateData;
import com.google.firebase.iid.FirebaseInstanceId;
import com.neura.resources.user.UserDetailsCallbacks;
import com.neura.resources.user.UserDetails;
import java.util.Map;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;
import android.os.Bundle;
import static com.neura.standalonesdk.engagement.EngagementFeatureAction.CLOSE;
import static com.neura.standalonesdk.engagement.EngagementFeatureAction.OPT_OUT;
import static com.neura.standalonesdk.engagement.EngagementFeatureAction.REJECT;
import static com.neura.standalonesdk.engagement.EngagementFeatureAction.SNOOZE;
import static com.neura.standalonesdk.engagement.EngagementFeatureAction.SUCCESS;

import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReadableMap;
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
     * @param phone
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
    private void authenticateWithPhone(String phone, final Promise promise) {
        AuthenticationRequest request = new AuthenticationRequest(Permission.list(new String[]{"presenceAtHome",  "sleepingHabits"}));
        request.setPhone(phone);
        NeuraIntegrationSingleton.getInstance().getNeuraApiClient().authenticate(request, new AuthenticateCallback() {
            @Override
            public void onSuccess(AuthenticateData authenticateData) {
                String successMessage = "Successfully authenticated with neura. Token: " +  authenticateData.getAccessToken()+  " User Id: " +  authenticateData.getNeuraUserId();
                Log.i(getClass().getSimpleName(), successMessage);
                promise.resolve( authenticateData.getAccessToken());
            }

            @Override
            public void onFailure(int errorCode) {
                String errorMessage = "Failed to authenticate with neura. Reason" +  SDKUtils.errorCodeToString(errorCode);
                Log.e(getClass().getSimpleName(), errorMessage);
                promise.reject(SDKUtils.errorCodeToString(errorCode), errorMessage);
            }
        });
    }



    @ReactMethod
    private void authenticateAnon(final Promise promise) {
        String startMessage = "Anon auth starting";
        Log.i(getClass().getSimpleName(), startMessage);

        if ( NeuraIntegrationSingleton.getInstance().getNeuraApiClient().isLoggedIn()) {
            Log.i(getClass().getSimpleName(), "Already Logged In");
            NeuraIntegrationSingleton.getInstance().onAuth();
            promise.resolve(LoginSuccessMessage);
        } else {
            Log.i(getClass().getSimpleName(), "Will attempt to log in");
            //Get the FireBase Instance ID, we will use it to instantiate AnonymousAuthenticationRequest
            String pushToken = FirebaseInstanceId.getInstance().getToken();

            Log.i(getClass().getSimpleName(), "PUSH TOKEN:" + pushToken);

            //Instantiate AnonymousAuthenticationRequest instance.
            AnonymousAuthenticationRequest request = new AnonymousAuthenticationRequest(pushToken);

            //Pass the AnonymousAuthenticationRequest instance and register a call back for success and failure events.
            NeuraIntegrationSingleton.getInstance().getNeuraApiClient().authenticate(request, new AnonymousAuthenticateCallBack() {
                @Override
                public void onSuccess(AnonymousAuthenticateData authenticateData) {
                    NeuraIntegrationSingleton.getInstance().registerAuthStateListener();
                    String debug = LoginSuccessMessage + " Neura Id: "  + authenticateData.getNeuraUserId() + " Is logged in is: " +  NeuraIntegrationSingleton.getInstance().getNeuraApiClient().isLoggedIn();
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
    public void notificationHandler(ReadableMap details, final Promise promise) {
        Map detailsMap = NeuraIntegrationMapUtil.recursivelyDeconstructReadableMap(details);
        Context mContext = getReactApplicationContext().getCurrentActivity().getBaseContext();

        boolean isNeuraPush = NeuraPushCommandFactory.getInstance().isNeuraPush(mContext, detailsMap, new NeuraEventCallBack() {
            @Override
            public void neuraEventDetected(NeuraEvent event) {
                //Log.d("Neura event:", event);
                promise.resolve(event);
            }
        });


        if(!isNeuraPush) {
            promise.reject(new Error("Neura event not found"));
        }
    }

    @ReactMethod
    public void simulateAnEvent(final Promise promise) {
        NeuraIntegrationSingleton.getInstance().getNeuraApiClient().simulateAnEvent("userArrivedHomeByWalking", new SimulateEventCallBack(){
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