package com.task.data.local

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.GsonBuilder
import com.task.*
import com.task.data.Resource
import com.task.data.dto.login.LoginRequest
import com.task.data.dto.login.LoginResponse
import com.task.data.error.PASS_WORD_ERROR
import javax.inject.Inject


class LocalData @Inject constructor(val context: Context) {

    fun doLogin(loginRequest: LoginRequest): Resource<LoginResponse> {
        if (loginRequest == LoginRequest("ahmed@ahmed.ahmed", "ahmed")) {
//            return Resource.Success(
//                LoginResponse(
//                    "123", "Ahmed", "Mahmoud",
//                    "FrunkfurterAlle", "77", "12000", "Berlin",
//                    "Germany", "ahmed@ahmed.ahmed"
//                )
//            )
        }
        return Resource.DataError(PASS_WORD_ERROR)
    }

    fun getCachedFavourites(): Resource<Set<String>> {
        val sharedPref = context.getSharedPreferences(PREF_PREFERENCES_FILE_NAME, 0)
        return Resource.Success(sharedPref.getStringSet(PREF_FAVOURITES_KEY, setOf()) ?: setOf())
    }

    fun isFavourite(id: String): Resource<Boolean> {
        val sharedPref = context.getSharedPreferences(PREF_PREFERENCES_FILE_NAME, 0)
        val cache = sharedPref.getStringSet(PREF_FAVOURITES_KEY, setOf<String>()) ?: setOf()
        return Resource.Success(cache.contains(id))
    }

    fun cacheFavourites(ids: Set<String>): Resource<Boolean> {
        val sharedPref = context.getSharedPreferences(PREF_PREFERENCES_FILE_NAME, 0)
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putStringSet(PREF_FAVOURITES_KEY, ids)
        editor.apply()
        val isSuccess = editor.commit()
        return Resource.Success(isSuccess)
    }

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

    fun removeFromFavourites(id: String): Resource<Boolean> {
        val sharedPref = context.getSharedPreferences(PREF_PREFERENCES_FILE_NAME, 0)
        var set =
            sharedPref.getStringSet(PREF_FAVOURITES_KEY, mutableSetOf<String>())?.toMutableSet()
                ?: mutableSetOf()
        if (set.contains(id)) {
            set.remove(id)
        }
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.clear()
        editor.apply()
        editor.commit()
        editor.putStringSet(PREF_FAVOURITES_KEY, set)
        editor.apply()
        val isSuccess = editor.commit()
        return Resource.Success(isSuccess)
    }
}

