[![Build Status](https://travis-ci.org/mario222k/RuledScrollView.svg?branch=master)](https://travis-ci.org/mario222k/RuledScrollView)
[![Download](https://api.bintray.com/packages/mario222k/maven/RuledScrollView/images/download.svg) ](https://bintray.com/mario222k/maven/RuledScrollView/_latestVersion)
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

Copyright (c) 2015, LOVOO GmbH
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, this
  list of conditions and the following disclaimer.

* Redistributions in binary form must reproduce the above copyright notice,
  this list of conditions and the following disclaimer in the documentation
  and/or other materials provided with the distribution.

* Neither the name of LOVOO GmbH nor the names of its
  contributors may be used to endorse or promote products derived from
  this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.