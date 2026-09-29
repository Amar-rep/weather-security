package com.example.guardian.clientTest;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
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

import com.example.guardian.client.OpenWeather;
import com.example.guardian.dto.OpenWeatherResponse;
import com.example.guardian.dto.WeatherResponse;
import com.example.guardian.dto.WeatherDto.Main;
import com.example.guardian.dto.WeatherDto.Weather;
import com.example.guardian.dto.WeatherDto.Wind;
import com.example.guardian.exception.WeatherApiException;
@ExtendWith(MockitoExtension.class)
class OpenWeatherTest {

	@Mock
	private RestClient restClient;

	@Mock
	@SuppressWarnings("rawtypes")
	private RestClient.RequestHeadersUriSpec requestSpec;

	@Mock
	private RestClient.ResponseSpec responseSpec;

	@InjectMocks
	private OpenWeather openWeather;

	@BeforeEach
	void setUp() {
		ReflectionTestUtils.setField(openWeather, "apiKey", "test-key");
	}

	@Test
	void getWeather_success() {

		OpenWeatherResponse response = new OpenWeatherResponse();
		Main main = new Main();
		main.setTemp(28.5);
		main.setHumidity(70);
		Wind wind = new Wind();
		wind.setSpeed(4.2);
		Weather weather = new Weather();
		weather.setDescription("clear sky");
		response.setName("Bangalore");
		response.setMain(main);
		response.setWind(wind);
		response.setWeather(List.of(weather));
		when(restClient.get()).thenReturn(requestSpec);
		when(requestSpec.uri(any(Function.class)))
        .thenAnswer(invocation -> {

            Function<UriBuilder, URI> function =
                    invocation.getArgument(0);
            function.apply(
                    new DefaultUriBuilderFactory().builder());
            return requestSpec;
        });


		when(requestSpec.retrieve()).thenReturn(responseSpec);
		when(responseSpec.body(OpenWeatherResponse.class)).thenReturn(response);
		WeatherResponse result = openWeather.getWeather(new BigDecimal("12.97"), new BigDecimal("77.59"));
		assertEquals("Bangalore", result.getCity());
		assertEquals(28.5, result.getTemperature());
	}

	@Test
	void getWeather_shouldThrow_whenResponseNull() {
		when(restClient.get()).thenReturn(requestSpec);
		when(requestSpec.uri(any(Function.class))).thenReturn(requestSpec);
		when(requestSpec.retrieve()).thenReturn(responseSpec);
		when(responseSpec.body(OpenWeatherResponse.class)).thenReturn(null);
		assertThrows(WeatherApiException.class,
				() -> openWeather.getWeather(new BigDecimal("12.97"), new BigDecimal("77.59")));
	}

	@Test
	void getWeather_shouldThrow_whenApiFails() {
		when(restClient.get()).thenThrow(new RestClientException("API failed"));
		assertThrows(WeatherApiException.class,
				() -> openWeather.getWeather(new BigDecimal("12.97"), new BigDecimal("77.59")));
	}
}