package bgu.spl.mics;

import bgu.spl.mics.application.messages.*;

import bgu.spl.mics.example.messages.ExampleBroadcast;
import bgu.spl.mics.example.messages.ExampleEvent;

import java.util.HashMap;
import java.util.concurrent.*;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 *
 * This class is designed by Semaphore in order to make the class thread safe.
 */
public class MessageBusImpl implements MessageBus {


	//values for the Semaphore.
	private int activeSendEvents; //number of the threads in SendEvent method.
	private int waitingSendEvents; //number of the threads that wait for SendEvent method.
	private int activeUnregisters; //number of the threads in Unregister method.
	private int waitingUnregisters; //number of the threads that wait for Unregister method.
private Object lock;

	//each key is a string m, representing a Message type , and has a queue of services that can handle it.
	private ConcurrentHashMap<Class<? extends Message>, ConcurrentLinkedQueue<MicroService>> whosTurn;


	//each key i a microService m, and has a queue into which we insert events for m to handle.
	private ConcurrentHashMap<MicroService, BlockingQueue<Message>> queuesOfServices;

	//each key i an event m, and has a future to handle.
	private ConcurrentHashMap<Event, Future> futures;
	private Object lockSubscribeEvent,lockSubscribeBroadcast;

	/**
	 * Singleton design pattern
	 */
	private static class SingletonHolder {
		private static MessageBusImpl instance = new MessageBusImpl();
	}

	/**
	 * Private MessageBusImpl constructor.
	 */
	private MessageBusImpl() {
		whosTurn = new ConcurrentHashMap<>();
		queuesOfServices = new ConcurrentHashMap<>();
		futures = new ConcurrentHashMap<>();
		lockSubscribeBroadcast = new Object();
		lockSubscribeEvent = new Object();
		activeSendEvents = 0;
		waitingSendEvents = 0;
		activeUnregisters = 0;
		waitingUnregisters = 0;
		lock=new Object();

		init();

	}

	/**
	 * This method initials the messages queues in the whosTurn HashMap.
	 */
	private void init() {
		whosTurn.put(AcquireVehicleEvent.class, new ConcurrentLinkedQueue<>());
		whosTurn.put(BookOrderEvent.class, new ConcurrentLinkedQueue<>());
		whosTurn.put(CheckAvailabilityEvent.class, new ConcurrentLinkedQueue<>());
		whosTurn.put(DeliveryEvent.class, new ConcurrentLinkedQueue<>());
		whosTurn.put(ReleaseVehicleEvent.class, new ConcurrentLinkedQueue<>());
		whosTurn.put(GetBookEvent.class,new ConcurrentLinkedQueue<>());
		whosTurn.put(TerminateBroadcast.class,new ConcurrentLinkedQueue<>());
		whosTurn.put(TickBroadcast.class,new ConcurrentLinkedQueue<>());



	}

	/**
	 * GetInstance Method of the singleton design.
	 * @return SingletonHolder instance.
	 */
	public static MessageBusImpl getInstance() {
		return SingletonHolder.instance;
	}


	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		whosTurn.get(type).add(m);
	}


	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
			if(whosTurn.get(type)!=null)
				whosTurn.get(type).add(m);



	}


	@Override
	public void sendBroadcast(Broadcast b) {
			if (whosTurn.get(b.getClass()).size() > 0) {
				for (MicroService microService : whosTurn.get(b.getClass())) {
					if (queuesOfServices.get(microService) != null && !queuesOfServices.get(microService).contains(b)) {
						queuesOfServices.get(microService).add(b);
					}
				}
			}

	}


	@Override
	public <T> void complete(Event<T> e, T result) {
		futures.get(e).resolve(result);
	}

	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		beforeSendEvent();
		Future ans=send1(e);
		afterSendEvent();

		return ans;

	}

	/**
	 * This method sends the event to the match service.
	 * @param e Event to handle.
	 * @param <T> The type of the event.
	 * @return the event's future result.
	 */
	private <T>Future<T>send1(Event<T> e){
		MicroService temp;
		//find the next Service by round robin order

		if (whosTurn.get(e.getClass()).size() > 0) {
			synchronized (whosTurn.get(e.getClass())) {
				temp = whosTurn.get(e.getClass()).poll();
				whosTurn.get(e.getClass()).add(temp);
			}
			futures.put(e,new Future());
			queuesOfServices.get(temp).add(e);
		}
		else {
			return null;
		}

		return futures.get(e);



	}

	@Override
	public void register(MicroService m) {
		queuesOfServices.put(m, new LinkedBlockingQueue<>());
	}

	@Override
	public void unregister(MicroService m) {
		beforeUnregister();
		unregister1(m);
		afterUnregister();

	}

	/**
	 * This method unregisters the microservice.
	 * @param m microservice to handle.
	 */
	private void unregister1(MicroService m ){
		queuesOfServices.remove(m);
		//remove the microService from all messages' round robin queue
		for (HashMap.Entry<Class<? extends Message>, ConcurrentLinkedQueue<MicroService>> entry : whosTurn.entrySet()) {
			entry.getValue().remove(m);
		}
		for(HashMap.Entry<Event,Future> eventFutureEntry: futures.entrySet()){
			eventFutureEntry.getValue().resolve(null);
		}

	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		return queuesOfServices.get(m).take();

	}

	/**
	 * Semaphore implementation:
	 * This method makes sure that no microservice will unregister while
	 * the send event is not done.
	 */
	private void beforeSendEvent(){
		synchronized (lock) {
			++waitingSendEvents;
			while (!allowSendEvent())
				try {
					lock.wait();
				} catch (InterruptedException ex) {
				}
			--waitingSendEvents;
			++activeSendEvents;
		}
	}

	/**
	 * afterSendEvent method in Semaphore implementation.
	 */
	private void afterSendEvent(){
		synchronized (lock) {
			--activeSendEvents;
			lock.notifyAll();  // Will unblock any pending writer
		}
	}

	/**
	 * beforeUnregister method in Semaphore implementation.
	 */
	private void beforeUnregister(){
		synchronized (lock) {
			++waitingUnregisters;
			while (!allowUnregister())
				try {
					lock.wait();
				} catch (InterruptedException ex) {
				}
			--waitingUnregisters;
			++activeUnregisters;
		}
	}

	/**
	 * afterUnregister method in Semaphore implementation.
	 */
	private void afterUnregister() {
		synchronized (lock) {
			--activeUnregisters;
			lock.notifyAll(); // Will unblock waiting writers and waiting readers
		}
	}

	/**
	 * allowSendEvent method in Semaphore implementation.
	 * @return whether the SendEvent is allowed to be used.
	 */
	private boolean allowSendEvent(){
		return waitingUnregisters == 0 && activeUnregisters == 0;
	}

	/**
	 * allowUnregister method in Semaphore implementation.
	 * @return whether the Unregister is allowed to be used.
	 */
	private boolean allowUnregister(){
		return activeUnregisters == 0 && activeSendEvents == 0;
	}


}
