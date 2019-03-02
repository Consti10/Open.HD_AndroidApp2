package com.none.non.openhd.newStuff;

//TODO: Really dirty as of yet

@SuppressWarnings("WeakerAccess")
public class Message {
    public final String cmd;
    public final String data;
    public final String dataKey,dataValue;

    public Message(final String msg){
        final String[] tmp=msg.split(" ",2);
        cmd=tmp[0];
        data=(tmp.length==1) ? null : tmp[1];
        if(data!=null){
            final String[] keyValue = data.split("=");
            dataKey=keyValue[0];
            dataValue=keyValue[1];
        }else{
            dataKey=null;
            dataValue=null;
        }
        //System.out.println(toString());
    }


    @Override
    public String toString(){
        StringBuilder builder=new StringBuilder();
        builder.append("cmd:").append(cmd);
        //if(data!=null){
        //    builder.append("data:").append(data);
        //}
        if(dataKey!=null){
            builder.append("dataKey:").append(dataKey);
        }
        if(dataValue!=null){
            builder.append("dataValue:").append(dataValue);
        }
        return builder.toString();
    }

}
