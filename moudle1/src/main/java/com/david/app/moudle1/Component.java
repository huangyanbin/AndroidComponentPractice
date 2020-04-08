package com.david.app.moudle1;

import android.content.Intent;
import android.widget.Toast;

import com.david.app.cc.CC;
import com.david.app.cc.IComponent;

public class Component implements IComponent {


    @Override
    public String getName() {
        return "module1";
    }

    @Override
    public boolean onCall(CC cc) {
        switch (cc.getActionName()){
            case "test":
                Intent intent = new Intent(cc.getContext(),TestActivity.class);
                cc.getContext().startActivity(intent);
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
