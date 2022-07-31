package kr.co.weightmanager

import android.app.Application
import android.content.Context
import com.squareup.okhttp.internal.Internal.instance
import io.realm.Realm
import io.realm.RealmConfiguration

class MyApplication : Application() {

    init {
        instance = this
    }

    companion object {
        lateinit var instance: MyApplication
        fun getContext() : Context {
            return instance.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        initRealm()
    }

    private fun initRealm(){
        Realm.init(this)
        var config = RealmConfiguration.Builder()
            .allowWritesOnUiThread(true)
            .deleteRealmIfMigrationNeeded()
            .build()

        Realm.setDefaultConfiguration(config)
    }

    fun getContext(): Context{
        return this
    }



}