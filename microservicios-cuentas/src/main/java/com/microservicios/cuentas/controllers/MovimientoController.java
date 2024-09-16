package com.microservicios.cuentas.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microservicios.cuentas.dto.EstadoCuentaDTO;
import com.microservicios.cuentas.dto.MovimientoDTO;
import com.microservicios.cuentas.services.IMovimientoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/movimientos")
@RequiredArgsConstructor
public class MovimientoController {

	private final IMovimientoService movimientoService;

	@GetMapping()
	public ResponseEntity<List<MovimientoDTO>> obtenerTodos() {
		List<MovimientoDTO> listaMovimientosDto = movimientoService.obtenerTodos();
		return ResponseEntity.ok(listaMovimientosDto);
	}

	@GetMapping("/{id}")
	public ResponseEntity<MovimientoDTO> obtenerPorId(@PathVariable(value = "id") Long id) {

		MovimientoDTO movimientoDto = movimientoService.obtenerPorId(id);
		return ResponseEntity.ok().body(movimientoDto);

	}

	@PostMapping
	public MovimientoDTO crearMovimiento(@Valid @RequestBody MovimientoDTO movimientoDto) {
		MovimientoDTO movimientoDtoCreado = movimientoService.crearMovimiento(movimientoDto);
		return movimientoDtoCreado;
	}

	@GetMapping("/reportes/{identificacion}/{fechaDesde}/{fechaHasta}")
	public ResponseEntity<List<EstadoCuentaDTO>> obtenerMovimiento(@PathVariable String identificacion,
			@PathVariable String fechaDesde, @PathVariable String fechaHasta) {
		List<EstadoCuentaDTO> listaEstadoCuenta = movimientoService.obtenerEstadoCuenta(identificacion,
				LocalDate.parse(fechaDesde).atStartOfDay(), LocalDate.parse(fechaHasta).atStartOfDay());
		return ResponseEntity.ok().body(listaEstadoCuenta);

	}

}