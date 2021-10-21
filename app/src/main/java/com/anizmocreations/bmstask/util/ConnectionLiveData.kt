package com.anizmocreations.bmstask.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.telephony.TelephonyManager
import androidx.lifecycle.LiveData
import com.anizmocreations.bmstask.model.ConnectionModel
import com.anizmocreations.bmstask.model.ConnectionType

class ConnectionLiveData(var context: Context) : LiveData<ConnectionModel>() {

    private var cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    override fun onActive() {
        super.onActive()
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        context.registerReceiver(networkReceiver, filter)
    }

    override fun onInactive() {
        super.onInactive()
        context.unregisterReceiver(networkReceiver)
    }

    private val networkReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.extras != null) {
                val activeNetwork = cm.activeNetworkInfo
                val isConnected = activeNetwork != null && activeNetwork.isConnected
                if (isConnected) {
                    postValue(ConnectionModel(ConnectionType.CONNECTION, isConnected = true))
                } else {
                    postValue(ConnectionModel(ConnectionType.NO_CONNECTION, isConnected = false))
                }
            }
        }
    }
}