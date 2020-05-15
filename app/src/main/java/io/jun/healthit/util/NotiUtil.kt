package io.jun.healthit.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import io.jun.healthit.R
import io.jun.healthit.service.TimerService
import io.jun.healthit.view.MainActivity
import kotlin.properties.Delegates

class NotiUtil {

    companion object {

        private lateinit var channelId: String
        private lateinit var notificationManager: NotificationManagerCompat

        private lateinit var pendingIntent:PendingIntent
        private lateinit var pausePendingIntent:PendingIntent
        private lateinit var stopPendingIntent:PendingIntent

        //타임 끝난 후 다시 재생할 때 초기 세팅 시간값을 그대로 사용하기 위함
        private var setStartTime by Delegates.notNull<Int>()

        private fun setNotification(context: Context) {

            channelId = "${context.packageName}.timer"
            notificationManager = NotificationManagerCompat.from(context)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(channelId, "timer", NotificationManager.IMPORTANCE_LOW).apply {
                    setShowBadge(false)
                }
                notificationManager.createNotificationChannel(channel)
            }

            //노티 클릭시
            val intent = Intent(context, MainActivity::class.java)
            intent.action = "TimerFragment"
             pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT)

            //노티 내 버튼 클릭시 intent
            val pauseIntent = Intent(context, TimerService::class.java)
            pauseIntent.action = "PAUSE"
            val stopIntent = Intent(context, TimerService::class.java)
            stopIntent.action = "STOP"

            pausePendingIntent = PendingIntent.getService(context, 0, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            stopPendingIntent = PendingIntent.getService(context, 0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        }



        private fun initNotification(context: Context, leftTime: String, isPlaying: Boolean, isPausing: Boolean): Notification {

            //노티 버튼 play click
            val playIntent = Intent(context, TimerService::class.java)
            playIntent.action = "PLAY"
            playIntent.putExtra("setTime", setStartTime)
            if (isPausing) playIntent.putExtra("forReplay", true)

            val playPendingIntent = PendingIntent.getService(
                context, 0, playIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            return NotificationCompat.Builder(context, channelId).apply {
                setSmallIcon(R.drawable.ic_timer_noti)
                setContentTitle(context.getString(R.string.title_timer))
                setContentText(leftTime)
                setShowWhen(false)  //생성 시간 표시 x
                color = if(Build.VERSION.SDK_INT > 22) context.getColor(R.color.colorNotification)
                        else context.resources.getColor(R.color.colorNotification)
                priority = NotificationCompat.PRIORITY_LOW
                setAutoCancel(false)    //터치시 사라짐 x
                setOnlyAlertOnce(true)
                setContentIntent(pendingIntent)
                    if(!isPlaying)
                        addAction(R.drawable.ic_play_noti, "play", playPendingIntent)
                    else
                        addAction(R.drawable.ic_pause_noti, "pause", pausePendingIntent)
                addAction(R.drawable.ic_stop_noti, "stop", stopPendingIntent)
                setStyle(androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0, 1))
                }
                .build()
        }

        fun createNotification(context: Context, setTime: Int): Notification {

            setNotification(context)

            this.setStartTime = setTime

            val minutesUntilFinished = (setTime-1) / 60
            val secondsInMinuteUntilFinished = ((setTime-1) - minutesUntilFinished * 60)
            val secondsStr = secondsInMinuteUntilFinished.toString()
            val showTime = "$minutesUntilFinished : ${if (secondsStr.length == 2) secondsStr else "0$secondsStr"}"

            return initNotification(context, showTime, isPlaying = true, isPausing = false)
        }

        fun updateNotification(context: Context, leftTime: String, isPlaying: Boolean, isPausing: Boolean) {

            notificationManager.notify(1, initNotification(context, leftTime, isPlaying, isPausing))
        }

        fun removeNotification() {
            notificationManager.cancelAll()
        }
    }
}