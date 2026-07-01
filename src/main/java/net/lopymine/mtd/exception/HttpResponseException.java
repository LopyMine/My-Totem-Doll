package net.lopymine.mtd.exception;

import java.io.IOException;
import lombok.*;

@Getter
@Setter
public class HttpResponseException extends IOException {

	private final int statusCode;
	private final String reasonPhrase;

	public HttpResponseException(final int statusCode, final String reasonPhrase) {
		super(String.format("status code: %d" + (reasonPhrase != null && reasonPhrase.isBlank() ? "" : ", reason phrase: %s"), statusCode, reasonPhrase));
		this.statusCode   = statusCode;
		this.reasonPhrase = reasonPhrase;
	}

}
