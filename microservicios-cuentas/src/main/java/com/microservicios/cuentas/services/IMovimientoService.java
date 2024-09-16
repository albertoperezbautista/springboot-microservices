


package com.microservicios.cuentas.services;

import java.time.LocalDateTime;
import java.util.List;

import com.microservicios.cuentas.dto.EstadoCuentaDTO;
import com.microservicios.cuentas.dto.MovimientoDTO;

public interface IMovimientoService {

	public List<MovimientoDTO> obtenerTodos();

	public MovimientoDTO obtenerPorId(Long id);

	public MovimientoDTO crearMovimiento(MovimientoDTO movimientoDto);

	public List<EstadoCuentaDTO> obtenerEstadoCuenta(String identificacion, LocalDateTime fechaDesde,
			LocalDateTime fechaHasta);

}
