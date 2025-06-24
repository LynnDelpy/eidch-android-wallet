package ch.admin.foitt.wallet.platform.utils

fun <K, V> Iterable<K>.associateWithNotNull(valueSelector: (K) -> V?): Map<K, V> {
    return this.mapNotNull { element ->
        valueSelector(element)?.let { value ->
            element to value
        }
    }.toMap()
}
