package com.dsuapp.alarmtest001;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dsuapp.alarmtest001.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AlarmActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;

    TextView rainTv, rainValueTv, minTv, minValueTv, maxTv, maxValueTv, skyValueTv, dropTv;
//    TextView gps;

    public static int TO_GRID = 0;
    double Tx, Ty;
    String key = "rpMwTSh%2BVMD0HPIj8dl5E1MKHJAj89r1JHK7TGup%2FYZh8C0PtjIEgxnQltQcJ%2F%2BPl7wDRUVx4y104Y5fw%2B10jQ%3D%3D";

    private Calendar calendar;

    boolean inCat = false, inValue = false, inTime = false;

    String Cat = null, Value = null, Time = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        StrictMode.enableDefaults();

        this.calendar = Calendar.getInstance();
        Log.e(">>Check", "AlarmActivity Good");
        int a = calendar.HOUR_OF_DAY;
        Log.e(">>hourCheck", "h = " + a);

        rainTv = findViewById(R.id.rainTv);
        rainValueTv = findViewById(R.id.rainValueTv);
        minTv = findViewById(R.id.minTv);
        minValueTv = findViewById(R.id.minValueTv);
        maxTv = findViewById(R.id.maxTv);
        maxValueTv = findViewById(R.id.maxValueTv);
        skyValueTv = findViewById(R.id.skyValueTv);
        dropTv = findViewById(R.id.dropTv);
//        gps = findViewById(R.id.gps);


        final LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);

        final LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.e(">>Check", "GPS Good");
                if (Cat == null) {
                    Tx = location.getLatitude();
                    Ty = location.getLongitude();

                    Log.e(">>1", "x = " + Tx + ", y = " + Ty);

                    LatXLngY tmp3 = convertGRID_GPS(TO_GRID, Tx, Ty);


                    Log.e(">>2", "x = " + tmp3.x + ", y = " + tmp3.y);

                    String nX = String.format("%.0f", tmp3.x);
                    String nY = String.format("%.0f", tmp3.y);

//                boolean inCat = false, inValue = false, inTime = false;
//
//                String Cat = null, Value = null, Time = null;

//                gps.setText("x : "+Tx+" y : "+Ty+"\nx : "+nX+" y : "+nY);


                    // 날짜 구해서 url 세팅
                    SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMdd");
                    Date time = new Date();
                    String day = format1.format(time);

                    try {
//
                        URL url = new URL(
                                "http://apis.data.go.kr/1360000/VilageFcstInfoService/getVilageFcst?" +
                                        "serviceKey=" + key + "&pageNo=1&numOfRows=50&dataType=XML&base_date=" + day + "&base_time=" + time + "&nx=" + nX + "&ny=" + nY + "&");

                        XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
                        XmlPullParser parser = parserCreator.newPullParser();

                        parser.setInput(url.openStream(), null);

                        int parserEvent = parser.getEventType();
                        System.out.println("파싱시작합니다.");

                        while (parserEvent != XmlPullParser.END_DOCUMENT) {
                            switch (parserEvent) {
                                case XmlPullParser.START_TAG:
                                    if (parser.getName().equals("category")) {
                                        inCat = true;
                                    }
                                    if (parser.getName().equals("fcstValue")) {
                                        inValue = true;
                                    }
                                    if (parser.getName().equals("fcstTime")) {
                                        inTime = true;
                                    }
                                    break;

                                case XmlPullParser.TEXT:
                                    if (inCat) {
                                        Cat = parser.getText();
                                        inCat = false;
                                    }
                                    if (inValue) {
                                        Value = parser.getText();
                                        inValue = false;
                                    }
                                    if (inTime) {
                                        Time = parser.getText();
                                        inTime = false;
                                    }
                                    break;

                                case XmlPullParser.END_TAG:
                                    if (parser.getName().equals("item")) {
                                        if (Cat.equals("POP")) {
                                            rainTv.setText("강수확률  :  ");
                                            rainValueTv.setText(Value + "%");
                                            Log.e(">>Check", "강수확률 : " + Value);
                                        }
                                        if (Cat.equals("TMN")) {
                                            minTv.setText("최저기온  :  ");
                                            minValueTv.setText(Value + "℃");
                                            Log.e(">>Check", "최저기온 : " + Value);
                                        }
                                        if (Cat.equals("TMX")) {
                                            maxTv.setText("최고기온  :  ");
                                            maxValueTv.setText(Value + "℃");
                                        }

                                        if (Cat.equals("SKY")) {
                                            if (Time.equals("1200")) {
                                                if (Value.equals("1")) {
                                                    skyValueTv.setText("맑음");
                                                } else if (Value.equals("3")) {
                                                    skyValueTv.setText("구름 많음");
                                                } else if (Value.equals("4")) {
                                                    skyValueTv.setText("흐림");
                                                }
                                            }
                                        }
                                        if (Cat.equals("PTY")) {
                                            if (Time.equals("1200")) {
                                                if (Value.equals("0")) {
                                                    mediaPlayer = MediaPlayer.create(AlarmActivity.this, R.raw.sunny);
                                                    mediaPlayer.start();
                                                    dropTv.setText("비 없음");
                                                } else if (Value.equals("1") || Value.equals("2") || Value.equals("4")) {
                                                    dropTv.setText("비");
                                                    mediaPlayer = MediaPlayer.create(AlarmActivity.this, R.raw.rain);
                                                    mediaPlayer.start();
                                                } else if (Value.equals("3")) {
                                                    dropTv.setText("눈");
                                                    mediaPlayer = MediaPlayer.create(AlarmActivity.this, R.raw.snow);
                                                    mediaPlayer.start();
                                                }
                                            }
                                        }
                                        ImageView weatherIv = findViewById(R.id.weatherIv);
                                        if (dropTv.getText().equals("비 없음")) {
                                            if (skyValueTv.getText().equals("맑음")) {
                                                weatherIv.setImageResource(R.drawable.sunny);
                                            } else if (skyValueTv.getText().equals("구름 많음")) {
                                                weatherIv.setImageResource(R.drawable.cloudy1);
                                            } else if (skyValueTv.getText().equals("흐림")) {
                                                weatherIv.setImageResource(R.drawable.cloudy2);
                                            }
                                        } else if (dropTv.getText().equals("")) {
                                            weatherIv.setImageResource(R.drawable.rain);
                                        } else if (dropTv.getText().equals("")) {
                                            weatherIv.setImageResource(R.drawable.snow);
                                        }
                                    }
                                    break;
                            }
                            parserEvent = parser.next();

//                        locationManager.removeUpdates(locationListener);

                        }
                    } catch (Exception e) {
                        rainTv.setText("error");
                        rainValueTv.setText("error");
                        minTv.setText("error");
                        minValueTv.setText("error");
                        maxTv.setText("error");
                        maxValueTv.setText("error");
                        skyValueTv.setText("error");
                        dropTv.setText("error");
                    }
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };


        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(
                        this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                        this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                0, 0, locationListener);

        findViewById(R.id.btnClose).setOnClickListener(mClickListener);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // MediaPlayer release
        if (this.mediaPlayer != null) {
            this.mediaPlayer.release();
            this.mediaPlayer = null;
        }
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    /* 알람 종료 */
    private void close() {
        if (this.mediaPlayer.isPlaying()) {
            this.mediaPlayer.stop();
            this.mediaPlayer.release();
            this.mediaPlayer = null;
        }

        finish();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnClose:
                    // 알람 종료
                    close();
//                    onDestroy();

                    break;
            }
        }
    };

    private LatXLngY convertGRID_GPS(int mode, double lat_X, double lng_Y) {
        double RE = 6371.00877; // 지구 반경(km)
        double GRID = 5.0; // 격자 간격(km)
        double SLAT1 = 30.0; // 투영 위도1(degree)
        double SLAT2 = 60.0; // 투영 위도2(degree)
        double OLON = 126.0; // 기준점 경도(degree)
        double OLAT = 38.0; // 기준점 위도(degree)
        double XO = 43; // 기준점 X좌표(GRID)
        double YO = 136; // 기1준점 Y좌표(GRID)

        //
        // LCC DFS 좌표변환 ( code : "TO_GRID"(위경도->좌표, lat_X:위도,  lng_Y:경도), "TO_GPS"(좌표->위경도,  lat_X:x, lng_Y:y) )
        //


        double DEGRAD = Math.PI / 180.0;
        double RADDEG = 180.0 / Math.PI;

        double re = RE / GRID;
        double slat1 = SLAT1 * DEGRAD;
        double slat2 = SLAT2 * DEGRAD;
        double olon = OLON * DEGRAD;
        double olat = OLAT * DEGRAD;

        double sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) / Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn);
        double sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sf = Math.pow(sf, sn) * Math.cos(slat1) / sn;
        double ro = Math.tan(Math.PI * 0.25 + olat * 0.5);
        ro = re * sf / Math.pow(ro, sn);
        LatXLngY rs = new LatXLngY();

        if (mode == TO_GRID) {
            rs.lat = lat_X;
            rs.lng = lng_Y;
            double ra = Math.tan(Math.PI * 0.25 + (lat_X) * DEGRAD * 0.5);
            ra = re * sf / Math.pow(ra, sn);
            double theta = lng_Y * DEGRAD - olon;
            if (theta > Math.PI) theta -= 2.0 * Math.PI;
            if (theta < -Math.PI) theta += 2.0 * Math.PI;
            theta *= sn;
            rs.x = Math.floor(ra * Math.sin(theta) + XO + 0.5);
            rs.y = Math.floor(ro - ra * Math.cos(theta) + YO + 0.5);
        } else {
            rs.x = lat_X;
            rs.y = lng_Y;
            double xn = lat_X - XO;
            double yn = ro - lng_Y + YO;
            double ra = Math.sqrt(xn * xn + yn * yn);
            if (sn < 0.0) {
                ra = -ra;
            }
            double alat = Math.pow((re * sf / ra), (1.0 / sn));
            alat = 2.0 * Math.atan(alat) - Math.PI * 0.5;

            double theta = 0.0;
            if (Math.abs(xn) <= 0.0) {
                theta = 0.0;
            } else {
                if (Math.abs(yn) <= 0.0) {
                    theta = Math.PI * 0.5;
                    if (xn < 0.0) {
                        theta = -theta;
                    }
                } else theta = Math.atan2(xn, yn);
            }
            double alon = theta / sn + olon;
            rs.lat = alat * RADDEG;
            rs.lng = alon * RADDEG;
        }
        return rs;
    }


    class LatXLngY {
        public double lat;
        public double lng;

        public double x;
        public double y;

    }
}