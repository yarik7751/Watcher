package com.yarik.watcher.navigation.args

import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import com.github.terrakok.cicerone.androidx.FragmentScreen

inline fun <reified F: Fragment, Args: Parcelable> getFragmentInstanceWithArgs(args: Args? = null): FragmentScreen {
    val fragment = F::class.java.getDeclaredConstructor().newInstance().apply {
        if (args != null) {
            val bundle = Bundle()
            bundle.putParcelable("screenArgs", args)
            this.arguments = bundle
        }
    }
    return FragmentScreen { fragment }
}

@Suppress("UNCHECKED_CAST")
inline fun <reified F: Fragment, Args: Parcelable> F.getArgs(): Args {
    val args = requireNotNull(this.requireArguments().getParcelable("screenArgs"))
    return args as Args
}