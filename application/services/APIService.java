package bgu.spl.mics.application.services;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BookOrderEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * APIService is in charge of the connection between a client and the store.
 * It informs the store about desired purchases using {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class APIService extends MicroService {

	private int index;
	private Customer customer;
	private OrderSchedule[] orderSchedules;
	private List <Future <OrderReceipt> >Futures;
	private boolean flag;

	/**
	 * API Service Constructor.
	 * @param customer using API to order.
	 * @param orderSchedules of the customer.
	 */
	public APIService(Customer customer, OrderSchedule[] orderSchedules) {
		super("APIService");
		this.orderSchedules = Arrays.copyOf(orderSchedules, orderSchedules.length);
		orderArray();
		this.customer = customer;
		Futures= new ArrayList<>();
		index = 0;
		flag=true;
	}


	@Override
	protected void initialize() {

		subscribeBroadcast(TickBroadcast.class, ev -> {
			int i = index;
			while (i < orderSchedules.length && flag) {
				if (ev.getTimePassed() >= orderSchedules[i].getTick()) {
					if(i==orderSchedules.length-1) {
						flag = false;
						index=i+1;
					}
					Future<OrderReceipt> future = (Future<OrderReceipt>) sendEvent(new BookOrderEvent(customer, orderSchedules[i].getBookTitle(), orderSchedules[i].getTick()));
					Futures.add(future);
					i++;
				} else {
					index=i;
					break;
				}
			}

			for(int j=0;j<Futures.size();j++){
				if(Futures.get(j).get()!=null)
					customer.setReceipts(Futures.get(j).get());
				Futures.remove(Futures.get(j));
			}
		});
		subscribeBroadcast(TerminateBroadcast.class, ev -> {
			this.terminate();
		});
	}

	/**
	 * Sorts the order schedule array.
	 */
	private void orderArray() {
		Arrays.sort(orderSchedules, (OrderSchedule::compareTo));
	}

}


