package me.libme.module.zookeeper.fn.ls;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by J on 2018/1/27.
 */
public class LeaderConfig {

    private String basePath;

    private String name;

    private Map external=new HashMap();

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public LeaderConfig put(String key,Object value){
        external.put(key,value);
        return this;
    }

    public Object get(String key){
        return external.get(key);
    }

    public Object get(String key,Object _default){
        Object val= external.get(key);
        if(val==null) return _default;
        return val;
    }




}
