package com.example.app1;

import org.parceler.Parcel;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Parcel
public class Payload{
     String device_id;
     String time_stamp;
     String temperature;
     String humidity;
     String barometric;
     String luminostiy;

    public Payload() {}

    public Payload(String device_id, String time_stamp, String temperature, String humidity, String barometric, String luminostiy) {
        this.device_id = device_id;
        this.time_stamp = time_stamp;
        this.temperature = temperature;
        this.humidity = humidity;
        this.barometric = barometric;
        this.luminostiy = luminostiy;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(String time_stamp) {
        this.time_stamp = time_stamp;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getBarometric() {
        return barometric;
    }

    public void setBarometric(String barometric) {
        this.barometric = barometric;
    }

    public String getLuminostiy() {
        return luminostiy;
    }

    public void setLuminostiy(String luminostiy) {
        this.luminostiy = luminostiy;
    }

    public void parseFirebaseEntryValues(String values) {
        List<String> readings = Arrays.asList(values.split(" "));
        this.device_id = readings.get(0);
        this.time_stamp = readings.get(1) + " " + readings.get(2);
        this.temperature = readings.get(3);
        this.humidity = readings.get(4);
        this.luminostiy = readings.get(5);
        this.barometric = readings.get(6);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payload payload = (Payload) o;
        return Objects.equals(device_id, payload.device_id) &&
                Objects.equals(time_stamp, payload.time_stamp);
    }

    @Override
    public String toString() {
        return device_id + " " + time_stamp + " " + temperature + " " + humidity + " " + barometric + " " + luminostiy;
    }

}
