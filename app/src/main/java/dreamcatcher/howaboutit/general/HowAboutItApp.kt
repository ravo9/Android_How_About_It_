package dreamcatcher.howaboutit.general

import android.app.Application
import android.content.Context

class HowAboutItApp : Application() {

    companion object {
        private var context: Context? = null

        fun getAppContext(): Context? {
            return context
        }
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}