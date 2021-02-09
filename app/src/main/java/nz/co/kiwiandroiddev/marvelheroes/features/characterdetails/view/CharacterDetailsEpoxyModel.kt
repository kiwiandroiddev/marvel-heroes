package nz.co.kiwiandroiddev.marvelheroes.features.characterdetails.view

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.bumptech.glide.Glide
import nz.co.kiwiandroiddev.marvelheroes.R

@EpoxyModelClass(layout = R.layout.item_character_details)
abstract class CharacterDetailsEpoxyModel :
    EpoxyModelWithHolder<CharacterDetailsEpoxyModel.Holder>() {

    @EpoxyAttribute
    var name: String? = null

    @EpoxyAttribute
    var description: String? = null

    @EpoxyAttribute
    var comicsAppearedIn: List<String> = emptyList()

    @EpoxyAttribute
    var imagePath: String? = null

    override fun bind(holder: Holder) {
        holder.nameTextView.text = name
        holder.descriptionTextView.text = if (description.isNullOrBlank()) "???" else description
        holder.comicsTextView.text =
            if (comicsAppearedIn.isNotEmpty()) comicsAppearedIn.joinToString(",\n") else "???"

        Glide.with(holder.imageView.context)
            .load(imagePath)
            .centerCrop()
            .into(holder.imageView)
    }

    class Holder : EpoxyHolder() {
        internal lateinit var nameTextView: TextView
        internal lateinit var descriptionTextView: TextView
        internal lateinit var comicsTextView: TextView
        internal lateinit var imageView: ImageView

        override fun bindView(itemView: View) {
            nameTextView = itemView.findViewById(R.id.item_character_details_name)
            descriptionTextView = itemView.findViewById(R.id.item_character_details_description)
            comicsTextView = itemView.findViewById(R.id.item_character_details_comics)
            imageView = itemView.findViewById(R.id.item_character_details_image)
        }
    }
}