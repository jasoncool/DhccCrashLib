package com.dhcc.crashlib.data;

import android.content.Context;

public class ReportData {
   private   ICollector iCollector;

    public ReportData(ICollector iCollector){
       this.iCollector=iCollector;
    }
    public String collectInfo(Context context){
            return iCollector.collectInfo(context);
    }
}
