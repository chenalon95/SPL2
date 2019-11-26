package bgu.spl.mics.application.services;


import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;

import java.util.Timer;
import java.util.TimerTask;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link Tick Broadcast}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{

	private int speed;
	private int duration;
	private Timer timer;
	private int count;
	private TickBroadcast tickBroadCast;

	/**
	 * Time Service constructor.
	 * @param Speed of the timer.
	 * @param duration of the program.
	 */
	public TimeService(int Speed, int duration) {
		super("SellingService");
		count= 1;
		this.speed=Speed;
		this.duration=duration;


	}

	@Override
	protected void initialize() {
		subscribeBroadcast(TerminateBroadcast.class,ev->{
			terminate();
		});

		timer=new Timer();

		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (count==duration){ //if is last tick
					timer.cancel();
					timer.purge();
					sendBroadcast(new TerminateBroadcast());
					return;
				}else{
					tickBroadCast= new TickBroadcast(speed,count, duration);
					sendBroadcast(tickBroadCast);
				}
				count++;

			}
		},0,speed);

	}

}
