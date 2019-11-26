package bgu.spl.mics.application.passiveObjects;


import bgu.spl.mics.application.FileRW;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Passive data-object representing the store inventory.
 * It holds a collection of {@link BookInventoryInfo} for all the
 * books in the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class Inventory {

	private ConcurrentHashMap<String,BookInventoryInfo> Stock;

	/**
	 * Singleton design.
	 */
	private static class SingletonHolder {
		private static Inventory instance = new Inventory();
	}
	private Inventory() {
		this.Stock=new ConcurrentHashMap<>();
	}

	/**
	 * Retrieves the single instance of this class.
	 */
	public static Inventory getInstance() {
		return SingletonHolder.instance;
	}

	/**
	 * Initializes the store inventory. This method adds all the items given to the store
	 * inventory.
	 * <p>
	 * @param inventory 	Data structure containing all data necessary for initialization
	 * 						of the inventory.
	 */
	public void load (BookInventoryInfo[ ] inventory ) {
		for (int i = 0; i < inventory.length; i++) {
			if (!Stock.containsKey(inventory[i].getBookTitle()))
				Stock.put(inventory[i].getBookTitle(), inventory[i]);
		}
	}

	/**
	 * Attempts to take one book from the store.
	 * <p>
	 * @param book 		Name of the book to take from the store
	 * @return 	an {@link Enum} with options NOT_IN_STOCK and SUCCESSFULLY_TAKEN.
	 * 			The first should not change the state of the inventory while the
	 * 			second should reduce by one the number of books of the desired type.
	 */
	public OrderResult take (String book) {
		synchronized (Stock.get(book)){
			if (checkAvailabiltyAndGetPrice(book)!=-1){
				Stock.get(book).setAmountInInventory(Stock.get(book).getAmountInInventory().get()-1);
				//if (Stock.get(book).getAmountInInventory().get()==0)
					//Stock.remove(book);
				return OrderResult.SUCCESSFULLY_TAKEN;
			}
		}
		return OrderResult.NOT_IN_STOCK;
	}



	/**
	 * Checks if a certain book is available in the inventory.
	 * <p>
	 * @param book 		Name of the book.
	 * @return the price of the book if it is available, -1 otherwise.
	 */
	public int checkAvailabiltyAndGetPrice(String book) {
		if (Stock.containsKey(book))
                    if (Stock.get(book).getAmountInInventory().get()>0)
			return Stock.get(book).getPrice();
		return -1;

	}

	/**
	 *
	 * <p>
	 * Prints to a file name @filename a serialized object HashMap<String,Integer> which is a
	 * Map of all the books in the inventory. The keys of the Map (type {@link String})
	 * should be the titles of the books while the values (type {@link Integer}) should be
	 * their respective available amount in the inventory.
	 * This method is called by the main method in order to generate the output.
	 */
	public void printInventoryToFile(String filename) {
		HashMap<String,Integer> books = new HashMap<>();
		for(Map.Entry<String, BookInventoryInfo> entry : Stock.entrySet()) {
			String key = entry.getKey();
			BookInventoryInfo value = entry.getValue();
			books.put(key,value.getAmountInInventory().get());
		}

		try
		{ //Try to write the books into a file.
			FileOutputStream fos =
					new FileOutputStream(filename);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(books);
			oos.close();
			fos.close();
		}catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
	}

}
