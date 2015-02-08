wayland-java-bindings
=====================

The Wayland protocol Java-language Binding

Javadoc
=======
[Here](http://zubnix.github.io/wayland-java-bindings/)

Usage
====================
Run `gradle install` in the root of the project and add the maven dependency 
```xml
 <dependency>
  <groupId>org.freedesktop</groupId>
  <artifactId>wayland</artifactId>
  <version>1.1.0</version>
 </dependency>
```

Private protocol
================
The project allows you to generate your own private wayland protocol bindings.
 - Add `org.freedesktop:generator:1.1.0` to your build path, no need to put it on your classpath as it will only be used during compilation.
 - Add `org.freedesktop:stubs:1.1.0` to your classpath.
 - Add a `@Protocols` annotation to your own private `package-info.java` file and set it to use your own private protocol xml file. Here's an [example](wayland/src/main/java/org/freedesktop/wayland/package-info.java).
 - Build with maven or gradle. The generated bindings should automatically appear in the same package as your `package-info.java` file.

State
=====
 - Usable.
 - Pure Java (JNA)

Known Issues
============
 - None.

TODO
====
 - Unit tests.
 - Debug logging.

License
=======
   Copyright 2015 Erik De Rijcke

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
