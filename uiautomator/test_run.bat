adb push ./bin/DisableManager2Uiautomator.jar /data/local/tmp/
adb shell uiautomator runtest DisableManager2Uiautomator.jar -c com.nagopy.android.disablemanager2.MainActivityTest