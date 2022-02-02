package dev.stocky37.xiv.actions.xivapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.util.concurrent.RateLimiter;
import dev.stocky37.xiv.actions.data.Job;
import dev.stocky37.xiv.actions.data.Query;
import dev.stocky37.xiv.actions.json.JobDeserializer;
import dev.stocky37.xiv.actions.util.Util;
import dev.stocky37.xiv.actions.xivapi.json.SearchBody;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@SuppressWarnings("UnstableApiUsage")
@ApplicationScoped
public class XivApiClient {
	private final XivApi xivapi;
	private final RateLimiter rateLimiter;
	private final Util util;

	@Inject
	public XivApiClient(
		@RestClient XivApi xivapi,
		RateLimiter rateLimiter,
		Util util
	) {
		this.xivapi = xivapi;
		this.rateLimiter = rateLimiter;
		this.util = util;
	}

	public <T> List<T> search(Query query, Function<JsonNode, T> deserializer) {
		final SearchBody body = new SearchBody(
			String.join(",", query.indexes()),
			String.join(",", query.columns()),
			util.toJsonNode(query.query())
		);

		return wrapApi(() -> xivapi.search(body).Results().parallelStream().map(deserializer).toList());
	}

	public List<Job> getJobs() {
		return wrapApi(() -> xivapi.getClassJobs(JobDeserializer.ALL_FIELDS).Results());
	}

	private <T> T wrapApi(Supplier<T> supplier) {
		rateLimiter.acquire();
		try {
			return supplier.get();
		} catch (WebApplicationException e) {
			if(e.getResponse().getStatusInfo() == Response.Status.NOT_FOUND) {
				throw new NotFoundException(e);
			} else {
				throw new InternalServerErrorException(e);
			}
		}
	}
}
