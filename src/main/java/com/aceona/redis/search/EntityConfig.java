//package com.aceona.redis.search;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import com.sun.tools.jdi.LinkedHashMap;
//
//public class EntityConfig
//{
//    String title_field;
//    String aliases_field;
//    String id_field  = "id";
//    List<String> ext_fields;
//    String type;
//    List<String> condition_fields;
//    String score_field;
//    boolean prefix_index_enable = false;
//
//    public EntityConfig(LinkedHashMap options) 
//    {
//        title_field = (String) (options.get("title_field") !=null ? options.get("title_field") : title_field);
//        type = (String) (options.get("type") !=null ? options.get("type"):type);
//        aliases_field = (String) (options.get("aliases_field") !=null ? options.get("aliases_field") : aliases_field);
//        prefix_index_enable = (Boolean) (options.get("prefix_index_enable") !=null ? options.get("prefix_index_enable") : false);
//        ext_fields = (List<String>) (options.get("ext_fields") !=null ? options.get("ext_fields") : new ArrayList<String>());
//        score_field = (String) (options.get("score_field") !=null ? options.get("score_field") : "lastUpdated");
//        condition_fields = (List<String>) (options.get("condition_fields") !=null ? options.get("condition_fields") : new ArrayList<String>());
//        //score_field 添加到ext_fields
//        ext_fields.add(score_field);  
//        //condition_fields 添加到ext_fields
//        ext_fields.addAll(condition_fields);
//    }
//}
