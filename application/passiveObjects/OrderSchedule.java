package bgu.spl.mics.application.passiveObjects;

/**
 * Class representing an order.
 * Consists of a book title to be ordered and
 * a tick in which to execute the order.
 */
public class OrderSchedule implements Comparable <OrderSchedule> {

    private String bookTitle;
    private int tick;

    /**
     * Order Schedule Constructor.
     * @param tick time to order book.
     * @param bookTitle to be ordered.
     */
    public OrderSchedule(int tick, String bookTitle){
        this.tick=tick;
        this.bookTitle=bookTitle;
    }

    /**
     * Returns book title.
     * @return the book's title.
     */
    public String getBookTitle(){
        return bookTitle;
    }

    /**
     * Returns the time tick of the order schedule.
     * @return the time tick.
     */
    public int getTick(){
        return tick;
    }

    @Override
    public int compareTo(OrderSchedule o) {
        //a key of comparing to order schedules.
        return tick-o.getTick();
    }
}
