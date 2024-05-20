package com.johanntml.spacexlanzamientos

import com.johanntml.spacexlanzamientos.cache.Database
import com.johanntml.spacexlanzamientos.cache.DatabaseDriverFactory
import com.johanntml.spacexlanzamientos.entity.RocketLaunch
import com.johanntml.spacexlanzamientos.network.SpaceXApi

class SpaceXSDK(databaseDriverFactory: DatabaseDriverFactory, val api: SpaceXApi) {
    private val database = Database(databaseDriverFactory)

    @Throws(Exception::class)
    suspend fun getLaunches(forceReload: Boolean): List<RocketLaunch> {
        val cachedLaunches = database.getAllLaunches()
        return if (cachedLaunches.isNotEmpty() && !forceReload) {
            cachedLaunches
        } else {
            api.getAllLaunches().also {
                database.clearAndCreateLaunches(it)
            }
        }
    }
}