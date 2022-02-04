package dev.stocky37.xiv.actions.data;


import com.fasterxml.jackson.annotation.JsonProperty;
import java.net.URI;
import java.time.Duration;
import java.util.List;

public record Item(
	String id,
	String name,
	URI icon,
	URI iconHD,
	String description,
	Duration recast,
	Duration bonusDuration,
	List<Bonus> bonuses
) {
	public record Bonus(Attribute attribute, int value, int max) {
	}

	@JsonProperty
	public boolean onGCD() {
		return false;
	}
}
