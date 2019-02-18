package com.example.shim.simpledelivery.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

//네이버 지도 Api의 response를 받기 위한 클래스

public class ReverseGeoResponse {

    @SerializedName("status")
    @Expose
    private Status status;
    @SerializedName("results")
    @Expose
    private List<Result> results = null;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }


    public class Addition0 {

        @SerializedName("type")
        @Expose
        private String type;
        @SerializedName("value")
        @Expose
        private String value;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

    }


    public class Area0 {

        @SerializedName("name")
        @Expose
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }


    public class Area1 {

        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("alias")
        @Expose
        private String alias;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

    }


    public class Area2 {

        @SerializedName("name")
        @Expose
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }


    public class Area3 {

        @SerializedName("name")
        @Expose
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }

    public class Area4 {

        @SerializedName("name")
        @Expose
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }

    public class Land {

        @SerializedName("type")
        @Expose
        private String type;
        @SerializedName("number1")
        @Expose
        private String number1;
        @SerializedName("number2")
        @Expose
        private String number2;
        @SerializedName("addition0")
        @Expose
        private Addition0 addition0;
        @SerializedName("name")
        @Expose
        private String name;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getNumber1() {
            return number1;
        }

        public void setNumber1(String number1) {
            this.number1 = number1;
        }

        public String getNumber2() {
            return number2;
        }

        public void setNumber2(String number2) {
            this.number2 = number2;
        }

        public Addition0 getAddition0() {
            return addition0;
        }

        public void setAddition0(Addition0 addition0) {
            this.addition0 = addition0;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }


    public class Region {

        @SerializedName("area0")
        @Expose
        private Area0 area0;
        @SerializedName("area1")
        @Expose
        private Area1 area1;
        @SerializedName("area2")
        @Expose
        private Area2 area2;
        @SerializedName("area3")
        @Expose
        private Area3 area3;
        @SerializedName("area4")
        @Expose
        private Area4 area4;

        public Area0 getArea0() {
            return area0;
        }

        public void setArea0(Area0 area0) {
            this.area0 = area0;
        }

        public Area1 getArea1() {
            return area1;
        }

        public void setArea1(Area1 area1) {
            this.area1 = area1;
        }

        public Area2 getArea2() {
            return area2;
        }

        public void setArea2(Area2 area2) {
            this.area2 = area2;
        }

        public Area3 getArea3() {
            return area3;
        }

        public void setArea3(Area3 area3) {
            this.area3 = area3;
        }

        public Area4 getArea4() {
            return area4;
        }

        public void setArea4(Area4 area4) {
            this.area4 = area4;
        }

    }

    public class Result {

        @SerializedName("region")
        @Expose
        private Region region;
        @SerializedName("land")
        @Expose
        private Land land;

        public Region getRegion() {
            return region;
        }

        public void setRegion(Region region) {
            this.region = region;
        }

        public Land getLand() {
            return land;
        }

        public void setLand(Land land) {
            this.land = land;
        }

    }


    public class Status {

        @SerializedName("code")
        @Expose
        private Integer code;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("message")
        @Expose
        private String message;

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

    }

}
