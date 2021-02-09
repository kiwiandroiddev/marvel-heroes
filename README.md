# Marvel Heroes sample app

Comprehensive Marvel character database app. Data sourced from Marvel's APIs.

Demonstrates the MVI architectural pattern, the ViewModel architecture component for handling configuration changes, Epoxy for UI diffing/updates and Glide for image loading.

## Building and Running

You'll first need to sign up for a Marvel developer at https://developer.marvel.com and add your public and private API keys to `app/gradle.properties`. See `app/gradle.properties.sample` for an example.

Build the app from Android Studio or run `./gradlew assemble install` from the command line.
