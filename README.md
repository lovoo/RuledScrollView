[![Build Status](https://travis-ci.org/mario222k/RuledScrollView.svg?branch=master)](https://travis-ci.org/mario222k/RuledScrollView)
RuledScrollView
===============

Android ScrollView implementation with additional touch-interception-rules.

##Question##
Have you ever tried to use a **ScrollView** and another **ScrollContainer** in one layout tree?

Not yet? Lucky you!

For everyone else read carefully.

##Analysis##
Android updated there **ScrollView** *onInterceptTouchEvent(MotionEvent event)* implementation to ignore touch events when **ScrollView** is at top and can't scroll any further. There for with **API 14** (Android 4.0) comes **View**.*canScrollVertically(int direction)* and **View**.*canScrollHoricontally(int direction)*.

> if you need lower API, you can use **ViewCompat** instead

But why only one direction and one axis?

##Solution##
Create a custom **ScrollView** with an full implemented *onInterceptTouchEvent(MotionEvent event)* method. Enable some configuration with custom rules.

**Rule** let you manage all children views without extend any other classes.


##Benefits##

Change scroll behaviour of nested scrollable views in real time via code.

![alt tag](http://s14.directupload.net/images/141108/vw53apcc.png)

##Problems##
You will get some performance problems if you have done a really fancy view tree with a lot of **ViewGroups** and scrollable **Views**.

##License##
`Copyright 2014 Mario Kreußel

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.`