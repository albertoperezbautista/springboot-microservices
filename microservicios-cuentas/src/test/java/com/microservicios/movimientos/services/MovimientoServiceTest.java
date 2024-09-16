package com.microservicios.movimientos.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.microservicios.cuentas.dto.EstadoCuentaDTO;
import com.microservicios.cuentas.dto.MovimientoDTO;
import com.microservicios.cuentas.entities.Cuenta;
import com.microservicios.cuentas.entities.Movimiento;
import com.microservicios.cuentas.exceptions.RequestException;
import com.microservicios.cuentas.repository.ICuentaRepository;
import com.microservicios.cuentas.repository.IMovimientoRepository;
import com.microservicios.cuentas.services.MovimientoService;

@ExtendWith(MockitoExtension.class)
public class MovimientoServiceTest {

	@InjectMocks
	private MovimientoService movimientoService;

	@Mock
	private IMovimientoRepository movimientoRepo;

	@Mock
	private ICuentaRepository cuentaRepo;

	@Mock
	private ModelMapper modelMapper;

	private Cuenta cuenta;
	private Movimiento movimiento;
	private MovimientoDTO movimientoDTO;
	private List<Movimiento> listaMovimientos;

	@BeforeEach
	public void setUp() {
		cuenta = new Cuenta();
		cuenta.setIdCuenta(1L);
		cuenta.setNumeroCuenta(12345);
		cuenta.setLimiteDiario(1000L);

		movimiento = new Movimiento();
		movimiento.setIdMovimiento(1L);
		movimiento.setCuenta(cuenta);
		movimiento.setSaldo(500L);
		movimiento.setValor(500L);

		movimientoDTO = new MovimientoDTO();
		movimientoDTO.setIdMovimiento(1L);
		movimientoDTO.setNumeroCuenta(12345);
		movimientoDTO.setValor(100L);
		movimientoDTO.setTipoMovimiento("DEB");

		listaMovimientos = new ArrayList<>();
		listaMovimientos.add(movimiento);
	}

	@Test
	public void obtenerTodosTest() {
		when(movimientoRepo.findAll()).thenReturn(listaMovimientos);
		when(modelMapper.map(movimiento, MovimientoDTO.class)).thenReturn(movimientoDTO);

		List<MovimientoDTO> resultado = movimientoService.obtenerTodos();

		assertEquals(1, resultado.size());
		assertEquals(movimientoDTO.getIdMovimiento(), resultado.get(0).getIdMovimiento());
		assertEquals(movimientoDTO.getNumeroCuenta(), resultado.get(0).getNumeroCuenta());
	}

	@Test
	public void obtenerPorIdTest() {
		when(movimientoRepo.findById(1L)).thenReturn(Optional.of(movimiento));
		when(modelMapper.map(movimiento, MovimientoDTO.class)).thenReturn(movimientoDTO);

		MovimientoDTO resultado = movimientoService.obtenerPorId(1L);

		assertEquals(movimientoDTO.getIdMovimiento(), resultado.getIdMovimiento());
		assertEquals(movimientoDTO.getNumeroCuenta(), resultado.getNumeroCuenta());
	}

	@Test
	public void obtenerPorIdMovimientoNoEncontradoTest() {
		when(movimientoRepo.findById(1L)).thenReturn(Optional.empty());

		assertThrows(RequestException.class, () -> {
			movimientoService.obtenerPorId(1L);
		});
	}

	@Test
	public void crearMovimientoTest() {
		when(cuentaRepo.findByNumeroCuenta(movimientoDTO.getNumeroCuenta())).thenReturn(Optional.of(cuenta));
		when(movimientoRepo.obtenerIdUltimoMovimiento(cuenta)).thenReturn(1L);
		when(movimientoRepo.findById(1L)).thenReturn(Optional.of(movimiento));
		when(modelMapper.map(movimientoDTO, Movimiento.class)).thenReturn(movimiento);
		when(modelMapper.map(movimiento, MovimientoDTO.class)).thenReturn(movimientoDTO);

		MovimientoDTO resultado = movimientoService.crearMovimiento(movimientoDTO);

		verify(movimientoRepo).save(movimiento);
		assertEquals(movimientoDTO.getNumeroCuenta(), resultado.getNumeroCuenta());
	}

	@Test
	public void obtenerEstadoCuentaTest() {
		List<EstadoCuentaDTO> estadoCuentaList = new ArrayList<>();
		EstadoCuentaDTO estadoCuentaDTO = new EstadoCuentaDTO();
		estadoCuentaList.add(estadoCuentaDTO);

		LocalDateTime fechaDesde = LocalDateTime.now().minusDays(10);
		LocalDateTime fechaHasta = LocalDateTime.now();

		when(movimientoRepo.obtenerEstadoCuenta(fechaDesde, fechaHasta, "1700000001")).thenReturn(estadoCuentaList);

		List<EstadoCuentaDTO> resultado = movimientoService.obtenerEstadoCuenta("1700000001", fechaDesde, fechaHasta);

		assertEquals(1, resultado.size());

	}

}
