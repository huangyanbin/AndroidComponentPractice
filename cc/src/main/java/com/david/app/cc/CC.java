package com.david.app.cc;


import android.content.Context;

public class CC {
    private String actionName;
    private String componentName;
    private String callId;
    private Context context;


    private CC(){

    }

    public static Build obtainBuilder(String componentName){
        return new Build(componentName);
    }

    public void call(){
       CCManager.getInstance().send(this);
    }

    public String getActionName() {
        return actionName;
    }

    public String getComponentName() {
        return componentName;
    }

    public String getCallId() {
        return callId;
    }

    public Context getContext() {
        return context;
    }

    public static class Build{

        private String actionName;
        private String componentName;
        private String callId;
        private Context context;


        private Build( String componentName){
            this.componentName = componentName;
        }

        public Build setActionName(String actionName){
            this.actionName = actionName;
            return this;
        }

        public Build setContext(Context context){
            this.context = context;
            return this;
        }


        public CC build(){
            CC cc = new CC();
            cc.actionName = actionName;
            cc.componentName = componentName;
            cc.context = context;
            cc.callId = CCManager.getInstance().getNextCallId();
            return cc;
        }
    }

}
