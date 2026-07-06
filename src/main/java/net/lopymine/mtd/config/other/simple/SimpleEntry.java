package net.lopymine.mtd.config.other.simple;

import lombok.*;

import java.util.Map.Entry;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

@Getter
@AllArgsConstructor
public class SimpleEntry<K, V> implements Comparable<SimpleEntry<K, V>> {

	private K key;
	private V value;

	public static <V, K> SimpleEntry<K, V> of(Entry<K, V> entry) {
		return new SimpleEntry<>(entry.getKey(), entry.getValue());
	}

	public SimpleEntry<K, V> setKey(K key) {
		this.key = key;
		return this;
	}

	public SimpleEntry<K, V> setValue(V value) {
		this.value = value;
		return this;
	}

	@Override
	public int compareTo(@NotNull SimpleEntry<K, V> o) {
		return o.getKey().toString().compareTo(this.key.toString());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof SimpleEntry<?, ?> that)) return false;
		return Objects.equals(this.key, that.key) && Objects.equals(this.value, that.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.key, this.value);
	}
}
