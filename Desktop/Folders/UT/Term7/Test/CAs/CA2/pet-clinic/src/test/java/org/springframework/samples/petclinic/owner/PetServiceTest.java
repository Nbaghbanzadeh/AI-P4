package org.springframework.samples.petclinic.owner;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.springframework.samples.petclinic.utility.PetTimedCache;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
class PetServiceTest {

	@Mock private static OwnerRepository ownerRepository;
	@Mock private static PetTimedCache pets;
	@Mock private static Logger criticalLogger;
	@Mock private Pet pet;
	@Mock private Owner owner;

	@InjectMocks
	private PetService petService;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.initMocks(this);
		petService = new PetService(pets, ownerRepository, criticalLogger);
	}

	@Test
	public void testFindOwnerValidInput() {

		when(ownerRepository.findById(anyInt())).thenReturn(owner);
		try {
			petService.findOwner(1);
		}catch (Exception e) {
			System.out.println(e);
			fail("FAILED");
		}
		Mockito.verify(ownerRepository, Mockito.times(1)).findById(anyInt());
		ArgumentCaptor<Integer> ownerId = ArgumentCaptor.forClass(Integer.class);
		Mockito.verify(ownerRepository).findById(ownerId.capture());
		Assert.assertEquals(java.util.Optional.of(1), java.util.Optional.ofNullable(ownerId.getValue()));
	}

	@Test
	public void testNewPetValidInput() {

		try {
			petService.newPet(owner);
		}catch (Exception e) {
			System.out.println(e);
			fail("FAILED");
		}
		Mockito.verify(owner, Mockito.times(1)).getId();
		Mockito.verify(owner, Mockito.times(1)).addPet(any(Pet.class));
	}

	@Test
	public void testFindPetValidInput() {

		try {
			petService.findPet(1);
		}catch (Exception e) {
			System.out.println(e);
			fail("FAILED");
		}
		Mockito.verify(pets, Mockito.times(1)).get(anyInt());
	}

	@Test
	public void testSavePetValidInput() {

		try {
			petService.savePet(pet, owner);
		}catch (Exception e) {
			System.out.println(e);
			fail("FAILED");
		}
		Mockito.verify(pet, Mockito.times(1)).getId();
		Mockito.verify(owner, Mockito.times(1)).addPet(pet);
		Mockito.verify(pets, Mockito.times(1)).save(pet);
	}
}
