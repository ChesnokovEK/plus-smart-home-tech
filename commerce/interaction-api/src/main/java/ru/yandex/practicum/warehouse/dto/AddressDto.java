package ru.yandex.practicum.warehouse.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto {
    private static final String DEFAULT_COUNTRY = "default country";
    private static final String DEFAULT_CITY = "default city";
    private static final String DEFAULT_STREET = "default street";
    private static final String DEFAULT_HOUSE = "default house";
    private static final String DEFAULT_FLAT = "default flat";
    private String country;
    private String city;
    private String street;
    private String house;
    private String flat;

    public static AddressDto getDefaultAddress() {
        return new AddressDto(
                DEFAULT_COUNTRY,
                DEFAULT_CITY,
                DEFAULT_STREET,
                DEFAULT_HOUSE,
                DEFAULT_FLAT
        );
    }
}
