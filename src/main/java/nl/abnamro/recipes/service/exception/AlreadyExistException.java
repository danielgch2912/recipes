package nl.abnamro.recipes.service.exception;

import nl.abnamro.recipes.dto.ErrorDto;

public class AlreadyExistException extends ServiceErrorException {


    public AlreadyExistException(ErrorDto dto) {
        super(dto);
    }
}
