package ch.admin.foitt.wallet.platform.utils

import ch.admin.foitt.wallet.platform.ssi.domain.model.CredentialClaimItem

/**
 * Sorts this list in-place according to the `order` property of each [CredentialClaimItem].
 *
 * Items with a negative order value are treated as having the highest possible priority, ensuring they appear
 * after items with non-negative orders.
 */
fun MutableList<CredentialClaimItem>.sortInPlaceByOrder() = this.sortWith(claimItemComparator)

/**
 * Returns this list sorted according to the `order` property of each [CredentialClaimItem].
 *
 * Items with a negative order value are treated as having the highest possible priority, ensuring they appear
 * after items with non-negative orders.
 */
fun <T : CredentialClaimItem> Collection<T>.sortByOrder() = this.sortedWith(claimItemComparator)

private val claimItemComparator = Comparator<CredentialClaimItem> { item1, item2 ->
    val order1 = if (item1.order < 0) Int.MAX_VALUE else item1.order
    val order2 = if (item2.order < 0) Int.MAX_VALUE else item2.order
    when {
        order1 == order2 -> 0
        order1 < order2 -> -1
        else -> 1
    }
}
