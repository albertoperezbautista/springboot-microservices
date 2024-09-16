

package com.microservicios.cuentas.services;

import java.util.List;

import com.microservicios.cuentas.dto.CuentaDTO;

public interface ICuentaService {

	public List<CuentaDTO> obtenerTodos();

	public CuentaDTO obtenerPorId(Long id);

	public CuentaDTO crearCuenta(CuentaDTO cuentaDto);

	public CuentaDTO actualizarCuenta(Integer numeroCuenta, CuentaDTO cuentaDTO);

	public void eliminarCuenta(Long id);

	public CuentaDTO obtenerPorNumeroCuenta(Integer numeroCuenta);

}