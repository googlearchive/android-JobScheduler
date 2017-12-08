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

import android.app.Activity
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Messenger
import android.os.PersistableBundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import java.util.concurrent.TimeUnit

/**
 * Schedules and configures jobs to be executed by a [JobScheduler].
 *
 * [MyJobService] can send messages to this via a [Messenger]
 * that is sent in the Intent that starts the Service.
 */
class MainActivity : Activity() {

    lateinit private var anyConnectivityRadioButton: RadioButton
    lateinit private var deadlineEditText: EditText
    lateinit private var delayEditText: EditText
    lateinit private var durationTimeEditText: EditText
    lateinit private var requiresChargingCheckBox: CheckBox
    lateinit private var requiresIdleCheckbox: CheckBox
    lateinit private var wiFiConnectivityRadioButton: RadioButton

    // Handler for incoming messages from the service.
    lateinit private var handler: IncomingMessageHandler
    lateinit private var serviceComponent: ComponentName
    private var jobId = 0

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sample_main)

        anyConnectivityRadioButton = findViewById(R.id.checkbox_any)
        deadlineEditText = findViewById(R.id.deadline_time)
        delayEditText = findViewById(R.id.delay_time)
        durationTimeEditText = findViewById(R.id.duration_time)
        requiresChargingCheckBox = findViewById(R.id.checkbox_charging)
        requiresIdleCheckbox = findViewById(R.id.checkbox_idle)
        wiFiConnectivityRadioButton = findViewById(R.id.checkbox_unmetered)

        handler = IncomingMessageHandler(this)
        serviceComponent = ComponentName(this, MyJobService::class.java)

        findViewById<Button>(R.id.cancel_button).setOnClickListener { cancelAllJobs() }
        findViewById<Button>(R.id.finished_button).setOnClickListener { finishJob() }
        findViewById<Button>(R.id.schedule_button).setOnClickListener { scheduleJob() }
    }

    override fun onStop() {
        // A service can be "started" and/or "bound". In this case, it's "started" by this Activity
        // and "bound" to the JobScheduler (also called "Scheduled" by the JobScheduler). This call
        // to stopService() won't prevent scheduled jobs to be processed. However, failing
        // to call stopService() would keep it alive indefinitely.
        stopService(Intent(this, MyJobService::class.java))
        super.onStop()
    }

    override fun onStart() {
        super.onStart()
        // Start service and provide it a way to communicate with this class.
        val startServiceIntent = Intent(this, MyJobService::class.java)
        val messengerIncoming = Messenger(handler)
        startServiceIntent.putExtra(MESSENGER_INTENT_KEY, messengerIncoming)
        startService(startServiceIntent)
    }

    /**
     * Executed when user clicks on SCHEDULE JOB.
     */
    private fun scheduleJob() {
        val builder = JobInfo.Builder(jobId++, serviceComponent)

        val delay = delayEditText.text.toString()
        if (delay.isNotEmpty()) {
            builder.setMinimumLatency(delay.toLong() * TimeUnit.SECONDS.toMillis(1))
        }

        val deadline = deadlineEditText.text.toString()
        if (deadline.isNotEmpty()) {
            builder.setOverrideDeadline(deadline.toLong() * TimeUnit.SECONDS.toMillis(1))
        }

        if (wiFiConnectivityRadioButton.isChecked) {
            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
        } else if (anyConnectivityRadioButton.isChecked) {
            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
        }

        // Extras, work duration.
        val extras = PersistableBundle()
        var workDuration = durationTimeEditText.text.toString()
        if (workDuration.isEmpty()) workDuration = "1"
        extras.putLong(WORK_DURATION_KEY, workDuration.toLong() * TimeUnit.SECONDS.toMillis(1))

        // Finish configuring the builder
        builder.run {
            setRequiresDeviceIdle(requiresIdleCheckbox.isChecked)
            setRequiresCharging(requiresChargingCheckBox.isChecked)
            setExtras(extras)
        }

        // Schedule job
        Log.d(TAG, "Scheduling job")
        (getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler).schedule(builder.build())
    }

    /**
     * Executed when user clicks on CANCEL ALL.
     */
    private fun cancelAllJobs() {
        (getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler).cancelAll()
        showToast(getString(R.string.all_jobs_cancelled))
    }

    /**
     * Executed when user clicks on FINISH LAST TASK.
     */
    private fun finishJob() {
        val jobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        val allPendingJobs = jobScheduler.allPendingJobs
        if (allPendingJobs.size > 0) {
            // Finish the last one.
            // Example: If jobs a, b, and c are queued in that order, this method will cancel job c.
            val id = allPendingJobs.first().id
            jobScheduler.cancel(id)
            showToast(getString(R.string.cancelled_job, id))
        } else {
            showToast(getString(R.string.no_jobs_to_cancel))
        }
    }

    companion object {
        private val TAG = "MainActivity"
    }
}
