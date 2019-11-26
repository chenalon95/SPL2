package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

public class GetBookEvent implements Event {
    private String bookName;

    public GetBookEvent(String book){
        this.bookName=book;



    }

    public String getBookName(){
        return bookName;
    }
}
