
package com.example;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Example {

    @SerializedName("coord")
    @Expose
    private com.example.Coord coord;
    @SerializedName("weather")
    @Expose
    private List<com.example.Weather> weather = null;
    @SerializedName("base")
    @Expose
    private String base;
    @SerializedName("main")
    @Expose
    private com.example.Main main;
    @SerializedName("wind")
    @Expose
    private com.example.Wind wind;
    @SerializedName("clouds")
    @Expose
    private com.example.Clouds clouds;
    @SerializedName("dt")
    @Expose
    private Integer dt;
    @SerializedName("sys")
    @Expose
    private com.example.Sys sys;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("cod")
    @Expose
    private Integer cod;

    public com.example.Coord getCoord() {
        return coord;
    }

    public void setCoord(com.example.Coord coord) {
        this.coord = coord;
    }

    public List<com.example.Weather> getWeather() {
        return weather;
    }

    public void setWeather(List<com.example.Weather> weather) {
        this.weather = weather;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public com.example.Main getMain() {
        return main;
    }

    public void setMain(com.example.Main main) {
        this.main = main;
    }

    public com.example.Wind getWind() {
        return wind;
    }

    public void setWind(com.example.Wind wind) {
        this.wind = wind;
    }

    public com.example.Clouds getClouds() {
        return clouds;
    }

    public void setClouds(com.example.Clouds clouds) {
        this.clouds = clouds;
    }

    public Integer getDt() {
        return dt;
    }

    public void setDt(Integer dt) {
        this.dt = dt;
    }

    public com.example.Sys getSys() {
        return sys;
    }

    public void setSys(com.example.Sys sys) {
        this.sys = sys;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCod() {
        return cod;
    }

    public void setCod(Integer cod) {
        this.cod = cod;
    }

}
