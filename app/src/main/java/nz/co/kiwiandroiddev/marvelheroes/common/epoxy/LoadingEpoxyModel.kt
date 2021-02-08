package nz.co.kiwiandroiddev.marvelheroes.common.epoxy

import com.airbnb.epoxy.EpoxyModelClass
import nz.co.kiwiandroiddev.marvelheroes.R

@Suppress("LeakingThis")
@EpoxyModelClass
abstract class LoadingEpoxyModel : EpoxyModelWithLayout(R.layout.item_loading) {
    init {
        id("loading")
    }
}