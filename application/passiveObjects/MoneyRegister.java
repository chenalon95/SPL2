package bgu.spl.mics.application.passiveObjects;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive object representing the store finance management.
 * It should hold a list of receipts issued by the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class MoneyRegister implements Serializable {

	private ConcurrentLinkedQueue<OrderReceipt> Orders;
	private AtomicInteger TotalEarnings;


	/**
	 * Singleton design.
	 */
	private static class SingletonHolder {
		private static MoneyRegister instance = new MoneyRegister();
	}

	/**
	 * Money Register's private constructor.
	 */
	private MoneyRegister() {
		TotalEarnings= new AtomicInteger(0);
		Orders=new ConcurrentLinkedQueue<>();
	}

	/**
	 * Retrieves the single instance of this class.
	 */
	public static MoneyRegister getInstance() {
		return SingletonHolder.instance;
	}

	/**
	 * Saves an order receipt in the money register.
	 * <p>
	 * @param r		The receipt to save in the money register.
	 */
	public void file (OrderReceipt r) {
		if(r!=null)
			Orders.add(r);
	}

	/**
	 * Retrieves the current total earnings of the store.
	 */
	public int getTotalEarnings() {
		return TotalEarnings.get();
	}

	/**
	 * Charges the credit card of the customer a certain amount of money.
	 * <p>
	 * @param amount 	amount to charge
	 */
	public void chargeCreditCard(Customer c, int amount) {
		if (c.getAvailableCreditAmount()>=amount){
			c.setCreditAmount(c.getAvailableCreditAmount()-amount);
		}
		TotalEarnings.set(getTotalEarnings()+amount);
	}


	/**
	 * Prints to a file named @filename a serialized object List<OrderReceipt> which holds all the order receipts
	 * currently in the MoneyRegister
	 * This method is called by the main method in order to generate the output.
	 */
	public void printOrderReceipts(String filename) {

		List<OrderReceipt> toPrint = new ArrayList<>(Orders);
		try
		{ // try to write the receipts into a file.
			FileOutputStream fos =
					new FileOutputStream(filename);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(toPrint);
			oos.close();
			fos.close();
		}catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
}
