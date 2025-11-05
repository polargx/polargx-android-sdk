package com.library.polargx.di

import android.content.Context
import com.library.polargx.PolarApp
import com.library.polargx.api.ApiService
import com.library.polargx.api.ApiServiceImpl
import com.library.polargx.PolarConstants
import com.library.polargx.core.HttpClientFactory
import com.library.polargx.data.links.LinksRepository
import com.library.polargx.data.links.LinksRepositoryImpl
import com.library.polargx.data.links.local.LinksLocalDatasource
import com.library.polargx.data.links.local.LinksLocalDatasourceImpl
import com.library.polargx.data.links.remote.LinksRemoteDatasource
import com.library.polargx.data.links.remote.LinksRemoteDatasourceImpl
import com.library.polargx.data.others.OthersRepository
import com.library.polargx.data.others.OthersRepositoryImpl
import com.library.polargx.data.others.local.OthersLocalDatasource
import com.library.polargx.data.others.local.OthersLocalDatasourceImpl
import com.library.polargx.data.others.remote.OthersRemoteDatasource
import com.library.polargx.data.others.remote.OthersRemoteDatasourceImpl
import com.library.polargx.data.push.PushRepository
import com.library.polargx.data.push.PushRepositoryImpl
import com.library.polargx.data.push.local.PushLocalDatasource
import com.library.polargx.data.push.local.PushLocalDatasourceImpl
import com.library.polargx.data.push.remote.PushRemoteDatasource
import com.library.polargx.data.push.remote.PushRemoteDatasourceImpl
import com.library.polargx.data.tracking.TrackingRepository
import com.library.polargx.data.tracking.TrackingRepositoryImpl
import com.library.polargx.data.tracking.local.TrackingLocalDatasource
import com.library.polargx.data.tracking.local.TrackingLocalDatasourceImpl
import com.library.polargx.data.tracking.remote.TrackingRemoteDatasource
import com.library.polargx.data.tracking.remote.TrackingRemoteDatasourceImpl
import com.library.polargx.helpers.ThLocker
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

val polarModule = module {

    single(named(PolarConstants.Koin.THREAD_LOCKER)) {
        ThLocker()
    }

    single(named(PolarConstants.Koin.SHARED_PREFS)) {
        androidApplication().getSharedPreferences(
            "polar_gx.file",
            Context.MODE_PRIVATE
        )
    }

    single {
        LinksLocalDatasourceImpl(
            sharedPreferences = get(named(PolarConstants.Koin.SHARED_PREFS)),
        )
    } bind LinksLocalDatasource::class
    single {
        LinksRemoteDatasourceImpl(
            client = get(named(PolarConstants.Koin.RATE_LIMIT_HTTP_CLIENT)),
        )
    } bind LinksRemoteDatasource::class
    singleOf(::LinksRepositoryImpl) bind LinksRepository::class

    single {
        PushLocalDatasourceImpl(
            sharedPreferences = get(named(PolarConstants.Koin.SHARED_PREFS)),
        )
    } bind PushLocalDatasource::class
    single {
        PushRemoteDatasourceImpl(
            client = get(named(PolarConstants.Koin.RATE_LIMIT_HTTP_CLIENT)),
        )
    } bind PushRemoteDatasource::class
    singleOf(::PushRepositoryImpl) bind PushRepository::class

    single {
        TrackingLocalDatasourceImpl(
            sharedPreferences = get(named(PolarConstants.Koin.SHARED_PREFS)),
        )
    } bind TrackingLocalDatasource::class
    single {
        TrackingRemoteDatasourceImpl(
            client = get(named(PolarConstants.Koin.RATE_LIMIT_HTTP_CLIENT)),
        )
    } bind TrackingRemoteDatasource::class
    singleOf(::TrackingRepositoryImpl) bind TrackingRepository::class

    single {
        OthersLocalDatasourceImpl(
            sharedPreferences = get(named(PolarConstants.Koin.SHARED_PREFS)),
        )
    } bind OthersLocalDatasource::class
    single {
        OthersRemoteDatasourceImpl(
            client = get(named(PolarConstants.Koin.RATE_LIMIT_HTTP_CLIENT)),
        )
    } bind OthersRemoteDatasource::class
    singleOf(::OthersRepositoryImpl) bind OthersRepository::class

    single {
        ApiServiceImpl(
            client = get(named(PolarConstants.Koin.RATE_LIMIT_HTTP_CLIENT)),
            sf = get(named(PolarConstants.Koin.SHARED_PREFS)),
        )
    } bind ApiService::class

    single(named(PolarConstants.Koin.RATE_LIMIT_HTTP_CLIENT)) {
        HttpClientFactory.createRateLimitHttpClient(
            context = androidApplication(),
            xApiKey = PolarApp.shared.apiKey,
            locker = get(named(PolarConstants.Koin.THREAD_LOCKER)),
        )
    }
}