package nz.co.kiwiandroiddev.marvelheroes.common.widget

import android.view.View
import android.widget.TextView

fun TextView.setTextOrHide(text: String?) {
    if (text != null) {
        this.visibility = View.VISIBLE
        this.text = text
    } else {
        this.visibility = View.GONE
    }
}