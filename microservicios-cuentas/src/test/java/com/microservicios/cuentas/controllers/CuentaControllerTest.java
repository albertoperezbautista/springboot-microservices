package com.microservicios.cuentas.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservicios.cuentas.dto.ClienteDTO;
import com.microservicios.cuentas.dto.CuentaDTO;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CuentaControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

//	@Test
	void crearYObtenerCuentaTest() throws Exception {

		// CREAMOS UN CLIENTE
		ClienteDTO nuevoCliente = new ClienteDTO();
		nuevoCliente.setNombre("Cliente Prueba");
		nuevoCliente.setIdentificacion("1700000001");

		// LUEGO LA CUENTA
		CuentaDTO nuevaCuenta = new CuentaDTO();
		nuevaCuenta.setNumeroCuenta(12345);
		nuevaCuenta.setIdentificacion(nuevoCliente.getIdentificacion());
		nuevaCuenta.setSaldoInicial(1000L);

		MvcResult createCuentaResult = mockMvc
				.perform(post("/cuentas").contentType(MediaType.APPLICATION_JSON_VALUE)
						.content(objectMapper.writeValueAsString(nuevaCuenta)).accept(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk()).andReturn();

		String cuentaResponseBody = createCuentaResult.getResponse().getContentAsString();
		CuentaDTO cuentaCreada = objectMapper.readValue(cuentaResponseBody, CuentaDTO.class);

		assertEquals(nuevaCuenta.getNumeroCuenta(), cuentaCreada.getNumeroCuenta());
		assertEquals(nuevoCliente.getIdentificacion(), cuentaCreada.getIdentificacion());

		// VERIFICO QUE SE HAYA CREADO LA CUENTA
		MvcResult getResult = mockMvc.perform(get("/cuentas").accept(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk()).andReturn();

		String getResponseBody = getResult.getResponse().getContentAsString();
		List<CuentaDTO> listaCuentas = objectMapper.readValue(getResponseBody, new TypeReference<List<CuentaDTO>>() {
		});

		assertTrue(listaCuentas.stream()
				.anyMatch(cuenta -> cuenta.getNumeroCuenta().equals(nuevaCuenta.getNumeroCuenta())));
	}

}
