
package com.microservicios.cuentas.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microservicios.cuentas.dto.CuentaDTO;
import com.microservicios.cuentas.services.CuentaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/cuentas")
@RequiredArgsConstructor
public class CuentaController {

	private final CuentaService cuentaService;

	@GetMapping()
	public ResponseEntity<List<CuentaDTO>> obtenerTodos() {
		List<CuentaDTO> listaCuentasDto = cuentaService.obtenerTodos();
		return ResponseEntity.ok(listaCuentasDto);
	}

	@GetMapping("/{id}")
	public ResponseEntity<CuentaDTO> obtenerPorId(@PathVariable(value = "id") Long id) {

		CuentaDTO cuentaDto = cuentaService.obtenerPorId(id);
		return ResponseEntity.ok().body(cuentaDto);

	}

	@GetMapping("/obtenerPorNumeroCuenta/{numeroCuenta}")
	public ResponseEntity<CuentaDTO> obtenerPorNumeroCuenta(
			@PathVariable(value = "numeroCuenta") Integer numeroCuenta) {
		CuentaDTO cuentaDto = cuentaService.obtenerPorNumeroCuenta(numeroCuenta);
		return ResponseEntity.ok().body(cuentaDto);
	}

	@PostMapping
	public CuentaDTO crearCuenta(@Valid @RequestBody CuentaDTO cuentaDto) {
		CuentaDTO cuentaDtoCreado = cuentaService.crearCuenta(cuentaDto);
		return cuentaDtoCreado;
	}

	@PutMapping("/{numeroCuenta}")
	public ResponseEntity<CuentaDTO> actualizarCuenta(@PathVariable(value = "numeroCuenta") Integer numeroCuenta,
			@RequestBody CuentaDTO cuentaDTO) {
		CuentaDTO cuentaResp = cuentaService.actualizarCuenta(numeroCuenta, cuentaDTO);
		return ResponseEntity.ok().body(cuentaResp);

	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminarCuenta(@PathVariable(value = "id") Long id) {
		cuentaService.eliminarCuenta(id);
		return ResponseEntity.ok().build();

	}

}