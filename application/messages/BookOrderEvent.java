package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.Customer;

public class BookOrderEvent implements Event {
    private Customer customer;
    private String book;
    private int orderTick;//tick in which the customer ordered the book

    public BookOrderEvent(Customer customer, String book, int tick){
        this.book=book;
        this.customer=customer;
        this.orderTick=tick;

    }

    public int getOrderTick(){
        return orderTick;
    }
    public  String getBookTitle(){

        return book;
    }
    public  Customer getCustomer(){

        return customer;
    }



    @Override
    public String toString() {
        return "BookOrderEvent";
    }
}
