package cc.femto.rxbrainwaves

import com.neurosky.connection.DataType.MindDataType
import com.neurosky.connection.EEGPower

sealed class Data {
    data class PoorSignal(val signal: Int) : Data()
    data class Raw(val value: Int) : Data()
    data class Attention(val value: Int) : Data()
    data class Meditation(val value: Int) : Data()
    data class EegPower(val value: EEGPower) : Data()
    data class Configuration(val value: Int) : Data()
    data class FilterType(val value: MindDataType.FilterType) : Data()
}