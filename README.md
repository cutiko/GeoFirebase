## What is GeoFirebase?

GeoFirebase is a library to help you query **Firebase Real-Time Database** based on **Geolocations** on Android

### Why not GeoFire?
There is no reason at all why you shouldn't use GeoFire, is a great library, and the people contributing to it are amazing developers. I made this library as an alternative to GeoFire because:

 - I want to work with Firebase Database and Geolocations while having control of my data

That is what you get! **Easy Firebase Database queries + Geolocations, while keeping control of your Data Structure**

If you are not convinced yet on using this library, let me offer you one more perk:  *Cool gifs in the documentation, so you have fun reading it :)*

![Mathematical](https://media.giphy.com/media/9lMoyThpKynde/giphy.gif)


# Add the library:

 1. Add Firebase and the Firebase Database to your project as you have always done. I reccommend using the assistant, is in the menu `tools\Firebase`
 2. Add the library

```
compile 'cl.cutiko:geofirebase:0.1.0'
```

You can look at this [gist](https://gist.github.com/cutiko/eb3526dafe7d29ec588a01c32074b0db), for a quick example, but please remember **your project have to add the** `google-service.json`

![Champion](https://media.giphy.com/media/Nk9vmTrmOVNuw/giphy.gif)


# How to use it?
Is very simple:
  1. You need to create model that extend the provided in the library `GeoPod`
  2. And for executing the query you have to instantiate an abstract class from my library `GeoEvent`

### Creating the model
Since the library is ment to be used with Geolocations, then the basic model provided makes sure the object being query have: *longitude and latitude*, also a *key*.
So the model you are gonna use for your *place*, *cofee shop*, or whatever is about your app have to extend `GeoPod`

```
public class GeoPlace extends GeoPod {
    //Every variable you need, name, category, rating, etc.
    //You need the empty constructor
    //Getters and setters, always, getters and setters, say it slow, say it loud, but don't forget it
}

```

### Getting the data
The code below is the anonymous instantiation mentioned before. Between the angle brackets, you have to pass your model, the same you use in the previous step.
Then in the constructor, the first 3 arguments are for the query. The longitude and latitude will be used as a center, and the third param will be the radius to query the data.
The fourth param is your model ass a class, pass it ass in the example `YourModel.class`, include the *dot class*.
The last 2 arguments in the constructor are for doing the query in the database. Since the promise of this library is to allow you control over your data structure, then you have to tell where to query.
The first String is the route in the database where the data is stored, and the second is a funnel node to filter the data better (more information further).

```
    new GeoEvent<GeoPlace>(
            latitude,
            longitude,
            GeoDistances.TWO_KM,
            GeoPlace.class,
            "locations",
            "cl"
    ) {
        @Override
        protected void results(List<GeoPlace> geoPods) {
            for (GeoPlace geoPlace : geoPods) {
                LatLng latLng = new LatLng(geoPlace.getLatitude(), geoPlace.getLongitude());
                Marker marker = googleMap.addMarker(new MarkerOptions().position(latLng));
                marker.setTag(geoPlace);
            }
        }
    };
```

In this example when the data is return every object is placed on a marker in a Google Map

If your are not familiar with doing this kind of implementations, don't worry about it. Take a deep breath and do this: write `new GeoEven<>` pass inside the angle brackets `<>` your model, and then pass every argument to the constructor, one last thing that should fix everything: **press alt+enter and select implement methods**.

![Hackerman](https://media.giphy.com/media/Q2W4hziDOyzu0/giphy.gif)


## How to upload data
You can do whatever you want to upload your data, you can simply `setValue(yourObject)` or do something more as `updadateChilder()`. **The library will take care of the data query in any node you ask**. To keep this readme short if you want to know more about this, please see the [wiki page about uploading data](https://github.com/cutiko/GeoFirebase/wiki/How-to-upload-data)

## Database Rules
Since you are gonna work with indexing, then you should use indexing, please read the [Firebase Docs](https://firebase.google.com/docs/database/security/indexing-data). This is what is being used in the example app:

```
{
  "rules": {
    ".read": true,
    ".write": true,
      "locations": {
        "cl": {
          ".indexOn": ["latitude"]
        }
      }
  }
}
```

## How to use the demo app

 1. git clone this repo
 2. Add your google-services.json inside the app folder or connect the app using the Android Studio assistant
 3. You can [download a simple json for uploading to your database](https://www.dropbox.com/s/wobke3i5naiuik4/geodata.json?dl=0)
 4. This project assume your database rules are ment to be write and read `true` regardless login or not, please mind this security, this is only for demostration purpouse. Set this rules in your database rules.
 5. You have to add an API key for using Google Maps, if you haven't done this before, create a new project and select `Google Maps Activity` that will create an `.xml` file inside the `values` folder, look at it and follow the instructions there. When you are finish you can copy paste that key or the whole file to this project.
 6. Or maybe just [download the .APK](https://www.dropbox.com/s/qo6rij8icsug4o5/app-debug.apk?dl=0)

![Victory Dance](https://media.giphy.com/media/l41Yh18f5TbiWHE0o/giphy.gif)

## Thanks to
 - The people in [GeoFire](https://github.com/firebase/geofire-java) I was very inspired by their work
 - The people in [Firebase-Ui-Android](https://github.com/firebase/FirebaseUI-Android) I learned a lot of what I use here looking at their `FirebaseRecyclerAdapter` and the `FirebaseIndexRecyclerAdapter`
 - [Cristian Vidal](https://github.com/Himuravidal) we was working together on solving this prior to coming with the idea of this humble library, the fruitful discussions we had about this made possible this library.

## What's next?
 - Something like a `FirebaseRecyclerAdapter` for Google Maps would be really cool
 - Since this is for maps mostly, there should be some utility to adjust Google Map zoom based on the radius distance
 - Better gifs! Better quality, full hd! Bigger, 2400px width! But most important, the dankest gif in the internet!

**Suggestions and PRs are welcome**
