package org.springframework.samples.petclinic.utility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.owner.PetRepository;

import java.util.HashMap;
import java.util.concurrent.Callable;

/**
 * this simple class shows the main idea behind a Dependency Injection library
 */
public class SimpleDI {

	@Autowired
	private PetRepository petRepository;
	public static SimpleDI getDIContainer() throws Exception {
		// todo return the singleton instance of your implementation of dependency injection container
		return new SimpleDI();
	}

	public void provideByInstance(Class<PetRepository> petRepositoryClass, PetRepository petRepository) {
		this.petRepository = petRepository;
	}

//	public void provideByAConstructorFunction(Class<?> typeClass, Callable<Object> providerFunction) {
//		this.petRepository.providerFunction()
//	}

	public Object getInstanceOf(Class<PetRepository> petRepositoryClass) throws Exception {
		return this.petRepository;
	}
}
