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
map.put("places_category/"+countryIso+"/"+key, category);

root.updateChildren(map);
```

After this you have to capture other user interactions, such as creating a favorite and upload it to that node.

The above examples have the key included, because when the user interact with something that represent the object (a marker in the map, by example), then you can use the key to reach the complete version of the uploaded data. Remember, the full version of your object, can have a lot of attributes, to provide a lighter data transfer to the user a simplified version lead to a complete heavier node.

You can read more about indexing and denormalization in the [Firebase Documentation](https://firebase.google.com/docs/database/android/structure-data) and in this great [series of videos by David East](https://www.youtube.com/watch?v=WacqhiI-g_o&list=PLl-K7zZEsYLlP-k-RKFa7RyNPa9_wCH2s)

![Mind Blown](https://media.giphy.com/media/26ufdipQqU2lhNA4g/giphy.gif)

#### What else can I do with this
Following this data structure you could create other sort of apps, real state or maybe delivery:

```
{
    "estates_details":{
        "us_kansas":{
            "hjoijpojwdwda":{
                "photos":[
                    "http://prettyphoto2.com",
                    "http://prettyphoto1.com"
                ],
                "photo": "http://prettyphoto.com",
                "price": 500000,
                "name": "Big Loft",
                "latitude": -33.429072,
                "longitude": -70.603748
            },
            "llmlwndlawndkawnd":{
                "photos":[
                    "http://prettyphoto2.com",
                    "http://prettyphoto1.com"
                ],
                "photo": "http://prettyphoto.com",
                "price": 500000,
                "name": "Luxury Condo",
                "latitude": -33.439072,
                "longitude": -70.613748
            }
        },
        "us_california":{
            "wadknawoidjaw9d":{
                "photos":[
                    "http://prettyphoto2.com",
                    "http://prettyphoto1.com"
                ],
                "photo": "http://prettyphoto.com",
                "price": 500000,
                "name": "Spanish Style",
                "latitude": -33.429072,
                "longitude": -70.603748
            },
            "w9sjaw0jd":{
                "photos":[
                    "http://prettyphoto2.com",
                    "http://prettyphoto1.com"
                ],
                "photo": "http://prettyphoto.com",
                "price": 1000,
                "name": "2 bedrooms, 2 bathrooms",
                "latitude": -33.439072,
                "longitude": -70.613748
            }
        }
    },
    "real_estates":{
        "us_kansas":{
            "hjoijpojwdwda":{
                "price": 500000,
                "name": "Big Loft",
                "latitude": -33.429072,
                "longitude": -70.603748
            },
            "llmlwndlawndkawnd":{
                "price": 500000,
                "name": "Luxury Condo",
                "latitude": -33.439072,
                "longitude": -70.613748
            }
        },
        "us_california":{
            "wadknawoidjaw9d":{
                "price": 500000,
                "name": "Spanish Style",
                "latitude": -33.429072,
                "longitude": -70.603748
            },
            "w9sjaw0jd":{
                "price": 1000,
                "name": "2 bedrooms, 2 bathrooms",
                "latitude": -33.439072,
                "longitude": -70.613748
            }
        }
    },    
    "estates_category":{
        "us_kansas":{
            "hjoijpojwdwda": "rental",
            "llmlwndlawndkawnd": "closure"
        },
        "us_california":{
            "wadknawoidjaw9d": "sale",
            "w9sjaw0jd": "closure"
        }
    }
}
```

In the real estate example you have some very similar indexing to what is seen before. The funnel node for this is the country plus the city name.

```
{
    "routes":{
        "2017_08_28":{
            "awkdbawoidboawidb":{
                "package": "awkdbawoidboawidb",
                "receiver": "Nice Person 1",
                "latitude": -33.429072,
                "longitude": -70.603748
            },
            "aowidh09awhdoaiwhd":{
                "package": "aowidh09awhdoaiwhd",
                "receiver": "Nice Person 2",
                "latitude": -33.439072,
                "longitude": -70.613748
            }
        },
        "2017_08_27":{
            "wadknawoidjaw9d":{
                "package": "wadknawoidjaw9d",
                "receiver": "Nice Person 3",
                "name": "center included",
                "latitude": -33.429072,
                "longitude": -70.603748
            }
        }
    },
    "packages":{
        "2017_08_27":{
            "wadknawoidjaw9d":{
                "package_id": "wadknawoidjaw9d",
                "size": "Huge!",
                "delivered": false,
                "priority": "Same Day Delivery",
                "address": "P. Sherman 42 Wallaby Way"
            }
        }
    }
}
```

In the delivery example, the delivery shifts are organized by date, and the date it self is the funnel node.

The funnel node is being mentioned several times in this documentation, that term has being preffered to emphasize the role of it, to narrow down the data. But a funnel node, as has being called here, is part of the **data fan-out** structure. You can read more about it:

 - [Firebase Docs](https://firebase.google.com/docs/database/android/read-and-write)
 - [Firebase Blog](https://firebase.googleblog.com/2015/10/client-side-fan-out-for-data-consistency_73.html)
 - [StackOverflow Question](https://stackoverflow.com/questions/38181973/firebase-database-the-fan-out-technique)

![High five](https://media.giphy.com/media/120jXUxrHF5QJ2/giphy.gif)

## Database Rules
Since you are gonna work with indexing, then you should use indexing, please read the [Firebase Docs](https://firebase.google.com/docs/database/security/indexing-data)

## How to use the demo app

 1. git clone this repo
 2. Add your google-services.json inside the app folder or connect the app using the Android Studio assistant
 3. You can [download a simple json for uploading to your database](https://www.dropbox.com/s/wobke3i5naiuik4/geodata.json?dl=0)
 4. This project assume your database rules are ment to be write and read `true` regardless login or not, please mind this security, this is only for demostration purpouse. Set this rules in your database rules.
 5. You have to add an API key for using Google Maps, if you haven't done this before, create a new project and select `Google Maps Activity` that will create an `.xml` file inside the `values` folder, look at it and follow the instructions there. When you are finish you can copy paste that key or the whole file to this project.
 6. Or maybe just [download the .APK](https://www.dropbox.com/s/qo6rij8icsug4o5/app-debug.apk?dl=0)

![Victory Dance](https://media.giphy.com/media/l41Yh18f5TbiWHE0o/giphy.gif)

## How this work under the hood
##### The problem

##### Will this delivery too much data?
I don't think so. That is why there is funnel node with the country code. Since you can't make double queries in Firebase, the longitude-latitude requirement is hard to met. A composed attribute `long_lat` could work but it will face the same problem is attempted to solved here. To get the near data, 1km is more or less 0.01 degrees. So, current latitude, plus 0.01 and less 0.01 will give you 1km arround. The problem is that is not constrained by the other coordinate, so you get the entire world data. That could be a lot, and that cannot be solved by the compose attribute aproach, because the concatenation will also give you unexpected extra data. By this aproach, you limit whatever query only to a country reducing the ammount of data to something quickly transmitable for Firebase.

##### Experiments results

## Thanks to
 - The people in [GeoFire](https://github.com/firebase/geofire-java) I was very inspired by their work
 - The people in [Firebase-Ui-Android](https://github.com/firebase/FirebaseUI-Android) I learned a lot of what I use here looking at their `FirebaseRecyclerAdapter` and the `FirebaseIndexRecyclerAdapter`
 - [Cristian Vidal](https://github.com/Himuravidal) we was working together on solving this prior to coming with the idea of this humble library

## What's next?
 - Something like a `FirebaseRecyclerAdapter` for Google Maps would be really cool
 - Since this is for maps mostly, there should be some utility to adjust Google Map zoom based on the radius distance
 - Better gifs! Better quality, full hd! Bigger, 2400px width! But most important, the dankest gif in the internet!

**Suggestions and PRs are welcome**