package com.example.haipingguo.dialogview.responChainMode;

public class MainPerson {
    //责任链模式
    public static void resolveBug() {
        Person noPerson = new NoPerson();
        Person limitPerson = new LimitPerson();
        Person charlie = new OddPerson();
        noPerson.setNext(limitPerson).setNext(charlie).resolve(new Bug(3));
    }
}
