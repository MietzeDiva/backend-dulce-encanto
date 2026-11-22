package cl.dulceencanto.ms_usuarios.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Crear/editar usuarios desde el panel (solo ADMIN).
 * En PUT la contraseña es opcional: si viene vacía se conserva la actual.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record UsuarioRequest(
        @NotBlank(message = "El nombre es obligatorio") @Size(max = 100) String nombre,
        @NotBlank(message = "El correo es obligatorio") @Email(message = "Correo inválido") @Size(max = 100) String correo,
        @Size(min = 8, max = 72, message = "La contraseña debe tener entre 8 y 72 caracteres") String password,
        @NotBlank(message = "El rol es obligatorio")
        @Pattern(regexp = "ADMIN|VENDEDOR|CLIENTE", message = "Rol inválido (ADMIN, VENDEDOR o CLIENTE)") String rol) {
}
