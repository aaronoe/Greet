# Greet
Greet is a simple social network for Android written using Firebase Cloud Firestore and Cloud Functions. 
It is my final project for the [Android Developer Nanodegree Program by Udacity](https://www.udacity.com/course/android-developer-nanodegree-by-google--nd801).

## Tech Stack
- Firebase Cloud Firestore as a NoSQL real time database
- Firebase Cloud Functions as a backend which are triggered by writes to the Firestore
- Firebase Storage for hosting uploaded images, Firebase Auth for authentication
- MVVM architecture using Android's new [architecture components](https://developer.android.com/topic/libraries/architecture/index.html) to use the Observer pattern when listening to database updates in realtime
- [Android Annotations](http://androidannotations.org/) to reduce boilerplate
