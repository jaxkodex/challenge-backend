package pe.com.intercorp.retail.challenge.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Representa a un cliente de la empresa")
public class Client {
    private static final ZoneId LIMA_ZONE = ZoneId.of("America/Lima");
    @ApiModelProperty(value = "Identificador único del cliente, autogenerado UUID",
            example = "41da0ff1-e276-41be-8c4a-d954250e5e04")
    private String id;
    @ApiModelProperty(value = "Nombre del cliente, este valor no puede ser vacío",
            example = "Juan")
    @NotEmpty
    private String name;
    @ApiModelProperty(value = "Apellidos del cliente, este valor no puede ser vacío",
            example = "Perez")
    @NotEmpty
    private String lastName;
    @ApiModelProperty(value = "Fecha de nacimiento del cliente, este valor no puede ser vacío y debe encontrarse en el pasado",
            example = "01/01/2018")
    @Past
    @NotNull
    private Date birthDate;

    @ApiModelProperty(value = "Edad del cliente, este valor es calculado a partir de la fecha de nacimiento",
            example = "25")
    public Integer getAge() {
        LocalDate birthLocalDate = Optional.ofNullable(birthDate).orElse(new Date())
                .toInstant().atZone(LIMA_ZONE).toLocalDate();
        return Period.between(birthLocalDate, LocalDate.now(LIMA_ZONE)).getYears();
    }
}
