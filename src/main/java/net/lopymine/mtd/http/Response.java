package net.lopymine.mtd.http;

import org.jetbrains.annotations.Nullable;

public record Response<T>(int statusCode, @Nullable T value) {

	public static <T> Response<T> empty(int statusCode) {
		return new Response<>(statusCode, null);
	}

	public boolean isEmpty() {
		return this.value == null;
	}
}
