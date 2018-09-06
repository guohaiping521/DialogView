package com.example.haipingguo.dialogview.builderPattern;

 public interface BikeBuider {
    // 组装轮胎
    BikeBuider buildTyres(String tyres);

    // 组装车架
    BikeBuider buildFrame(String frame);

    // 组装GPS定位装置
    BikeBuider buildGPS(String gps);

    // 获取自行车
     Bike getBike();
}
