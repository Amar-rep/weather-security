package com.example.guardian.clientTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;

import com.example.guardian.client.OpenGeoCoding;
import com.example.guardian.dto.GeocodingResponse;
import com.example.guardian.exception.GeoCodingException;

@ExtendWith(MockitoExtension.class)
@ExtendWith(MockitoExtension.class)
class OpenGeoCodingTest {

	@Mock
	private RestClient restClient;

	@Mock
	@SuppressWarnings("rawtypes")
	private RestClient.RequestHeadersUriSpec requestSpec;

	@Mock
	private RestClient.ResponseSpec responseSpec;

	@InjectMocks
	private OpenGeoCoding openGeoCoding;

	@BeforeEach
	void setUp() {
		ReflectionTestUtils.setField(openGeoCoding, "apiKey", "test-key");
	}

	@Test
	void getGeoCoding_success() {

		GeocodingResponse geo = new GeocodingResponse();
		geo.setState("Kerala");
		when(restClient.get()).thenReturn(requestSpec);
		when(requestSpec.uri(any(Function.class))).thenAnswer(invocation -> {
			Function<UriBuilder, URI> function = invocation.getArgument(0);
			function.apply(new DefaultUriBuilderFactory().builder());
			return requestSpec;
		});

		when(requestSpec.retrieve()).thenReturn(responseSpec);
		when(responseSpec.body(GeocodingResponse[].class)).thenReturn(new GeocodingResponse[] { geo });
		List<GeocodingResponse> result = openGeoCoding.getGeoCoding("Bakkalam", "Kerala", "IN");
		assertEquals(1, result.size());
	}

	@Test
	void getGeoCoding_shouldThrow_whenNoData() {

		when(restClient.get()).thenReturn(requestSpec);
		when(requestSpec.uri(any(Function.class))).thenReturn(requestSpec);
		when(requestSpec.retrieve()).thenReturn(responseSpec);
		when(responseSpec.body(GeocodingResponse[].class)).thenReturn(new GeocodingResponse[0]);
		assertThrows(GeoCodingException.class, () -> openGeoCoding.getGeoCoding("Bakkalam", "Kerala", "IN"));
	}

	@Test
	void getGeoCoding_shouldThrow_whenApiFails() {
		when(restClient.get()).thenThrow(new RestClientException("API failed"));
		assertThrows(GeoCodingException.class, () -> openGeoCoding.getGeoCoding("Bakkalam", "Kerala", "IN"));
	}

	@Test
	void getGeoCoding_shouldThrow_whenResponseIsNull() {

		when(restClient.get()).thenReturn(requestSpec);
		when(requestSpec.uri(any(Function.class))).thenReturn(requestSpec);
		when(requestSpec.retrieve()).thenReturn(responseSpec);
		when(responseSpec.body(GeocodingResponse[].class)).thenReturn(null);
		assertThrows(GeoCodingException.class, () -> openGeoCoding.getGeoCoding("Bakkalam", "Kerala", "IN"));
	}

	@Test
	void getGeoCoding_shouldFilterWrongAndNullStates() {
		GeocodingResponse correct = new GeocodingResponse();
		correct.setState("Kerala");
		GeocodingResponse wrong = new GeocodingResponse();
		wrong.setState("Tamil Nadu");
		GeocodingResponse nullState = new GeocodingResponse();
		nullState.setState(null);
		when(restClient.get()).thenReturn(requestSpec);
		when(requestSpec.uri(any(Function.class))).thenReturn(requestSpec);
		when(requestSpec.retrieve()).thenReturn(responseSpec);
		when(responseSpec.body(GeocodingResponse[].class))
				.thenReturn(new GeocodingResponse[] { correct, wrong, nullState });

		List<GeocodingResponse> result = openGeoCoding.getGeoCoding("Bakkalam", "Kerala", "IN");
		assertEquals(1, result.size());
		assertEquals("Kerala", result.get(0).getState());
	}
}