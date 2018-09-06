package com.example.haipingguo.dialogview.builderPattern;

public class EngineeringDepartment {

    public void show(BikeBuider bikeBuider) {
        bikeBuider.getBike();
    }


    /* ConcreteBuider concreteBuider=new ConcreteBuider();
        concreteBuider.buildTyres("黑色轮胎")
                .buildFrame("黄色车架")
                .buildGPS("ofo定制版GPS定位装置");
        EngineeringDepartment department=new EngineeringDepartment();
        department.show(concreteBuider);*/

}
