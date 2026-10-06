package com.yarik.watcher.navigation

import android.os.Parcelable

/**
 * Контракт экрана приложения.
 *
 * [route] — уникальный идентификатор destination в графе Compose Navigation.
 * Типизированные аргументы (когда появятся) передаются через SavedStateHandle
 * записи back stack; [args] описывает их тип на уровне контракта.
 */
interface JoyScreen<A : Parcelable> {

    val args: A

    val route: String
}
