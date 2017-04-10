package com.hujlin.jsonlibrary;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;



/**
 * Created by che on 2017/4/10.
 */

public class JsonUtils {
    /**
     * json 转object
     * @param json
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T json2Object(String json,Class<T> clazz){
      Object object = toObject(json,clazz);
      return (T) object;
    }

    /**
     * object 转json
     * @param obj
     * @return
     */
    public static String obj2json(Object obj){
        try {
            return Obj2Json.obj2json(obj);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static  Object toObject(String json, Class clazz) {
        Object object =null;
        if(json.charAt(0)=='{'){
            try {
                JSONObject jsonObject = new JSONObject(json);
                object = clazz.newInstance();
                Iterator iterator = jsonObject.keys();
                //拿到module的成员变量
                List<Field> list = getAllFields(clazz);
                while (iterator.hasNext()){
                    //得到json key
                    String key = (String) iterator.next();
                    //找映射
                    for (Field field:list){

                        if (field.getName().equalsIgnoreCase(key)){
                            //进行赋值
                            field.setAccessible(true);
                            Object value = getFieldValue(field,jsonObject,key);
                            field.set(object,value);
                            field.setAccessible(false);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }else if(json.charAt(0)=='['){
            List<Field>  fields = getAllFields(clazz);
            for (Field field:fields)
            {
                Class<?> fieldType = field.getType();
                String name =fieldType.getName();
            }
            toList(json,clazz);
        }

        return object;
    }
    //处理jsonArray
    private static List toList(String json, Class clazz) {
        List<Object> list =null;
        try {
            JSONArray jsonArray =new JSONArray(json);
            list = new ArrayList<>();
            for (int i=0;i<jsonArray.length();i++){

                String jsonValue =jsonArray.getJSONObject(i).toString();
                switch (getJsonType(jsonValue)){
                    case JSON_TYPE_OBJECT:
                        list.add(toObject(jsonValue,clazz));
                        break;
                    case JSON_TYPE_ARRAY:
                        //jsonArray还有jsonArray的情况
                        List infoList = toList(jsonValue,clazz);
                        list.add(infoList);
                        break;
                    case JSON_TYPE_ERROR:
                        break;
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();

        }
        return list;
    }

    private static Object getFieldValue(Field field, JSONObject jsonObject, String key) throws JSONException {
        Object fieldValue =null;
        Class clazz = field.getType();
        if(clazz.getSimpleName().toString().equals("int")||
                clazz.getSimpleName().toString().equals("Integer")){
            fieldValue = jsonObject.getInt(key);
        }else if(clazz.getSimpleName().toString().equals("String")){
            fieldValue = jsonObject.getString(key);
        }else if(clazz.getSimpleName().toString().equals("double")||
                clazz.getSimpleName().toString().equals("Double")){
            fieldValue = jsonObject.getDouble(key);
        }else if(clazz.getSimpleName().toString().equals("long")||
                clazz.getSimpleName().toString().equals("Long")){
            fieldValue = jsonObject.getLong(key);
        }else if(clazz.getSimpleName().toString().equals("boolean")||
                clazz.getSimpleName().toString().equals("Boolean")){
            fieldValue = jsonObject.getBoolean(key);
        }else{
            //类的类型
            String jsonValue =jsonObject.getString(key);
            //判断是jsonObject 还是jsonArray
            switch (getJsonType(jsonValue)){
                case JSON_TYPE_OBJECT:
                    toObject(jsonValue,clazz);
                    break;
                case JSON_TYPE_ARRAY:
                    //拿到泛型的class
                    Type gennicFieldType = field.getGenericType();
                    //拿到list集合所对应的泛型
                    if (gennicFieldType instanceof ParameterizedType){
                        ParameterizedType parameterizedType = (ParameterizedType) gennicFieldType;
                        Type[] types = parameterizedType.getActualTypeArguments();
                        for (Type type:types){
                            Class<?> fieldClazz = (Class<?>) type;
                            String fieldType =fieldClazz.getSimpleName().toString();
                            if(fieldType.equals("Integer")){
                                List<Integer> result = new ArrayList<>();
                                jsonValue =jsonValue.substring(1,jsonValue.length()-1);
                                String[] a = jsonValue.split(",");
                                for (String r:a){
                                    result.add(Integer.parseInt(r));
                                }
                                return result;
                            }else if(fieldType.equals("String")){
                                List<String> result = new ArrayList<>();
                                jsonValue =jsonValue.substring(1,jsonValue.length()-1);
                                String[] a = jsonValue.split(",");
                                for (String r:a){
                                    result.add(r);
                                }
                                return result;
                            }else if(fieldType.equals("Double")){
                                List<Double> result = new ArrayList<>();
                                jsonValue =jsonValue.substring(1,jsonValue.length()-1);
                                String[] a = jsonValue.split(",");
                                for (String r:a){
                                    result.add(Double.parseDouble(r));
                                }
                                return result;
                            }else if(fieldType.equals("Long")){
                                List<Long> result = new ArrayList<>();
                                jsonValue =jsonValue.substring(1,jsonValue.length()-1);
                                String[] a = jsonValue.split(",");
                                for (String r:a){
                                    result.add(Long.parseLong(r));
                                }
                                return result;
                            }else if(fieldType.equals("Float")){
                                List<Float> result = new ArrayList<>();
                                jsonValue =jsonValue.substring(1,jsonValue.length()-1);
                                String[] a = jsonValue.split(",");
                                for (String r:a){
                                    result.add(Float.parseFloat(r));
                                }
                                return result;
                            }else if (fieldType.equals("Boolean")){
                                List<Boolean> result = new ArrayList<>();
                                jsonValue =jsonValue.substring(1,jsonValue.length()-1);
                                String[] a = jsonValue.split(",");
                                for (String r:a){
                                    result.add(Boolean.parseBoolean(r));
                                }
                                return result;
                            }else {
                                fieldValue = toList(jsonValue, fieldClazz);
                            }
                        }
                    }
                    break;
                case JSON_TYPE_ERROR:

                    break;
            }

        }
        return fieldValue;
    }

    private static JSON_TYPE getJsonType(String jsonValue) {
        final char[] strChar = jsonValue.substring(0,1).toCharArray();
        final char str =strChar[0];
        if (str=='{'){
            return JSON_TYPE.JSON_TYPE_OBJECT;
        }else if(str=='['){
            return JSON_TYPE.JSON_TYPE_ARRAY;
        }
        return JSON_TYPE.JSON_TYPE_ERROR;
    }

    private enum JSON_TYPE{
        JSON_TYPE_OBJECT,
        JSON_TYPE_ARRAY,
        JSON_TYPE_ERROR
    }

    private static List<Field> getAllFields(Class clazz) {
        List<Field> fields = new ArrayList<>();
        while (fields!=null){
            String name =clazz.getName();
            if (name.startsWith("java.")||name.startsWith("javax")||
                    name.startsWith("android.")){
                break;
            }
            Field[] fieldSelf = clazz.getDeclaredFields();
            for (Field f:fieldSelf){
                //排除被final修饰的成员变量
                if (!Modifier.isFinal(f.getModifiers()))
                {
                    fields.add(f);
                }
            }
            clazz =clazz.getSuperclass();
        }
        return fields;
    }
}
