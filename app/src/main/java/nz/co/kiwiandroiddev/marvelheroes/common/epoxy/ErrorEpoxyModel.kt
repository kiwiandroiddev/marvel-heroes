package nz.co.kiwiandroiddev.marvelheroes.common.epoxy

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.view.isVisible
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import nz.co.kiwiandroiddev.marvelheroes.R
import nz.co.kiwiandroiddev.marvelheroes.common.widget.setTextOrHide

@Suppress("LeakingThis")
@EpoxyModelClass(layout = R.layout.item_error)
abstract class ErrorEpoxyModel : EpoxyModelWithHolder<ErrorEpoxyModel.Holder>() {

    @EpoxyAttribute
    lateinit var errorTitle: String

    @EpoxyAttribute
    var errorDescription: String? = null

    @EpoxyAttribute
    var actionTitle: String? = null

    @EpoxyAttribute
    var onActionClickListener: (() -> Unit)? = null

    init {
        id("error")
    }

    override fun bind(holder: Holder) {
        holder.errorTitle.text = errorTitle

        holder.errorActionButton.text =
            actionTitle ?: holder.errorActionButton.context.getString(R.string.retry)

        holder.errorDescription.setTextOrHide(errorDescription)

        this.onActionClickListener?.let { clickListener ->
            holder.errorActionButton.isVisible = true
            holder.errorActionButton.setOnClickListener { clickListener() }
        } ?: {
            holder.errorActionButton.isVisible = false
            holder.errorActionButton.setOnClickListener(null)
        }()
    }

    class Holder : EpoxyHolder() {
        internal lateinit var errorTitle: TextView
        internal lateinit var errorDescription: TextView
        internal lateinit var errorActionButton: Button

        override fun bindView(itemView: View) {
            errorTitle = itemView.findViewById(R.id.error_title)
            errorDescription = itemView.findViewById(R.id.error_description)
            errorActionButton = itemView.findViewById(R.id.retry_button)
        }
    }
}