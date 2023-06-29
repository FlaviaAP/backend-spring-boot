package com.backendabstractmodel.demo.domain.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO implements BaseDTO<UUID> {

    private UUID id;

    @NotNull
    private String name;

    @NotNull
    private Double price;

    @Override
    public boolean isValidForSave() {
        return Stream.of(name, price).allMatch(Objects::nonNull);
    }
}
