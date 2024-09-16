


package com.microservicios.cuentas.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.microservicios.cuentas.dto.EstadoCuentaDTO;
import com.microservicios.cuentas.entities.Cuenta;
import com.microservicios.cuentas.entities.Movimiento;

@Repository
public interface IMovimientoRepository extends JpaRepository<Movimiento, Long> {

	@Query("SELECT MAX(m.idMovimiento) FROM Movimiento m WHERE m.cuenta = :cuenta ")
	Long obtenerIdUltimoMovimiento(@Param("cuenta") Cuenta cuenta);

	@Query("SELECT SUM(m.valor) FROM Movimiento m WHERE m.tipoMovimiento = 'DEB' and m.fechaMovimiento >= :fechaInicioDia ")
	Long obtenerValorCupoUtilizado(@Param("fechaInicioDia") LocalDateTime fechaInicioDia);

	@Query(" SELECT new com.microservicios.cuentas.dto.EstadoCuentaDTO(mo.fechaMovimiento, concat(mo.cuenta.cliente.nombre, ' ',  mo.cuenta.cliente.apellido), "
			+ " mo.cuenta.numeroCuenta, mo.cuenta.tipoCuenta, mo.cuenta.saldoInicial, mo.cuenta.estado, mo.valor, mo.saldo)"
			+ " FROM Movimiento mo " + " WHERE mo.fechaMovimiento BETWEEN :fechaDesde AND :fechaHasta "
			+ " AND mo.cuenta.cliente.identificacion = :identificacion ")

	List<EstadoCuentaDTO> obtenerEstadoCuenta(@Param("fechaDesde") LocalDateTime fechaDesde,
			@Param("fechaHasta") LocalDateTime fechaHasta, @Param("identificacion") String identificacion);

}

