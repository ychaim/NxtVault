package com.nxt.nxtvault.framework;

import android.util.Log;

import com.nxt.nxtvault.preference.PreferenceManager;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by bcollins on 2015-06-02.
 */

@Singleton
public class PinManager {
    PreferenceManager mPreferences;
    MessageDigest md;

    private String mSessionPin = null;

    @Inject
    public PinManager(PreferenceManager preferenceManager){
        mPreferences = preferenceManager;

        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public String getSessionPin(){
        return mSessionPin;
    }

    public boolean signIn(String pin){
        String md5 = getMd5Hash(pin);

        if (md5.equals(mPreferences.getPinDigest())){
            mSessionPin = pin;
            return true;
        }

        return false;
    }

    public boolean changePin(String newPin){
        if (!mPreferences.getPinIsSet() || (mSessionPin != null && signIn(mSessionPin))){
            String md5 = getMd5Hash(newPin);

            mPreferences.putPinDigest(md5);
            mSessionPin = newPin;

            return true;
        }

        return false;
    }

    public void clearSession(){
        mSessionPin = null;
    }

    public static String getMd5Hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            String md5 = number.toString(16);

            while (md5.length() < 32)
                md5 = "0" + md5;

            return md5;
        } catch (NoSuchAlgorithmException e) {
            Log.e("MD5", e.getLocalizedMessage());
            return null;
        }
    }

    private byte[] getBytes(String pin){
        try {
            return pin.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }
}
