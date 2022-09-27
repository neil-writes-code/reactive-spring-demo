package ca.neilwhite.hrservice.models.requests;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public record CreateEmployeeRequest(@NotNull @NotEmpty String firstName, @NotNull @NotEmpty String lastName,
                                    @NotNull @NotEmpty String position, @NotNull boolean isFullTime) {
}
