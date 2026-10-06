package com.yarik.watcher.logic.source.local.pref

import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class LocalValuesProviderImpl @Inject constructor(
    private val sharedPref: SharedPreferences,
) : LocalValuesProvider {

    private val dataChangedFlow = MutableSharedFlow<Unit>(replay = 1)

    init {
        dataChangedFlow.tryEmit(Unit)
    }

    override fun onLocalDataChanged() = dataChangedFlow

    override var userToken: String? by sharedPref.pref(defaultValue = "") {
        dataChangedFlow.tryEmit(Unit)
    }

    override var latitude: Float by sharedPref.pref(defaultValue = 0f) {
        dataChangedFlow.tryEmit(Unit)
    }

    override var longitude: Float by sharedPref.pref(defaultValue = 0f) {
        dataChangedFlow.tryEmit(Unit)
    }
}

@Suppress("UNCHECKED_CAST")
inline fun <reified T> SharedPreferences.pref(
    key: String? = null,
    defaultValue: T,
    noinline onDataChanged: (String) -> Unit,
): ReadWriteProperty<Any?, T> {
    return when (T::class) {
        String::class -> SharedPrefStringDelegate(this, key, defaultValue as String, onDataChanged)
        Boolean::class -> SharedPrefBooleanDelegate(this, key, defaultValue as Boolean, onDataChanged)
        Int::class -> SharedPrefIntDelegate(this, key, defaultValue as Int, onDataChanged)
        Float::class -> SharedPrefFloatDelegate(this, key, defaultValue as Float, onDataChanged)
        else -> IllegalArgumentException("Unsupportes ype")
    } as ReadWriteProperty<Any?, T>
}

@PublishedApi
internal class SharedPrefStringDelegate(
    private val sharedPref: SharedPreferences,
    private val key: String? = null,
    private val defaultValue: String? = null,
    private val onDataChanged: (String) -> Unit,
) : ReadWriteProperty<Any?, String?> {

    override fun getValue(
        thisRef: Any?,
        property: KProperty<*>,
    ): String? {
        return sharedPref.getString(key ?: property.name, defaultValue)
    }

    override fun setValue(
        thisRef: Any?,
        property: KProperty<*>,
        value: String?,
    ) {
        val key = key ?: property.name
        sharedPref.edit {
            putString(key, value)
        }
        onDataChanged(key)
    }

}

@PublishedApi
internal class SharedPrefBooleanDelegate(
    private val sharedPref: SharedPreferences,
    private val key: String? = null,
    private val defaultValue: Boolean = false,
    private val onDataChanged: (String) -> Unit,
) : ReadWriteProperty<Any?, Boolean> {

    override fun getValue(
        thisRef: Any?,
        property: KProperty<*>,
    ): Boolean {
        return sharedPref.getBoolean(key ?: property.name, defaultValue)
    }

    override fun setValue(
        thisRef: Any?,
        property: KProperty<*>,
        value: Boolean,
    ) {
        val key = key ?: property.name
        sharedPref.edit {
            putBoolean(key, value)
        }
        onDataChanged(key)
    }
}

@PublishedApi
internal class SharedPrefIntDelegate(
    private val sharedPref: SharedPreferences,
    private val key: String? = null,
    private val defaultValue: Int,
    private val onDataChanged: (String) -> Unit,
) : ReadWriteProperty<Any?, Int> {

    override fun getValue(
        thisRef: Any?,
        property: KProperty<*>,
    ): Int {
        return sharedPref.getInt(key ?: property.name, defaultValue)
    }

    override fun setValue(
        thisRef: Any?,
        property: KProperty<*>,
        value: Int,
    ) {
        val key = key ?: property.name
        sharedPref.edit {
            putInt(key, value)
        }
        onDataChanged(key)
    }
}

@PublishedApi
internal class SharedPrefFloatDelegate(
    private val sharedPref: SharedPreferences,
    private val key: String? = null,
    private val defaultValue: Float,
    private val onDataChanged: (String) -> Unit,
) : ReadWriteProperty<Any?, Float> {

    override fun getValue(
        thisRef: Any?,
        property: KProperty<*>,
    ): Float {
        return sharedPref.getFloat(key ?: property.name, defaultValue)
    }

    override fun setValue(
        thisRef: Any?,
        property: KProperty<*>,
        value: Float,
    ) {
        val key = key ?: property.name
        sharedPref.edit {
            putFloat(key, value)
        }
        onDataChanged(key)
    }
}