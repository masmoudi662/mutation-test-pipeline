java
package ch.rasc.wamp2spring.message;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;

public class EventMessageTest {

	@Test
	public void testDeserialize() throws IOException {
		String json = "[36, 123, 456, {\"topic\":\"test.topic\", \"publisher\": 789, \"retained\": true}, [\"arg1\", 2], {\"key1\":\"value1\"}]";
		JsonFactory factory = new JsonFactory();
		JsonParser parser = factory.createParser(json);

		EventMessage eventMessage = EventMessage.deserialize(parser);

		assertThat(eventMessage.getSubscriptionId()).isEqualTo(123L);
		assertThat(eventMessage.getPublicationId()).isEqualTo(456L);
		assertThat(eventMessage.getTopic()).isEqualTo("test.topic");
		assertThat(eventMessage.getPublisher()).isEqualTo(789);
		assertThat(eventMessage.isRetained()).isTrue();
		assertThat(eventMessage.getArguments()).containsExactly("arg1", 2);
		assertThat(eventMessage.getArgumentsKw()).containsEntry("key1", "value1");
	}

	@Test
	public void testDeserializeWithNullArguments() throws IOException {
		String json = "[36, 123, 456, {\"topic\":\"test.topic\", \"publisher\": 789, \"retained\": true}]";
		JsonFactory factory = new JsonFactory();
		JsonParser parser = factory.createParser(json);

		EventMessage eventMessage = EventMessage.deserialize(parser);

		assertThat(eventMessage.getSubscriptionId()).isEqualTo(123L);
		assertThat(eventMessage.getPublicationId()).isEqualTo(456L);
		assertThat(eventMessage.getTopic()).isEqualTo("test.topic");
		assertThat(eventMessage.getPublisher()).isEqualTo(789);
		assertThat(eventMessage.isRetained()).isTrue();
		assertThat(eventMessage.getArguments()).isNull();
		assertThat(eventMessage.getArgumentsKw()).isNull();
	}

	@Test
	public void testDeserializeWithoutDetails() throws IOException {
		String json = "[36, 123, 456]";
		JsonFactory factory = new JsonFactory();
		JsonParser parser = factory.createParser(json);

		EventMessage eventMessage = EventMessage.deserialize(parser);

		assertThat(eventMessage.getSubscriptionId()).isEqualTo(123L);
		assertThat(eventMessage.getPublicationId()).isEqualTo(456L);
		assertThat(eventMessage.getTopic()).isNull();
		assertThat(eventMessage.getPublisher()).isNull();
		assertThat(eventMessage.isRetained()).isFalse();
		assertThat(eventMessage.getArguments()).isNull();
		assertThat(eventMessage.getArgumentsKw()).isNull();
	}

	@Test
	public void testDeserializeWithEmptyArraysAndObjects() throws IOException {
		String json = "[36, 123, 456, {\"topic\":\"test.topic\", \"publisher\": 789, \"retained\": true}, [], {}]";
		JsonFactory factory = new JsonFactory();
		JsonParser parser = factory.createParser(json);

		EventMessage eventMessage = EventMessage.deserialize(parser);

		assertThat(eventMessage.getSubscriptionId()).isEqualTo(123L);
		assertThat(eventMessage.getPublicationId()).isEqualTo(456L);
		assertThat(eventMessage.getTopic()).isEqualTo("test.topic");
		assertThat(eventMessage.getPublisher()).isEqualTo(789);
		assertThat(eventMessage.isRetained()).isTrue();
		assertThat(eventMessage.getArguments()).isEmpty();
		assertThat(eventMessage.getArgumentsKw()).isEmpty();
	}
}