# RxBrainwaves

[![](https://jitpack.io/v/cc.femto/rx-brainwaves.svg)](https://jitpack.io/#cc.femto/rx-brainwaves)

RxJava wrapper for the [NeuroSky MindWave](http://developer.neurosky.com/) headset Android SDK.

## Connection
```kotlin
val brainwaves = RxBrainwaves()
```
```kotlin
// Observe connection state
brainwaves.connectionStates()
    .subscribe { state ->
        if (state == ConnectionState.WORKING) {
            Log.i("working")
        } else {
            Log.i("not yet working")
        }
    }

// Connect
brainwaves.connect(bluetoothAddress)

// Disconnect
brainwaves.disconnect()
```


## Examples

### Signal quality
```kotlin
brainwaves.data().ofType<Data.PoorSignal>()
    .distinctUntilChanged()
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe { (signal) ->
        when (signal) {
            0 -> Log.i("good signal")
            200 -> Log.i("not worn")
            else -> Log.i("bad signal")
        }
    }
```
    
### Signal quality over time
```kotlin
brainwaves.data().ofType<Data.PoorSignal>()
    .buffer(3, TimeUnit.SECONDS)
    .map { samples -> samples.isNotEmpty() && samples.all { it.signal == 0 } }
    .distinctUntilChanged()
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe { hasGoodSignal ->
        if (hasGoodSignal) {
            Log.i("good signal for three seconds")
        } else {
            Log.i("interrupted signal")
        }
    }
```

### Attention within a timespan
```kotlin
brainwaves.data().ofType<Data.Attention>()
    .map { it.value } // unwrap value
    .filter { it > 0 } // wait for good signal
    .delay(2, TimeUnit.SECONDS) // wait before sampling
    .buffer(3, TimeUnit.SECONDS) // sample for three seconds
    .take(1) // unsubscribe after taking the sample
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe { samples ->
        val isElevated = samples.count { it in 80..100 } >= 2
        val isNeutral = samples.count { it in 50..80 } >= 2
        when {
            isNeutral -> Log.i("neutral attention")
            isElevated -> Log.i("elevated attention")
            else -> Log.i("lowered attention")
        }
    }
```

### Meditation
```kotlin
brainwaves.data().ofType<Data.Meditation>()
    .subscribe { (meditation) ->
        Log.i("meditation level: $meditation")
    }
```

See the full list of [ReactiveX operators](http://reactivex.io/documentation/operators.html) for many more ways to process and manipulate the data stream from the headset.


## Binaries
```gradle
dependencies {
    implementation "cc.femto:rx-brainwaves:0.1"
}
```

Requires the JitPack repository:
```gradle
repositories {
    maven { url "https://jitpack.io" }
}
```
