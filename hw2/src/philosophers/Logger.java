package philosophers;

public interface Logger {
	boolean isCorrect();
	public void eats(int seat);
	public void thinks(int seat);
}
