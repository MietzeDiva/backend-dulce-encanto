package cl.dulceencanto.ms_productos.util;

public final class ProductoImagenUtils {

    private static final String IMAGENES_PRODUCTOS_PREFIX = "/images/productos/";

    private ProductoImagenUtils() {
    }

    public static String normalize(String imagenUrl) {
        if (imagenUrl == null) {
            return null;
        }

        String normalized = imagenUrl.trim();
        if (normalized.startsWith(IMAGENES_PRODUCTOS_PREFIX + "http://")
                || normalized.startsWith(IMAGENES_PRODUCTOS_PREFIX + "https://")) {
            normalized = normalized.substring(IMAGENES_PRODUCTOS_PREFIX.length());
        }
        return normalized.isEmpty() ? null : normalized;
    }
}
