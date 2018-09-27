package com.example.haipingguo.dialogview.responChainMode;

import android.util.Log;

public class OddPerson extends Person{
    @Override
    protected boolean resolveQuestion(Bug bug) {
        Log.i("ghppp","odd");
        return bug.getNum() % 2 == 1;
    }
}
