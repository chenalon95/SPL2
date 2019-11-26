package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AcquireVehicleEvent;
import bgu.spl.mics.application.messages.DeliveryEvent;
import bgu.spl.mics.application.messages.ReleaseVehicleEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

/**
 * Logistic service in charge of delivering books that have been purchased to customers.
 * Handles {@link DeliveryEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LogisticsService extends MicroService {


	/**
	 * Logistic Service constructor.
	 */
	public LogisticsService() {
		super("LogisticsService");
	}


	@Override
	protected void initialize() {
		subscribeBroadcast(TerminateBroadcast.class, ev->{
			this.terminate();
		});

		subscribeEvent(DeliveryEvent.class, ev-> {
			DeliveryVehicle vehicle;
			vehicle=(DeliveryVehicle)sendEvent(new AcquireVehicleEvent()).get();//tries to acquire a vehicle
			if (vehicle != null) {
				vehicle.deliver(ev.getAddress(), ev.getDistance());//execute delivery
			}
			sendEvent(new ReleaseVehicleEvent(vehicle));//return vehicle
			complete(ev, "Delivery completed");

		});

	}

}
