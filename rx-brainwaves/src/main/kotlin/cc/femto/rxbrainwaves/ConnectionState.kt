package cc.femto.rxbrainwaves

import com.neurosky.connection.ConnectionStates

enum class ConnectionState {
    INIT,
    CONNECTING,
    CONNECTED,
    WORKING,
    STOPPED,
    DISCONNECTED,
    COMPLETE,
    RECORDING_START,
    RECORDING_END,
    GET_DATA_TIME_OUT,
    FAILED,
    ERROR;

    companion object {
        fun fromRawValue(rawValue: Int) = when (rawValue) {
            ConnectionStates.STATE_INIT -> INIT
            ConnectionStates.STATE_CONNECTING -> CONNECTING
            ConnectionStates.STATE_CONNECTED -> CONNECTED
            ConnectionStates.STATE_WORKING -> WORKING
            ConnectionStates.STATE_STOPPED -> STOPPED
            ConnectionStates.STATE_DISCONNECTED -> DISCONNECTED
            ConnectionStates.STATE_COMPLETE -> COMPLETE
            ConnectionStates.STATE_RECORDING_START -> RECORDING_START
            ConnectionStates.STATE_RECORDING_END -> RECORDING_END
            ConnectionStates.STATE_GET_DATA_TIME_OUT -> GET_DATA_TIME_OUT
            ConnectionStates.STATE_FAILED -> FAILED
            ConnectionStates.STATE_ERROR -> ERROR
            else -> throw IllegalArgumentException("Unknown raw value for ConnectionState: $rawValue")
        }
    }
}