package my.edu.tarc.cargolite

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class NotificationApp : Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel1 = NotificationChannel(
                    CHANNEL_1_ID,
                    "Channel 1",
                    NotificationManager.IMPORTANCE_HIGH
            )
            channel1.description = "This is Channel 1"
            val channel2 = NotificationChannel(
                    CHANNEL_2_ID,
                    "Channel 2",
                    NotificationManager.IMPORTANCE_LOW
            )
            channel2.description = "This is Channel 2"
            val channel3 = NotificationChannel(
                    CHANNEL_3_ID,
                    "Channel 3",
                    NotificationManager.IMPORTANCE_DEFAULT
            )
            channel3.description = "This is Channel 3"
            val channel4 = NotificationChannel(
                    CHANNEL_4_ID,
                    "Channel 4",
                    NotificationManager.IMPORTANCE_DEFAULT
            )
            channel4.description = "This is Channel 4"

            val manager = getSystemService(
                    NotificationManager::class.java
            )
            manager.createNotificationChannel(channel1)
            manager.createNotificationChannel(channel2)
            manager.createNotificationChannel(channel3)
            manager.createNotificationChannel(channel4)
        }
    }

    companion object {
        const val CHANNEL_1_ID = "channel1"
        const val CHANNEL_2_ID = "channel2"
        const val CHANNEL_3_ID = "channel3"
        const val CHANNEL_4_ID = "channel4"
    }
}