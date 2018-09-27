package com.example.haipingguo.dialogview.responChainMode;

public class NoPerson extends Person{
    @Override
    protected boolean resolveQuestion(Bug bug) {
        return false;
    }
}
