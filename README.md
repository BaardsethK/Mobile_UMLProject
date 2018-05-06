# Mobile_UMLProject
Done for class IMT3674, NTNU in Gjovik
Author @BaardsethK, Kristoffer Baardseth
Android application used to take photos of hand-drawn diagrams, such as UML or relation diagrams.
The photos can then be scaled down to desired size, and color can be removed.
The photo can then be saved, and will appear in phone gallery.
Added extra: the program creates a 0/1 map of the bitmap, which can be used to create maps for games.
This is output as a .txt file.

## Getting started
The code is made in/for Android Studio, without any external dependencies.
It is using the API for Android Nougat (7.0.0) and has only been tested on Nougat, specifically model: Huawei Honor 8 Lite.

## To compile
Build project with Android Studio. Use a connected device or emulator (not tested) to run the application.

## To use APK
An unsigned APK is located in Mobile_UMLProject/app/build/outputs/apk/release. Install this on phone in developer mode to use.

## Code organisation
Most of the program functions is run in MainActivity.
Support class SingleMediaScanner sends update to Phone gallery.
SettingsActivity is used to manage settings screen(Much TODO here)

## Linter
Linter has suppressed warning about Gradle build version. Adds error if updated. Added to TODO
Otherwise 18 spell check errors, mostly debug tag or naming convention.

## TODO
* Fix Gradle Update failures
* Lock image functions when no bitmap is recorded.
* Add full commit - push integration with GitHub API .
* Add variation to "remove color" method:
  * Remove by distance from least/most saturated.
  * Remove by difference in color value.
* Add image cropping utility
* Add "reduce color" method.
  * Reduce from full specter to black/white/red/green/blue.
* Add feedback to utility buttons
  * Loading icon when downscaling/removing color
  * Save file notation with file location ("Image saved to your/file/folder/and/file.img")

## Report
Report is in project as IMT3673 Project Report.pdf
