package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;

import bgu.spl.mics.application.messages.CheckAvailabilityEvent;
import bgu.spl.mics.application.messages.GetBookEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.*;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * InventoryService is in charge of the book inventory and stock.
 * Holds a reference to the {@link Inventory} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */

public class InventoryService extends MicroService{


	private Inventory inventory;


	/**
	 * Inventory Service constructor.
	 */
	public InventoryService() {
		super("InventoryService");
		inventory= Inventory.getInstance();
	}

	@Override
	protected void initialize() {
		subscribeBroadcast(TerminateBroadcast.class, ev->{
			this.terminate();
		});

		subscribeEvent(CheckAvailabilityEvent.class,ev->{//checks if book is available
			int price=inventory.checkAvailabiltyAndGetPrice(ev.getBook());
			complete(ev, price);
		});

		subscribeEvent(GetBookEvent.class,ev->{ //takes a book from inventory
			complete (ev,inventory.take(ev.getBookName()));
		});


	}


}
