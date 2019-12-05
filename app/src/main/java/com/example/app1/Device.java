package com.example.app1;

import android.os.Parcelable;

import org.parceler.Parcel;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Objects;

@Parcel
public class Device {
    String device_id;
    ArrayList<Payload> payloads;

    public Device() {
        payloads = new ArrayList<>();
    }

    public Device(String device_id) {
        payloads = new ArrayList<>();
        this.device_id = device_id;
    }

    public String getDevice_id() {
        return device_id;
    }

    public Payload getPayload(String date, String time) {
        for(Payload payload : payloads) {
            if (payload.getDate_stamp().equals(date) && payload.getTime_stamp().equals(time))
                return payload;
        }
        return null;
    }

    public ArrayList<Payload> getDayPayload(String date) {
        ArrayList<Payload> dayPayload = new ArrayList<>();
        for (Payload payload : payloads) {
            if (payload.getDate_stamp().equals(date))
                dayPayload.add(payload);
        }
        return dayPayload;
    }

    public ArrayList<Payload> getPayloads() {
        return payloads;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public void addToPayloads(Payload payload) {
        payloads.add(payload);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Device device = (Device) o;
        return Objects.equals(device_id, device.device_id);
    }
}
