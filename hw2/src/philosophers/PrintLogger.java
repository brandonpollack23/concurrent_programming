package philosophers;

import static java.lang.System.out;

public class PrintLogger implements Logger{

	@Override
	public void eats(int philosopher) {
		out.println("Philosopher " + philosopher + " is eating");
	}

	@Override
	public void thinks(int philosopher) {
		out.println("Philosopher " + philosopher + " is thinking");
	}

	@Override
	public boolean isCorrect() {
		out.println(" (WARNING:  Correctness not checked) ");
		return true;
	}

}
