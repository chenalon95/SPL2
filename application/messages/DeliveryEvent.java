package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

public class DeliveryEvent  implements Event {
    private int distance;
    private String address;


    public DeliveryEvent(int distance, String address){
        this.distance=distance;
        this.address=address;
    }

    public int getDistance(){
        return distance;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return "DeliveryEvent";
    }
}
