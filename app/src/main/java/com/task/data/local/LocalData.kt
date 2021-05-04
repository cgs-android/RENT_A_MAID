package com.task.data.local

import android.content.Context
import com.google.gson.GsonBuilder
import com.task.*
import com.task.data.dto.credential.login.LoginResponse
import javax.inject.Inject


class LocalData @Inject constructor(val context: Context) {

    fun putLoginResponseData(loginResponse: LoginResponse) {
        val sharedPref = context.getSharedPreferences(PREF_PREFERENCES_FILE_NAME, 0)
        //Convert object to JSON String.
        val jsonString = GsonBuilder().create().toJson(loginResponse)
        //Save that String in SharedPreferences
        sharedPref.edit().putString(PREF_LOGIN_DATA_KEY, jsonString).apply()
    }

    fun getLoginResponseData(): LoginResponse {
        val sharedPref = context.getSharedPreferences(PREF_PREFERENCES_FILE_NAME, 0)
        //We read JSON String which was saved.
        val value = sharedPref.getString(PREF_LOGIN_DATA_KEY, null)
        return GsonBuilder().create().fromJson(value, LoginResponse::class.java)
    }

    fun putProjectId(projectId: String) {
        val sharedPref = context.getSharedPreferences(PREF_PREFERENCES_FILE_NAME, 0)
        sharedPref.edit().putString(PREF_PROJECT_ID_KEY, projectId).apply()
    }

    fun getProjectId(): String {
        val sharedPref = context.getSharedPreferences(PREF_PREFERENCES_FILE_NAME, 0)
        val value: String? = sharedPref.getString(PREF_PROJECT_ID_KEY, "")
        return value!!
    }


    fun putProjectIdForBackgroundService(projectId: String) {
        val sharedPref = context.getSharedPreferences(PREF_PREFERENCES_FILE_NAME, 0)
        sharedPref.edit().putString(PREF_PROJECT_ID_BACKGROUND_SERVICE_KEY, projectId).apply()
    }

    fun getProjectIdForBackgroundService(): String {
        val sharedPref = context.getSharedPreferences(PREF_PREFERENCES_FILE_NAME, 0)
        val value: String? = sharedPref.getString(PREF_PROJECT_ID_BACKGROUND_SERVICE_KEY, "")
        return value!!
    }


    fun putTravelStartId(travelStartId: Int) {
        val sharedPref = context.getSharedPreferences(PREF_PREFERENCES_FILE_NAME, 0)
        sharedPref.edit().putInt(PREF_TRAVEL_START_ID_KEY, travelStartId).apply()
    }

    fun getTravelStartId(): Int {
        val sharedPref = context.getSharedPreferences(PREF_PREFERENCES_FILE_NAME, 0)
        val value: Int? = sharedPref.getInt(PREF_TRAVEL_START_ID_KEY, 0)
        return value!!
    }

    fun putTravelDistanceInKilometer(travelDistance: String) {
        val sharedPref = context.getSharedPreferences(PREF_PREFERENCES_FILE_NAME, 0)
        sharedPref.edit().putString(PREF_TRAVEL_DISTANCE_KEY, travelDistance).apply()
    }

    fun getTravelDistanceInKilometer(): String {
        val sharedPref = context.getSharedPreferences(PREF_PREFERENCES_FILE_NAME, 0)
        val value: String? = sharedPref.getString(PREF_TRAVEL_DISTANCE_KEY, "")
        return value!!
    }

    fun putUserRoleStatus(userRole: Boolean) {
        val sharedPref = context.getSharedPreferences(PREF_PREFERENCES_FILE_NAME, 0)
        sharedPref.edit().putBoolean(PREF_USER_ROLE, userRole).apply()
    }

    fun getUserRoleStatus(): Boolean {
        val sharedPref = context.getSharedPreferences(PREF_PREFERENCES_FILE_NAME, 0)
        val value: Boolean? = sharedPref.getBoolean(PREF_USER_ROLE, false)
        return value!!
    }


}

