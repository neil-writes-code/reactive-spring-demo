package ca.neilwhite.hrservice.models.requests;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public record CreateDepartmentRequest(
        @NotNull(message = "Name can not be null") @NotEmpty(message = "Name can not be empty") String name) {
}
