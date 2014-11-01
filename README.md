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
Create a custom **ScrollView** with an full implemented *onInterceptTouchEvent(MotionEvent event)* method. Enable some configuration with custom rules:
**RuledScrollView**.*setRule(Rule rule)*

**Rule** let you enable/disable:
 - if **RuledScrollView** should retake touch event, when an currently touch handled children can't scroll anymore


##Benefits##

One full implemented **RuledScrollView** can be used to enable scrolling in complex layouts like:

`<RuledScrollView>
   <LinearLayout>
       <HEADERVIEW />
       <ScrollView>
         <CONTENTVIEW />
       </ScrollView>
       <FOOTERVIEW />
   </LinearLayout>
</RuledScrollView>`

Or one step further with dynamic layouts

`<RuledScrollView>
   <LinearLayout>
       <fragmentA />
       <fragmentB />
   </LinearLayout>
</RuledScrollView>`

##Problems##
You will get some performance problems if you have done a really fancy view tree with a lot of **ViewGroups** and scrollable **Views**.

##Todo##
 - create sample with an fragment pager
 - add more rules
 - wait for feedback :)

##License##
This projected is licensed under the terms of the Apache License 2.0 license.