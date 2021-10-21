package com.anizmocreations.bmstask.model

data class ConnectionModel(var type: ConnectionType = ConnectionType.CONNECTION,  var isConnected: Boolean = false)

enum class ConnectionType {
    NO_CONNECTION,
    CONNECTION

}
