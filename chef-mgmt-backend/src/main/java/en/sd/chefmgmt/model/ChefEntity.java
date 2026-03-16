package en.sd.chefmgmt.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChefEntity {

    private UUID id;
    private String name;
    private String email;
    private ZonedDateTime birthDate;
    private double rating;
}
