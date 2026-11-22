package cl.dulceencanto.ms_usuarios.dto;

import cl.dulceencanto.ms_usuarios.model.Usuario;

/** Lo único que sale hacia el cliente: nunca incluye password (ni el hash). */
public record UsuarioResponse(Long id, String nombre, String correo, String rol) {
    public static UsuarioResponse from(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getNombre(), u.getCorreo(), u.getRol());
    }
}
