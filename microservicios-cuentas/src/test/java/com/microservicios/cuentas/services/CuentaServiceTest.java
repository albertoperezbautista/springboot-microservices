package com.microservicios.cuentas.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;

import com.microservicios.cuentas.dto.ClienteDTO;
import com.microservicios.cuentas.dto.CuentaDTO;
import com.microservicios.cuentas.entities.Cliente;
import com.microservicios.cuentas.entities.Cuenta;
import com.microservicios.cuentas.repository.ICuentaRepository;
import com.microservicios.cuentas.utils.MapStructMapper;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
public class CuentaServiceTest {

	@InjectMocks
	private CuentaService cuentaService;

	@Mock
	private ICuentaRepository cuentaRepo;

	@Mock
	private ModelMapper modelMapper;

	@Mock
	private MapStructMapper mapper;

	@Mock
	private WebClient.Builder webClientBuilder;

	@Mock
	private WebClient webClient;

	@Mock
	private RequestHeadersUriSpec requestHeadersUriSpec;

	@Mock
	private RequestHeadersSpec requestHeadersSpec;

	@Mock
	private ResponseSpec responseSpec;

	private Cliente cliente;
	private Cuenta cuenta;
	private CuentaDTO cuentaDTO;
	private List<Cuenta> listaCuentas;

	@BeforeEach
	public void setUp() {

		cliente = new Cliente();
		cliente.setIdCliente(1L);
		cliente.setIdentificacion("1700000001");

		cuenta = new Cuenta();
		cuenta.setIdCuenta(1L);
		cuenta.setNumeroCuenta(12345);
		cuenta.setCliente(cliente);

		cuentaDTO = new CuentaDTO();
		cuentaDTO.setIdCuenta(1L);
		cuentaDTO.setNumeroCuenta(12345);
		cuentaDTO.setIdentificacion("1700000001");

		listaCuentas = new ArrayList<>();
		listaCuentas.add(cuenta);

	}

	@Test
	public void obtenerTodosTest() {
		when(cuentaRepo.findAll()).thenReturn(listaCuentas);
		when(modelMapper.map(cuenta, CuentaDTO.class)).thenReturn(cuentaDTO);

		List<CuentaDTO> resultado = cuentaService.obtenerTodos();

		assertEquals(1, resultado.size());
		assertEquals(cuentaDTO.getIdCuenta(), resultado.get(0).getIdCuenta());
		assertEquals(cuentaDTO.getIdentificacion(), resultado.get(0).getIdentificacion());
	}

	@Test
	public void obtenerPorIdTest() {
		Long cuentaId = 1L;

		when(cuentaRepo.findById(cuentaId)).thenReturn(Optional.of(cuenta));
		when(modelMapper.map(cuenta, CuentaDTO.class)).thenReturn(cuentaDTO);

		CuentaDTO result = cuentaService.obtenerPorId(cuentaId);

		assertEquals(cuentaDTO.getIdCuenta(), result.getIdCuenta());
		assertEquals(cuentaDTO.getNumeroCuenta(), result.getNumeroCuenta());
	}

	@Test
	public void crearCuentaTest() {

		when(webClientBuilder.build()).thenReturn(webClient);
		when(webClient.get()).thenReturn(requestHeadersUriSpec);
		when(requestHeadersUriSpec.uri(Mockito.anyString())).thenReturn(requestHeadersSpec);
		when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
		when(responseSpec.bodyToMono(ClienteDTO.class)).thenReturn(Mono.just(new ClienteDTO()));
		when(modelMapper.map(cuentaDTO, Cuenta.class)).thenReturn(cuenta);
		when(cuentaRepo.save(cuenta)).thenReturn(cuenta);
		when(modelMapper.map(cuenta, CuentaDTO.class)).thenReturn(cuentaDTO);

		CuentaDTO resultado = cuentaService.crearCuenta(cuentaDTO);

		assertEquals(cuentaDTO.getIdCuenta(), resultado.getIdCuenta());
		assertEquals(cuentaDTO.getNumeroCuenta(), resultado.getNumeroCuenta());
	}

	@Test
	public void actualizarCuentaTest() {
		Integer numeroCuenta = 12345;

		when(cuentaRepo.findByNumeroCuenta(numeroCuenta)).thenReturn(Optional.of(cuenta));
		doNothing().when(mapper).updateCuentaFromDto(cuentaDTO, cuenta);
		when(modelMapper.map(cuenta, CuentaDTO.class)).thenReturn(cuentaDTO);

		CuentaDTO resultado = cuentaService.actualizarCuenta(numeroCuenta, cuentaDTO);

		assertEquals(cuentaDTO.getIdCuenta(), resultado.getIdCuenta());
		assertEquals(cuentaDTO.getNumeroCuenta(), resultado.getNumeroCuenta());
	}

	@Test
	public void eliminarCuentaTest() {
		Long cuentaId = 1L;

		when(cuentaRepo.findById(cuentaId)).thenReturn(Optional.of(cuenta));

		cuentaService.eliminarCuenta(cuentaId);

		verify(cuentaRepo).findById(cuentaId);
		verify(cuentaRepo).delete(cuenta);
	}

}
