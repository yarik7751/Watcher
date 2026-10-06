package com.yarik.watcher.logic.source.local.pref

import kotlinx.coroutines.flow.Flow

interface LocalValuesProvider {

    fun onLocalDataChanged(): Flow<Unit>

    var userToken: String?

    var latitude: Float

    var longitude: Float
}