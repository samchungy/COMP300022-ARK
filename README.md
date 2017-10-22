# COMP30022 IT-Project: Find My Squad

![Find My Squad](https://raw.githubusercontent.com/COMP30022/ARK/master/app/src/main/assets/finalsplash.png?token=ARLrRjhsb1T9xbVbMGkZeHRMYSXIL-Mpks5Z9d6UwA%3D%3D)


Find My Squad is a mobile application that helps users to find and keep track of people in a group using a map and an AR (Augmented Reality) View. Group members can also chat with each other and set waypoints on the map as places of interest to meet up.

The application consists of the following sections:
- [Authentication Folder](https://github.com/COMP30022/ARK/tree/master/app/src/main/java/ark/ark/Authentication): Contains Login Files
- [Chat Folder](https://github.com/COMP30022/ARK/tree/master/app/src/main/java/ark/ark/Chat): Contains Chat Fragments, Chat Log ACtivity files
- [Groups Folder](https://github.com/COMP30022/ARK/tree/master/app/src/main/java/ark/ark/Groups): Contains Information about the Current User, Groups & Friends
- [Map Folder](https://github.com/COMP30022/ARK/tree/master/app/src/main/java/ark/ark/Map): Contains all the relevant Map Files and classes.
- [Profile Folder](https://github.com/COMP30022/ARK/tree/master/app/src/main/java/ark/ark/Profile): Contains Login, Signup and Group Joining Files
- [User Location](https://github.com/COMP30022/ARK/tree/master/app/src/main/java/ark/ark/UserLocation): Contains Location Services & User Location related files.
- [AR Activity](https://github.com/COMP30022/ARK/blob/master/app/src/main/java/ark/ark/ArActivity.java): Wikitude Javascript Plugin

## Installation and Usage

1. Import the folder into Android Studio and run a gradle build.
2. Create an account or login.
3. Join a group or create a group.
4. Get your friends to join the group.

![App Preview](https://raw.githubusercontent.com/COMP30022/ARK/master/app/src/main/assets/Screenshot_20171017-235152.jpg?token=ARLrRiTVb8dMos_qEsTNEeE-_GpyVc-aks5Z9Ff0wA%3D%3D)

5. Set a Group Waypoint by selecting the button on the right.
6. For more options click or swipe up on the bottom panel.
7. To see all of your current group members, swipe left to access the navigation drawer.
8. Tap on any of your members to zoom in smoothly to their location. 
9. Use the floating action buttons at the bottom-right of the screen to access chat and the Augmented Reality view. 

## Testing
Tests can be found in the [app/src/test/java/ark/ark](https://github.com/COMP30022/ARK/tree/master/app/src/test/java/ark/ark) folder.

## Built With

- [Wikitude](https://www.wikitude.com) - an all-in-one augmented reality SDK with geolocation functionality. 
- [Amazon Web Services](https://aws.amazon.com/) - for all backend server operations and processing.
- [Google Maps Android API](https://developers.google.com/maps/documentation/android-api/)
- [Google Maps Places API](https://developers.google.com/places/android-api/)

## Authors

- Austin Huang
- Jane Ho
- Kyaw Min Htin
- Michael Zeng
- Sam Chung

## Acknowledgements

- The [TextDrawable](https://github.com/amulyakhare/TextDrawable) library for the rendering of custom user icons. 
