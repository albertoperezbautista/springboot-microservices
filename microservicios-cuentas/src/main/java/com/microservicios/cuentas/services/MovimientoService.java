
package com.microservicios.cuentas.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.microservicios.cuentas.dto.EstadoCuentaDTO;
import com.microservicios.cuentas.dto.MovimientoDTO;
import com.microservicios.cuentas.entities.Cuenta;
import com.microservicios.cuentas.entities.Movimiento;
import com.microservicios.cuentas.exceptions.BusinessException;
import com.microservicios.cuentas.exceptions.RequestException;
import com.microservicios.cuentas.repository.ICuentaRepository;
import com.microservicios.cuentas.repository.IMovimientoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MovimientoService implements IMovimientoService {

	private final ModelMapper modelMapper;

	private final IMovimientoRepository movimientoRepo;

	private final ICuentaRepository cuentaRepo;

	@Override
	public List<MovimientoDTO> obtenerTodos() {
		List<Movimiento> listaMovimientos = movimientoRepo.findAll();
		List<MovimientoDTO> respuesta = new ArrayList<>();
		listaMovimientos.forEach(movimiento -> {

			MovimientoDTO dto = mapearDTO(movimiento);
			dto.setIdCuenta(movimiento.getCuenta().getIdCuenta());
			dto.setNumeroCuenta(movimiento.getCuenta().getNumeroCuenta());
			respuesta.add(dto);
		});

		return respuesta;
	}

	@Override
	public MovimientoDTO obtenerPorId(Long id) {

		Movimiento movimiento = movimientoRepo.findById(id)
				.orElseThrow(() -> new RequestException("P-401", "Movimiento no encontrado" + id));
		MovimientoDTO movimientoDTO = modelMapper.map(movimiento, MovimientoDTO.class);

		movimientoDTO.setIdCuenta(movimiento.getCuenta().getIdCuenta());
		movimientoDTO.setNumeroCuenta(movimiento.getCuenta().getNumeroCuenta());

		return movimientoDTO;

	}

	@Override
	public MovimientoDTO crearMovimiento(MovimientoDTO movimientoDTO) {

		Cuenta cuentaVerificacion = cuentaRepo.findByNumeroCuenta(movimientoDTO.getNumeroCuenta())
				.orElseThrow(() -> new BusinessException("P-401", HttpStatus.BAD_REQUEST,
						"Cuenta no encontrada con numero cuenta " + movimientoDTO.getNumeroCuenta()));

		Long idUltimoMovimiento = movimientoRepo.obtenerIdUltimoMovimiento(cuentaVerificacion);
		Optional<Movimiento> ultimoMovimiento = movimientoRepo.findById(idUltimoMovimiento);
		LocalDate fechaHoy = LocalDate.now();
		Movimiento movimiento = modelMapper.map(movimientoDTO, Movimiento.class);
		movimiento.setCuenta(cuentaVerificacion);
		movimiento.setFechaMovimiento(LocalDateTime.now());

		if (movimientoDTO.getTipoMovimiento().equals("DEB")) {

			Long cupoUtilizado = movimientoRepo.obtenerValorCupoUtilizado(fechaHoy.atStartOfDay());
			if (null == cupoUtilizado) {
				cupoUtilizado = 0L;
			}

			if ((Math.abs(cupoUtilizado) + movimientoDTO.getValor()) > cuentaVerificacion.getLimiteDiario()) {
				throw new BusinessException("E500", HttpStatus.INTERNAL_SERVER_ERROR, "Cupo diario excedido");
			}

			if (Math.abs(movimientoDTO.getValor()) > Math.abs(ultimoMovimiento.get().getSaldo())) {
				throw new BusinessException("E500", HttpStatus.INTERNAL_SERVER_ERROR, "Saldo insuficiente");
			}

			if (ultimoMovimiento.get().getSaldo() == 0) {
				throw new BusinessException("E500", HttpStatus.INTERNAL_SERVER_ERROR, "Saldo no disponible");
			}

			movimiento.setValor(movimiento.getValor() * -1);
		}
		movimiento.setSaldo(ultimoMovimiento.get().getSaldo() + movimiento.getValor());
		movimiento.setEstadoMovimiento("PROCESADO");
		movimiento.setEstado("ACT");

		movimientoRepo.save(movimiento);
		MovimientoDTO movimientoDTOCreado = modelMapper.map(movimiento, MovimientoDTO.class);
		movimientoDTOCreado.setIdCuenta(cuentaVerificacion.getIdCuenta());
		movimientoDTOCreado.setNumeroCuenta(cuentaVerificacion.getNumeroCuenta());
		movimientoDTOCreado.setValor(Math.abs(movimientoDTOCreado.getValor()));

		return movimientoDTOCreado;

	}

	@Override
	public List<EstadoCuentaDTO> obtenerEstadoCuenta(String identificacion, LocalDateTime fechaDesde,
			LocalDateTime fechaHasta) {
		return movimientoRepo.obtenerEstadoCuenta(fechaDesde, fechaHasta, identificacion);

	}

	private MovimientoDTO mapearDTO(Movimiento movimiento) {
		MovimientoDTO movimientoDTO = modelMapper.map(movimiento, MovimientoDTO.class);
		return movimientoDTO;
	}

}
