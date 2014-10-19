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
**RuledScrollView**.*setRule(int direction, int mode, boolean intercept)*

direction:
 - **RuledScrollView**.*DIRECTION_UP*
 - **RuledScrollView**.*DIRECTION_DOWN*
 - **RuledScrollView**.*DIRECTION_LEFT*
 - **RuledScrollView**.*DIRECTION_RIGHT*

mode:
 - **RuledScrollView**.*WHEN_SCROLLABLE*
 - **RuledScrollView**.*WHEN_ANOTHER_CHILD_SCROLLABLE*

##Additional##
We got a problem when our higher **ScrollContainer** and the lower one is scrollable at the same direction. The higher **View** will always consume the event, there for comes rule *WHEN_ANOTHER_CHILD_SCROLLABLE*. But how determine, if one child can scroll as well?

Simply observe own children and remember all scrollable **Views**. These **Views** will be stored in a list and checked when rule *WHEN_ANOTHER_CHILD_SCROLLABLE* is active.
> TODO: make an **MotionEvent**.*DOWN* hit test with child view
> 
> TODO: make an child view *getParent()*, *isScrollContainer()*, *isTouchable()* and *isEnabled()* check

**RuledScrollView**.*OnAttach()* will cause an layout parse for all children:
 - all **ViewGroups** get an *onHierarchyChangeListener* (will trigger same parsing for there added **View**)
 - all **Views** or **ViewGroups** that are scroll container, will be add to list and get an *onDetachListener* (to remove view from list)
 
**RuledScrollView**.*OnDetach()* will clean up list and listener

##Benefits##

One full implemented **RuledScrollView** can be used to enable scrolling in complex Layouts like:

`<RuledScrollView>
   <LinearLayout>
       <HEADERVIEW />
       <ScrollView>
         <CONTENTVIEW />
       </ScrollView>
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
**RuledScrollView** wont be notified if one child change *View*.*isScrollContainer()* value.

You will get some performance problems if you add and remove child-views all over the time. Or you have done a really fancy view tree with a lot of **ViewGroups** and scrollable **Views**.

You can simply copy and paste code to get the same for **ListView** but be aware that **ListViews** will do a lot layout changes
> TODO: possible solution ignore there children