package com.yarik.watcher.feature.rendernode.impl

import com.yarik.watcher.core.navigation.ScreenContentProvider
import com.yarik.watcher.core.ui.ScreenKey
import com.yarik.watcher.feature.rendernode.api.RenderNodeJoyScreen
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module
class RenderNodeModule {

    @Provides
    @IntoMap
    @ScreenKey(RenderNodeJoyScreen.ROUTE)
    fun provideRenderNodeContent(): ScreenContentProvider = RenderNodeScreenProvider()
}
