package com.example.haipingguo.dialogview.responChainMode;

import android.util.Log;

public class LimitPerson extends Person {
    @Override
    protected boolean resolveQuestion(Bug bug) {
        Log.i("ghppp","limit");
        return bug.getNum() < 4;
    }
}