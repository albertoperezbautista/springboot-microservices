package com.microservicios.clientes.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservicios.clientes.dto.ClienteDTO;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ClienteControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void crearYObtenerClienteTest() throws Exception {

		ClienteDTO nuevoCliente = new ClienteDTO();
		nuevoCliente.setNombre("Cliente Prueba");
		nuevoCliente.setIdentificacion("1700000001");

		MvcResult createResult = mockMvc.perform(post("/clientes").contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objectMapper.writeValueAsString(nuevoCliente)).accept(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk()).andReturn();

		String responseBody = createResult.getResponse().getContentAsString();
		ClienteDTO clienteCreado = objectMapper.readValue(responseBody, ClienteDTO.class);
		assertEquals(nuevoCliente.getNombre(), clienteCreado.getNombre());

		MvcResult getResult = mockMvc.perform(get("/clientes").accept(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk()).andReturn();

		String getResponseBody = getResult.getResponse().getContentAsString();
		List<ClienteDTO> listaClientes = objectMapper.readValue(getResponseBody, new TypeReference<List<ClienteDTO>>() {
		});

		assertTrue(listaClientes.stream()
				.anyMatch(cliente -> cliente.getIdentificacion().equals(nuevoCliente.getIdentificacion())));
	}
}
