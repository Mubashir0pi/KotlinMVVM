package com.plutuscommerce.gbk.utils

import android.annotation.TargetApi
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.*
import android.location.Geocoder
import android.location.LocationManager
import android.media.ExifInterface
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.text.format.DateFormat
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.balysv.materialripple.MaterialRippleLayout
import com.nfinity.mvvm.R
import com.tapadoo.alerter.Alerter
import com.tapadoo.alerter.OnHideAlertListener
import com.tapadoo.alerter.OnShowAlertListener
import org.json.JSONArray
import org.json.JSONException
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

//Author Muhammad Mubashir 10/30/2018

class AppUtils(private val mContext: Context) {


    /****************************************
     * Get "String" to check is it null or not
     */
    fun getErrorDefinition(errorCode: Int): String {
        when (errorCode) {
            400 -> return "Missing required parameters or invalid parameters/values ($errorCode)"
            401 -> return "Incorrect email or password.($errorCode)"
            403 -> return "Account exists and user provided correct password, but account does not have a valid status.($errorCode)"
            500 -> return "Server Failure. ($errorCode)"
            else -> return "An error has occurred. ($errorCode)"
        }
    }

    fun checkDates(context: Context, Todate: String, FromDate: String): Boolean {
        //SimpleDateFormat dfDate = new SimpleDateFormat("yyyy-MM-dd");
        val dateFormatter = SimpleDateFormat("dd-MM-yyyy")
        var b = false
        try {
            if (dateFormatter.parse(Todate).before(dateFormatter.parse(FromDate))) {
                b = true
                //AppUtils.ShowToast(context, "Valid Date");//If start date is before end date
            } else b = dateFormatter.parse(Todate) == dateFormatter.parse(FromDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return b
    }


    private fun getRealPathFromURI(contentURI: String): String? {
        val contentUri = Uri.parse(contentURI)
        val cursor = mContext.contentResolver.query(contentUri, null, null, null, null)
        if (cursor == null) {
            return contentUri.path
        } else {
            cursor.moveToFirst()
            val index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            return cursor.getString(index)
        }
    }

    companion object {

        private val latitude: Double = 0.toDouble()
        private val longitude: Double = 0.toDouble()
        private val pref: SharedPreferences? = null
        private val editor: SharedPreferences.Editor? = null
        private val locale: Locale? = null
        private val languageCode: String? = null


        /********************
         * For Bitmap Streaming
         */
        fun CopyStream(`is`: InputStream, os: OutputStream) {
            val buffer_size = 1024
            try {
                val bytes = ByteArray(buffer_size)
                while (true) {
                    val count = `is`.read(bytes, 0, buffer_size)
                    if (count == -1)
                        break
                    os.write(bytes, 0, count)
                }
            } catch (ex: Exception) {
            }

        }

        private val OPTIONAL_ZERO_REGEX = Pattern.quote("(0")

        fun removeOptionalZero(phoneNumber: String): String {
            val split = phoneNumber.split(OPTIONAL_ZERO_REGEX.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            return if (split.size == 2 && !split[0].isEmpty()) { // Only remove the optional zero when preceded by a country code
                phoneNumber.replaceFirst(OPTIONAL_ZERO_REGEX.toRegex(), "")
            } else phoneNumber
        }

        fun trimLeadingZeros(source: String): String {
            for (i in 0 until source.length) {
                val c = source[i]
                if (c != '0') {
                    return source.substring(i)
                }
            }
            return "" // or return "0";
        }

        fun removeFirstChar(s: String): String {
            return s.substring(1)
        }

        fun displayPromptForEnablingGPS(activity: Activity) {

            val builder = AlertDialog.Builder(activity)
            val action = Settings.ACTION_LOCATION_SOURCE_SETTINGS
            val message = "Do you want on GPS  from  setting?"

            builder.setMessage(message)
                    .setPositiveButton("OK"
                    ) { d, id ->
                        activity.startActivity(Intent(action))
                        d.dismiss()
                    }
                    .setNegativeButton("Cancel"
                    ) { d, id -> d.cancel() }
            builder.create().show()
        }

        /********************
         * For Bitmap Rotation
         */
        fun rotateBitmap(source: Bitmap, angle: Float): Bitmap {
            val matrix = Matrix()
            matrix.postRotate(angle)
            return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
        }

        /**********************
         * For email validation
         */
        fun isEmailValid(email: String): Boolean {
            val regExpn = ("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                    + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                    + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                    + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$")
            val pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE)
            val matcher = pattern.matcher(email)
            return matcher.matches()
        }

        fun isValidPassword(s: String): Boolean {
            val PASSWORD_PATTERN = Pattern.compile(
                    "[a-zA-Z0-9\\!\\@\\#\\$]{8,24}")

            return !TextUtils.isEmpty(s) && PASSWORD_PATTERN.matcher(s).matches()
        }

        /****************************************
         * Get "String" to check is it null or not
         */
        fun isSet(string: String?): Boolean {
            return string != null && string.trim { it <= ' ' }.length > 0
        }

        /**********************************
         * Get city and country name method
         */
        fun getcity(context: Context, latitude: Double, longitude: Double): String {
            val results = StringBuilder()
            try {
                val geocoderr = Geocoder(context, Locale.getDefault())
                val addresses_city = geocoderr.getFromLocation(latitude, longitude, 1)
                if (addresses_city.size > 0) {
                    val address_city = addresses_city[0]
                    results.append(address_city.locality).append(" ,")
                    results.append(address_city.countryName)
                }
            } catch (e: IOException) {
                Log.e("tag", e.message)
            }

            return results.toString()
        }

        /***********************************
         * Get city and country name method
         */
        fun getAddress(context: Context, latitude: Double, longitude: Double): String {
            val results = StringBuilder()
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                if (addresses.size > 0) {
                    val address = addresses[0]
                    results.append(address.getAddressLine(0))

                }
            } catch (e: IOException) {
                Log.e("tag", e.message)
            }

            return results.toString()
        }


        /*****************************************************
         * Apply Blink Effect On Every TextView And Button etc
         */
        fun buttonEffect(button: View?) {
            val color = Color.parseColor("#00000000")
            try {
                button?.setOnTouchListener { v, event ->
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            v.background.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
                            v.invalidate()
                        }
                        MotionEvent.ACTION_UP -> {
                            v.background.clearColorFilter()
                            v.invalidate()
                        }
                    }
                    false
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        /******************************
         * Apply Effect On Every LocationSelectionView(Button, TextView, LinearLayout etc)
         * Please Add In Gradle This Code First (compile 'com.balysv:material-ripple:1.0.2')
         */
        fun setRippleEffect(view: View) {
            MaterialRippleLayout.on(view)
                    .rippleColor(Color.parseColor("#0183b5"))
                    .rippleAlpha(0.1f)
                    .rippleHover(true)
                    .create()
        }

        fun isLocationEnabled(context: Context): Boolean {
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.P -> {
                    // This is new method provided in API 28
                    val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                    return lm.isLocationEnabled
                }
                else -> {
                    // This is Deprecated in API 28
                    val mode = Settings.Secure.getInt(context.contentResolver, Settings.Secure.LOCATION_MODE,
                            Settings.Secure.LOCATION_MODE_OFF)
                    return mode != Settings.Secure.LOCATION_MODE_OFF

                }
            }
        }

        fun isLeapYear(year: Int): Boolean {
            return when {
                year % 4 != 0 -> false
                year % 400 == 0 -> true
                year % 100 == 0 -> false
                else -> true
            }
        }

        fun preventTwoClick(view: View) {
            view.isEnabled = false
            view.postDelayed({ view.isEnabled = true }, 500)
        }

        /******************************
         * Apply Font On Whole Activity
         */
        fun applyFont(context: Context, root: View, fontPath: String) {
            try {
                if (root is ViewGroup) {
                    val childCount = root.childCount
                    for (i in 0 until childCount)
                        applyFont(context, root.getChildAt(i), fontPath)
                } else if (root is TextView)
                    root.typeface = Typeface.createFromAsset(context.assets, fontPath)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        /****************************************
         * Set First Word Capitalize Of String
         */
        fun firstWordCapitalize(line: String?): String {
            return if (line != null && line.length > 0) {
                Character.toUpperCase(line[0]) + line.substring(1)
            } else {
                ""
            }
        }

        /*******************************************
         * Decode File For "Out Of Memory" Exception
         */
        fun decodeFile(f: File, WIDTH: Int, HIGHT: Int): Bitmap? {
            try {
                val o = BitmapFactory.Options()
                o.inJustDecodeBounds = true
                BitmapFactory.decodeStream(FileInputStream(f), null, o)
                // The new size we want to scale to
                // Find the correct scale value. It should be the power of 2.
                var scale = 1
                while (o.outWidth / scale / 2 >= WIDTH && o.outHeight / scale / 2 >= HIGHT)
                    scale *= 2
                // Decode with inSampleSize
                val o2 = BitmapFactory.Options()
                o2.inSampleSize = scale
                return BitmapFactory.decodeStream(FileInputStream(f), null, o2)
            } catch (e: FileNotFoundException) {
            }

            return null
        }

        /*************************
         * To set Image Rotation
         */
        fun getCameraPhotoOrientation(context: Context, imageUri: Uri,
                                      imagePath: String): Int {
            var rotate = 0
            try {
                context.contentResolver.notifyChange(imageUri, null)
                val imageFile = File(imagePath)
                val exif = ExifInterface(imageFile.absolutePath)
                val orientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL)
                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_270 -> rotate = 270
                    ExifInterface.ORIENTATION_ROTATE_180 -> rotate = 180
                    ExifInterface.ORIENTATION_ROTATE_90 -> rotate = 90
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return rotate
        }

        /******************************************
         * To set first letter capitalize of particular string.
         */
        fun capitalize(line: String): String {
            return Character.toUpperCase(line[0]) + line.substring(1)
        }


        /*********************************************************
         * Multiple texts with different color into single TextView
         */
        fun getColoredSpanned(text: String, color: String): String {
            return "<font color=$color>$text</font>"
        }

        fun parseTodaysDate(time: String): String? {
            val inputPattern = "yyyy-MM-dd"
            val outputPattern = "MM-dd-yyyy"

            val inputFormat = SimpleDateFormat(inputPattern)
            val outputFormat = SimpleDateFormat(outputPattern)
            var date: Date?
            var str: String? = null
            try {
                date = inputFormat.parse(time)
                str = outputFormat.format(date)
                //URLogs.i("mini", "Converted Date Today:" + str);
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            return str
        }

        fun isTimeBetweenTwoTime(argStartTime: String, argEndTime: String, argCurrentTime: String): Boolean {
            var valid = false;
            var startTime =  SimpleDateFormat("h:mm a")
                    .parse(argStartTime);
            var startCalendar = Calendar.getInstance();
            startCalendar.setTime(startTime);

            // Current Time
            var currentTime =  SimpleDateFormat("h:mm a")
                    .parse(argCurrentTime);
            var currentCalendar = Calendar.getInstance();
            currentCalendar.setTime(currentTime);

            // End Time
            var endTime =  SimpleDateFormat("h:mm a")
                    .parse(argEndTime);
            var endCalendar = Calendar.getInstance();
            endCalendar.setTime(endTime);

            //
            if (currentTime.compareTo(endTime) < 0) {

                currentCalendar.add(Calendar.DATE, 1);
                currentTime = currentCalendar.getTime();

            }

            if (startTime.compareTo(endTime) < 0) {

                startCalendar.add(Calendar.DATE, 1);
                startTime = startCalendar.getTime();

            }
            //
            if (currentTime.before(startTime)) {

                System.out.println(" Time is Lesser ");

                valid = false;
            } else {

                if (currentTime.after(endTime)) {
                    endCalendar.add(Calendar.DATE, 1);
                    endTime = endCalendar.getTime();

                }

                System.out.println("Comparing , Start Time /n " + startTime);
                System.out.println("Comparing , End Time /n " + endTime);
                System.out
                        .println("Comparing , Current Time /n " + currentTime);

                if (currentTime.before(endTime)) {
                    System.out.println("RESULT, Time lies b/w");
                    valid = true;
                } else {
                    valid = false;
                    System.out.println("RESULT, Time does not lies b/w");
                }

            }
            return valid;
        }

        fun parseTodayCalenderDateOrTime(time: String): String? {
            val inputPattern = "EEE MMM d HH:mm:ss zzz yyyy"
            //String outputPattern = "dd-MM-yyyy";
            val outputPattern = "dd-MM-yyyy"

            val inputFormat = SimpleDateFormat(inputPattern)
            val outputFormat = SimpleDateFormat(outputPattern)

            var date: Date? = null
            var str: String? = null

            try {
                date = inputFormat.parse(time)
                str = outputFormat.format(date)

                Log.i("mini", "Converted Date Today:" + str!!)
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            return str
        }

        fun parseTodaysDateII(time: String): String? {
            val inputPattern = "yyyy-MM-dd HH:mm:ss"
            val outputPattern = "MM-dd-yyyy HH:mm "

            val inputFormat = SimpleDateFormat(inputPattern)
            val outputFormat = SimpleDateFormat(outputPattern)
            var date: Date? = null
            var str: String? = null
            try {
                date = inputFormat.parse(time)
                str = outputFormat.format(date)
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            return str
        }

        fun getDate(time: Long): String {
            val cal = Calendar.getInstance(Locale.ENGLISH)
            cal.timeInMillis = time * 1000
            return DateFormat.format("EEEE, dd MMMM, yyyy hh:mm A", cal).toString()
        }

        fun getMonthDate(time: Long): String {
            val cal = Calendar.getInstance(Locale.ENGLISH)
            cal.timeInMillis = time * 1000
            return DateFormat.format("dd MMM", cal).toString()
        }

        fun getTime(time: Long): String {
            val cal = Calendar.getInstance(Locale.ENGLISH)
            cal.timeInMillis = time * 1000
            return DateFormat.format("HH:mm", cal).toString()
        }

        fun parseTime(time: String): String? {

            val inputPattern = "hh:mm a"
            val outputPattern = "HH:mm:ss"

            val inputFormat = SimpleDateFormat(inputPattern)
            val outputFormat = SimpleDateFormat(outputPattern)
            var date: Date? = null
            var str: String? = null
            try {
                date = inputFormat.parse(time)
                str = outputFormat.format(date)
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            return str
        }

        fun parseTimeReverse(time: String): String? {

            val outputPattern = "hh:mm a"
            val inputPattern = "HH:mm:ss"
            val inputFormat = SimpleDateFormat(inputPattern, Locale.US)
            val outputFormat = SimpleDateFormat(outputPattern, Locale.US)
            var date: Date? = null
            var str: String? = null
            try {
                date = inputFormat.parse(time)
                str = outputFormat.format(date)
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            return str
        }

        fun parseTimeReverseForOpeningHour(time: String): String? {

            val outputPattern = "h:mm a"
            val inputPattern = "HH:mm:ss"

            val inputFormat = SimpleDateFormat(inputPattern)
            val outputFormat = SimpleDateFormat(outputPattern)
            var date: Date? = null
            var str: String? = null
            try {
                date = inputFormat.parse(time)
                str = outputFormat.format(date)
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            return str
        }

        fun parseTimeFromTimePicker(time: String): String? {
            val inputPattern = "EEE MMM d HH:mm:ss zzz yyyy"
            //String outputPattern = "HH:mm a";
            val outputPattern = "HH:mm:ss"

            val inputFormat = SimpleDateFormat(inputPattern, Locale.US)
            val outputFormat = SimpleDateFormat(outputPattern, Locale.US)
            var date: Date? = null
            var str: String? = null
            try {
                date = inputFormat.parse(time)
                str = outputFormat.format(date)
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            return str
        }

        fun formatTimeFromServer(dateStr: String): String {
            var dateStr = dateStr
            val inputPattern = "yyyy-MM-dd hh:mm:ss"
            val outputPattern = "hh:mm aa"
            val inputFormat = SimpleDateFormat(inputPattern)
            val outputFormat = SimpleDateFormat(outputPattern)

            var date: Date? = null
            try {
                date = inputFormat.parse(dateStr)
                dateStr = outputFormat.format(date)
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            return dateStr
        }

        fun getMonth(dateStr: String): String {
            var dateStr = dateStr
            val inputPattern = "MM"
            val outputPattern = "MMMM"
            val inputFormat = SimpleDateFormat(inputPattern)
            val outputFormat = SimpleDateFormat(outputPattern)

            var date: Date? = null
            try {
                date = inputFormat.parse(dateStr)
                dateStr = outputFormat.format(date)
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            return dateStr.toUpperCase()
        }
        /*
        Time Stamp to date and time
        * */
        @Throws(JSONException::class)
         fun jsonStringToArray(jsonString: String): ArrayList<String> {

            val stringArray = ArrayList<String>()

            val jsonArray = JSONArray(jsonString)

            for (i in 0 until jsonArray.length()) {
                stringArray.add(jsonArray.getString(i))
            }

            return stringArray
        }
        fun getDateMonthTimeStamp(timeStamp: Long): String {

            try {
                val sdf = SimpleDateFormat("dd MMMM HH:mm")
                val netDate = Date(timeStamp)
                return sdf.format(netDate)
            } catch (ex: Exception) {
                return ""
            }

        }

        fun getDateInTimeStamp(timeStamp: Long): String {

            try {
                val sdf = SimpleDateFormat("dd MMMM YYYY")
                val netDate = Date(timeStamp)
                return sdf.format(netDate)
            } catch (ex: Exception) {
                return ""
            }

        }

        /**
         * @param s H:m timestamp, i.e. [Hour in day (0-23)]:[Minute in hour (0-59)]
         * @return total minutes after 00:00
         */
        fun parseHrstoMins(s: String): Int {
            val str = s.split(" ".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
            val stringHourMins = str[0]
            val hourMin = stringHourMins.split(":".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
            val hour = Integer.parseInt(hourMin[0])
            val mins = Integer.parseInt(hourMin[1])
            val hoursInMins = hour * 60
            return hoursInMins + mins
        }
        /*******************************
         * Method for check negative value
         */

        /*******************************
         * Method for network is in working state or not.
         */


        fun isNegative(value: Double): Boolean {
            return when {
                value < 0 -> true
                else -> false
            }
        }



        fun getDeviceName(): String {
            val manufacturer = Build.MANUFACTURER
            val model = Build.MODEL
            return if (model.toLowerCase().startsWith(manufacturer.toLowerCase())) {
                capitalize(model)
            } else {
                capitalize(manufacturer) + " " + model
            }
        }


        /*******************************
         * Hide keyboard from edit text
         */
        fun hideKeyboard(context: AppCompatActivity) {
            val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            val view = context.currentFocus
            if (view != null) {
                inputManager.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }

        /*******************************
         * Show keyboard with edit text
         */
        fun showKeyboardWithFocus(v: View, a: AppCompatActivity) {
            try {
                v.requestFocus()
                val imm = a.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT)
                a.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        /*************************************
         * Show alerter with your custom messages
         */
        fun showDropDownNotification(mContext: AppCompatActivity, title: String, message: String) {
            Alerter.create(mContext)
                    .setTitle(title)
                    .setText(message)
                    .setBackgroundColorRes(R.color.red)// or setBackgroundColorInt(Color.CYAN) // setBackgroundColorRes(R.color.)
                    .setIcon(R.drawable.ic_alert)
                    .setIconColorFilter(0)
                    .setDuration(2000)
                    .show()
        }

        fun showInfoNotification(mContext: AppCompatActivity, title: String, message: String) {
            Alerter.create(mContext)
                    .setTitle(title)
                    .setText(message)
                    .setBackgroundColorRes(R.color.deep_sky_blue)// or setBackgroundColorInt(Color.CYAN) // setBackgroundColorRes(R.color.)
                    .setIcon(R.drawable.ic_alert)
                    .setIconColorFilter(0)
                    .setDuration(2000)
                    .show()
        }

        fun showDropDownSuccessNotification(mContext: AppCompatActivity, title: String, message: String) {
            Alerter.create(mContext)
                    .setTitle(title)
                    .setText(message)
                    .setBackgroundColorRes(R.color.green)// or setBackgroundColorInt(Color.CYAN) // setBackgroundColorRes(R.color.)
                    .setIcon(R.drawable.ic_success)
                    .setIconColorFilter(0)
                    .setDuration(2000)
                    .show()
        }

        fun showDropDownSuccessNotificationBackMove(mContext: AppCompatActivity, title: String, message: String) {
            Alerter.create(mContext)
                    .setTitle(title)
                    .setText(message)
                    .setBackgroundColorRes(R.color.green)// or setBackgroundColorInt(Color.CYAN) // setBackgroundColorRes(R.color.)
                    .setIcon(R.drawable.ic_success)
                    .setIconColorFilter(0)
                    .setDuration(2000)
                    .setOnShowListener(OnShowAlertListener { })
                    .setOnHideListener(OnHideAlertListener { mContext.onBackPressed() })
                    .show()
        }

        fun showDropDownErrorNotificationBackMove(mContext: AppCompatActivity, title: String, message: String) {
            Alerter.create(mContext)
                    .setTitle(title)
                    .setText(message)
                    .setBackgroundColorRes(R.color.red)// or setBackgroundColorInt(Color.CYAN) // setBackgroundColorRes(R.color.)
                    .setIcon(R.drawable.ic_alert)
                    .setIconColorFilter(0)
                    .setDuration(2000)

                    .setOnShowListener(OnShowAlertListener { })
                    .setOnHideListener(OnHideAlertListener { mContext.onBackPressed() })
                    .show()
        }

        fun showDropDownSuccessNotificationAndMoveToNextActivity(mContext: AppCompatActivity, title: String, message: String, cls: Class<*>) {
            Alerter.create(mContext)
                    .setTitle(title)
                    .setText(message)
                    .setBackgroundColorRes(R.color.green)// or setBackgroundColorInt(Color.CYAN) // setBackgroundColorRes(R.color.colorAccent)
                    .setIcon(R.drawable.ic_success)
                    .setIconColorFilter(0)
                    .setDuration(2000)
                    .setOnShowListener {

                    }
                    .setOnHideListener {
                        val myIntent = Intent(mContext, cls)

                        mContext.startActivity(myIntent)
                        myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        mContext.finish()
                    }
                    .show()
        }

        /*************************************
         * Show progress bar with callback true or false
         */
        private fun showProgress(btn: Button, progressBar: ProgressBar, progressVisible: Boolean) {
            btn.isEnabled = !progressVisible
            progressBar.visibility = if (progressVisible) View.VISIBLE else View.GONE
        }

        fun initStatusBar(activity: AppCompatActivity, isWhite: Boolean, isKeyboardNeeded: Boolean) {

            /*For Full screen layout*/
            when {
                !isKeyboardNeeded -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
                }
            }

            /*Change Status and Navigation Bar colors*/

            /*Move root view above then navigation bar*/

            /*Change Status and Navigation Bar colors*/
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> if (!isWhite) {
                    activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR)
                    activity.getWindow().setNavigationBarColor(Color.WHITE)
                    activity.getWindow().setStatusBarColor(Color.WHITE)
                } else {
                    activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR)
                    activity.getWindow().setNavigationBarColor(Color.WHITE)
                }
            }

            /*Move root view above then navigation bar*/

            /*Move root view above then navigation bar*/
            when {
                !isKeyboardNeeded -> {
                    val viewGroup = (activity.findViewById(android.R.id.content) as ViewGroup).getChildAt(0) as ViewGroup
                    val params = viewGroup.layoutParams as FrameLayout.LayoutParams
                    val height = getSoftButtonsBarSizePort(activity)
                    params.setMargins(0, 0, 0, height)
                }
            }
        }

        fun getSoftButtonsBarSizePort(activity: AppCompatActivity): Int {
            // getRealMetrics is only available with API 17 and +
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 -> {
                    val metrics = DisplayMetrics()
                    activity.windowManager.defaultDisplay.getMetrics(metrics)
                    val usableHeight = metrics.heightPixels
                    activity.windowManager.defaultDisplay.getRealMetrics(metrics)
                    val realHeight = metrics.heightPixels
                    return if (realHeight > usableHeight)
                        realHeight - usableHeight
                    else
                        0
                }
                else -> return 0
            }
        }


        /*************************************
         * Get bitmap from Uri
         */
        @Throws(IOException::class)
        fun getBitmapFromUri(uri: Uri, context: Context): Bitmap {
            val parcelFileDescriptor = context
                    .contentResolver.openFileDescriptor(uri, "r")
            val fileDescriptor = parcelFileDescriptor!!
                    .fileDescriptor
            val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            parcelFileDescriptor.close()
            return image
        }

        fun getBitmapFromVectorDrawable(context: Context, drawableId: Int): Bitmap {
            var drawable = ContextCompat.getDrawable(context, drawableId)
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                drawable = DrawableCompat.wrap(drawable!!).mutate()
            }

            val bitmap = Bitmap.createBitmap(drawable!!.intrinsicWidth,
                    drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)

            return bitmap
        }

        /*****************************************************
         * Show error into edit text with your custom messages
         */
        fun ShowError(et: EditText, error: String) {
            when {
                et.length() == 0 -> et.error = error
            }
        }

        /*************************************
         * Get bitmap from Uri
         */
        fun getBitmapFromURL(src: String): Bitmap? {
            try {
                val url = URL(src)
                val connection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                val input = connection.inputStream
                return BitmapFactory.decodeStream(input)
            } catch (e: IOException) {
                // URLogs exception
                return null
            }

        }

        fun resolveTransparentStatusBarFlag(ctx: Context): Int {
            val libs = ctx.packageManager.systemSharedLibraryNames
            var reflect: String? = null
            when (libs) {
                null -> return 0
                else -> {
                    val SAMSUNG = "touchwiz"
                    val SONY = "com.sonyericsson.navigationbar"
                    for (lib in libs) {
                        if (lib == SAMSUNG) {
                            reflect = "SYSTEM_UI_FLAG_TRANSPARENT_BACKGROUND"
                        } else if (lib.startsWith(SONY)) {
                            reflect = "SYSTEM_UI_FLAG_TRANSPARENT"
                        }
                    }
                    when (reflect) {
                        null -> return 0
                        else -> {
                            try {
                                val field = View::class.java!!.getField(reflect)
                                if (field.getType() == Integer.TYPE) {
                                    return field.getInt(null)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                            return 0
                        }
                    }

                }
            }

        }

        @TargetApi(Build.VERSION_CODES.KITKAT)
        fun setTranslucentStatus(win: Window, on: Boolean) {
            val winParams = win.attributes
            val bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
            if (on) {
                winParams.flags = winParams.flags or bits
            } else {
                winParams.flags = winParams.flags and bits.inv()
            }
            win.attributes = winParams
        }

        /*************************************
         * Get bitmap path with 100% quality*/
        @Throws(IOException::class)


        fun getImagePath(selectedImage: Uri, ctx: Context): String? {
            var filePath: String? = null
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA, MediaStore.Images.Media.DISPLAY_NAME)
            val cursor = ctx.contentResolver.query(selectedImage, filePathColumn, null, null, null)
            if (cursor!!.moveToFirst()) {
                val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                filePath = cursor.getString(columnIndex)
            }
            cursor.close()
            return filePath
        }

        /********************************************************
         * Decodes image and scales it to reduce memory consumption
         */
        //
        fun decodeFile(f: File): Bitmap? {
            try {
                val o = BitmapFactory.Options()
                o.inJustDecodeBounds = true
                BitmapFactory.decodeStream(FileInputStream(f), null, o)

                //Find the correct scale value. It should be the power of 2.
                val REQUIRED_SIZE = 200
                var width_tmp = o.outWidth
                var height_tmp = o.outHeight
                var scale = 1
                while (true) {
                    if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
                        break
                    width_tmp /= 2
                    height_tmp /= 2
                    scale *= 2
                }

                //decode with inSampleSize
                val o2 = BitmapFactory.Options()
                o2.inSampleSize = scale
                return BitmapFactory.decodeStream(FileInputStream(f), null, o2)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return null
        }






        fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
            val height = options.outHeight
            val width = options.outWidth
            var inSampleSize = 1

            if (height > reqHeight || width > reqWidth) {
                val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
                val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())
                inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
            }
            val totalPixels = (width * height).toFloat()
            val totalReqPixelsCap = (reqWidth * reqHeight * 2).toFloat()
            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++
            }

            return inSampleSize
        }

        fun formatDateFromServer(dateStr: String): String {
            var dateStr = dateStr
            val inputPattern = "yyyy-MM-dd hh:mm:ss"
            val outputPattern = "dd MMM yyyy"
            val inputFormat = SimpleDateFormat(inputPattern)
            val outputFormat = SimpleDateFormat(outputPattern)

            var date: Date? = null
            try {
                date = inputFormat.parse(dateStr)
                dateStr = outputFormat.format(date)
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            return dateStr
        }

        fun formatDateFromPicker(dateStr: String): String {
            var dateStr = dateStr
            val inputPattern = "dd-MMMM-yyyy"
            val outputPattern = "yyyy-MM-dd"
            val inputFormat = SimpleDateFormat(inputPattern)
            val outputFormat = SimpleDateFormat(outputPattern)

            var date: Date? = null
            try {
                date = inputFormat.parse(dateStr)
                dateStr = outputFormat.format(date)
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            return dateStr
        }





        fun isValidEmail(target: String?): Boolean {
            return if (target == null) false else android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches()

        }

        fun isMatch(target: String, src: String): Boolean {

            return target == src
        }
    }


}
