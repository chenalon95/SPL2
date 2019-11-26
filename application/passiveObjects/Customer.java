package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive data-object representing a customer of the store.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class Customer implements Serializable {

	private String name;
	private int id;
	private String address;
	private int distance;
	private List <OrderReceipt> Receipts;
	private int availableAmountInCreditCard;
	private int creditCard;//credit card id


	/**
	 * Customer Constructor.
	 * @param name of the customer.
	 * @param id of the customer.
	 * @param address of the customer.
	 * @param distance from the store.
	 * @param creditCard number.
	 * @param amount of money in credit card.
	 */
	public Customer(String name, int id, String address, int distance, int creditCard,int amount){
		this.name=name;
		this.id=id;
		this.address=address;
		this.distance=distance;
		this.creditCard=creditCard;
		this.availableAmountInCreditCard=amount;
		this.Receipts=new LinkedList<>();

	}

	/**
	 * Retrieves the name of the customer.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Retrieves the ID of the customer  .
	 */
	public int getId() {
		return id;
	}

	/**
	 * Retrieves the address of the customer.
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * Retrieves the distance of the customer from the store.
	 */
	public int getDistance() {
		return distance;
	}


	/**
	 * Retrieves a list of receipts for the purchases this customer has made.
	 * <p>
	 * @return A list of receipts.
	 */
	public List<OrderReceipt> getCustomerReceiptList() {
		return Receipts;
	}

	/**
	 * Retrieves the amount of money left on this customers credit card.
	 * <p>
	 * @return Amount of money left.
	 */
	public int getAvailableCreditAmount() {
		return availableAmountInCreditCard;
	}


	/**
	 * Sets the credit card amount.
	 * @param amount of the credit card.
	 */
	public void setCreditAmount(int amount){
		availableAmountInCreditCard=amount;

	}

	/**
	 * Retrieves this customers credit card serial number.
	 */
	public int getCreditNumber() {
		return creditCard;
	}


	/**
	 * Adds a receipt to the customre's list of receipts.
	 * @param r the receipt to be added.
	 */
	public void setReceipts(OrderReceipt r){
		Receipts.add(r);
	}


}
