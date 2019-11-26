package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.Customer;

public class CheckAvailabilityEvent implements Event {
    private String book;

    public CheckAvailabilityEvent( String book, Customer c){
        this.book=book;
    }
    public String getBook(){
        return book;
    }

    @Override
    public String toString() {
        return "CheckAvailabilityEvent";
    }
}
