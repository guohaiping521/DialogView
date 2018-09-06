package com.example.haipingguo.dialogview.builderPattern;

import android.util.Log;

public class ConcreteBuider implements BikeBuider{

    private Bike mBike=new Bike();

    @Override
    public BikeBuider buildTyres(String tyres) {
        mBike.setTyre(tyres);
        return this;
    }

    @Override
    public BikeBuider buildFrame(String frame) {
        mBike.setFrame(frame);
        return this;
    }

    @Override
    public BikeBuider buildGPS(String gps) {
        mBike.setGps(gps);
        return this;
    }

    @Override
    public Bike getBike() {
        Log.i("ghpppp","Tyres=="+mBike.getTyre());
        Log.i("ghpppp","Frame=="+mBike.getFrame());
        Log.i("ghpppp","Gps=="+mBike.getGps());
        return mBike;
    }
}

