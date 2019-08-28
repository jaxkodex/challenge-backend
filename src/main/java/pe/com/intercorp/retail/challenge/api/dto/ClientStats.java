package pe.com.intercorp.retail.challenge.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "Representa las últimas estadísticas del cliente")
public class ClientStats {
    @ApiModelProperty(value = "Edad promedio de los clientes", example = "24")
    private Double average;
    @ApiModelProperty(value = "Desviación estandar de la edad de los clientes", example = "4")
    private Double standardDeviation;
}
