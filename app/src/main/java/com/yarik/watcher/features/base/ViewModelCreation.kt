package com.yarik.watcher.features.base

import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelLazy
import androidx.lifecycle.ViewModelStoreOwner
import kotlin.reflect.KClass

inline fun <reified VM : ViewModel, F> F.viewModels(): ViewModelLazy<VM> where F : ViewModelStoreOwner, F : BaseComposeFragment, F : HasDefaultViewModelProviderFactory {
    return lazyViewModel(VM::class)
}

fun <VM : ViewModel, F> F.lazyViewModel(
    kClass: KClass<VM>,
): ViewModelLazy<VM> where F : ViewModelStoreOwner, F : BaseComposeFragment, F : HasDefaultViewModelProviderFactory {
    return ViewModelLazy(
        viewModelClass = kClass,
        storeProducer = { viewModelStore },
        factoryProducer = { this.viewModelFactory },
        extrasProducer = { defaultViewModelCreationExtras },
    )
}