## What is GeoFirebase?

GeoFirebase is a library to help you query **Firebase Real-Time Database** based on **Geolocations** on Android

### Why not GeoFire?
There is no reason at all why you shouldn't use GeoFire, is a great library, and the people contributing to it are amazing developers. I made this library as an alternative to GeoFire because:

 - I want to work with Firebase Database and Geolocations while having control of my data

That is what you get! **Easy Firebase Database queries + Geolocations, while keeping control of your Data Structure**

If you are not convinced yet on using my library, let me offer you one more perk:  *I'm going to add cool gifs in the documentation, so you have fun reading it :)*

![Mathematical](https://media.giphy.com/media/9lMoyThpKynde/giphy.gif)


# Add the library:

 1. Add Firebase to your project as you have always done. I reccommend using the assistant, is in the menu `tools\Firebase`
 2. Add the library

```
compile 'cl.cutiko:geofirebase:0.1.0'
```


![Champion](https://media.giphy.com/media/uokhSmdmyfh2o/giphy.gif)


You can look at this [gist](https://gist.github.com/cutiko/eb3526dafe7d29ec588a01c32074b0db), for a quick example, but please remember **your project have to add the** `google-service.json`


# How to use it?
Is very simple:
  1. You need to create model that extend the provided in the library `GeoPod`
  2. And for executing the query you have to instantiate an abstract class from my library `GeoEvent.

#### Creating the model
Since the library is ment to be used with Geolocations, then the basic model I'm providing makes sure the object being query have: *longitude and lantitude*, also a *key* (we will get to this later).
So the model you are gonna use for your *place*, *cofee shop*, or whatever is about your app have to extend `GeoPod`

```
public class GeoPlace extends GeoPod {
    //Every variable you need, name, category, rating, etc.
    //You need the empty constructor
    //Getters and setters, always, getters and setters, say it slow, say it loud, but don't forget it
}

```

#### Getting the data
The code below is the anonymous instantiation mentioned before. Between the angle brackets, you have to pass your model, the same you use in the previous step.
Then in the constructor, the first 3 arguments are for the query. The longitude and latitude will be used as a center, and the third param will be the radios to query data.
The fourth param is your model ass a class, pass it ass in the example `YourModel.class`, include the *dot class*
The last 2 arguments in the constructor are for doing the query in the database. Since the promise of this library is to allow you control over your data structure, then you have to tell where to query.
The first String is the route in the database where the data is stored, and the second is funnel node to filter the data better (more information further in text).

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

If your are not familiar with doing this kind of implementations, don't worry about it. Take a deep breath and do this: write `new GeoEven<>` pass inside the angle brackets `<>` your model, and then pass every argument to the constructor, one last thing that should fix everything: **press alt+enter and select implement methods**.

The library provide a class with constants to use as your 3rd argument `GeoDistances`, most of the radius you need will be there, but for other cases this is the trick:

 - 0.01 = 1KM

There is a catch you should be aware. Data type and the consequences of mathematical truncation can play us fool some times, so **if you want to make sure your data will be retrieved add 1 extra Km to your radius**. If you want everything in 1Km radius better be safe than sorry, ask for 2Km radius.

In this case "cl" is the ISO country code obtained from the SIM card (if you are interested see the example app), but you can use whatever you want, it can be a category, or maybe some other divition in your country, etc.
A more sophisticated app could get the location first then based on that get the city and then do the queries based on that, the sky is the limit.

![Hackerman](https://media.giphy.com/media/l46C6sdSa5DVSJnLG/giphy.gif)

## Instructions

 1. Add your google-services.json inside the app folder or connect the app using the Android Studio assistant
 2. You can [download a simple json for uploading to your database](https://www.dropbox.com/s/wobke3i5naiuik4/geodata.json?dl=0)
 3. This project assume your database rules are ment to be write and read `true` regardless login or not, please mind this security, this is only for demostration purpouse
 
 ### Why not Geofire?
 
 Geofire is a great project, which simplify the georeferencing work, but I don't feel using a library for my data structure is ok.
**For creating this I have base heavily in Geofire data structure**

## How does it work?

It will look for locations arround the current location based on the longitude or latitude. To reduce the data delivered to the user, there is an extra node which is the country code. This country code is obtained by the sim country code. **This work as long as your app contemplate to be used by phones**, for using it the user will need 3G or 4G therefore it will have a SIM card. Another approach could be getting the location, get the country, and then set it as the funnel node.
Once the funnel and query data is obtained there is a simple presenter filtering the data again by the other coordinate (latitude or longitude, the opposite of your query choice).

Other queries can be performed by using the [indexed data aproach](https://firebase.google.com/docs/database/android/structure-data).

## Will this deliver too much data?

I don't think so. That is why there is funnel node with the country code. Since you can't make double queries in Firebase, the longitude-latitude requirement is hard to met. A composed attribute `long_lat` could work but it will face the same problem is attempted to solved here. To get the near data, 1km is more or less 0.01 degrees. So, current latitude, plus 0.01 and less 0.01 will give you 1km arround. The problem is that is not constrained by the other coordinate, so you get the entire world data. That could be a lot, and that cannot be solved by the compose attribute aproach, because the concatenation will also give you unexpected extra data. By this aproach, you limit whatever query only to a country reducing the ammount of data to something quickly transmitable for Firebase.

**Suggestions and PRs are welcome**
