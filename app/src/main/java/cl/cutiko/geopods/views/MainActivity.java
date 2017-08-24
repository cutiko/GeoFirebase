package cl.cutiko.geopods.views;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.View;

import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

import cl.cutiko.geopods.R;
import cl.cutiko.geopods.adapters.SectionsPagerAdapter;
import cl.cutiko.geopods.data.Nodes;
import cl.cutiko.geopods.models.GeoPod;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        ViewPager viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double latitude = -33.434874;
                double longitude = -70.603061;
                String name = "this is a great test!";
                TelephonyManager telephonyManager = (TelephonyManager) MainActivity.this.getSystemService(TELEPHONY_SERVICE);
                String countryIso = telephonyManager.getSimCountryIso();
                String key = new Nodes().locations(countryIso).push().getKey();

                GeoPod reduced = new GeoPod();
                reduced.setLatitude(latitude);
                reduced.setLongitude(longitude);
                reduced.setName(name);
                reduced.setKey(key);

                GeoPod geoPod = new GeoPod();
                geoPod.setLatitude(latitude);
                geoPod.setLongitude(longitude);
                geoPod.setName(name);
                geoPod.setCategory("category1");
                geoPod.setKey(key);

                DatabaseReference reference = new Nodes().getRoot();
                Map<String, Object> map = new HashMap<>();
                map.put("locations/"+countryIso+"/"+key, reduced);
                map.put("places/"+countryIso+"/"+key, geoPod);

                reference.updateChildren(map);


            }
        });

    }


}
