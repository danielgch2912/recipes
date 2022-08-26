package nl.abnamro.recipes.service.exception;

import nl.abnamro.recipes.dto.ErrorDto;

import java.util.Arrays;
import java.util.List;

public abstract class ServiceErrorException extends RuntimeException {

    private List<ErrorDto> dto;

    public ServiceErrorException(ErrorDto dto) {
        this.dto = Arrays.asList(dto);
    }

    public List<ErrorDto> getErrors() {
        return dto;
    }

}
