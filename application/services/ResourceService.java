package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AcquireVehicleEvent;
import bgu.spl.mics.application.messages.ReleaseVehicleEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * ResourceService is in charge of the store resources - the delivery vehicles.
 * Holds a reference to the {@link ResourcesHolder} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link MoneyRegister}, {@link Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ResourceService extends MicroService{



	/**
	 * Resource Holder Constructor.
	 */
	public ResourceService() {
		super("ResourceService");
	}

	@Override
	protected void initialize() {

		subscribeBroadcast(TerminateBroadcast.class, ev->{
			this.terminate();
		});

		subscribeEvent(AcquireVehicleEvent.class, ev->{
			complete(ev,ResourcesHolder.getInstance().acquireVehicle());
		});

		subscribeEvent(ReleaseVehicleEvent.class, ev->{
			DeliveryVehicle toRelease = ev.getVehicleToRelease();
			ResourcesHolder.getInstance().releaseVehicle(toRelease);
			complete(ev,"Vehicle released");


		});
	}



}
