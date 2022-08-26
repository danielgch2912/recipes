package nl.abnamro.recipes.service.exception;

import nl.abnamro.recipes.dto.ErrorDto;

public class BadRequestException extends ServiceErrorException {

    public BadRequestException(ErrorDto dto) {
        super(dto);
    }
}
