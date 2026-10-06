package com.yarik.watcher.di.module.worker

import androidx.work.ListenableWorker
import dagger.MapKey
import kotlin.reflect.KClass

@MapKey
@Target(AnnotationTarget.FUNCTION)
annotation class WorkerKey(val value: KClass<out ListenableWorker>)