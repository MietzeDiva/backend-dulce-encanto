package cl.dulceencanto.ms_usuarios.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Registro público: NO incluye rol. Toda cuenta creada por aquí es CLIENTE. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RegistroRequest(
        @NotBlank(message = "El nombre es obligatorio") @Size(max = 100) String nombre,
        @NotBlank(message = "El correo es obligatorio") @Email(message = "Correo inválido") @Size(max = 100) String correo,
        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, max = 72, message = "La contraseña debe tener entre 8 y 72 caracteres") String password) {
}
