# Covid Tracer App

A native Android app that traces exposures to Covid-19 patients. Developed as the team's final project for the Mobile Application Development university coursework.

<p align="center">
  <img src="https://github.com/AHB99/covid-tracer-app-cmp354/blob/master/ghimages/login.png" height="300" />
  <img src="https://github.com/AHB99/covid-tracer-app-cmp354/blob/master/ghimages/home.png" height="300" />
  <img src="https://github.com/AHB99/covid-tracer-app-cmp354/blob/master/ghimages/detection.png" height="300" />
</p>

## Technologies Used
* Java
* Android SDK
* Google Maps API
* Firebase Realtime Database

## Overview

The Covid Tracer app allows users to register for the service, hence tracing their locations while the app is active. If any user reports a positive PCR test, the app will check for exposures and notify relevant users.

Currently, the app possesses the following features:

### Exposure Detection

If a user has come into close contact with a Covid-19 positive patient within the past 2 weeks, they are then sent a notification, and the exposure is added to their account.

### View Exposures via List/Map

Users are able to view all exposures to Covid-19 positive patients in the form of a list. If the user selects a given exposure, they are also able to view the exposure in a Map interface.

### User Management

Users have unique accounts that allow them to be registered in the system. Users are also able to update their account with their latest PCR positive/negative test results.
