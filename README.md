## What is GeoFirebase?

GeoFirebase is a library to help you query **Firebase Real-Time Database** based on **Geolocations** on Android

### Why not GeoFire?
There is no reason at all why you shouldn't use GeoFire, is a great library, and the people contributing to it are amazing developers. I made this library as an alternative to GeoFire because:

 - I want to work with Firebase Database and Geolocations while having control of my data

That is what you get! **Easy Firebase Database queries + Geolocations, while keeping control of your Data Structure**

If you are not convinced yet on using this library, let me offer you one more perk:  *Cool gifs in the documentation, so you have fun reading it :)*

![Mathematical](https://media.giphy.com/media/9lMoyThpKynde/giphy.gif)


# Add the library:

 1. Add Firebase to your project as you have always done. I reccommend using the assistant, is in the menu `tools\Firebase`
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
Since the library is ment to be used with Geolocations, then the basic provided makes sure the object being query have: *longitude and lantitude*, also a *key* (we will get to this later).
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

##### The Radius

The library provide a class with constants to use as your 3rd argument `GeoDistances`, most of the radius you need will be there, but for other cases this is the trick:

 - 0.01 = 1KM

There is a catch you should be aware. Data type and the consequences of mathematical truncation can play us fool some times, so **if you want to make sure your data will be retrieved add 1 extra Km to your radius**. If you want everything in 1Km radius better be safe than sorry, ask for 2Km radius.

##### The funnel node

In this case "cl" is the ISO country code obtained from the SIM card (if you are interested see the example app), but you can use whatever you want, it can be a category, or maybe some other division in your country, etc.

**It can even be an empty String**, this library enforce the use of the funnel node as a good way to have scalable data availability, however it does not force you to do it. **As long as you never use `null` it will work.**

A more sophisticated app could get the location first then based on that get the city and then do the queries based on that, the sky is the limit.

![Beautifull](https://media.giphy.com/media/Tx1Q3U3qfYDbG/giphy.gif)

## How to upload data
As is explained before you can have the data structure you want, the library will query whatever POJO on any node you have uploaded to. I do can give you a couple of recommendations.

##### The simple way
This is straight forward, but is not recommended. Your data structure could be like this

```
{
    "locations": {
        "wadknawoidjaw9d": {
            "key": "wadknawoidjaw9d",
            "name": "Some turistic atraction",
            "latitude": -33.429072,
            "longitude": -70.603748
        },
        "w9sjaw0jd": {
            "key": "w9sjaw0jd",
            "name": "Maybe some company office",
            "latitude": -33.439072,
            "longitude": -70.613748
        },
        "wdnaw09djwdjawpodm": {
            "key": "wdnaw09djwdjawpodm",
            "name": "Some delivery point",
            "latitude": -33.439072,
            "longitude": -70.593748
        }
    }
}
```
For uploading data to that you have to simply:

```
DatabaseReference root = FirebaseDatabase.getInstance().getReference();
DatabaseReference locations = root.child("locations");
//Lets assume you have your model ready and you only need the key
String key = locations.push().getKey();
yourModel.setKey(key);
locations.child(key).setValue(yourModel);
```
Later you will be able to query thoose values because you can pass a second String ass empty in the query.

The problem here is, **if you have too much data, a query like that could be slow**. [This questions in SO have more about it](https://stackoverflow.com/questions/39712833/firebase-performance-how-many-children-per-node).

##### Indexing data
This is the way I recommend you to upload your data. A key concept when working with Firebase Database is **denormalization**. This means that duplicating data to make better queries is something you want to do. Another key concepto is indexing the data, wich means there is a special node with a simple list to get the reference to get the data, this is commonly known as a **look up table**.

In one node, you will have a simplified version, in another a complete version, and you can have many other nodes with list of references.

```
{
    "places": {
        "cl": {
            "wadknawoidjaw9d": {
                "category": "category1",
                "name": "center included",
                "latitude": -33.429072,
                "longitude": -70.603748
            },
            "wdnaw09djwdjawpodm":{
                "category": "category2",
                "name": "lat minus long plus included",
                "latitude": -33.439072,
                "longitude": -70.593748
            },
            "oiwueowuepowuepowue":{
                "category": "category4",
                "name": "not mentionable minus 0.04",
                "latitude": -33.469072,
                "longitude": -70.633748
            }
        }
    },    
    "locations": {
        "cl":{
            "wadknawoidjaw9d": {
                "key": "wadknawoidjaw9d",
                "name": "center included",
                "latitude": -33.429072,
                "longitude": -70.603748
            },
            "wdnaw09djwdjawpodm": {
                "key": "wdnaw09djwdjawpodm",
                "name": "lat minus long plus included",
                "latitude": -33.439072,
                "longitude": -70.593748
            },
            "oiwueowuepowuepowue": {
                "key": "oiwueowuepowuepowue",
                "name": "not mentionable minus 0.04",
                "latitude": -33.469072,
                "longitude": -70.633748
            }
        }
    },
    "places_category": {
        "cl": {
            "wadknawoidjaw9d": "category1",
            "wdnaw09djwdjawpodm": "category2",
            "oiwueowuepowuepowue": "category4"
        }
    },
    "favorites": {
        "user_uid_1": {
            "cl": {
                "wadknawoidjaw9d": "true",
                "w9sjaw0jd": "true"
            }
        }
    }
}
```

In this case the node `places` have the full object, it's only have 1 extra attribute (the `category`), in comparison to the `locations` node. For your application it could be more, photos, descriptions, rating, etc. The benefit of the reduce `locations` node is to **improve data transfer for the user**. There are other 2 extra nodes, `places_category` and `favorites`. Both have a list of references. With this structure if you want to show the user a `RecyclerView` with their favorites or with the places of some category, using a [FirebaseIndexRecyclerAdapter](https://github.com/firebase/FirebaseUI-Android/blob/master/database/README.md#using-firebaseui-with-indexed-data) is very simple. Or adding every `locations` near by to a Google Map. In both cases when the user interact with the object, you have the key, so you can directly get the full object to show the user the details.

To upload data to this structure, you need to do a multiple *set value* at the same time. This method is called `updateChildren()` and use a `Map<String, Object>` to do it so. So, whenever a user creates a *place*, you have to upload a simplified reference to the `locations` node and the full reference to the `places` node.

```
YourModel reduced = new YourModel();
reduced.setLatitude(latitude);
reduced.setLongitude(longitude);
reduced.setName(name);
reduced.setKey(key);

YourModel place = new YourModel();
place.setLatitude(latitude);
place.setLongitude(longitude);
place.setName(name);
place.setCategory(category);
place.setKey(key);

DatabaseReference root = FirebaseDatabase.getInstance().getReference();
Map<String, Object> map = new HashMap<>();
map.put("locations/"+countryIso+"/"+key, reduced);
map.put("places/"+countryIso+"/"+key, place);
map.put("place_category/"+countryIso+"/"+key, category);

root.updateChildren(map);
```

After this you have to capture other user interactions, such as creating a favorite and upload it to that node.

You can read more about indexing and denormalization in the [Firebase Documentation](https://firebase.google.com/docs/database/android/structure-data) and in this great [series of videos by David East](https://www.youtube.com/watch?v=WacqhiI-g_o&list=PLl-K7zZEsYLlP-k-RKFa7RyNPa9_wCH2s)

![Mind Blown](https://media.giphy.com/media/26ufdipQqU2lhNA4g/giphy.gif)


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

# Esto de acá abajo, la indexación (al menos una mención), las reglas y cómo probar la app de prueba, tengo que probar la data
## Will this deliver too much data?

I don't think so. That is why there is funnel node with the country code. Since you can't make double queries in Firebase, the longitude-latitude requirement is hard to met. A composed attribute `long_lat` could work but it will face the same problem is attempted to solved here. To get the near data, 1km is more or less 0.01 degrees. So, current latitude, plus 0.01 and less 0.01 will give you 1km arround. The problem is that is not constrained by the other coordinate, so you get the entire world data. That could be a lot, and that cannot be solved by the compose attribute aproach, because the concatenation will also give you unexpected extra data. By this aproach, you limit whatever query only to a country reducing the ammount of data to something quickly transmitable for Firebase.

**Suggestions and PRs are welcome**
