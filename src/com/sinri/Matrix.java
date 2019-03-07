package com.sinri;

import java.util.ArrayList;

class Matrix<T> {
    private ArrayList<ArrayList<T>> data;

    public Matrix(){
        reset();
    }

    public Matrix(T[][] base){
        reset(base);
    }

    private void reset(){
        data=new ArrayList<>();
//        for(int x=0;x<size;x++){
//            ArrayList<T> row = new ArrayList<T>();
//            for(int y=0;y<size;y++){
//                row.add(null);
//            }
//            data.add(row);
//        }
    }

    private void reset(T[][] base){
        data=new ArrayList<>();
        for(int i=0;i<base.length;i++){
            for(int j=0;j<base[i].length;j++){
                setCell(i,j,base[i][j]);
            }
        }
    }

    public ArrayList<T> getRow(int x){
        return data.get(x);
    }

    public void setRow(int x,ArrayList<T> row){
        if(x+1>=data.size()){
            for(int i=data.size();i<=x;i++){
                data.add(new ArrayList<>());
            }
        }
        data.set(x,row);
    }

    public T getCell(int x,int y){
        return getCell(x,y,null);
    }

    private T getCell(int x, int y, T defaultValue){
        ArrayList<T> row;
        try {
            row = data.get(x);
        }catch (Exception e){
            // no such row
            return defaultValue;
        }
        try{
            return row.get(y);
        }catch (Exception e){
            return defaultValue;
        }
    }

    public void setCell(int x,int y,T value){
        if(x+1>=data.size()){
            for(int i=data.size();i<=x;i++){
                data.add(new ArrayList<>());
            }
        }

        ArrayList<T> row = data.get(x);

        if(y+1>=row.size()){
            for(int i=row.size();i<=y;i++){
                row.add(null);
            }
        }

        row.set(y,value);
    }

    public ArrayList<T> createNullRow(int length){
        ArrayList<T> row=new ArrayList<>();
        for(int i=0;i<length;i++)row.add(null);
        return row;
    }
}
