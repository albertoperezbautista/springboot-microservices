package com.microservicios.clientes.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.microservicios.clientes.dto.ClienteDTO;
import com.microservicios.clientes.entities.Cliente;
import com.microservicios.clientes.repository.IClienteRepository;
import com.microservicios.clientes.utils.MapStructMapper;

@ExtendWith(MockitoExtension.class)
public class ClienteServiceTest {

	@InjectMocks
	private ClienteService clienteService;

	@Mock
	private IClienteRepository clienteRepo;

	@Mock
	private ModelMapper modelMapper;

	@Mock
	private MapStructMapper mapper;

	Cliente cliente1 = new Cliente();
	Cliente cliente2 = new Cliente();
	ClienteDTO clienteDTO1 = new ClienteDTO();
	ClienteDTO clienteDTO2 = new ClienteDTO();
	List<Cliente> listaClientes = new ArrayList<>();

	@BeforeEach
	public void setUp() {
		cliente1.setIdCliente(1L);
		cliente1.setNumeroCliente(123L);
		cliente1.setNombre("Cliente 1");
		cliente2.setIdCliente(2L);
		cliente2.setNombre("Cliente 2");

		clienteDTO1.setIdCliente(1L);
		clienteDTO1.setNombre("Cliente 1");
		clienteDTO2.setIdCliente(2L);
		clienteDTO2.setNombre("Cliente 2");
		listaClientes.add(cliente1);
		listaClientes.add(cliente2);

	}

	@Test
	public void obtenerTodosTest() {

		when(clienteRepo.findAll()).thenReturn(listaClientes);
		when(modelMapper.map(cliente1, ClienteDTO.class)).thenReturn(clienteDTO1);
		when(modelMapper.map(cliente2, ClienteDTO.class)).thenReturn(clienteDTO2);

		List<ClienteDTO> resultado = clienteService.obtenerTodos();

		assertEquals(2, resultado.size());

	}

	@Test
	public void obtenerPoIdTest() {

		Long clienteId = 1L;

		when(clienteRepo.findById(clienteId)).thenReturn(java.util.Optional.of(cliente1));
		when(modelMapper.map(cliente1, ClienteDTO.class)).thenReturn(clienteDTO1);

		ClienteDTO result = clienteService.obtenerPoId(clienteId);

		assertEquals(clienteDTO1.getIdCliente(), result.getIdCliente());

	}

	@Test
	public void obtenerPorNumeroClienteTest() {

		Long numeroCliente = 123L;
		when(clienteRepo.findByNumeroCliente(numeroCliente)).thenReturn(java.util.Optional.of(cliente1));
		when(modelMapper.map(cliente1, ClienteDTO.class)).thenReturn(clienteDTO1);

		ClienteDTO result = clienteService.obtenerPorNumeroCliente(numeroCliente);
		assertEquals(clienteDTO1.getIdCliente(), result.getIdCliente());
		assertEquals(clienteDTO1.getNombre(), result.getNombre());

	}

	@Test
	public void findByIdentificacionoTest() {

		String identificacion = "1700000001";
		when(clienteRepo.findByIdentificacion(identificacion)).thenReturn(java.util.Optional.of(cliente1));
		when(modelMapper.map(cliente1, ClienteDTO.class)).thenReturn(clienteDTO1);

		ClienteDTO result = clienteService.findByIdentificacion(identificacion);
		assertEquals(clienteDTO1.getIdCliente(), result.getIdCliente());
		assertEquals(clienteDTO1.getNombre(), result.getNombre());

	}

	@Test
	public void crearClienteTest() {
		ClienteDTO clienteDtoCreado = new ClienteDTO();
		clienteDtoCreado.setIdCliente(1L);
		clienteDtoCreado.setNombre("Cliente 1");

		when(modelMapper.map(clienteDTO1, Cliente.class)).thenReturn(cliente1);
		when(clienteRepo.save(cliente1)).thenReturn(cliente1);
		when(modelMapper.map(cliente1, ClienteDTO.class)).thenReturn(clienteDtoCreado);

		ClienteDTO resultado = clienteService.crearCliente(clienteDTO1);

		assertEquals(clienteDtoCreado.getIdCliente(), resultado.getIdCliente());
		assertEquals(clienteDtoCreado.getNombre(), resultado.getNombre());

	}

	@Test
	public void actualizarClienteTest() {

		Long id = 1L;
		ClienteDTO clienteDTO = new ClienteDTO();
		clienteDTO.setNombre("Cliente Actualizado");

		Cliente clienteExistente = new Cliente();
		clienteExistente.setIdCliente(id);
		clienteExistente.setNombre("Cliente Original");

		ClienteDTO clienteDtoActualizado = new ClienteDTO();
		clienteDtoActualizado.setIdCliente(id);
		clienteDtoActualizado.setNombre("Cliente Actualizado");

		when(clienteRepo.findById(id)).thenReturn(java.util.Optional.of(clienteExistente));
		when(modelMapper.map(clienteExistente, ClienteDTO.class)).thenReturn(clienteDtoActualizado);

		ClienteDTO resultado = clienteService.actualizarCliente(id, clienteDTO);
		assertEquals(clienteDtoActualizado.getNombre(), resultado.getNombre());

	}

	@Test
	public void eliminarClienteExistenteTest() {

		Long id = 1L;
		Cliente cliente = new Cliente();
		cliente.setIdCliente(id);

		when(clienteRepo.findById(id)).thenReturn(java.util.Optional.of(cliente));
		clienteService.eliminarCliente(id);
		verify(clienteRepo).findById(id);
		verify(clienteRepo).delete(cliente);

	}
}
