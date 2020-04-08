package com.david.app.cc;

import android.widget.Toast;

public class Component implements IComponent {


    @Override
    public String getName() {
        return "smart";
    }

    @Override
    public boolean onCall(CC cc) {
        switch (cc.getActionName()){
            case "get":
                Toast.makeText(cc.getContext(),"成功了get",Toast.LENGTH_SHORT).show();
                break;
            case "set":
                Toast.makeText(cc.getContext(),"成功了set",Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return false;
    }
}
