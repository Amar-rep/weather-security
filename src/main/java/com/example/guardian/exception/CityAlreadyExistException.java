package com.example.guardian.exception;

public class CityAlreadyExistException extends RuntimeException {

	public CityAlreadyExistException(String message) {
		super(message);
	}
}