
Android JobScheduler Sample (Kotlin)
====================================

Demonstration of the JobScheduler API, which provides an interface for scheduling
background tasks when certain tasks apply.

To understand how this sample works, try these different scenarios:

- Unplug device, schedule a task that requires the device to be plugged in. Job will start when the
device is plugged in.
- Set a delay of 10 seconds and press back. The activity and service are finished but the service is
launched again in 10 seconds (logcat will show debug messages).
- Set a delay of 5 seconds and a work duration of 10 seconds. Schedule job and press the
back button. Open the activity again after 6 seconds. The activity will show the onStopTask even
though both activity and service were shut down.

Pre-requisites
--------------

- Android SDK 27
- Android Support Repository

Getting Started
---------------

This sample uses the Gradle build system. To build this project, use the
"gradlew build" command or use "Import Project" in Android Studio.

Support
-------

- Google+ Community: https://plus.google.com/communities/105153134372062985968
- Stack Overflow: http://stackoverflow.com/questions/tagged/android

If you've found an error in this sample, please file an issue:
https://github.com/googlesamples/android-JobScheduler

Patches are encouraged, and may be submitted by forking this project and
submitting a pull request through GitHub. Please see CONTRIBUTING.md for more details.

License
-------

Copyright 2017 The Android Open Source Project, Inc.

Licensed to the Apache Software Foundation (ASF) under one or more contributor
license agreements.  See the NOTICE file distributed with this work for
additional information regarding copyright ownership.  The ASF licenses this
file to you under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License.  You may obtain a copy of
the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
License for the specific language governing permissions and limitations under
the License.
