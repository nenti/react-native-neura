
# react-native-neura

## Prerequisites

  * An app created in Neura's console
  * A React Native project

## Set-up

Adding the library to the project
  - Run the following command at your project root directory:
    `$ npm i react-native-neura-integration`
  - (Github repository: https://github.com/NeuraLabs/react-native-neura.git)

### Android specific instructions

1. Make sure the `minSdkVersion` in your Project-level `build.gradle` is 19 or above
2. Add the following to the `dependencies` {...} section in your App-level `build.gradle`:

    ```java
    implementation ("com.theneura:android-sdk:+") {
    exclude group: "com.google.android.gms"
    exclude group: "com.google.firebase"
    }
    implementation ("com.google.android.gms:play-services-gcm:16.0.0") 
    implementation ("com.google.android.gms:play-services-location:16.0.0")
    implementation ("com.google.android.gms:play-services-awareness:16.0.0")
    implementation "com.android.support:design:${rootProject.ext.supportLibVersion}"
    ```

3. Initiate the NeuraApiClient with your app_uid & app_secret, by adding the following to the onCreate method in the `MainApplication.java` file:
    ```java
    import com.RNpublic.neuraintegration.NeuraIntegrationSingleton;
    import com.neura.standalonesdk.service.NeuraApiClient;
    ```

    ```java
    NeuraApiClient mNeuraApiClient = NeuraApiClient.getClient(getApplicationContext(), "<uid>", "<secret>");

    NeuraIntegrationSingleton.getInstance().setNeuraApiClient(mNeuraApiClient);
    ```

#### Firebase Set-up
  - If you don't have Firebase installed yet, add it to your project following these instructions: https://firebase.google.com/docs/android/setup#create-firebase-project
    and add the following inside the <application> tag in your Android manifest file:
    ```xml
    <service
      android:name="com.RNpublic.neuraintegration.NeuraIntegrationEventsService"
      android:exported="false">
     <intent-filter>
     <action android:name="com.google.firebase.MESSAGING_EVENT" />
     </intent-filter>
    </service>
    ```

  - If you are already using Firebase in your project:
    Add the following to the top of your onMessageReceived method in the Firebase Listener class:
 
     ```java
     boolean isNeuraPush = NeuraPushCommandFactory.getInstance().isNeuraPush(getApplicationContext(), message.getData(), new NeuraEventCallBack() {
         @Override
         public void neuraEventDetected(NeuraEvent event) {
         // If you subscribed to neura moments this method will be invoked
         }
     }); 

     if (isNeuraPush)
     {
        return;
     }
     ```

  - If you are handling Firebase using a 3rd party library, add this to the gradle file of the library in order to be able to access Neura objects:

    ```java 
    compileOnly "com.theneura:android-sdk:+" 
    ```
### iOS specific instructions
1. Run: `cd ios & pod install` at your project root directory:
2. Follow steps 2-4 of "Configure your project for use with Neura" & step 1 of "Finalize Integration" from the iOS tutorial: https://dev.theneura.com/tutorials/ios 


## Usage
```javascript
import RNNeuraIntegration from 'react-native-neura';

...
RNNeuraIntegration.authenticateAnon();
```



 

 


