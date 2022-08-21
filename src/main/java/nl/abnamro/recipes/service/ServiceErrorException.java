package nl.abnamro.recipes.service;

import nl.abnamro.recipes.dto.ErrorDto;

import java.util.Arrays;
import java.util.List;

public class ServiceErrorException extends Exception {

    private List<ErrorDto> dto;

    public ServiceErrorException(ErrorDto dto) {
        this.dto = Arrays.asList(dto);
    }

    public List<ErrorDto> getErrors() {
        return dto;
    }

}
