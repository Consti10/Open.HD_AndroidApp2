package com.none.non.openhd.newStuff;

//TODO: Really dirty implementation. Improve !

import android.os.Bundle;

import java.security.Key;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Pattern;

@SuppressWarnings("WeakerAccess")
public class Message {
    public final String src;
    public final String cmd;
    public final String data;
    public final ArrayList<String> dataAsList;

    public static final String GET_OK="GET_OK";
    public static final String CHANGE_OK="CHANGE_OK";
    public static final String HELLO="HELLO";
    public static final String HELLO_OK="HELLO_OK";

    //Data of messages must not contain this symbol
    public static final String SEPERATOR="?";


    public Message(final String msg){
        final String[] tmp=msg.split(Pattern.quote(SEPERATOR),3);
        src=tmp[0];
        cmd=tmp[1];
        data=(tmp.length==2) ? null : tmp[2];
        if(data!=null){
            dataAsList=new ArrayList<>(Arrays.asList(data.split(Pattern.quote(SEPERATOR))));
        }else{
            dataAsList=null;
        }
        //System.out.println(toString());
    }

    public ArrayList<KeyValuePair> getKeyValuePairs(){
        ArrayList<KeyValuePair> ret=new ArrayList<>();
        for(final String pair:dataAsList){
            System.out.println(pair);
            final String[] keyValue=pair.split("=");
            ret.add(new KeyValuePair(keyValue[0],keyValue[1]));
        }
        return ret;
    }


    public boolean ground(){
        return src.equals("G");
    }

    public static String BuildMessageGET(final boolean groundOnly,final String key){
        return BuildMessageGET(groundOnly, new ArrayList<String>(Collections.singletonList(key)));
    }
    public static String BuildMessageCHANGE(final boolean groundOnly,final String key,final String value){
        return BuildMessageCHANGE(groundOnly,new ArrayList<KeyValuePair>(Collections.singletonList(new KeyValuePair(key,value))));
    }

    public static String BuildMessageGET(final boolean groundOnly, final ArrayList<String> keys){
        if(keys.size()<1){
            System.out.println("Error get should contain at least one key");
        }
        StringBuilder builder=new StringBuilder();
        for(final String key:keys){
            builder.append(key).append(SEPERATOR);
        }
        //We don't want the last character
        final String keysAsString=builder.substring(0,builder.length()-1);
        return BuildMessage(groundOnly? "G":"GA","GET",keysAsString);
    }

    public static String BuildMessageCHANGE(final boolean groundOnly,final ArrayList<KeyValuePair> keyValuePairs){
        if(keyValuePairs.size()<1){
            System.out.println("Error change should contain at least one key");
        }
        StringBuilder builder=new StringBuilder();
        for(final KeyValuePair keyValuePair:keyValuePairs){
            builder.append(keyValuePair.key).append("=").append(keyValuePair.value).append(SEPERATOR);
        }
        //We don't want the last character
        final String keyValuePairsAsString=builder.substring(0,builder.length()-1);
        return BuildMessage(groundOnly? "G":"GA","CHANGE",keyValuePairsAsString);
    }


    public static String BuildMessageHELLO_OK(){
        return BuildMessage("G","HELLO_OK","");
    }

    public static String BuildMessageHELLO(final boolean groundOnly){
        return BuildMessage(groundOnly? "G":"GA","HELLO","");
    }

    private static String BuildMessage(final String dst,final String cmd,final String data){
        return dst+SEPERATOR+cmd+SEPERATOR+data;
    }

    @Override
    public String toString(){
        StringBuilder builder=new StringBuilder();
        builder.append("src:").append(src);
        builder.append("cmd:").append(cmd);
        //if(data!=null){
        //    builder.append("data:").append(data);
        //}
        if(data!=null){
            builder.append(data);
        }
        return builder.toString();
    }

}
