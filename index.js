import { NativeModules } from 'react-native';

const { RNNeuraIntegration } = NativeModules;

module.exports = {
    authenticateAnon: function() {
        return RNNeuraIntegration.authenticateAnon();
    },

    getUserAccessToken: function() {
        return RNNeuraIntegration.getUserAccessToken();
    },

    getUserId: function() {
        return RNNeuraIntegration.getUserId();
    },

    setExternalId: function(externalId) {
        return RNNeuraIntegration.setExternalId(externalId);
    },

    isAuthenticated: function() {
        return RNNeuraIntegration.isAuthenticated();
    },

    neuraLogout: function() {
        return RNNeuraIntegration.neuraLogout();
    },

    simulateAnEvent: function(eventName) {
        return RNNeuraIntegration.simulateAnEvent(eventName);
    },

    subscribeToEvent: function(eventName, eventID, webhookID) {
        return RNNeuraIntegration.subscribeToEvent(eventName, eventID, webhookID);
    },

    /**
     *
     * @param featureName string mandatory
     *      The tag. A string that defines the feature.
     * @param data = {} object mandatory
     *      @param data.instanceId string optional
     *      @param data.value string optional
     *      @param data.action string optional
     *          One of 'CLOSE', 'OPT_OUT', 'REJECT', 'SNOOZE', 'SUCCESS'
     * @param type
     */
    tagEngagement: function(featureName, data, type = 'feature') {
        const { instanceId = null, action = 'SUCCESS', value = null } = data;
        const allowed_actions = { 'CLOSE': 1, 'OPT_OUT': 1, 'REJECT': 1, 'SNOOZE': 1, 'SUCCESS': 1 };

        if (action && !allowed_actions[action]) {
            throw `Neura tag event: action provided is invalid. 
            Please check data.action allowed values are: 'CLOSE', 'OPT_OUT', 'REJECT', 'SNOOZE', 'SUCCESS'`;
        }

        if (!featureName) {
            throw `Neura tag event: featureName was not provided. Please make sure to provide a feature name`;
        }

        if (type === 'feature') {
            return RNNeuraIntegration.tagEngagementFeature(featureName, instanceId, action, value);
        }

        return RNNeuraIntegration.tagEngagementAttempt(featureName, instanceId, value);
    }
};
