package mx.tvmaze;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

record CommentRequest(
        @JsonProperty("show_id") @NotNull @Positive Long showId,
        @NotBlank String comment,
        @NotNull @Min(0) @Max(5) Integer rating
) {
}
