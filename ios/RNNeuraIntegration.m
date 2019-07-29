#import <Foundation/Foundation.h>
#import "RNNeuraIntegration.h"
#import <React/RCTLog.h>
#import <NeuraSDK/NeuraSDK.h>

@implementation RNNeuraIntegration

- (dispatch_queue_t)methodQueue
{
return dispatch_get_main_queue();
}

RCT_EXPORT_MODULE(RNNeuraIntegration);

RCT_REMAP_METHOD(authenticateAnon, authenticateResolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    dispatch_async(dispatch_get_main_queue(), ^{
        NeuraAnonymousAuthenticationRequest *request = [NeuraAnonymousAuthenticationRequest new];

        [NeuraSDK.shared authenticateWithRequest:request callback:^(NeuraAuthenticationResult *result) {
            if (result.success) {
                resolve(result.info.accessToken);
            } else {
                NeuraAPIError *err = result.error;
                reject([NSString stringWithFormat: @"%lu", (long)err.code], err.localizedDescription, err);
            }
        }];
    });
}

RCT_REMAP_METHOD(isAuthenticated, isAuthenticatedResolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    resolve(@([NeuraSDK.shared isAuthenticated]));
}

RCT_REMAP_METHOD(getUserAccessToken, getUserAccessTokenResolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    resolve([NeuraSDK.shared appToken]);
}

RCT_REMAP_METHOD(getUserId, getUserIdResolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    resolve([NeuraSDK.shared neuraUserId]);
}


RCT_EXPORT_METHOD(tagEngagementFeature:
                  (NSString *)featureName
                  instanceId:( nullable NSString *)instanceId
                  actionString:( nullable NSString *)actionString
                  value:( nullable NSString *)value )
{
    NSError *err = nil;
    [NeuraSDK.shared tagEngagementFeature:featureName action: NEngagementFeatureActionSuccess value:value instanceId:instanceId error:&err];

}

RCT_EXPORT_METHOD(tagEngagementAttempt:
                  (NSString *)featureName
                  instanceId:( nullable NSString *)instanceId
                  value:( nullable NSString *)value )
{
    NSError *err = nil;
    [NeuraSDK.shared tagEngagementAttempt:featureName value:value instanceId:instanceId error:&err];
}

RCT_EXPORT_METHOD(simulateAnEvent:
                  (NSString *)eventName)
{
    NEventName enumEventName = [NEvent enumForEventName:eventName];
    [NeuraSDK.shared simulateEvent:(enumEventName) callback:^(NeuraAPIResult * result) {
    if (!result.success) {
        RCTLogInfo(@"Not able to simulate event: %@, Error: %@", eventName, result.errorString);
    } else {
        RCTLogInfo(@"Simulated event: %@", eventName);
    }}];
}

RCT_EXPORT_METHOD(subscribeToEvent:
                  (NSString *)eventName 
                  eventID: (NSString *) eventID 
                  webhookID: (NSString *) webhookID)
{
    NSubscription *subscription = [[NSubscription alloc] initWithEventName:eventName identifier:eventID webhookId:webhookID method:NSubscriptionMethodWebhook];
    [NeuraSDK.shared addSubscription:(subscription) callback:^(NeuraAPIResult * _Nonnull result) {
        if (!result.success) {
            RCTLogInfo(@"Error while trying to create subscription: %@, Error: %@", eventID, result.error);
        } else {
            RCTLogInfo(@"Created subscription: %@", eventName);
        }
    }];
}

RCT_EXPORT_METHOD(neuraLogout)
{
    if (!NeuraSDK.shared.isAuthenticated) return;
    [NeuraSDK.shared logoutWithCallback:^(NeuraLogoutResult * _Nonnull result) {
        // Perform tasks after the callback has returned
    }];
}
@end
