package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;

import java.io.*;
import java.util.*;

/**
 * This class is for reading the json file
 * and creating the program objects.
 */
public class FileRW {

    private String path;
    private FileReader reader;

    private ObjectHolder ObjectHolder;


    private BookInventoryInfo[] books;
    private DeliveryVehicle[] deliveryVehicles;
    private TimeService timer;
    private SellingService[] sellingService;
    private LogisticsService[] logisticsServices;
    private ResourceService[] resourcesService;
    private Customer[] customers;
    private APIService[] APIservices;
    private InventoryService[] inventoryService;
    private Thread timerThread;


    /**
     * Singleton design.
     */
    private static class SingletonHolder {
        private static FileRW instance = new FileRW();

    }
    private FileRW() {

    }

    /**
     * Retrieves the single instance of this class.
     */
    public static FileRW getInstance()
    {
        return SingletonHolder.instance;
    }

    /**
     * This method sets a filename for reading.
     * @param file name.
     */
    public void setFile(String file)  {
        path=file;
        try {
            File file1 = new File(file);
            if (file1.exists()) {
                reader = new FileReader(path);
            }
        }
        catch (Exception e){

        }
        read();
        createObjects();
        runServices();

        try {reader.close(); }
        catch(IOException e){ }
    }

    /**
     * This method reads from file.
     */
    private void read(){
        getObjects();
    }

    /**
     * This method creates the inner store object.
     */
    private void getObjects() {
        {
            ObjectHolder = new Gson().fromJson(reader, ObjectHolder.class);
        }
    }

    /**
     * ObjectHolder inner class for the Gson.
     */
    private class ObjectHolder{
        private Book [] initialInventory;
        private initialResources[] initialResources;
        private services services;
    }

    /**
     * Book inner class.
     */
    private class Book {
        private String bookTitle;
        private int amount;
        private int price;
    }
    /**
     * initialResources inner class.
     */
    private class initialResources{
        private vehicle [] vehicles;
    }

    /**
     * vehicle inner class.
     */
    private class vehicle{
        private int license;
        private int speed;
    }

    /**
     * services inner class.
     */
    private class services{
        private time time;
        private int selling;
        private int inventoryService;
        private int logistics;
        private int resourcesService;
        private customer [] customers;

    }

    /**
     * time inner class
     */
    private class time{
        private int speed;
        private int duration;
    }

    /**
     * customer inner class.
     */
    private class customer{
        private int id;
        private String name;
        private String address;
        private int distance;
        private creditCard creditCard;
        private orderSchedule []orderSchedule;

        private class orderSchedule{
            private String bookTitle;
            private int tick;

        }
    }

    /**
     * creditCard inner class.
     */
    private class creditCard{
        private int number;
        private int amount;
    }


    /**
     * This method creates the book store objects.
     */
    private void createObjects() {

        loadPassiveObjects();

        loadServices();

        loadCustomers();


    }

    /**
     * This method loads the Passive Objects.
     */
    private void loadPassiveObjects(){
        books= new BookInventoryInfo[ObjectHolder.initialInventory.length]; //create the store books.
        BookInventoryInfo book;
        for (int i=0;i<ObjectHolder.initialInventory.length;i++){
            books[i]=new BookInventoryInfo(ObjectHolder.initialInventory[i].bookTitle,ObjectHolder.initialInventory[i].price,ObjectHolder.initialInventory[i].amount);
        }
        Inventory.getInstance().load(books); //load the store books in the inventory singleton.

        deliveryVehicles= new DeliveryVehicle[ObjectHolder.initialResources[0].vehicles.length]; //create the store vehicles.

        for (int i=0;i<ObjectHolder.initialResources[0].vehicles.length;i++) {
            deliveryVehicles[i]  = new DeliveryVehicle(ObjectHolder.initialResources[0].vehicles[i].license, ObjectHolder.initialResources[0].vehicles[i].speed);

        }
        ResourcesHolder.getInstance().load(deliveryVehicles); //load the store vehicles in the ResourcesHolder singleton.

    }

    /**
     * This method loads the services.
     */
    private void loadServices(){
        timer = new TimeService(ObjectHolder.services.time.speed,ObjectHolder.services.time.duration); //create the store's time service.
        sellingService= new SellingService[ObjectHolder.services.selling]; //create the store's selling services.
        for (int i=0;i<sellingService.length;i++) {
            sellingService[i] = new SellingService();
        }
        logisticsServices= new LogisticsService[ObjectHolder.services.logistics]; //create the store's logistics services.
        for (int i=0;i<logisticsServices.length;i++) {
            logisticsServices[i] = new LogisticsService();
        }
        resourcesService= new ResourceService[ObjectHolder.services.resourcesService]; //create the store's resources services.
        for (int i=0;i<resourcesService.length;i++) {
            resourcesService[i] = new ResourceService();
        }
        inventoryService = new InventoryService[ObjectHolder.services.inventoryService]; //create the store's inventory services.
        for (int i=0;i<inventoryService.length;i++) {
            inventoryService[i] = new InventoryService();
        }
    }

    /**
     * This method loads the customers.
     */
    private void loadCustomers(){
        customers = new Customer[ObjectHolder.services.customers.length]; //create the store's customers.
        APIservices= new APIService[ObjectHolder.services.customers.length]; //create the store's API services, one for each customer.
        OrderSchedule[] orderSchedules; //create the customer's order schedules.
        for (int i=0;i<customers.length;i++){
            orderSchedules =new OrderSchedule[ObjectHolder.services.customers[i].orderSchedule.length];
            for (int j=0;j<ObjectHolder.services.customers[i].orderSchedule.length;j++) {
                orderSchedules[j] = new OrderSchedule(ObjectHolder.services.customers[i].orderSchedule[j].tick,ObjectHolder.services.customers[i].orderSchedule[j].bookTitle);
            }
            customers[i]=new Customer(ObjectHolder.services.customers[i].name,ObjectHolder.services.customers[i].id,ObjectHolder.services.customers[i].address,ObjectHolder.services.customers[i].distance,ObjectHolder.services.customers[i].creditCard.number,ObjectHolder.services.customers[i].creditCard.amount);
            APIservices[i]=new APIService(customers[i],orderSchedules);
        }
    }

    /**
     * This method run all services as threads.
     */
    private void runServices(){
        run1();
        run2();


    }

    /**
     * This method runs the first threads.
     */
    private void run1(){

        Thread t;
        for (int i=0;i<inventoryService.length;i++){ // run inventory threads
            t=new Thread(inventoryService[i]);
            t.setName("inventoryService "+i);
            t.start();
        }
        for(int i=0;i<sellingService.length;i++){ // run selling threads
            t=new Thread(sellingService[i]);
            t.setName("sellingService "+i);
            t.start();
        }
        for(int i=0;i<logisticsServices.length;i++){ // run logistics threads
            t=new Thread(logisticsServices[i]);
            t.setName("logisticsServices "+i);
            t.start();
        }
    }

    /**
     * This method runs the other threads.
     */
    private void run2(){
        Thread t;
        for(int i=0;i<resourcesService.length;i++){ // run resources threads
            t=new Thread(resourcesService[i]);
            t.setName("resourcesService "+i);
            t.start();
        }
        for(int i=0;i<customers.length;i++){ // run API threads
            t=new Thread(APIservices[i]);
            t.setName("APIservices "+i);
            t.start();
        }

        t=new Thread(timer); // run timer thread
        t.setName("timerThread");
        t.start();
        timerThread=t;
    }

    /**
     * This method returns the timer thread.
     * @return timer thread.
     */
    public Thread getTimerThread(){
        return timerThread;
    }

    /**
     * This method prints the MoneyRegister of the store.
     * @param filename to print.
     */
    public void printMoneyRegister(String filename){
        try
        { // try to write the money register's information to a file.
            FileOutputStream fos =
                    new FileOutputStream(filename);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            MoneyRegister m = MoneyRegister.getInstance();
            oos.writeObject(m);
            oos.close();
            fos.close();
        }catch(IOException ioe)
        {
            ioe.printStackTrace();
        }
    }

    /**
     * This method prints the customers of the store.
     * @param filename to print.
     */
    public void printCustomers(String filename){

        HashMap<Integer,Customer> customersToPrint = new HashMap<>();
        for (int i=customers.length-1;i>=0;i--){
            customersToPrint.put(new Integer(customers[i].getId()),customers[i]);
        }

        try
        { // try to write customers into a file.
            FileOutputStream fos =
                    new FileOutputStream(filename);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(customersToPrint);
            oos.close();
            fos.close();
        }catch(IOException ioe)
        {
            ioe.printStackTrace();
        }
    }



}
