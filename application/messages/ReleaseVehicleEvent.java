package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

public class ReleaseVehicleEvent implements Event {
    private DeliveryVehicle vehicleToRelease;

    public ReleaseVehicleEvent(DeliveryVehicle vehicle){
        this.vehicleToRelease=vehicle;
    }
    public DeliveryVehicle getVehicleToRelease(){
        return vehicleToRelease;
    }

    @Override
    public String toString() {
        return "ReleaseVehicleEvent";
    }
}
