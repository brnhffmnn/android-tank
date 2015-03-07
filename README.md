android-tank
============
[![Build Status](https://travis-ci.org/panzerfahrer/android-tank.svg?branch=master)](https://travis-ci.org/panzerfahrer/android-tank) [![Release](https://img.shields.io/github/tag/panzerfahrer/android-tank.svg?label=maven)](https://jitpack.io/#panzerfahrer/android-tank/v1.0.0)


A collection of Android related implementations that I find valuable.


And here comes a tank:

```
      ___
     |"""\--=
     (____)
  
```


Contents
=========

* [Content](de/slowpoke/androidtank/content)
 - [Crypto](de/slowpoke/androidtank/content/Crypto.java): Collection of cryptography en-/decryption utilities
 - [Persistable](de/slowpoke/androidtank/content/Persistable.java): De-/Serialization using the `Parcelable` way
* [Graphics](de/slowpoke/androidtank/graphics)
 - [PathParcelable](de/slowpoke/androidtank/graphics/PathParcelable.java): a `android.graphics.Path` that implements `Parcelable`
* [Drawable](de/slowpoke/androidtank/graphics/drawable)
 - [RotatedDrawable](de/slowpoke/androidtank/graphics/drawable/RotatedDrawable.java): a `Drawable` that can be programmatically rotated
  

Usage
======

    repositories {
        maven { url "https://jitpack.io" }
    }
    
    dependencies {
        compile 'com.github.panzerfahrer:android-tank:1.0.0'
    }

License
=======

```
  Copyright 2015 Brian Hoffmann, slowpoke.de

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0
 
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
```
