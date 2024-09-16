

package com.microservicios.cuentas.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.microservicios.cuentas.constants.Constantes;
import com.microservicios.cuentas.dto.ClienteDTO;
import com.microservicios.cuentas.dto.CuentaDTO;
import com.microservicios.cuentas.entities.Cliente;
import com.microservicios.cuentas.entities.Cuenta;
import com.microservicios.cuentas.entities.Movimiento;
import com.microservicios.cuentas.exceptions.BusinessException;
import com.microservicios.cuentas.exceptions.RequestException;
import com.microservicios.cuentas.repository.ICuentaRepository;
import com.microservicios.cuentas.utils.MapStructMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CuentaService implements ICuentaService {

	private final ModelMapper modelMapper;

	private final MapStructMapper mapper;

	private final WebClient.Builder webClientBuilder;
	private final ICuentaRepository cuentaRepo;

	@Override
	public List<CuentaDTO> obtenerTodos() {
		List<Cuenta> listaCuentas = cuentaRepo.findAll();
		List<CuentaDTO> respuesta = new ArrayList<>();
		listaCuentas.forEach(cuenta -> {

			CuentaDTO dto = mapearDTO(cuenta);
			dto.setIdentificacion(cuenta.getCliente().getIdentificacion());
			respuesta.add(dto);
		});

		return respuesta;
	}

	@Override
	public CuentaDTO obtenerPorId(Long id) {

		Cuenta cuenta = cuentaRepo.findById(id)
				.orElseThrow(() -> new RequestException("P-401", "Cuenta no encontrada " + id));
		CuentaDTO cuentaDTO = modelMapper.map(cuenta, CuentaDTO.class);

		cuentaDTO.setIdentificacion(cuenta.getCliente().getIdentificacion());

		return cuentaDTO;

	}

	@Override
	public CuentaDTO crearCuenta(CuentaDTO cuentaDTO) {

		Cuenta cuentaVerificacion = cuentaRepo.findByNumeroCuenta(cuentaDTO.getNumeroCuenta()).orElse(null);

		if (cuentaVerificacion != null) {
			throw new BusinessException("501", HttpStatus.CONFLICT,
					Constantes.ERROR_INTEGRIDAD_DATOS + " Número de cuenta ya registrado.");
		}

		ClienteDTO clienteDTO = this.webClientBuilder.build().get()
				.uri("http://localhost:8082/clientes/findByIdentificacion/" + cuentaDTO.getIdentificacion()).retrieve()
				.bodyToMono(ClienteDTO.class).block();

		if (clienteDTO == null) {
			throw new BusinessException("P-401", HttpStatus.BAD_REQUEST,
					"Cliente no encontrado con identificacion " + cuentaDTO.getIdentificacion());
		}

		System.out.println("ENCUENTRA CLIENTE::: " + clienteDTO);
		Cliente cliente = new Cliente();
		cliente.setIdCliente(clienteDTO.getIdCliente());
		cliente.setIdentificacion(clienteDTO.getIdentificacion());

		Cuenta cuenta = modelMapper.map(cuentaDTO, Cuenta.class);

		cuenta.setCliente(cliente);

		cuenta.setEstado(Constantes.ACTIVO);
		List<Movimiento> movimientos = new ArrayList<>();
		Movimiento movimientoInicial = new Movimiento();
		movimientoInicial.setCuenta(cuenta);
		movimientoInicial.setFechaMovimiento(LocalDateTime.now());
		movimientoInicial.setValor(cuentaDTO.getSaldoInicial());
		movimientoInicial.setSaldo(cuentaDTO.getSaldoInicial());
		movimientoInicial.setEstadoMovimiento("PROCESADO");
		movimientoInicial.setEstado("ACT");
		movimientoInicial.setTipoMovimiento("CRE");

		movimientos.add(movimientoInicial);

		cuenta.setListaMovimientos(movimientos);
		cuentaRepo.save(cuenta);
		CuentaDTO CuentaDTOCreado = modelMapper.map(cuenta, CuentaDTO.class);
		CuentaDTOCreado.setIdentificacion(cliente.getIdentificacion());
		return CuentaDTOCreado;

	}

	@Override
	public CuentaDTO actualizarCuenta(Integer numeroCuenta, CuentaDTO cuentaDTO) {

		Cuenta cuentaExistente = cuentaRepo.findByNumeroCuenta(numeroCuenta).orElseThrow(
				() -> new RequestException("P-401", Constantes.ERROR_REGISTRO_NO_ENCONTRADO + numeroCuenta));

		System.out.println("tipo de cuenta:: " + cuentaDTO.getTipoCuenta());
		mapper.updateCuentaFromDto(cuentaDTO, cuentaExistente);

		cuentaRepo.save(cuentaExistente);

		CuentaDTO cuentaDTOActualizado = modelMapper.map(cuentaExistente, CuentaDTO.class);
		cuentaDTOActualizado.setIdentificacion(cuentaExistente.getCliente().getIdentificacion());
		return cuentaDTOActualizado;

	}

	@Override
	public void eliminarCuenta(Long id) {
		Cuenta cuenta = cuentaRepo.findById(id)
				.orElseThrow(() -> new RequestException("P-401", Constantes.ERROR_REGISTRO_NO_ENCONTRADO + id)

				);
		cuentaRepo.delete(cuenta);

	}

	private CuentaDTO mapearDTO(Cuenta cuenta) {
		CuentaDTO cuentaDTO = modelMapper.map(cuenta, CuentaDTO.class);
		return cuentaDTO;
	}

	@Override
	public CuentaDTO obtenerPorNumeroCuenta(Integer numeroCuenta) {

		Cuenta cuenta = cuentaRepo.findByNumeroCuenta(numeroCuenta)
				.orElseThrow(() -> new RequestException("P-401", "con numeroCuenta" + numeroCuenta));
		CuentaDTO cuentaDTO = modelMapper.map(cuenta, CuentaDTO.class);
		cuentaDTO.setIdentificacion(cuenta.getCliente().getIdentificacion());

		return cuentaDTO;

	}

}