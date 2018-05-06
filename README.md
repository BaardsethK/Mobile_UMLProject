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

# Report

## Team
Kristoffer Baardseth

## Development
Development was done by single contributor.
Developed in "relaxed" Incremental Cycle.
Functionality listing:
Take photo
Display photo to bitmap
Scale photo
Remove color from photo
Save photo
Display Photo in gallery
Github Integration

Extra:
Bitmap .txt map

Problems:
Got properly started too late, might've understood how to integrate Git functionality if I had started earlier.

## Testing
Testing was done by "trial and error", no defined unit tests.
Functions were tested one by one when implemented:
- Photo/Show bitmap
- Scale photo
- Remove color
- Save to location.
Extra:
- Bitmap .txt map
Application was made using "relaxed" incremental development style. Testing was as follows:
Start development of function -> Partial function usable -> Test -> 
->(opt) fix errors -> Function usable -> Test -> (opt) fix errors -> Integrated.

## Missing
The project is missing the GitHub integration, because of a failure to learn how to integrate it.
This is mostly because I struggle to understand the use of OAuth in Android Studio, and how to properly integrate it.
The documentation does not fit my learning style at all, and this is something I need to work on.

## Experiences
The easy part:
Taking images, doing some manipulations and saving. While I struggled to fully understand it at first,
once I got started it felt easy to make small changes and edits fast. This was what I was most interested in learning,
and I am very satisfied with how it turned out.
The hard part:
Integrating the GitHub API to connect with a user and push to a repository proved a challenge. 
I struggled to understand the documentation, and I did not find exmaples or tutorials that worked for me.
What I learned:
Image manipulation, saving file to storage, updating gallery, taking photos and using them inside an application.
Android Studio/code examples feels lacking and "all over the place". Usually finding a solution to a problem means finding many 
solutions and extrapolating a solution that fits your version.

