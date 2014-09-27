wayland-java-bindings
=====================

The Wayland protocol Java-language Binding

Javadoc
=======
[Here](http://zubnix.github.io/wayland-java-bindings/)

Building
========
Run `gradle install` in the root of the project.

Add the maven dependency to your project: `org.freedesktop:wayland:1.0.0-SNAPSHOT`

Private protocol
================
The project allows you to generate your own private wayland protocol bindings.
 - Add `org.freedesktop:generator:1.0.0-SNAPSHOT` to your build path, no need to put it on your classpath as it will only be used during compilation.
 - Add `org.freedesktop:stubs:1.0.0-SNAPSHOT` to your classpath.
 - Add a `@Package` annotation to your own private `package-info.java` file and set it to use your own private protocol xml file. Here's an [example](wayland/src/main/java/org/freedesktop/wayland/package-info.java).
 - Build with maven or gradle. The generated bindings should automatically appear in the same package as your `package-info.java` file.

State
=====
Usable

Known Issues
============
 - Probably some memory leaks.
 - Native JNI code is not as robust is should be i.e. no corner case checks.
 - No unit tests.

TODO
====
 - Unit tests.
 - EGL Wayland.
