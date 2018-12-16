package cc.femto.rxbrainwaves

import android.bluetooth.BluetoothAdapter
import com.neurosky.connection.DataType.MindDataType
import com.neurosky.connection.EEGPower
import com.neurosky.connection.TgStreamHandler
import com.neurosky.connection.TgStreamReader
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class RxBrainwaves {

    private val connectionStatesSubject = BehaviorSubject.create<ConnectionState>()
    private val dataSubject = PublishSubject.create<Data>()

    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        try {
            BluetoothAdapter.getDefaultAdapter()
        } catch (t: Throwable) {
            null
        }
    }

    private var streamReader: TgStreamReader? = null

    private val streamHandler = object : TgStreamHandler {
        override fun onStatesChanged(state: Int) {
            connectionStatesSubject.onNext(ConnectionState.fromRawValue(state))
        }

        override fun onDataReceived(type: Int, value: Int, obj: Any?) {
            val data = when (type) {
                MindDataType.CODE_POOR_SIGNAL -> Data.PoorSignal(value)
                MindDataType.CODE_RAW -> Data.Raw(value)
                MindDataType.CODE_ATTENTION -> Data.Attention(value)
                MindDataType.CODE_MEDITATION -> Data.Meditation(value)
                MindDataType.CODE_EEGPOWER -> Data.EegPower(obj as EEGPower)
                MindDataType.CODE_CONFIGURATION -> Data.Configuration(value)
                MindDataType.CODE_FILTER_TYPE -> Data.FilterType(
                    when (value) {
                        MindDataType.FilterType.FILTER_50HZ.value -> MindDataType.FilterType.FILTER_50HZ
                        MindDataType.FilterType.FILTER_60HZ.value -> MindDataType.FilterType.FILTER_60HZ
                        else -> throw IllegalArgumentException("Invalid filter type: $value")
                    }
                )
                else -> null
            }
            data?.let { dataSubject.onNext(it) }
        }

        override fun onChecksumFail(payload: ByteArray?, length: Int, checksum: Int) {}

        override fun onRecordFail(p0: Int) {}
    }

    fun connectionStates(): Observable<ConnectionState> = connectionStatesSubject
        .distinctUntilChanged()
        .startWith(ConnectionState.INIT)

    fun data(): Observable<Data> = dataSubject

    /**
     * @param bluetoothAddress of the device to be connected
     * @throws IllegalStateException if bluetooth is not enabled
     */
    fun connect(bluetoothAddress: String) {
        val bluetoothDevice = bluetoothAdapter?.getRemoteDevice(bluetoothAddress)
            ?: throw IllegalStateException("Bluetooth not enabled")

        when (streamReader) {
            null -> streamReader = TgStreamReader(bluetoothDevice, streamHandler)
            else -> streamReader?.changeBluetoothDevice(bluetoothDevice)
        }
        streamReader?.connectAndStart()
    }

    fun disconnect() {
        streamReader?.apply {
            stop()
            close()
        }
        streamReader = null
    }
}