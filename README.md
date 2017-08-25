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
