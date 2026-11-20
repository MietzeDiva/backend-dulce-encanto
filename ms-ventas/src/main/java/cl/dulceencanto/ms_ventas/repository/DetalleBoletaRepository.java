package cl.dulceencanto.ms_ventas.repository;

import cl.dulceencanto.ms_ventas.model.DetalleBoleta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetalleBoletaRepository extends JpaRepository<DetalleBoleta, Long> {
}