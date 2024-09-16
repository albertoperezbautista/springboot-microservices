

package com.microservicios.cuentas.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.microservicios.cuentas.entities.Cuenta;

@Repository
public interface ICuentaRepository extends JpaRepository<Cuenta, Long> {
	Optional<Cuenta> findByNumeroCuenta(Integer numeroCuenta);

}