package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.Future;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Passive object representing the resource manager.
 * You must not alter any of the given public methods of this class.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class ResourcesHolder {


	private ConcurrentLinkedQueue<DeliveryVehicle> vehicles; //collections of all vehicles

	private List<DeliveryVehicle> usedVehicles; //list of all vehicles currently in delivery

	private ConcurrentLinkedQueue<Future<DeliveryVehicle>> waiting;//list of people waiting for vehicles

	private static class SingletonHolder {
		private static ResourcesHolder instance = new ResourcesHolder();
	}

	/**
	 * Resource Holder Constructor.
	 */
	private ResourcesHolder() {
		this.vehicles=new ConcurrentLinkedQueue<>();
		this.usedVehicles=new ArrayList<>();
		this.waiting=new ConcurrentLinkedQueue<>();

	}

	/**
	 * Retrieves the single instance of this class.
	 */
	public static ResourcesHolder getInstance() {

		return SingletonHolder.instance;
	}

	/**
	 * Tries to acquire a vehicle and gives a future object which will
	 * resolve to a vehicle.
	 * <p>
	 * @return 	{@link Future<DeliveryVehicle>} object which will resolve to a
	 * 			{@link DeliveryVehicle} when completed.
	 */
	public Future<DeliveryVehicle> acquireVehicle() {
		Future<DeliveryVehicle> availableVehicle = new Future<>();
		DeliveryVehicle vehicle=vehicles.poll();

		if(vehicle!=null) {//there's an available vehicle
			if (waiting.size() == 0) {//no one else needs a delivery at the moment
				availableVehicle.resolve(vehicle);
			} else {
				waiting.add(availableVehicle);//this delivery waits for it's turn
				waiting.poll().resolve(vehicle);//give the vehicle to the first delivery waiting
			}
		}else{//no available car
			waiting.add(availableVehicle);
		}
		usedVehicles.add(vehicle);

		return availableVehicle;
	}

	/**
	 * Releases a specified vehicle, opening it again for the possibility of
	 * acquisition.
	 * <p>
	 * @param vehicle	{@link DeliveryVehicle} to be released.
	 */
	public void releaseVehicle(DeliveryVehicle vehicle) {
		if(waiting.size()>0){//someone's waiting for a car
			waiting.poll().resolve(vehicle);
		}else {
			vehicles.add(vehicle);
			usedVehicles.remove(vehicle);
		}

	}

	/**
	 * Receives a collection of vehicles and stores them.
	 * <p>
	 * @param vehicles	Array of {@link DeliveryVehicle} instances to store.
	 */
	public void load(DeliveryVehicle[] vehicles) {
		sort(vehicles);
		for (int i=0;i<vehicles.length;i++){
			this.vehicles.add(vehicles[i]);
		}
	}

	/**
	 * sorting the delivery vehicles.
	 * @param vehicles array to sort.
	 */
	private void sort(DeliveryVehicle[] vehicles){
		List <DeliveryVehicle> toSort=Arrays.asList(vehicles);
		Collections.sort(toSort, Comparator.comparingInt(DeliveryVehicle::getSpeed));
		toSort.toArray(vehicles);
	}

}
