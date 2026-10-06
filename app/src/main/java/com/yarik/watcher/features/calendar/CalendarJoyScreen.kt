package com.yarik.watcher.features.calendar

import com.github.terrakok.cicerone.Screen
import com.yarik.watcher.navigation.JoyScreen
import com.yarik.watcher.navigation.args.NoArgs
import com.yarik.watcher.navigation.args.getFragmentInstanceWithArgs

object CalendarJoyScreen : JoyScreen<NoArgs> {
    override val args: NoArgs
        get() = NoArgs

    override val screen: Screen
        get() = getFragmentInstanceWithArgs<CalendarFragment, NoArgs>()
}
