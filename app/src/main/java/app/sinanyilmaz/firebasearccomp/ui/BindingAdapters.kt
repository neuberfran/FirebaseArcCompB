package app.sinanyilmaz.firebasearccomp.ui

import android.databinding.BindingAdapter
import android.view.View

object BindingAdapters {
    @BindingAdapter("visibleGone")

    fun showHide(view: View, show: Boolean) {
        view.visibility = if (show) View.VISIBLE else View.GONE
    }
}
