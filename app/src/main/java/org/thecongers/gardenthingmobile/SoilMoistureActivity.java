package org.thecongers.gardenthingmobile;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.androidplot.xy.CatmullRomInterpolator;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.thecongers.gardenthingmobile.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;


public class SoilMoistureActivity extends AppCompatActivity {

    private ProgressBar mLoadingIndicator;
    private XYPlot plot;
    private SharedPreferences sharedPrefs;
    private String host;
    private String query;
    Toast mToast;
    Number[] dateArray;
    Number[] valueArray;

    private static final String TAG = "GardenThingMobile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getIntent().getExtras();
        int sensor = -1;
        if(b != null)
            sensor = b.getInt("key");
        setContentView(R.layout.activity_soil_moisture);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        plot = (XYPlot) findViewById(R.id.plot);

        // query for data
        host = sharedPrefs.getString("prefHost", "");
        Log.d(TAG,"Host is: " + host);
        Log.d(TAG,"Sensor is #" + sensor);

        if (!host.equals("")) {
            if (sensor == 1) {
                query = "soilmoisture1";
            } else if (sensor == 2) {
                query = "soilmoisture2";
            } else if (sensor == 3) {
                query = "soilmoisture3";
            }
            makeGardenThingQuery(query);
        }

    }

    /**
     * This method constructs the URL (using {@link NetworkUtils}) for the github repository you'd
     * like to find, displays that URL in a TextView, and finally fires off an AsyncTask to perform
     * the GET request using our {@link MainActivity.GardenThingQueryTask}
     */
    private void makeGardenThingQuery(String query) {
        URL gardenThingUrl = NetworkUtils.buildUrl(host,query);
        new SoilMoistureActivity.GardenThingQueryTask().execute(gardenThingUrl);
    }

    public class GardenThingQueryTask extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
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
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            plot.setVisibility(View.VISIBLE);
            if (gardenThingResults != null && !gardenThingResults.equals("")) {
                Log.d(TAG,"Results: " + gardenThingResults);

                String label = "";
                JSONArray jArray;

                try {
                    JSONObject jObject = new JSONObject(gardenThingResults.substring(gardenThingResults.indexOf("{"), gardenThingResults.lastIndexOf("}") + 1));
                    JSONObject valueObject = jObject.getJSONObject(query);
                    label = valueObject.getString("label");
                    jArray = valueObject.getJSONArray("values");
                    valueArray = new Number[jArray.length()];
                    dateArray = new Number[jArray.length()];
                    for (int i=0; i < jArray.length(); i++)
                    {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        Date date;
                        try {
                            // Pulling items from the jsonArray
                            JSONObject valuesObject = jArray.getJSONObject(i);
                            String dateStamp = valuesObject.getString("date");
                            String value = valuesObject.getString("value");
                            valueArray[i] = Float.parseFloat(value);
                            date = sdf.parse(dateStamp);
                            dateArray[i] = date.getTime();

                        } catch (JSONException | ParseException e){
                            e.printStackTrace();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // TODO: Move to strings.xml
                plot.setTitle(label + " Moisture");

                // turn the above arrays into XYSeries':
                // (Y_VALS_ONLY means use the element index as the x value)
                // TODO: Move to strings.xml
                XYSeries series1 = new SimpleXYSeries(
                        Arrays.asList(dateArray), Arrays.asList(valueArray), "Moisture");

                // create formatters to use for drawing a series using LineAndPointRenderer
                // and configure them from xml:
                LineAndPointFormatter series1Format =
                        new LineAndPointFormatter(SoilMoistureActivity.this, R.xml.line_point_formatter_with_labels);

                // just for fun, add some smoothing to the lines:
                // see: http://androidplot.com/smooth-curves-and-androidplot/
                series1Format.setInterpolationParams(
                        new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));
                series1Format.setLegendIconEnabled(false);

                // add a new series' to the xyplot:
                plot.addSeries(series1, series1Format);

                plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).
                        setFormat(new Format() {

                            private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd hh:mm");

                            @Override
                            public StringBuffer format(Object obj, StringBuffer toAppendTo,
                                                       FieldPosition pos) {

                                long timestamp = ((Number) obj).longValue();
                                Date date = new Date(timestamp);
                                return dateFormat.format(date, toAppendTo, pos);
                            }

                            @Override
                            public Object parseObject(String source, ParsePosition pos) {
                                return null;

                            }
                        });
            } else {
                if ( mToast != null) {
                    mToast.cancel();
                }
                mToast.makeText(getApplicationContext(), getString(R.string.error_message),
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}
