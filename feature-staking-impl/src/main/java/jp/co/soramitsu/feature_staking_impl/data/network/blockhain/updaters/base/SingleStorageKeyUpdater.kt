package jp.co.soramitsu.feature_staking_impl.data.network.blockhain.updaters.base

import jp.co.soramitsu.common.utils.SuspendableProperty
import jp.co.soramitsu.core.model.StorageChange
import jp.co.soramitsu.core.model.StorageEntry
import jp.co.soramitsu.core.storage.StorageCache
import jp.co.soramitsu.core.updater.SubscriptionBuilder
import jp.co.soramitsu.core.updater.UpdateScope
import jp.co.soramitsu.core.updater.Updater
import jp.co.soramitsu.fearless_utils.runtime.RuntimeSnapshot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.onEach

suspend fun StorageCache.insert(storageChange: StorageChange) {
    val storageEntry = StorageEntry(
        storageKey = storageChange.key,
        content = storageChange.value,
        runtimeVersion = currentRuntimeVersion()
    )

    insert(storageEntry)
}

abstract class SingleStorageKeyUpdater<S : UpdateScope>(
    override val scope: S,
    private val runtimeProperty: SuspendableProperty<RuntimeSnapshot>,
    private val storageCache: StorageCache
) : Updater {

    /**
     * @return a storage key to update. null in case updater does not want to update anything
     */
    abstract suspend fun storageKey(runtime: RuntimeSnapshot): String?

    override suspend fun listenForUpdates(storageSubscriptionBuilder: SubscriptionBuilder): Flow<Updater.SideEffect> {
        val runtime = runtimeProperty.get()
        val storageKey = storageKey(runtime) ?: return emptyFlow()

        return storageSubscriptionBuilder.subscribe(storageKey)
            .onEach(storageCache::insert)
            .noSideAffects()
    }
}
