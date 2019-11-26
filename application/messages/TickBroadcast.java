package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

public class TickBroadcast implements Broadcast {

    private int Speed;
    private int TimePassed;
    private int TotalTime;

    public TickBroadcast(int speed,int count, int duration ){
        this.TimePassed=count;
        this.TotalTime=duration;
        this.Speed=speed;
    }


    public int getTimePassed(){
        return TimePassed;
    }
    public int getTotalTime(){
        return TotalTime;
    }

    @Override
    public String toString() {
        return "TickBroadcast";
    }
}
