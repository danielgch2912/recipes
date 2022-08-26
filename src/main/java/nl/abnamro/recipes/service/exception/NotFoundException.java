package nl.abnamro.recipes.service.exception;

import nl.abnamro.recipes.dto.ErrorDto;

public class NotFoundException extends ServiceErrorException {


    public NotFoundException(ErrorDto dto) {
        super(dto);
    }
}
