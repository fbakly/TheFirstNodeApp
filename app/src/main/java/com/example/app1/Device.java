/**
 * @Class: Device
 * @Description: Device class which contains unique devices and their payloads
 * @Author: Fouad Elbakly
 */

package com.example.app1;

import org.parceler.Parcel;
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
