package philosophers;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.System.out;

public class Table {
	int numSeats;
	Thread[] phils;
	ReentrantLock[] forks;
	final Random r = new Random();
	final int  timesToEat;
	static final int MAXMSECS = 1000;
	final Logger log;
	
	class Philosopher implements Runnable{
		final int seat;  //where this philosopher is seated, seats numbered from 0 to numSeats-1
	    int timesToEat; 
	    private ReentrantLock leftFork;
	    private ReentrantLock rightFork; //the two forks that this guy can use, one on his left and right, shared between him and his adjacent phils
		
		Philosopher(int seat, int timesToEat){
			this.seat = seat;
			this.timesToEat = timesToEat;
			this.leftFork = forks[seat]; //left fork can just be "their" fork
			if(seat + 1 == numSeats) //right fork you can use the person to the right of you's fork
			{
				this.rightFork = forks[0]; //circular table, go back to the 0th person
			}
			else
			{
				this.rightFork = forks[seat + 1]; //wonderful, there is someone to the right normally without "looping" around the table
			}
		}
		
		private boolean pickUpRight(long timeout) throws InterruptedException
		{
			if(rightFork.tryLock(10, TimeUnit.MILLISECONDS)) //so we try to obtain the lock for 10 ms
			{
				//System.out.println("Philosopher " + seat + " has picked up the right fork");
				return true;
			}
			else return false; //if we can't get it then make sure we let the calling method know
		}
		
		private boolean pickUpLeft(long timeout) throws InterruptedException
		{
			if(leftFork.tryLock(10, TimeUnit.MILLISECONDS)) //same as above
			{
				//System.out.println("Philosopher " + seat + " has picked up the left fork");
				return true;
			}
			else return false;
		}
		
		private void putDownBoth() //unlock those forks, only if THIS is the one who holds them
		{
			if(rightFork.isHeldByCurrentThread()) rightFork.unlock();
			if(leftFork.isHeldByCurrentThread()) leftFork.unlock();
		}
		
		void think(){
			log.thinks(seat); //log that I'm thinking
			try{
				Thread.sleep(r.nextInt(MAXMSECS)); //for some amount of time
			}
			catch(InterruptedException e){
				/*ignore*/
			}
		}

		void eat(){		
			boolean done = false; //just a bool to check when we are done
			try{
				while(!done)
				{
					if(pickUpLeft(r.nextInt(10)) && pickUpRight(r.nextInt(10))) //attempt for up to 10 ms to aquire each locks
					{
						log.eats(seat); //both locks GET, now eat
						Thread.sleep(r.nextInt(MAXMSECS)); //do it for so long
						done = true; //we cna stop trying to eat since we finished successfully
					}
					/*This Block is to prevent deadlock, say all phils tried to grab their left forks, no one could also grab the right fork
					 * So i have them each wait for a random amount of time (very short) trying to grab both
					 * if they cant they drop both and another phil waiting can grab it, this way someone can always eat and stops deadlocks
					 * there is a scenario where random keeps returning the same value for all philosophers, but that is pretty unlikely
					 * and can't continue forever
					 */
					else
					{
						putDownBoth(); //did not aquire both forks, so puth down whatever you have, so that others can try
					}
				}
			}
			catch(InterruptedException e){
				/*ignore*/
			}
			finally
			{
				putDownBoth(); //put down both forks so that the the others can eat, even if there was an exception
			}
		}
			
		public void run(){
			for (;timesToEat > 0; --timesToEat){
				think();
				eat();
			}
		}
	}
	
	Table(int numSeats, int timesToEat, Logger log) throws InterruptedException{
		this.numSeats = numSeats;  //set the number of seats around the table.  Must be at least 2
		this.timesToEat = timesToEat;  //number of times each philosopher should eat
		this.log = log;
		phils = new Thread[numSeats];  //create a Thread for each philosopher
		forks = new ReentrantLock[numSeats]; //create a fork/lock for each phil
		for (int i = 0; i < numSeats; i++)
		{
			forks[i] = new ReentrantLock();
		}
		for (int i = 0; i < numSeats; i++)
		{
			phils[i] = new Thread(new Philosopher(i, timesToEat));
		}
	}
	
	void startDining(){
		for (int i = 0; i < numSeats; i++) phils[i].start();
	}
	
	void closeRestaurant() throws InterruptedException{
		for (int i = 0; i < numSeats; i++) phils[i].join();
	}
	
	
	
	public static void main(String[] args) throws InterruptedException{
		if (args.length < 2){
			out.println("usage:  java Table numSeats timesToEat");
			return;
		}
		int numPhils = Integer.parseInt(args[0]);
		int timesToEat = Integer.parseInt(args[1]);
		Logger log = new CheckingLogger(numPhils, timesToEat);
		Table table = new Table(numPhils, timesToEat, log);
		table.startDining();
		table.closeRestaurant();
	    System.out.println("restaurant closed.  Behavior was " + (log.isCorrect()?"correct":"incorrect"));
	}
}


