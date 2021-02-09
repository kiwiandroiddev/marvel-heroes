package nz.co.kiwiandroiddev.marvelheroes.features.characterlist.view

import com.airbnb.epoxy.EpoxyModelClass
import nz.co.kiwiandroiddev.marvelheroes.R
import nz.co.kiwiandroiddev.marvelheroes.common.epoxy.EpoxyModelWithLayout

@Suppress("LeakingThis")
@EpoxyModelClass
abstract class InlineLoadingEpoxyModel : EpoxyModelWithLayout(R.layout.item_loading_inline) {
    init {
        id("loading_inline")
    }
}