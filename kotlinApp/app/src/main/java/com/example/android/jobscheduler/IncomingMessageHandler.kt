/*
 * Copyright 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.jobscheduler

import android.os.Handler
import android.os.Message
import android.os.Messenger
import android.support.v4.content.ContextCompat.getColor
import android.view.View
import android.widget.TextView
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit

/**
 * A [Handler] allows you to send messages associated with a thread. A [Messenger]
 * uses this handler to communicate from [MyJobService]. It's also used to make
 * the start and stop views blink for a short period of time.
 */
internal class IncomingMessageHandler(activity: MainActivity) : Handler() {

    // Prevent possible leaks with a weak reference.
    private val mainActivity: WeakReference<MainActivity> = WeakReference(activity)

    override fun handleMessage(msg: Message) {
        val mainActivity = mainActivity.get() ?: return
        val showStartView = mainActivity.findViewById<View>(R.id.onstart_textview)
        val showStopView = mainActivity.findViewById<View>(R.id.onstop_textview)
        when (msg.what) {
            /*
             * Receives callback from the service when a job has landed
             * on the app. Turns on indicator and sends a message to turn it off after
             * a second.
             */
            MSG_COLOR_START -> {
                // Start received, turn on the indicator and show text.
                showStartView.setBackgroundColor(getColor(mainActivity, R.color.start_received))
                updateParamsTextView(msg.obj, "started")
                sendMessageDelayed(Message.obtain(this, MSG_UNCOLOR_START),
                        TimeUnit.SECONDS.toMillis(1))
            }
            /*
             * Receives callback from the service when a job that previously landed on the
             * app must stop executing. Turns on indicator and sends a message to turn it
             * off after two seconds.
             */
            MSG_COLOR_STOP -> {
                // Stop received, turn on the indicator and show text.
                showStopView.setBackgroundColor(getColor(mainActivity, R.color.stop_received))
                updateParamsTextView(msg.obj, "stopped")
                sendMessageDelayed(obtainMessage(MSG_UNCOLOR_STOP), TimeUnit.SECONDS.toMillis(1))
            }
            MSG_UNCOLOR_START -> {
                uncolorButtonAndClearText(showStartView, mainActivity)
            }
            MSG_UNCOLOR_STOP -> {
                uncolorButtonAndClearText(showStopView, mainActivity)
            }
        }
    }

    private fun uncolorButtonAndClearText(textView: View, activity: MainActivity) {
        textView.setBackgroundColor(getColor(activity, R.color.none_received))
        updateParamsTextView()
    }

    private fun updateParamsTextView(jobId: Any? = null, action: String = "") {
        val mainActivity = mainActivity.get() ?: return
        val paramsTextView = mainActivity.findViewById<TextView>(R.id.task_params)
        if (jobId == null) {
            paramsTextView.text = ""
            return
        }
        paramsTextView.text = mainActivity.getString(R.string.job_status, jobId.toString(), action)
    }
}
