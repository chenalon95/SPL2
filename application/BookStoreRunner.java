package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;

import java.util.Scanner;

/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class BookStoreRunner {
    public static void main(String[] args) {

        FileRW.getInstance().setFile(args[0]); // create the store objects.

        try {
            FileRW.getInstance().getTimerThread().join(); // wait for the timer thread to terminate.
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        FileRW.getInstance().printCustomers(args[1]); // print the store customers.
        Inventory.getInstance().printInventoryToFile(args[2]); // print the inventory.
        MoneyRegister.getInstance().printOrderReceipts(args[3]); // print order receipts.
        FileRW.getInstance().printMoneyRegister(args[4]); // print the Money Register of the store.





    }
}

