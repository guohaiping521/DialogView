package com.example.haipingguo.dialogview.responChainMode;

import android.util.Log;

public abstract class Person {
    private String name;
    private Person nextPerson;

    public Person setNext(Person person) {
        this.nextPerson = person;
        return nextPerson;
    }

    public void resolve(Bug bug){
        if(resolveQuestion(bug)){
            done();
        }else if(nextPerson!=null){
            nextPerson.resolve(bug);
        }else {
            fail(bug);
        }

    }

    protected void done() {
        Log.i("ghppppp","person问题解决");
    }

    protected void fail(Bug bug) {  // 未解决
        System.out.println(bug + " cannot be resolved.");
    }

    protected abstract boolean resolveQuestion(Bug bug);
}
