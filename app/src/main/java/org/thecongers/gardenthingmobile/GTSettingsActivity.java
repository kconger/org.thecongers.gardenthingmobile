package org.thecongers.gardenthingmobile;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.thecongers.gardenthingmobile.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

public class GTSettingsActivity extends AppCompatActivity {

    private SharedPreferences sharedPrefs;

    private EditText mWebPassword;
    private EditText mWebPassword2;
    private Spinner mSensorsPollingPeriod;
    private CheckBox mSensorsCO2;
    private CheckBox mSensorsLight;
    private CheckBox mSensorsTH;
    private Spinner mSensorsTHPort;
    private CheckBox mSensorsFahrenheit;
    private CheckBox mSensorsMoisture1;
    private EditText mSensorsMoisture1Label;
    private CheckBox mSensorsMoisture2;
    private EditText mSensorsMoisture2Label;
    private CheckBox mSensorsMoisture3;
    private EditText mSensorsMoisture3Label;
    private CheckBox mAlertsMoisture;
    private EditText mAlertsMoistureLow;
    private EditText mAlertsMoistureHigh;
    private CheckBox mAlertsTemperature;
    private EditText mAlertsTemperatureLow;
    private EditText mAlertsTemperatureHigh;
    private CheckBox mAlertsHumidity;
    private EditText mAlertsHumidityLow;
    private EditText mAlertsHumidityHigh;
    private CheckBox mAlertsLights;
    private EditText mAlertsLightsLow;
    private EditText mAlertsLightsHigh;
    private CheckBox mAlertsCO2;
    private EditText mAlertsCO2Low;
    private EditText mAlertsCO2High;

    private String host;
    private HashMap<String, String> postData;
    private String dialogSelection;

    private static final String TAG = "GardenThingMobile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gtsettings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        host = sharedPrefs.getString("prefHost", "");
        postData = new HashMap<String, String>();

        mWebPassword = (EditText) findViewById(R.id.et_password);
        mWebPassword2 = (EditText) findViewById(R.id.et_password2);
        mSensorsPollingPeriod = (Spinner) findViewById(R.id.sp_sensors_polling_period);
        mSensorsCO2 = (CheckBox) findViewById(R.id.cb_sensors_co2);
        mSensorsLight = (CheckBox) findViewById(R.id.cb_sensors_light);
        mSensorsTH = (CheckBox) findViewById(R.id.cb_sensors_th);
        mSensorsTHPort = (Spinner) findViewById(R.id.sp_sensors_th_port);
        mSensorsFahrenheit = (CheckBox) findViewById(R.id.cb_sensors_temp_unit);
        mSensorsMoisture1 = (CheckBox) findViewById(R.id.cb_sensors_moisture1);
        mSensorsMoisture1Label = (EditText) findViewById(R.id.et_sensors_moisture1_label);
        mSensorsMoisture2 = (CheckBox) findViewById(R.id.cb_sensors_moisture2);
        mSensorsMoisture2Label = (EditText) findViewById(R.id.et_sensors_moisture2_label);
        mSensorsMoisture3 = (CheckBox) findViewById(R.id.cb_sensors_moisture3);
        mSensorsMoisture3Label = (EditText) findViewById(R.id.et_sensors_moisture3_label);
        mAlertsMoisture = (CheckBox) findViewById(R.id.cb_alerts_moisture);
        mAlertsMoistureLow = (EditText) findViewById(R.id.et_alerts_moisture_low);
        mAlertsMoistureHigh = (EditText) findViewById(R.id.et_alerts_moisture_high);
        mAlertsTemperature = (CheckBox) findViewById(R.id.cb_alerts_temperature);
        mAlertsTemperatureLow = (EditText) findViewById(R.id.et_alerts_temperature_low);
        mAlertsTemperatureHigh = (EditText) findViewById(R.id.et_alerts_temperature_high);
        mAlertsHumidity = (CheckBox) findViewById(R.id.cb_alerts_humidity);
        mAlertsHumidityLow = (EditText) findViewById(R.id.et_alerts_humidity_low);
        mAlertsHumidityHigh = (EditText) findViewById(R.id.et_alerts_humidity_high);
        mAlertsLights = (CheckBox) findViewById(R.id.cb_alerts_light);
        mAlertsLightsLow = (EditText) findViewById(R.id.et_alerts_light_low);
        mAlertsLightsHigh = (EditText) findViewById(R.id.et_alerts_light_high);
        mAlertsCO2 = (CheckBox) findViewById(R.id.cb_alerts_co2);
        mAlertsCO2Low = (EditText) findViewById(R.id.et_alerts_co2_low);
        mAlertsCO2High = (EditText) findViewById(R.id.et_alerts_co2_high);

        makeGardenThingQuery("settings");

        //TODO: enable/disable widgets dynamically

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mWebPassword.getText().toString().equals(mWebPassword2.getText().toString())){
                    postData.put("webpassword",mWebPassword.getText().toString());
                    postData.put("moisturesensor1label",mSensorsMoisture1Label.getText().toString());
                    postData.put("moisturesensor2label",mSensorsMoisture2Label.getText().toString());
                    postData.put("moisturesensor3label",mSensorsMoisture3Label.getText().toString());
                    postData.put("co2alerthigh",mAlertsCO2High.getText().toString());
                    postData.put("co2alertlow",mAlertsCO2Low.getText().toString());
                    if (mAlertsCO2.isChecked()) {
                        postData.put("co2alerts","true");
                    } else {
                        postData.put("co2alerts","false");
                    }
                    if (mSensorsCO2.isChecked()) {
                        postData.put("co2sensor","true");
                    } else {
                        postData.put("co2sensor","false");
                    }
                    if (mSensorsFahrenheit.isChecked()) {
                        postData.put("temperatureunit","true");
                    } else {
                        postData.put("co2alerts","false");
                    }
                    postData.put("humidityalerthigh",mAlertsHumidityHigh.getText().toString());
                    postData.put("humidityalertlow",mAlertsHumidityLow.getText().toString());
                    if (mAlertsHumidity.isChecked()) {
                        postData.put("humidityalerts","true");
                    } else {
                        postData.put("humidityalerts","false");
                    }
                    postData.put("lightalerthigh",mAlertsLightsHigh.getText().toString());
                    postData.put("lightalertlow",mAlertsLightsLow.getText().toString());
                    if (mAlertsLights.isChecked()) {
                        postData.put("lightalerts","true");
                    } else {
                        postData.put("lightalerts","false");
                    }
                    if (mSensorsLight.isChecked()) {
                        postData.put("lightsensor","true");
                    } else {
                        postData.put("lightsensor","false");
                    }
                    postData.put("moisturealerthigh",mAlertsMoistureHigh.getText().toString());
                    postData.put("moisturealertlow",mAlertsMoistureLow.getText().toString());
                    if (mAlertsMoisture.isChecked()) {
                        postData.put("moisturealerts","true");
                    } else {
                        postData.put("moisturealerts","false");
                    }
                    if (mSensorsMoisture1.isChecked()) {
                        postData.put("moisturesensor1","true");
                    } else {
                        postData.put("moisturesensor1","false");
                    }
                    if (mSensorsMoisture2.isChecked()) {
                        postData.put("moisturesensor2","true");
                    } else {
                        postData.put("moisturesensor2","false");
                    }
                    if (mSensorsMoisture3.isChecked()) {
                        postData.put("moisturesensor3","true");
                    } else {
                        postData.put("moisturesensor3","false");
                    }
                    postData.put("interval", String.valueOf(mSensorsPollingPeriod.getSelectedItem()));
                    postData.put("temperaturealerthigh",mAlertsTemperatureHigh.getText().toString());
                    postData.put("temperaturealertlow",mAlertsTemperatureLow.getText().toString());
                    if (mAlertsTemperature.isChecked()) {
                        postData.put("temperaturealerts","true");
                    } else {
                        postData.put("temperaturealerts","false");
                    }
                    if (mSensorsTH.isChecked()) {
                        postData.put("thsensor","true");
                    } else {
                        postData.put("thsensor","false");
                    }
                    postData.put("thsensorport", String.valueOf(mSensorsTHPort.getSelectedItem()));

                    makeGardenThingPost();
                } else {
                    Toast.makeText(getBaseContext(), getString(R.string.password_message), Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gtsettings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_purge_database:
                dialogSelection = "purge_database";
                AlertDialog.Builder alertPurgeDatabase = new AlertDialog.Builder(this);
                alertPurgeDatabase.setMessage(getString(R.string.alert_message_purge_database)).setPositiveButton(getString(R.string.alert_message_yes), dialogClickListener)
                        .setNegativeButton(getString(R.string.alert_message_no), dialogClickListener).show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    //Yes button clicked
                    if (dialogSelection.equals("purge_database")){
                        makeGardenThingQuery("purge_database");
                    }
                    dialogSelection = "";
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    dialogSelection = "";
                    break;
            }
        }
    };

    /**
     * This method constructs the URL (using {@link NetworkUtils}) for the github repository you'd
     * like to find, displays that URL in a TextView, and finally fires off an AsyncTask to perform
     * the GET request using our {@link MainActivity.GardenThingQueryTask}
     */
    private void makeGardenThingQuery(String query) {
        URL gardenThingUrl = NetworkUtils.buildUrl(host,query);
        new GardenThingQueryTask().execute(gardenThingUrl);
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
            String gardenThingResults = null;
            try {
                gardenThingResults = NetworkUtils.getResponseFromHttpUrl(searchUrl,username,password);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return gardenThingResults;
        }

        @Override
        protected void onPostExecute(String gardenThingResults) {
            if (gardenThingResults != null && !gardenThingResults.equals("")) {
                String web_password = "";
                String sensors_polling_period = "";
                String sensors_co2 = "";
                String sensors_light = "";
                String sensors_th = "";
                String sensors_th_port = "";
                String sensors_temperature_unit = "";
                String sensors_moisture1 = "";
                String sensors_moisture1_label = "";
                String sensors_moisture2 = "";
                String sensors_moisture2_label = "";
                String sensors_moisture3 = "";
                String sensors_moisture3_label = "";
                String alerts_moisture = "";
                String alerts_moisture_low = "";
                String alerts_moisture_high = "";
                String alerts_temperature = "";
                String alerts_temperature_low = "";
                String alerts_temperature_high = "";
                String alerts_humidity = "";
                String alerts_humidity_low = "";
                String alerts_humidity_high = "";
                String alerts_lights = "";
                String alerts_lights_low = "";
                String alerts_lights_high = "";
                String alerts_co2 = "";
                String alerts_co2_low = "";
                String alerts_co2_high = "";

                try {
                    JSONObject jObject = new JSONObject(gardenThingResults);
                    web_password = jObject.getString("webpassword");
                    sensors_polling_period = jObject.getString("interval");
                    sensors_co2 = jObject.getString("co2sensor");
                    sensors_light = jObject.getString("lightsensor");
                    sensors_th = jObject.getString("thsensor");
                    sensors_th_port = jObject.getString("thsensorport");
                    sensors_temperature_unit = jObject.getString("temperatureunit");
                    sensors_moisture1 = jObject.getString("moisturesensor1");
                    sensors_moisture1_label = jObject.getString("moisturesensor1label");
                    sensors_moisture2 = jObject.getString("moisturesensor2");
                    sensors_moisture2_label = jObject.getString("moisturesensor2label");
                    sensors_moisture3 = jObject.getString("moisturesensor3");
                    sensors_moisture3_label = jObject.getString("moisturesensor3label");
                    alerts_moisture = jObject.getString("moisturealerts");
                    alerts_moisture_low = jObject.getString("moisturealertlow");
                    alerts_moisture_high = jObject.getString("moisturealerthigh");
                    alerts_temperature = jObject.getString("temperaturealerts");
                    alerts_temperature_low = jObject.getString("temperaturealertlow");
                    alerts_temperature_high = jObject.getString("temperaturealerthigh");
                    alerts_humidity = jObject.getString("humidityalerts");
                    alerts_humidity_low = jObject.getString("humidityalertlow");
                    alerts_humidity_high = jObject.getString("humidityalerthigh");
                    alerts_lights = jObject.getString("lightalerts");
                    alerts_lights_low = jObject.getString("lightalertlow");
                    alerts_lights_high = jObject.getString("lightalerthigh");
                    alerts_co2 = jObject.getString("co2alerts");
                    alerts_co2_low = jObject.getString("co2alertlow");
                    alerts_co2_high = jObject.getString("co2alerthigh");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mWebPassword.setText(web_password);
                mWebPassword2.setText(web_password);
                mSensorsPollingPeriod.setSelection(getIndex(mSensorsPollingPeriod, sensors_polling_period));
                if (sensors_co2.equals("true")){
                    mSensorsCO2.setChecked(true);
                } else {
                    mSensorsCO2.setChecked(false);
                }
                if (sensors_light.equals("true")){
                    mSensorsLight.setChecked(true);
                } else {
                    mSensorsLight.setChecked(false);
                }
                if (sensors_th.equals("true")){
                    mSensorsTH.setChecked(true);
                    mSensorsTHPort.setEnabled(true);
                    mSensorsFahrenheit.setEnabled(true);
                } else {
                    mSensorsTH.setChecked(false);
                    mSensorsTHPort.setEnabled(false);
                    mSensorsFahrenheit.setEnabled(false);
                }
                if (sensors_temperature_unit.equals("true")){
                    mSensorsFahrenheit.setChecked(true);
                } else {
                    mSensorsFahrenheit.setChecked(false);
                }
                mSensorsTHPort.setSelection(getIndex(mSensorsTHPort, sensors_th_port));
                if (sensors_moisture1.equals("true")){
                    mSensorsMoisture1.setChecked(true);
                    mSensorsMoisture1Label.setEnabled(true);
                } else {
                    mSensorsMoisture1.setChecked(false);
                    mSensorsMoisture1Label.setEnabled(false);
                }
                mSensorsMoisture1Label.setText(sensors_moisture1_label);
                if (sensors_moisture2.equals("true")){
                    mSensorsMoisture2.setChecked(true);
                    mSensorsMoisture2Label.setEnabled(true);
                } else {
                    mSensorsMoisture2.setChecked(false);
                    mSensorsMoisture2Label.setEnabled(false);
                }
                mSensorsMoisture2Label.setText(sensors_moisture2_label);
                if (sensors_moisture3.equals("true")){
                    mSensorsMoisture3.setChecked(true);
                    mSensorsMoisture3Label.setEnabled(true);
                } else {
                    mSensorsMoisture3.setChecked(false);
                    mSensorsMoisture3Label.setEnabled(false);
                }
                mSensorsMoisture3Label.setText(sensors_moisture3_label);
                if (alerts_moisture.equals("true")){
                    mAlertsMoisture.setChecked(true);
                    mAlertsMoistureLow.setEnabled(true);
                    mAlertsMoistureHigh.setEnabled(true);
                } else {
                    mAlertsMoisture.setChecked(false);
                    mAlertsMoistureLow.setEnabled(false);
                    mAlertsMoistureHigh.setEnabled(false);
                }
                mAlertsMoistureLow.setText(alerts_moisture_low);
                mAlertsMoistureHigh.setText(alerts_moisture_high);
                if (alerts_temperature.equals("true")){
                    mAlertsTemperature.setChecked(true);
                    mAlertsTemperatureLow.setEnabled(true);
                    mAlertsTemperatureHigh.setEnabled(true);
                } else {
                    mAlertsTemperature.setChecked(false);
                    mAlertsTemperatureLow.setEnabled(false);
                    mAlertsTemperatureHigh.setEnabled(false);
                }
                mAlertsTemperatureLow.setText(alerts_temperature_low);
                mAlertsTemperatureHigh.setText(alerts_temperature_high);
                if (alerts_humidity.equals("true")){
                    mAlertsHumidity.setChecked(true);
                    mAlertsHumidityLow.setEnabled(true);
                    mAlertsHumidityHigh.setEnabled(true);
                } else {
                    mAlertsHumidity.setChecked(false);
                    mAlertsHumidityLow.setEnabled(false);
                    mAlertsHumidityHigh.setEnabled(false);
                }
                mAlertsHumidityLow.setText(alerts_humidity_low);
                mAlertsHumidityHigh.setText(alerts_humidity_high);
                if (alerts_lights.equals("true")){
                    mAlertsLights.setChecked(true);
                    mAlertsLightsLow.setEnabled(true);
                    mAlertsLightsHigh.setEnabled(true);
                } else {
                    mAlertsLights.setChecked(false);
                    mAlertsLightsLow.setEnabled(false);
                    mAlertsLightsHigh.setEnabled(false);
                }
                mAlertsLightsLow.setText(alerts_lights_low);
                mAlertsLightsHigh.setText(alerts_lights_high);
                if (alerts_co2.equals("true")){
                    mAlertsCO2.setChecked(true);
                    mAlertsCO2Low.setEnabled(true);
                    mAlertsCO2High.setEnabled(true);
                } else {
                    mAlertsCO2.setChecked(false);
                    mAlertsCO2Low.setEnabled(false);
                    mAlertsCO2High.setEnabled(false);
                }
                mAlertsCO2Low.setText(alerts_co2_low);
                mAlertsCO2High.setText(alerts_co2_high);
            } else {
                Toast.makeText(getBaseContext(), getString(R.string.error_message),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void makeGardenThingPost() {
        URL gardenThingUrl = NetworkUtils.buildUrl(host,"settings");
        new GardenThingPostTask().execute(gardenThingUrl);
    }

    private class GardenThingPostTask extends AsyncTask<URL, Void, String> {
        @Override
        protected String doInBackground(URL... params) {

            URL searchUrl = params[0];
            String username = sharedPrefs.getString("prefUsername", "gardener");
            String password = sharedPrefs.getString("prefPassword", "secret");
            String gardenThingResults = null;

            try {
                gardenThingResults = NetworkUtils.postJSONToHttpUrl(searchUrl,username,password,postData);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return gardenThingResults;
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Log.d(TAG,"POST Result: " + result);
            try {
                JSONObject jObject = new JSONObject(result);
                String statusCode = jObject.getString("status");
                if (statusCode.equals("200")){
                    SharedPreferences.Editor editor = sharedPrefs.edit();
                    editor.putString("prefPassword", mWebPassword.getText().toString());
                    editor.commit();
                    Toast.makeText(getBaseContext(), getString(R.string.saved_message), Toast.LENGTH_LONG).show();
                } else if (statusCode.equals("500")) {
                    Toast.makeText(getBaseContext(), getString(R.string.error_saving_passwords_message), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getBaseContext(), getString(R.string.saving_error_message), Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                Toast.makeText(getBaseContext(), getString(R.string.saving_error_message), Toast.LENGTH_LONG).show();
            }
        }
    }

    private int getIndex(Spinner spinner, String myString){
        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).equals(myString)){
                index = i;
            }
        }
        return index;
    }
}
