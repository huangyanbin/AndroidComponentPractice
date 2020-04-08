package com.david.app.cc;

import android.util.Log;

import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class CCManager {

    private static volatile CCManager mInstance;
    private LinkedHashMap<String,IComponent> componentMap = new LinkedHashMap<>();
    private  AtomicLong callId = new AtomicLong(0);
    private CCManager(){
            init();
    }

    public static CCManager getInstance(){
        if(mInstance ==null){
            synchronized (CCManager.class){
                if(mInstance == null){
                    mInstance = new CCManager();
                }
            }
        }
        return mInstance;
    }
    private void init(){

    }

    public void send(CC cc){
       IComponent component =  componentMap.get(cc.getComponentName());
       if(component != null){
           component.onCall(cc);
       }else{
           Log.e("huang","未找到"+cc.getComponentName()+" component");
       }
    }

    public String getNextCallId(){
        return "call"+callId.addAndGet(1);
    }
}
