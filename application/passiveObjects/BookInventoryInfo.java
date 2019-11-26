package bgu.spl.mics.application.passiveObjects;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive data-object representing a information about a certain book in the inventory.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class BookInventoryInfo {

    private String bookTitle;
    private int price;
    private AtomicInteger amountInInventory;


    public BookInventoryInfo(){

    }

    /**
     * Book Inventory Info Constructor.
     * @param bookTitle of the book.
     * @param price of the book.
     * @param amountInInventory1 of the book.
     */
    public BookInventoryInfo(String bookTitle, int price, int amountInInventory1){
        this.bookTitle=bookTitle;
        this.price=price;
        amountInInventory=new AtomicInteger(amountInInventory1);
        // amountNotChecked=new AtomicInteger(amountInInventory1);

    }

    /**
     * Retrieves the title of this book.
     * <p>
     * @return The title of this book.
     */
    public String getBookTitle() {
        return bookTitle;
    }

    /**
     * Retrieves the amount of books of this type in the inventory.
     * <p>
     * @return amount of available books.
     */
    public AtomicInteger getAmountInInventory() {
        return amountInInventory;
    }

    /**
     * Changes the amount of a book in the inventory.
     * @param amount to be changed to.
     */
    public void setAmountInInventory(int amount) {
        amountInInventory.set(amount);
    }

    /**
     * Retrieves the price for  book.
     * <p>
     * @return the price of the book.
     */
    public int getPrice() {
        return price;
    }

    /**
     * Sets the price  of a book.
     * @param price of the book.
     */
    public void setPrice(int price) {
        this.price=price;
    }

    /**
     * Sets the book's title.
     * @param bookTitle of the book.
     */
    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

}
