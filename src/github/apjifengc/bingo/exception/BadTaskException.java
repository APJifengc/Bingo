package github.apjifengc.bingo.exception;

public class BadTaskException extends Exception {

	private static final long serialVersionUID = 2038268786877998933L;

	public BadTaskException() {
		super("Caught a BadTaskException()");
	}

	public BadTaskException(String msg) {
		super(msg);
	}

}
