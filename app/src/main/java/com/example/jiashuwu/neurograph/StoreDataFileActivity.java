package com.example.jiashuwu.neurograph;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import javax.mail.Store;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class StoreDataFileActivity extends AppCompatActivity {

    private Button button_generate;

    private MyDatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    private MyDatabaseHelper databaseHelperD;
    private SQLiteDatabase databaseD;

    private MyDatabaseHelper databaseHelper1;
    private SQLiteDatabase database1;

    private MyDatabaseHelper databaseHelper2;
    private SQLiteDatabase database2;

    public String output_file_name;
    public String output_csv_file_name;

    private String databaseName = DatabaseInformation.databaseName;
    private int databaseVersion = DatabaseInformation.databaseVersion;

    private int test_id;
    private int user_id;
    private String name;
    private String test_starting_time;
    private String test_ending_time;
    private String test_type;
    private String image_type;
    private int interval_duration;
    private int number_of_points;
    private int age;
    private String gender;
    private String education;
    private int rating_score;
    private String current_receive_treatment;
    private String timestamp_of_point;
    private float x;
    private float y;
    private float pressure;
    private float touch_point_size;
    private String test_starting_time_number_only;
    private String test_ending_time_number_only;


    public static Calendar calendar;
    public static int year;
    public static int month;
    public static int day;
    public static int hour;
    public static int minute;
    public static int second;
    public static int millisecond;
    public static String month_s;
    public static String day_s;
    public static String hour_s;
    public static String minute_s;
    public static String second_s;
    public static String millisecond_s;



    private ArrayList<String> output_csv_strings;

    public ProgressDialog progressDialog;

    public ProgressBar progressBar;
    public TextView percent_number_textview;
    public TextView numbers_textview;

    private String store_data_file_button_clicked = "";

    private Switch delete_test_data_switch;
    private boolean should_delete_test_data = true;

    public void initLocaleLanguage ()
    {
        Resources resource = getApplicationContext().getResources();
        Configuration configuration = resource.getConfiguration();
        DisplayMetrics displayMetrics = resource.getDisplayMetrics();
        Locale DUTCH = new Locale("nl", "NL");
        Locale PORTUGAL = new Locale("pt", "PT");
        Locale RUSSIA = new Locale("ru", "RU");
        Locale SPAIN = new Locale("es", "ES");
        switch (Sharing.language)
        {
            case "English": configuration.locale = Locale.UK;break;
            case "Simplified Chinese": configuration.locale = Locale.CHINA;break;
            case "Traditional Chinese": configuration.locale = Locale.TAIWAN;break;
            case "Dutch": configuration.locale = DUTCH;break;
            case "French": configuration.locale = Locale.FRANCE;break;
            case "German": configuration.locale = Locale.GERMANY;break;
            case "Italian": configuration.locale = Locale.ITALY;break;
            case "Portuguese": configuration.locale = PORTUGAL;break;
            case "Russian": configuration.locale = RUSSIA;break;
            case "Spanish": configuration.locale = SPAIN;break;
            default: configuration.locale = Locale.UK;break;
        }
        resource.updateConfiguration(configuration, displayMetrics);
        getBaseContext().getResources().updateConfiguration(configuration, null);
    }

    public void init_theme ()
    {
        switch (Sharing.colour)
        {
            case "blue": setTheme(R.style.AppTheme); break;
            case "light_blue": setTheme(R.style.AppThemeLightBlue); break;
            case "green": setTheme(R.style.AppThemeGreen); break;
            case "purple": setTheme(R.style.AppThemePurple); break;
            case "pink": setTheme(R.style.AppThemePink); break;
            case "orange": setTheme(R.style.AppThemeOrange); break;
            default:setTheme(R.style.AppTheme); break;
        }
    }

    private void goToSetting(){
        if (Build.VERSION.SDK_INT >= 26) {
            Intent intent = new Intent(Settings.ACTION_SETTINGS);
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("android.provider.extra.APP_PACKAGE", StoreDataFileActivity.this.getApplicationContext().getPackageName());
            startActivity(intent);
        } else if (Build.VERSION.SDK_INT >= 21 && Build.VERSION.SDK_INT < 26) {
            Intent intent = new Intent(Settings.ACTION_SETTINGS);
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("android.provider.extra.APP_PACKAGE", StoreDataFileActivity.this.getApplicationContext().getPackageName());
            intent.putExtra("app_uid", StoreDataFileActivity.this.getApplicationInfo().uid);
            startActivity(intent);
        } else if (Build.VERSION.SDK_INT < 21)
        {
            Intent intent = new Intent(Settings.ACTION_SETTINGS);
            intent.setData(Uri.fromParts("package", StoreDataFileActivity.this.getApplicationContext().getPackageName(), null));
            startActivity(intent);
        }
    }

    public int getNumber_of_test_in_total ()
    {
        int answer = 0;

        databaseHelper2 = new MyDatabaseHelper(this, databaseName, null, databaseVersion);
        databaseHelper2.getReadableDatabase();

        database2 = databaseHelper2.getReadableDatabase();

        String query = "SELECT COUNT () FROM Test";
        String [] parameters = new String[]{};
        Cursor cursor = database2.rawQuery(query, parameters);
        while (cursor.moveToNext())
        {
            answer = cursor.getInt(0);
        }

        return answer;
    }

    public int getNumber_of_item_in_total ()
    {
        databaseHelper1 = new MyDatabaseHelper (this, databaseName, null, databaseVersion);
        databaseHelper1.getReadableDatabase();

        database1 = databaseHelper1.getReadableDatabase();

        int answer = 0;

        String query = "SELECT number_of_points FROM Test";
        String [] parameter = new String [] {};
        Cursor cursor = database1.rawQuery(query, parameter);
        while (cursor.moveToNext())
        {
            answer = answer + cursor.getInt(0);
        }

        if (cursor != null)
        {
            cursor.close();
        }
        if (database1 != null)
        {
            database1.close();
        }
        if (databaseHelper1 != null)
        {
            databaseHelper1.close();
        }
        return answer;
    }

    public void showProgressDialog()
    {
        final ProgressDialog progressDialog = new ProgressDialog(StoreDataFileActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle("Generating file");
        progressDialog.setMax(100);
        progressDialog.setMessage("Generating files");
        progressDialog.show();
        Log.d("showing", "showing");
        new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                while (i < 100)
                {
                    try
                    {
                        Thread.sleep(200);
                        progressDialog.incrementProgressBy(1);
                        i++;
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                // progressDialog.dismiss();
            }
        }).start();
    }

    public String generate_string_from_database ()
    {
        databaseHelper = new MyDatabaseHelper (this, databaseName, null, databaseVersion);
        databaseHelper.getReadableDatabase();

        database = databaseHelper.getReadableDatabase();

        Sharing.number_of_item_finished = 0;

        String output_string = "";

        String time_year;
        String time_month;
        String time_day;
        String time_hour;
        String time_minute;
        String time_second;
        String time_millisecond = "";

        String query = "SELECT * FROM Test";
        String query1 = "";
        String query2 = "";
        String [] parameter = new String [] {};
        String [] parameter1;
        String [] parameter2;
        Log.d("NULLL", String.valueOf(parameter == null));

        Cursor cursor = database.rawQuery(query, new String[] {});
        Cursor cursor1;
        Cursor cursor2;

        output_csv_strings = new ArrayList<>();
        while (cursor.moveToNext())
        {
            test_id = cursor.getInt(0);
            user_id = cursor.getInt(1);
            test_starting_time = cursor.getString(2).toString();
            test_ending_time = cursor.getString(3).toString();
            test_type = cursor.getString(4).toString();
            image_type = cursor.getString(5).toString();
            interval_duration = Integer.parseInt(cursor.getString(6).toString());
            number_of_points = cursor.getInt(7);
            int a = 0;
            test_starting_time_number_only = "";
            test_ending_time_number_only = "";
            Log.d("STRINGTEST", test_starting_time);
            for (a = 0 ; a < test_starting_time.length() ; a++)
            {
                if (Character.isDigit(test_starting_time.charAt(a)))
                {
                    test_starting_time_number_only = test_starting_time_number_only + test_starting_time.charAt(a);
                }
            }
            Log.d("STRINGTEST", test_starting_time_number_only);
            Log.d("STRINGTEST", test_ending_time);
            for (a = 0 ; a < test_ending_time.length() ; a++)
            {
                if (Character.isDigit(test_ending_time.charAt(a)))
                {
                    test_ending_time_number_only = test_ending_time_number_only + test_ending_time.charAt(a);
                }
            }
            query1 = "SELECT * FROM User WHERE user_id = ?";
            parameter1 = new String [] {String.valueOf(user_id)};
            cursor1 = database.rawQuery(query1, new String [] {String.valueOf(user_id)});
            while (cursor1.moveToNext())
            {
                name = cursor1.getString(1).toString();
                age = Integer.parseInt(cursor1.getString(2).toString());
                gender = cursor1.getString(3).toString();
                education = cursor1.getString(4).toString();
                rating_score = Integer.parseInt(cursor1.getString(5).toString());
                current_receive_treatment = cursor1.getString(6).toString();
            }
            if (cursor1 != null)
            {
                cursor1.close();
            }

            output_string = output_string + "test_id = " + String.valueOf(test_id) + "\n";
            // output_csv_strings.add("test_id = " + String.valueOf(test_id) + "\n");
            output_string = output_string + "user_id = " + String.valueOf(user_id) + "\n";
            // output_csv_strings.add("user_id = " + String.valueOf(user_id) + "\n");
            output_string = output_string + "test_starting_time = " + test_starting_time + "\n";
            // output_csv_strings.add("test_starting_time = " + test_starting_time + "\n");
            output_string = output_string + "test_ending_time = " + test_ending_time + "\n";
            // output_csv_strings.add("test_ending_time = " + test_ending_time + "\n");
            output_string = output_string + "test_type = " + test_type + "\n";
            // output_csv_strings.add("test_type = " + test_type + "\n");
            output_string = output_string + "image_type = " + image_type + "\n";
            // output_csv_strings.add("image_type = " + image_type + "\n");
            output_string = output_string + "interval duration = " + String.valueOf(interval_duration) + "\n";
            // output_csv_strings.add("interval duration = " + String.valueOf(interval_duration) + "\n");
            output_string = output_string + "number of points = " + String.valueOf(number_of_points) + "\n";

            output_string = output_string + "user name = " + name + "\n";
            // output_csv_strings.add("user name = " + name + "\n");
            output_string = output_string + "age = " + String.valueOf(age) + "\n";
            // output_csv_strings.add("age = " + String.valueOf(age) + "\n");
            output_string = output_string + "gender = " + gender + "\n";
            // output_csv_strings.add("gender = " + gender + "\n");
            output_string = output_string + "education = " + education + "\n";
            // output_csv_strings.add("education = " + education + "\n");
            output_string = output_string + "rating score = " + String.valueOf(rating_score) + "\n";
            // output_csv_strings.add("rating score = " + String.valueOf(rating_score) + "\n");
            output_string = output_string + "current receive treatment = " + current_receive_treatment + "\n";
            // output_csv_strings.add("current receive treatment = " + current_receive_treatment + "\n");


            output_string = output_string + "number of points = " + number_of_points + "\n";


            output_string = output_string + "device brand = " + Sharing.device_brand + "\n";
            output_string = output_string + "device model = " + Sharing.device_model + "\n";
            output_string = output_string + "device product name = " + Sharing.device_product_name + "\n";
            output_string = output_string + "device manufacturer = " + Sharing.device_product_name + "\n";
            output_string = output_string + "device screen height in pixels = " + Sharing.device_height_in_pixels + "\n";
            output_string = output_string + "device screen width in pixels = " + Sharing.device_width_in_pixels + "\n";
            output_string = output_string + "device screen testing/drawing height in pixels = " + Sharing.device_testing_area_height + "\n";
            output_string = output_string + "device screen testing/drawing width in pixels = " + Sharing.device_testing_area_width + "\n";
            output_string = output_string + "device navigation bar height = " + Sharing.device_navigation_bar_height + "\n";
            output_string = output_string + "device Android system version code = " + Sharing.device_system_version_code + "\n";


            query2 = "SELECT * FROM Data WHERE test_id = ?";
            parameter2 = new String [] {String.valueOf(test_id)};
            cursor2 = database.rawQuery(query2, new String [] {String.valueOf(test_id)});
            while (cursor2.moveToNext())
            {
                Sharing.number_of_item_finished = Sharing.number_of_item_finished + 1;
                timestamp_of_point = cursor2.getString(2).toString();
                x = cursor2.getFloat(3);
                y = cursor2.getFloat(4);
                pressure = cursor2.getFloat(5);
                touch_point_size = cursor2.getFloat(6);
                String new_line = timestamp_of_point + " " + String.valueOf(x) + " " + String.valueOf(y) + " " + String.valueOf(pressure) + " " + String.valueOf(touch_point_size) + "\n";
                output_string = output_string + new_line;

                Log.d("TIMESSTAMP", timestamp_of_point);
                time_year = timestamp_of_point.split(" ")[0].split("-")[0];
                time_month = timestamp_of_point.split(" ")[0].split("-")[1];
                time_day = timestamp_of_point.split(" ")[0].split("-")[2];
                Log.d("testingsss", timestamp_of_point.split(" ")[1].split(":")[0]);
                time_hour = timestamp_of_point.split(" ")[1].split(":")[0];
                time_minute = timestamp_of_point.split(" ")[1].split(":")[1];
                time_second = timestamp_of_point.split(" ")[1].split(":")[2];
                Log.d("testingggg", String.valueOf(time_second.contains(".")));
                int k = 0;
                int count = 0;
                time_millisecond = "";
                for (k = 0 ; k < time_second.length() ; k++)
                {
                    if (count == 1)
                    {
                        time_millisecond = time_millisecond + time_second.charAt(k);
                    }
                    if (time_second.charAt(k) == '.')
                    {
                        count = 1;
                    }
                }
                String new_time_second = "";
                k = 0;
                count = 0;
                for (k = 0 ; k < time_second.length() ; k++)
                {
                    if (time_second.charAt(k) != '.')
                    {
                        new_time_second = new_time_second + time_second.charAt(k);
                    }
                    else
                    {
                        break;
                    }
                }
                /*
                if (new_time_second.length() == 1)
                {
                    new_time_second = "0" + new_time_second;
                }
                if (time_millisecond.length() == 1)
                {
                    time_millisecond = "00" + time_millisecond;
                }
                else if (time_millisecond.length() == 2)
                {
                    time_millisecond = "0" + time_millisecond;
                }
                if (time_month.length() == 1)
                {
                    time_month = "0" + time_month;
                }
                if (time_minute.length() == 1)
                {
                    time_minute = "0" + time_minute;
                }
                */
                String new_csv_line = time_year + "," + time_month + "," + time_day + "," + time_hour + "," + time_minute + "," + new_time_second + "," + time_millisecond + "," + String.valueOf(x) + "," + String.valueOf(y) + "," + String.valueOf(pressure) + "," + String.valueOf(touch_point_size) + "," + String.valueOf(test_id) + "," + test_type + "," + image_type + "," + String.valueOf(interval_duration) + "," + String.valueOf(number_of_points) + "," + test_starting_time_number_only.substring(0, 8) + "," + test_starting_time_number_only.substring(8) + "," + test_ending_time_number_only.substring(0, 8) + "," + test_ending_time_number_only.substring(8) + "," + String.valueOf(user_id) + "," + name + "," + String.valueOf(age) + "," + gender + "," + education + "," + rating_score + "," + current_receive_treatment + "," + Sharing.device_brand + "," + Sharing.device_model + "," + Sharing.device_product_name + "," + Sharing.device_manufacturer + "," + Sharing.device_height_in_pixels + "," + Sharing.device_width_in_pixels + "," + Sharing.device_testing_area_height + "," + Sharing.device_testing_area_width + "," + Sharing.device_navigation_bar_height + "," + Sharing.device_system_version_code + "\n";
                output_csv_strings.add(new_csv_line);
            }

            output_string = output_string + "\n";

            if (cursor2 != null)
            {
                cursor2.close();
            }
            Log.d("TAG_OUTPUT", output_string);

        }
        if (cursor != null)
        {
            cursor.close();
        }

        Sharing.csv_string_arraylist = output_csv_strings;

        if (database != null)
        {
            database.close();
        }
        if (databaseHelper != null)
        {
            databaseHelper.close();
        }

        return output_string;
    }

    private boolean checkPermission() {
        int read_permission = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        int write_permission = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);

        return read_permission == PackageManager.PERMISSION_GRANTED  && write_permission == PackageManager.PERMISSION_GRANTED;
    }

    private void storeDataFileWorker ()
    {
        Log.d("WORKER_STARTED", "WORKER_STARTED");
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
        second = calendar.get(Calendar.SECOND);
        millisecond = calendar.get(Calendar.MILLISECOND);

        if (String.valueOf(month).length() == 1)
        {
            month_s = "0" + String.valueOf(month);
        }
        else
        {
            month_s = String.valueOf(month);
        }
        if (String.valueOf(day).length() == 1)
        {
            day_s = "0" + String.valueOf(day);
        }
        else
        {
            day_s = String.valueOf(day);
        }
        if (String.valueOf(hour).length() == 1)
        {
            hour_s = "0" + String.valueOf(hour);
        }
        else
        {
            hour_s = String.valueOf(hour);
        }
        if (String.valueOf(minute).length() == 1)
        {
            minute_s = "0" + String.valueOf(minute);
        }
        else
        {
            minute_s = String.valueOf(minute);
        }
        if (String.valueOf(second).length() == 1)
        {
            second_s = "0" + String.valueOf(second);
        }
        else
        {
            second_s = String.valueOf(second);
        }
        if (String.valueOf(millisecond).length() == 1)
        {
            millisecond_s = "00" + String.valueOf(millisecond);
        }
        else if (String.valueOf(millisecond).length() == 2)
        {
            millisecond_s = "0" + String.valueOf(millisecond);
        }
        else if (String.valueOf(millisecond).length() == 3)
        {
            millisecond_s = String.valueOf(millisecond);
        }

        String file_time = String.valueOf(year) + month_s + day_s + hour_s + minute_s + second_s + millisecond_s;

        output_file_name = "NeurographOutputDataFile" + file_time + ".txt";
        output_csv_file_name = "NeurographOutputDataFile" + file_time + ".csv";

        try {

            File file = new File(Environment.getExternalStorageDirectory(), "Neurograph");
            if (!file.exists()) {
                file.mkdirs();
            }


            File output_file = new File(Environment.getExternalStorageDirectory(), "/Neurograph/" + output_file_name);
            try {
                FileWriter fileWriter = new FileWriter(output_file);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                String content = generate_string_from_database();
                bufferedWriter.write(content);
                Log.d("CONTENTTTTT", content);
                bufferedWriter.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            File output_readme = new File (Environment.getExternalStorageDirectory(), "/Neurograph/" + "NeurographDataFileReadme.txt");
            try
            {
                FileWriter fileWriter = new FileWriter(output_readme);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                String content = SharingReadMe.readme;
                bufferedWriter.write(content);
                Log.d("CONTENTTTTT", content);
                bufferedWriter.close();
            }
            catch (IOException ioe)
            {
                ioe.printStackTrace();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }


            File output_csv_file = new File(Environment.getExternalStorageDirectory(), "/Neurograph/" + output_csv_file_name);
            FileWriter fileWriter1 = new FileWriter(output_csv_file);
            try {
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter1);
                int i = 0;
                for (i = 0; i < Sharing.csv_string_arraylist.size(); i++) {
                    bufferedWriter.write(Sharing.csv_string_arraylist.get(i));
                }
                bufferedWriter.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        String file_path = Environment.getExternalStorageDirectory() + "/Neurograph/" + output_file_name + "\n";
        file_path = file_path + Environment.getExternalStorageDirectory() + "/Neurograph/" + output_csv_file_name + "\n";
        file_path = file_path + Environment.getExternalStorageState() + "/Neurograph/" + "NeurographDataFileReadme.txt" + "\n";
        Log.d("file_path1", file_path);
        Sharing.file_path = file_path;
        String txt_file_path = Environment.getExternalStorageDirectory() + "/Neurograph/" + output_file_name;
        String csv_file_path = Environment.getExternalStorageDirectory() + "/Neurograph/" + output_csv_file_name;

        Intent push = new Intent ();
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, push, 0);
        NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = null;
            if (channel == null)
            {
                channel = new NotificationChannel("my_channel_01", getString(R.string.channel_name), importance);
                channel.setDescription("Neurograph_Notification");
                channel.enableLights(true);
                channel.setLightColor(Color.RED);
                channel.enableVibration(true);
                channel.setVibrationPattern(new long []{100, 200, 300, 400, 500, 400, 300, 200, 100});
                manager.createNotificationChannel(channel);
            }
            Notification notification = new NotificationCompat.Builder(getApplicationContext())
                    .setSmallIcon(R.drawable.spiral1)
                    .setContentTitle("File Saved")
                    .setContentInfo("Neurograph Notification")
                    .setContentText(Sharing.file_path)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setOngoing(false)
                    .setChannelId("my_channel_01")
                    .setAutoCancel(false)
                    .setFullScreenIntent(contentIntent, true)
                    .setVisibility(Notification.VISIBILITY_PUBLIC)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .build();
            manager.notify(90, notification);
        }
        else
        {
            Notification notification = new NotificationCompat.Builder(getApplicationContext())
                    .setSmallIcon(R.drawable.spiral1)
                    .setTicker("Neurograph Notification")
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentTitle("File Saved")
                    .setContentInfo("Neurograph Notification")
                    .setContentText(Sharing.file_path)
                    .setAutoCancel(false)
                    .setFullScreenIntent(contentIntent, true)
                    .setVisibility(Notification.VISIBILITY_PUBLIC)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .build();
            manager.notify(0, notification);
        }

        Sharing.stop_showing_process = 0;
        Intent broadcastMessage = new Intent ();
        broadcastMessage.setAction("com.example.jiashuwu.neurograph.action.MyReceiver");
        broadcastMessage.putExtra("stop_showing_process", "1");
        sendBroadcast(broadcastMessage);

        if (should_delete_test_data)
        {
            databaseHelperD = new MyDatabaseHelper (StoreDataFileActivity.this, databaseName, null, databaseVersion);
            databaseD = databaseHelperD.getWritableDatabase();
            databaseD.delete("Data", "", new String[] {});
            databaseD.delete("Test", "", new String[] {});
            if (databaseD != null)
            {
                databaseD.close();
            }
            if (databaseHelperD != null)
            {
                databaseHelperD.close();
            }
        }

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
        {
            TextScaleUtilsLower.scaleTextSize(StoreDataFileActivity.this, Sharing.isScale);
        }
        else
        {
            TextScaleUtils.scaleTextSize(StoreDataFileActivity.this, Sharing.isScale);
        }
        init_theme();
        initLocaleLanguage();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_data_file);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getNumber_of_test_in_total() == 0)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(StoreDataFileActivity.this);
            builder.setTitle("No Test Data");
            builder.setCancelable(false);
            builder.setMessage("There is no test data to store");
            builder.setPositiveButton("Got it", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent (StoreDataFileActivity.this, DataListActivity.class);
                    startActivity(intent);
                    StoreDataFileActivity.this.finish();
                }
            });
            builder.create();
            builder.show();
        }

        delete_test_data_switch = (Switch) findViewById(R.id.store_data_file_delete_data_switch);
        delete_test_data_switch.setChecked(true);

        should_delete_test_data = true;

        delete_test_data_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    should_delete_test_data = true;
                }
                else
                {
                    should_delete_test_data = false;
                }
            }
        });

        ActivityCompat.requestPermissions(StoreDataFileActivity.this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, 200);

        if (!NotificationManagerCompat.from(StoreDataFileActivity.this.getApplicationContext()).areNotificationsEnabled())
        {
            Log.d("NOTIFICATIONHHH", "NOTIFICATION");
            AlertDialog.Builder builder = new AlertDialog.Builder(StoreDataFileActivity.this);
            builder.setTitle("Notification permission");
            builder.setCancelable(false);
            builder.setMessage("Turn on notification");
            builder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Should do nothing here
                    // WARNING
                    // LEAVE THIS AS EMPRT BLOCK !!!
                }
            });
            builder.setPositiveButton("Go To Setting", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    goToSetting();
                    // The program will go to setting page if the user willing to turn on the notification;
                }
            });
            builder.create();
            builder.show();
        }

        /*
        progressBar = (ProgressBar) findViewById(R.id.store_data_file_progressbar);
        numbers_textview = (TextView) findViewById(R.id.store_data_file_numbers);
        percent_number_textview = (TextView) findViewById(R.id.store_data_file_percent_number);
        progressBar.setProgress(0);
        numbers_textview.setText("0/0");
        percent_number_textview.setText("0.0%");
        */

        button_generate = (Button) findViewById(R.id.store_data_file_generate_button);
        button_generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Sharing.number_of_item_finished = 0;
                Sharing.number_of_item_in_total = 0;
                Sharing.number_of_item_in_total = getNumber_of_item_in_total();
                Log.d("STATISTICS", String.valueOf(Sharing.number_of_item_in_total));


                //storeDataFileWorker();


                //storeDataFileWorker();


                AlertDialog.Builder builder1 = new AlertDialog.Builder(StoreDataFileActivity.this);
                builder1.setTitle("Generate Data Files");
                builder1.setCancelable(false);
                builder1.setMessage("Generating data files may takes a few second. Please wait");
                builder1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        store_data_file_button_clicked = "store";
                        Intent intent = new Intent(StoreDataFileActivity.this, DisplayProgressActivity.class);
                        startActivity(intent);
                        StoreDataFileActivity.this.finish();
                    }
                });

                builder1.create();
                builder1.show();


            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {

            Intent intent = new Intent(StoreDataFileActivity.this, DataListActivity.class);
            startActivity(intent);
            StoreDataFileActivity.this.finish();

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onStart ()
    {
        super.onStart();
    }


    @Override
    public void onRestart ()
    {
        Log.d("ON_STOP", "ON_RESTART");
        super.onRestart();
    }

    @Override
    public void onResume ()
    {
        Log.d("ON_STOP", "ON_RESUME");
        super.onResume();
    }

    @Override
    public void onPause ()
    {
        Log.d("ON_STOP", "ON_PAUSE");
        super.onPause();
    }

    @Override
    public void onStop ()
    {
        Log.d("ON_STOP", "ON_STOP");
        if (store_data_file_button_clicked.equalsIgnoreCase("store"))
        {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    storeDataFileWorker();
                }
            }).start();
        }
        super.onStop();
    }

    @Override
    public void onDestroy ()
    {
        Log.d("ON_STOP", "ON_DESTROY");
        super.onDestroy();
    }
}
