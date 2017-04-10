package com.hujlin.jsonlibrary;

import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by che on 2017/4/10.
 */

public class  Obj2Json {

    private static StringBuilder builder =new StringBuilder();

    public static String obj2json(Object obj) throws IllegalAccessException, InstantiationException {
        builder.delete(0,builder.length());
        String result = paraseJson(obj,null);
        return result;
    }

    private static String paraseJson(Object obj,String fieldName) throws IllegalAccessException {

        Class clazz = obj.getClass();
        //拿到module的成员变量
        List<Field> list = getAllFields(clazz);

        if (!TextUtils.isEmpty(fieldName)){
            builder.append(",\"");
            builder.append(fieldName);
            builder.append("\":");
        }

        if(list!=null&&list.size()>0){
                builder.append(",{");
            }
        for (int i=0;i<list.size();i++){
            Class<?> clazzType = list.get(i).getType();
            String typeName = clazzType.getSimpleName().toString();
            if (obj !=null){
                getValueByTypeName(list.get(i),typeName,obj,builder,i);
            }

        }

        if(list!=null&&list.size()>0){
             builder.append("}");
         }
        String result =builder.toString();
        if (result.contains("[,")){
            result = result.replace("[,","[");
        }
        if (result.contains("},]")){
            result = result.replace("},]","}]");
        }
        if (result.contains("}{")){
            result = result.replace("}{","},{");
        }
        if(result.contains(":,")){
            result = result.replace(":,",":");
        }
        if (result.startsWith(",")){
            result =result.substring(1,result.length());
        }
        if (result.contains("{,")){
            result = result.replace("{,","{");
        }
        return result;
    }


    public static void objArr2Str(Object result, StringBuilder builder, Field field, String fieldName){
       if (result==null){
           return;
       }
        createOtherType(result,builder,field,fieldName);

    }


    private static void getValueByTypeName(Field field, String typeName, Object obj, StringBuilder builder, int i) throws IllegalAccessException {
        String fieldName = field.getName();
        field.setAccessible(true);
        if(typeName.equals("int")||
                typeName.equals("Integer")){
           int result = field.getInt(obj);
            createJson(fieldName, result,builder,i);
        }else if(typeName.equals("String")){
           String result = (String) field.get(obj);
            createJson4Str(fieldName, result,builder,i);
        }else if(typeName.equals("double")||
                typeName.equals("Double")){
             double  result = field.getDouble(obj);
            createJson(fieldName, result,builder,i);
        }else if(typeName.equals("long")||
                typeName.equals("Long")){
            long result = field.getLong(obj);
            createJson(fieldName, result,builder,i);
        }else if(typeName.equals("boolean")||
                typeName.equals("Boolean")){
           boolean result = field.getBoolean(obj);
            createJson(fieldName, result,builder,i);
        }else if(typeName.equals("byte")||
                typeName.equals("Byte")){
            byte result = field.getByte(obj);
            createJson(fieldName, result,builder,i);
        }else if(typeName.equals("char")||
                typeName.equals("Character")){
            char  result = field.getChar(obj);
            createJson(fieldName, result,builder,i);
        }else if(typeName.equals("float")||
                typeName.equals("Float")){
           float result = field.getFloat(obj);
            createJson(fieldName, result,builder,i);
        }else if(typeName.equals("short")||
                typeName.equals("Short")){
            short result = field.getShort(obj);
            createJson(fieldName, result,builder,i);
        }else{
            Object result = field.get(obj);
            objArr2Str(result,builder,field,fieldName);
        }

    }

    private static<T1> void createOtherType(Object result, StringBuilder builder, Field field, String fieldName2) {

        if (result instanceof List){
            String fieldName = field.getName();
            builder.append(",\"");
            builder.append(fieldName);
            builder.append("\":");
            builder.append("[");
            for (T1 t: (List<T1>)result){
                try {
                    paraseJson(t,null);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            builder.append("]");
        }else if(result instanceof Object){
            try {
                T1 t = (T1) result;
                paraseJson(t,fieldName2);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }else{
            T1 t = (T1) result;
            try {
                paraseJson(t,null);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private static void createJson4Str(String fieldName, String result,StringBuilder builder,int i) {

        builder.append(",");
        builder.append("\"");
        builder.append(fieldName);
        builder.append("\":");
        if (result!=null)
        builder.append("\"");
        builder.append(result);
        if (result!=null)
        builder.append("\"");
    }

    private static void createJson(String fieldName, Object result,StringBuilder builder,int i) {

        builder.append(",");
        builder.append("\"");
        builder.append(fieldName);
        builder.append("\":");

        if (result instanceof Integer){
            builder.append((int) result);
        }else if(result instanceof Double){
            builder.append((double) result);
        }else if(result instanceof Long){
            builder.append((long) result);
        }else if (result instanceof Boolean){
            builder.append((boolean) result);
        }else if (result instanceof Byte){
            builder.append((byte)result);
        }else if(result instanceof Float){
            builder.append((float)result);
        } else if(result instanceof Character){
            builder.append((char)result);
        }else if(result instanceof Short){
            builder.append((short)result);
        }
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
