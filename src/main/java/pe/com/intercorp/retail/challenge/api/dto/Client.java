package pe.com.intercorp.retail.challenge.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Client {
    private static final ZoneId LIMA_ZONE = ZoneId.of("America/Lima");
    private String id;
    private String name;
    private String lastName;
    private Date birthDate;

    public Integer getAge() {
        LocalDate birthLocalDate = Optional.ofNullable(birthDate).orElse(new Date())
                .toInstant().atZone(LIMA_ZONE).toLocalDate();
        return Period.between(birthLocalDate, LocalDate.now(LIMA_ZONE)).getYears();
    }
}
