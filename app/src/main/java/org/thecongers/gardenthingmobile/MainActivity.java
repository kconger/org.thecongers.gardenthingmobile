package org.thecongers.gardenthingmobile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.thecongers.gardenthingmobile.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private TextView mStatus;
    private TextView mTemperature;
    private TextView mHumidity;
    private TextView mAmbientLight;
    private TextView mIrLight;
    private TextView mLux;
    private TextView mCo2;
    private TextView mSoilMoistureLabel1;
    private TextView mSoilMoistureLabel2;
    private TextView mSoilMoistureLabel3;
    private TextView mSoilMoisture1;
    private TextView mSoilMoisture2;
    private TextView mSoilMoisture3;
    private SharedPreferences sharedPrefs;
    private static final int SETTINGS_RESULT = 1;
    private String host;

    private static final String TAG = "GardenThingMobile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        mStatus = (TextView) findViewById(R.id.tv_status);
        mTemperature = (TextView) findViewById(R.id.tv_temperature);
        mHumidity = (TextView) findViewById(R.id.tv_humidity);
        mAmbientLight = (TextView) findViewById(R.id.tv_ambient);
        mIrLight = (TextView) findViewById(R.id.tv_ir);
        mLux = (TextView) findViewById(R.id.tv_lux);
        mCo2 = (TextView) findViewById(R.id.tv_co2);
        mSoilMoistureLabel1 = (TextView) findViewById(R.id.tv_soil_moisture1_label);
        mSoilMoistureLabel2 = (TextView) findViewById(R.id.tv_soil_moisture2_label);
        mSoilMoistureLabel3 = (TextView) findViewById(R.id.tv_soil_moisture3_label);
        mSoilMoisture1 = (TextView) findViewById(R.id.tv_soil_moisture1);
        mSoilMoisture2 = (TextView) findViewById(R.id.tv_soil_moisture2);
        mSoilMoisture3 = (TextView) findViewById(R.id.tv_soil_moisture3);

        refresh();
    }

    // Build http query and query GardenThing
    private void makeGardenThingQuery(String query) {
        URL groveGardenUrl = NetworkUtils.buildUrl(host,query);
        new GardenThingQueryTask().execute(groveGardenUrl);
    }

    public class GardenThingQueryTask extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(URL... params) {
            URL searchUrl = params[0];
            String username = sharedPrefs.getString("prefUsername", "gardener");
            String password = sharedPrefs.getString("prefPassword", "secret");
            String groveGardenResults = null;
            try {
                groveGardenResults = NetworkUtils.getResponseFromHttpUrl(searchUrl,username,password);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return groveGardenResults;
        }

        @Override
        protected void onPostExecute(String gardenThingResults) {
            if (gardenThingResults != null && !gardenThingResults.equals("")) {
                String temperature = "";
                String temperatureUnit = "C";
                String humidity = "";
                String humidityUnit = "%";
                String ambient = "";
                String ir = "";
                String lux = "";
                String co2 = "";
                String co2Unit = "ppm";
                String soilMoisture1Label = "";
                String soilMoisture2Label = "";
                String soilMoisture3Label = "";
                String soilMoisture1 = "";
                String soilMoisture2 = "";
                String soilMoisture3 = "";

                try {
                    JSONObject jObject = new JSONObject(gardenThingResults);
                    temperature = jObject.getString("temperature");
                    humidity = jObject.getString("humidity");
                    ambient = jObject.getString("ambient");
                    ir = jObject.getString("ir");
                    lux = jObject.getString("lux");
                    co2 = jObject.getString("co2");
                    soilMoisture1Label = jObject.getString("moisturesensor1label");
                    soilMoisture2Label = jObject.getString("moisturesensor2label");
                    soilMoisture3Label = jObject.getString("moisturesensor3label");
                    soilMoisture1 = jObject.getString("moisturesensor1");
                    soilMoisture2 = jObject.getString("moisturesensor2");
                    soilMoisture3 = jObject.getString("moisturesensor3");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (sharedPrefs.getBoolean("prefFahrenheit", false)) {
                    temperatureUnit = "F";
                    float temperatureC = convertCtoF(Double.parseDouble(temperature));
                    temperature = Integer.toString((int) Math.round(temperatureC));
                }
                mTemperature.setText(temperature + temperatureUnit);
                mHumidity.setText(humidity + humidityUnit);
                mAmbientLight.setText(ambient);
                mIrLight.setText(ir);
                mLux.setText(lux);
                mCo2.setText(co2 + " " + co2Unit);
                mSoilMoistureLabel1.setText(soilMoisture1Label);
                mSoilMoistureLabel2.setText(soilMoisture2Label);
                mSoilMoistureLabel3.setText(soilMoisture3Label);
                mSoilMoisture1.setText(soilMoisture1);
                mSoilMoisture2.setText(soilMoisture2);
                mSoilMoisture3.setText(soilMoisture3);
            } else {
                mStatus.setText(getString(R.string.error_message));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_gt_settings:
                // GardenThing settings was selected
                if (!host.equals("")) {
                    Intent startGTSettingsActivityIntent = new Intent(MainActivity.this, GTSettingsActivity.class);
                    startActivityForResult(startGTSettingsActivityIntent, SETTINGS_RESULT);
                } else {
                    Toast.makeText(getBaseContext(), getString(R.string.host_message), Toast.LENGTH_LONG).show();
                }
                return true;
            case R.id.action_settings:
                // Settings was selected
                Intent startSettingsActivityIntent = new Intent(MainActivity.this,  UserSettingActivity.class);
                startActivityForResult(startSettingsActivityIntent, SETTINGS_RESULT);
                return true;
            case R.id.action_refresh:
                // Refresh was selected
                refresh();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Runs when settings are updated
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==SETTINGS_RESULT)
        {
            refresh();
        }
    }

    // Refresh current values
    private void refresh()
    {
        host = sharedPrefs.getString("prefHost", "");
        if (host.equals("")) {
            mStatus.setText(getString(R.string.host_message));
            mTemperature.setClickable(false);
            mHumidity.setClickable(false);
            mAmbientLight.setClickable(false);
            mIrLight.setClickable(false);
            mLux.setClickable(false);
            mCo2.setClickable(false);
            mSoilMoisture1.setClickable(false);
            mSoilMoisture2.setClickable(false);
            mSoilMoisture3.setClickable(false);
        } else {
            mStatus.setText("");
            mTemperature.setClickable(true);
            mHumidity.setClickable(true);
            mAmbientLight.setClickable(true);
            mIrLight.setClickable(true);
            mLux.setClickable(true);
            mCo2.setClickable(true);
            mSoilMoisture1.setClickable(true);
            mSoilMoisture2.setClickable(true);
            mSoilMoisture3.setClickable(true);
            makeGardenThingQuery("overview");
        }
    }

    public void onClickTemperature(View v) {

        Context context = MainActivity.this;
        Class destinationActivity = TemperatureActivity.class;
        Intent startChildActivityIntent = new Intent(context, destinationActivity);
        startActivity(startChildActivityIntent);

    }
    public void onClickHumidity(View v) {

        Context context = MainActivity.this;
        Class destinationActivity = HumidityActivity.class;
        Intent startChildActivityIntent = new Intent(context, destinationActivity);
        startActivity(startChildActivityIntent);

    }
    public void onClickAmbient(View v) {

        Context context = MainActivity.this;
        Class destinationActivity = AmbientActivity.class;
        Intent startChildActivityIntent = new Intent(context, destinationActivity);
        startActivity(startChildActivityIntent);

    }
    public void onClickIr(View v) {

        Context context = MainActivity.this;
        Class destinationActivity = IrActivity.class;
        Intent startChildActivityIntent = new Intent(context, destinationActivity);
        startActivity(startChildActivityIntent);

    }
    public void onClickLux(View v) {

        Context context = MainActivity.this;
        Class destinationActivity = LuxActivity.class;
        Intent startChildActivityIntent = new Intent(context, destinationActivity);
        startActivity(startChildActivityIntent);

    }
    public void onClickCo2(View v) {

        Context context = MainActivity.this;
        Class destinationActivity = Co2Activity.class;
        Intent startChildActivityIntent = new Intent(context, destinationActivity);
        startActivity(startChildActivityIntent);

    }
    public void onClickSoilMoisture1(View v) {

        Context context = MainActivity.this;
        Class destinationActivity = SoilMoistureActivity.class;
        Intent startChildActivityIntent = new Intent(context, destinationActivity);
        Bundle b = new Bundle();
        b.putInt("key", 1);
        startChildActivityIntent.putExtras(b);
        startActivity(startChildActivityIntent);

    }
    public void onClickSoilMoisture2(View v) {

        Context context = MainActivity.this;
        Class destinationActivity = SoilMoistureActivity.class;
        Intent startChildActivityIntent = new Intent(context, destinationActivity);
        Bundle b = new Bundle();
        b.putInt("key", 2);
        startChildActivityIntent.putExtras(b);
        startActivity(startChildActivityIntent);

    }
    public void onClickSoilMoisture3(View v) {

        Context context = MainActivity.this;
        Class destinationActivity = SoilMoistureActivity.class;
        Intent startChildActivityIntent = new Intent(context, destinationActivity);
        Bundle b = new Bundle();
        b.putInt("key", 3);
        startChildActivityIntent.putExtras(b);
        startActivity(startChildActivityIntent);

    }

    private float convertCtoF(double temp) {
        return (float) temp * 9 / 5 + 32;
    }
}