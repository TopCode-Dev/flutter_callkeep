package co.doneservices.callkeep

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle

class CallKeepBroadcastReceiver : BroadcastReceiver() {

    companion object {

        const val ACTION_CALL_INCOMING =
                "co.doneservices.callkeep.ACTION_CALL_INCOMING"
        const val ACTION_CALL_START = "co.doneservices.callkeep.ACTION_CALL_START"
        const val ACTION_CALL_ACCEPT =
                "co.doneservices.callkeep.ACTION_CALL_ACCEPT"
        const val ACTION_CALL_DECLINE =
                "co.doneservices.callkeep.ACTION_CALL_DECLINE"
        const val ACTION_CALL_ENDED =
                "co.doneservices.callkeep.ACTION_CALL_ENDED"
        const val ACTION_CALL_TIMEOUT =
                "co.doneservices.callkeep.ACTION_CALL_TIMEOUT"
        const val ACTION_CALL_CALLBACK =
                "co.doneservices.callkeep.ACTION_CALL_CALLBACK"


        const val EXTRA_CALLKEEP_INCOMING_DATA = "EXTRA_CALLKEEP_INCOMING_DATA"

        const val EXTRA_CALLKEEP_ID = "EXTRA_CALLKEEP_ID"
        const val EXTRA_CALLKEEP_CALLER_NAME = "EXTRA_CALLKEEP_CALLER_NAME"
        const val EXTRA_CALLKEEP_APP_NAME = "EXTRA_CALLKEEP_APP_NAME"
        const val EXTRA_CALLKEEP_HANDLE = "EXTRA_CALLKEEP_HANDLE"
        const val EXTRA_CALLKEEP_HAS_VIDEO = "EXTRA_CALLKEEP_HAS_VIDEO"
        const val EXTRA_CALLKEEP_AVATAR = "EXTRA_CALLKEEP_AVATAR"
        const val EXTRA_CALLKEEP_DURATION = "EXTRA_CALLKEEP_DURATION"
        const val EXTRA_CALLKEEP_TEXT_ACCEPT = "EXTRA_CALLKEEP_TEXT_ACCEPT"
        const val EXTRA_CALLKEEP_TEXT_DECLINE = "EXTRA_CALLKEEP_TEXT_DECLINE"
        const val EXTRA_CALLKEEP_TEXT_MISSED_CALL = "EXTRA_CALLKEEP_TEXT_MISSED_CALL"
        const val EXTRA_CALLKEEP_TEXT_CALLBACK = "EXTRA_CALLKEEP_TEXT_CALLBACK"
        const val EXTRA_CALLKEEP_EXTRA = "EXTRA_CALLKEEP_EXTRA"
        const val EXTRA_CALLKEEP_HEADERS = "EXTRA_CALLKEEP_HEADERS"
        const val EXTRA_CALLKEEP_IS_CUSTOM_NOTIFICATION = "EXTRA_CALLKEEP_IS_CUSTOM_NOTIFICATION"
        const val EXTRA_CALLKEEP_IS_SHOW_LOGO = "EXTRA_CALLKEEP_IS_SHOW_LOGO"
        const val EXTRA_CALLKEEP_IS_SHOW_MISSED_CALL_NOTIFICATION = "EXTRA_CALLKEEP_IS_SHOW_MISSED_CALL_NOTIFICATION"
        const val EXTRA_CALLKEEP_IS_SHOW_CALLBACK = "EXTRA_CALLKEEP_IS_SHOW_CALLBACK"
        const val EXTRA_CALLKEEP_RINGTONE_PATH = "EXTRA_CALLKEEP_RINGTONE_PATH"
        const val EXTRA_CALLKEEP_BACKGROUND_COLOR = "EXTRA_CALLKEEP_BACKGROUND_COLOR"
        const val EXTRA_CALLKEEP_BACKGROUND_URL = "EXTRA_CALLKEEP_BACKGROUND_URL"
        const val EXTRA_CALLKEEP_ACTION_COLOR = "EXTRA_CALLKEEP_ACTION_COLOR"
        const val EXTRA_CALLKEEP_INCOMING_CALL_NOTIFICATION_CHANNEL_NAME = "EXTRA_CALLKEEP_INCOMING_CALL_NOTIFICATION_CHANNEL_NAME"
        const val EXTRA_CALLKEEP_MISSED_CALL_NOTIFICATION_CHANNEL_NAME = "EXTRA_CALLKEEP_MISSED_CALL_NOTIFICATION_CHANNEL_NAME"

        const val EXTRA_CALLKEEP_ACTION_FROM = "EXTRA_CALLKEEP_ACTION_FROM"

        fun getIntentIncoming(context: Context, data: Bundle?) =
                Intent(context, CallKeepBroadcastReceiver::class.java).apply {
                    action = ACTION_CALL_INCOMING
                    putExtra(EXTRA_CALLKEEP_INCOMING_DATA, data)
                }

        fun getIntentStart(context: Context, data: Bundle?) =
                Intent(context, CallKeepBroadcastReceiver::class.java).apply {
                    action = ACTION_CALL_START
                    putExtra(EXTRA_CALLKEEP_INCOMING_DATA, data)
                }

        fun getIntentAccept(context: Context, data: Bundle?) =
                Intent(context, CallKeepBroadcastReceiver::class.java).apply {
                    action = ACTION_CALL_ACCEPT
                    putExtra(EXTRA_CALLKEEP_INCOMING_DATA, data)
                }

        fun getIntentDecline(context: Context, data: Bundle?) =
                Intent(context, CallKeepBroadcastReceiver::class.java).apply {
                    action = ACTION_CALL_DECLINE
                    putExtra(EXTRA_CALLKEEP_INCOMING_DATA, data)
                }

        fun getIntentEnded(context: Context, data: Bundle?) =
                Intent(context, CallKeepBroadcastReceiver::class.java).apply {
                    action = ACTION_CALL_ENDED
                    putExtra(EXTRA_CALLKEEP_INCOMING_DATA, data)
                }

        fun getIntentTimeout(context: Context, data: Bundle?) =
                Intent(context, CallKeepBroadcastReceiver::class.java).apply {
                    action = ACTION_CALL_TIMEOUT
                    putExtra(EXTRA_CALLKEEP_INCOMING_DATA, data)
                }

        fun getIntentCallback(context: Context, data: Bundle?) =
                Intent(context, CallKeepBroadcastReceiver::class.java).apply {
                    action = ACTION_CALL_CALLBACK
                    putExtra(EXTRA_CALLKEEP_INCOMING_DATA, data)
                }
    }


    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent) {
        val callkitNotificationManager = CallkitNotificationManager(context)
        val action = intent.action ?: return
        val data = intent.extras?.getBundle(EXTRA_CALLKEEP_INCOMING_DATA) ?: return
        when (action) {
            ACTION_CALL_INCOMING -> {
                try {
                    callkitNotificationManager.showIncomingNotification(data)
                    sendEventFlutter(ACTION_CALL_INCOMING, data)
                    addCall(context, Data.fromBundle(data))

                    if (callkitNotificationManager.incomingChannelEnabled()) {
                        val soundPlayerServiceIntent =
                            Intent(context, CallKeepSoundPlayerService::class.java)
                        soundPlayerServiceIntent.putExtras(data)
                        context.startService(soundPlayerServiceIntent)
                    }
                } catch (error: Exception) {
                    error.printStackTrace()
                }
            }
            ACTION_CALL_START -> {
                try {
                    sendEventFlutter(ACTION_CALL_START, data)
                    addCall(context, Data.fromBundle(data), true)
                } catch (error: Exception) {
                    error.printStackTrace()
                }
            }
            ACTION_CALL_ACCEPT -> {
                try {
                    sendEventFlutter(ACTION_CALL_ACCEPT, data)
                    context.stopService(Intent(context, CallKeepSoundPlayerService::class.java))
                    callkitNotificationManager.clearIncomingNotification(data)
                    addCall(context, Data.fromBundle(data), true)
                } catch (error: Exception) {
                    error.printStackTrace()
                }
            }
            ACTION_CALL_DECLINE -> {
                try {
                    sendEventFlutter(ACTION_CALL_DECLINE, data)
                    context.stopService(Intent(context, CallKeepSoundPlayerService::class.java))
                    callkitNotificationManager.clearIncomingNotification(data)
                    removeCall(context, Data.fromBundle(data))
                } catch (error: Exception) {
                    error.printStackTrace()
                }
            }
            ACTION_CALL_ENDED -> {
                try {
                    sendEventFlutter(ACTION_CALL_ENDED, data)
                    context.stopService(Intent(context, CallKeepSoundPlayerService::class.java))
                    callkitNotificationManager.clearIncomingNotification(data)
                    removeCall(context, Data.fromBundle(data))
                } catch (error: Exception) {
                    error.printStackTrace()
                }
            }
            ACTION_CALL_TIMEOUT -> {
                try {
                    sendEventFlutter(ACTION_CALL_TIMEOUT, data)
                    context.stopService(Intent(context, CallKeepSoundPlayerService::class.java))
                    if (data.getBoolean(EXTRA_CALLKEEP_IS_SHOW_MISSED_CALL_NOTIFICATION, true)) {
                        callkitNotificationManager.showMissCallNotification(data)
                    }
                    removeCall(context, Data.fromBundle(data))
                } catch (error: Exception) {
                    error.printStackTrace()
                }
            }
            ACTION_CALL_CALLBACK -> {
                try {
                    callkitNotificationManager.clearMissCallNotification(data)
                    sendEventFlutter(ACTION_CALL_CALLBACK, data)
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                        val closeNotificationPanel = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
                        context.sendBroadcast(closeNotificationPanel)
                    }
                } catch (error: Exception) {
                    error.printStackTrace()
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun sendEventFlutter(event: String, data: Bundle) {
        val android = mapOf(
                "showCustomNotification" to data.getBoolean(EXTRA_CALLKEEP_IS_CUSTOM_NOTIFICATION, false),
                "ringtonePath" to data.getString(EXTRA_CALLKEEP_RINGTONE_PATH, ""),
                "backgroundColor" to data.getString(EXTRA_CALLKEEP_BACKGROUND_COLOR, ""),
                "backgroundUrl" to data.getString(EXTRA_CALLKEEP_BACKGROUND_URL, ""),
                "actionColor" to data.getString(EXTRA_CALLKEEP_ACTION_COLOR, ""),
                "incomingCallNotificationChannelName" to data.getString(EXTRA_CALLKEEP_INCOMING_CALL_NOTIFICATION_CHANNEL_NAME, ""),
                "missedCallNotificationChannelName" to data.getString(EXTRA_CALLKEEP_MISSED_CALL_NOTIFICATION_CHANNEL_NAME, ""),
        )
        val forwardData = mapOf(
                "id" to data.getString(EXTRA_CALLKEEP_ID, ""),
                "callerName" to data.getString(EXTRA_CALLKEEP_CALLER_NAME, ""),
                "avatar" to data.getString(EXTRA_CALLKEEP_AVATAR, ""),
                "number" to data.getString(EXTRA_CALLKEEP_HANDLE, ""),
                "hasVideo" to data.getBoolean(EXTRA_CALLKEEP_HAS_VIDEO, false),
                "duration" to data.getLong(EXTRA_CALLKEEP_DURATION, 0L),
                "acceptText" to data.getString(EXTRA_CALLKEEP_TEXT_ACCEPT, ""),
                "declineText" to data.getString(EXTRA_CALLKEEP_TEXT_DECLINE, ""),
                "missedCallText" to data.getString(EXTRA_CALLKEEP_TEXT_MISSED_CALL, ""),
                "callBackText" to data.getString(EXTRA_CALLKEEP_TEXT_CALLBACK, ""),
                "extra" to data.getSerializable(EXTRA_CALLKEEP_EXTRA) as HashMap<String, Any?>,
                "android" to android
        )
        FlutterCallkitIncomingPlugin.sendEvent(event, forwardData)
    }
}