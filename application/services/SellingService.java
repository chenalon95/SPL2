package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.*;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Selling service in charge of taking orders from customers.
 * Holds a reference to the {@link MoneyRegister} singleton of the store.
 * Handles {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link Inventory}.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class SellingService extends MicroService {

	private MoneyRegister register;
	private int currentTick;

	/**
	 * Selling Service constructor.
	 */
	public SellingService() {
		super("SellingService");
		register = MoneyRegister.getInstance();
		currentTick = 0;
	}

	@Override
	protected void initialize() {

		subscribeBroadcast(TerminateBroadcast.class,ev->{
			this.terminate();
		});
		subscribeBroadcast(TickBroadcast.class, ev -> {
			currentTick=ev.getTimePassed();
		});
		subscribeEvent(BookOrderEvent.class, ev -> {
			int proccessTick=currentTick;
			OrderReceipt receipt = null;
			OrderResult isAvailable;
			int price;
			boolean flag = false;
			synchronized (ev.getCustomer()) {
				//check if book is available
				price = (int) sendEvent(new CheckAvailabilityEvent(ev.getBookTitle(), ev.getCustomer())).get();
				if (price!=-1 && ev.getCustomer().getAvailableCreditAmount() - price >= 0) { //check if customer has enough money
					isAvailable = (OrderResult)sendEvent(new GetBookEvent(ev.getBookTitle())).get();//takes book from inventory
					if (isAvailable==OrderResult.SUCCESSFULLY_TAKEN) {
						register.chargeCreditCard(ev.getCustomer(), price);//charge via money register
						flag = true;
					}
				}
			}
			if (flag) { //if bought
				receipt = new OrderReceipt(ev.getCustomer(),ev.getBookTitle(),price,currentTick,ev.getOrderTick(), proccessTick,this.getName());
				register.file(receipt);
				sendEvent(new DeliveryEvent(ev.getCustomer().getDistance(),ev.getCustomer().getAddress()));
			}
			complete(ev, receipt);
		});
	}
}
