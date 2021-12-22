package io.novafoundation.nova.feature_dapp_impl.di

import io.novafoundation.nova.common.di.FeatureApiHolder
import io.novafoundation.nova.common.di.FeatureContainer
import io.novafoundation.nova.common.di.scope.ApplicationScope
import io.novafoundation.nova.feature_account_api.di.AccountFeatureApi
import io.novafoundation.nova.feature_dapp_impl.DAppRouter
import javax.inject.Inject

@ApplicationScope
class DAppFeatureHolder @Inject constructor(
    featureContainer: FeatureContainer,
    private val router: DAppRouter
) : FeatureApiHolder(featureContainer) {

    override fun initializeDependencies(): Any {
        val dApp = DaggerDAppFeatureComponent_DAppFeatureDependenciesComponent.builder()
            .commonApi(commonApi())
            .accountFeatureApi(getFeature(AccountFeatureApi::class.java))
            .build()

        return DaggerDAppFeatureComponent.factory()
            .create(router, dApp)
    }
}