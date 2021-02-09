package nz.co.kiwiandroiddev.marvelheroes.features.characterlist.view

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.bumptech.glide.Glide
import nz.co.kiwiandroiddev.marvelheroes.R

@EpoxyModelClass(layout = R.layout.item_character_summary)
abstract class CharacterSummaryEpoxyModel : EpoxyModelWithHolder<CharacterSummaryEpoxyModel.Holder>() {

    @EpoxyAttribute
    var name: String? = null

    @EpoxyAttribute
    var thumbnailPath: String? = null

    @EpoxyAttribute
    var onClickListener: () -> Unit = {}

    override fun bind(holder: Holder) {
        holder.container.setOnClickListener { onClickListener() }
        holder.nameTextView.setText(name)

        Glide.with(holder.thumbnailImageView.context)
            .load(thumbnailPath)
            .centerCrop()
            .into(holder.thumbnailImageView)
    }

    class Holder : EpoxyHolder() {
        internal lateinit var container: ViewGroup
        internal lateinit var nameTextView: TextView
        internal lateinit var thumbnailImageView: ImageView

        override fun bindView(itemView: View) {
            container = itemView.findViewById(R.id.container)
            nameTextView = itemView.findViewById(R.id.item_character_summary_name)
            thumbnailImageView = itemView.findViewById(R.id.item_character_summary_thumbnail)
        }
    }

}