package nl.abnamro.recipes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorDto {
    private String field;

    private String message;

    public ErrorDto(String message) {
        this.message = message;
    }

}
