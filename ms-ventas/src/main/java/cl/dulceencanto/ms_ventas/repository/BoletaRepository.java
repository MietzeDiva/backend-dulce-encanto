package cl.dulceencanto.ms_ventas.repository;

import cl.dulceencanto.ms_ventas.model.Boleta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoletaRepository extends JpaRepository<Boleta, Long> {
}