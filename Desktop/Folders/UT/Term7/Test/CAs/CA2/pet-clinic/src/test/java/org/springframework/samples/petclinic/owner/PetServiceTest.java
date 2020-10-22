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

	//	Null Inputs

	@Test
	public void testNewPetNullInput() {
		Pet p = null;
		try {
			p = petService.newPet(null);
			fail("null input was accepted in newPet");
		}catch (Exception e) {
			//Behavior
			Mockito.verify(owner, Mockito.times(0)).getId();
			Mockito.verify(owner, Mockito.times(0)).addPet(any(Pet.class));
			//State
			assertEquals(p, null);
		}
	}

	@Test
	public void testSavePetNullPet() {
		try {
			petService.savePet(null, owner);
			fail("null input was accepted in savePet");
		}catch (Exception e) {
			//Behavior
			Mockito.verify(pet, Mockito.times(0)).getId();
			Mockito.verify(owner, Mockito.times(0)).addPet(any(Pet.class));
			Mockito.verify(pets, Mockito.times(0)).save(any(Pet.class));
		}
	}

	@Test
	public void testSavePetNullOwner() {
		try {
			petService.savePet(pet, null);
			fail("null input was accepted in savePet");
		}catch (Exception e) {
			//Behavior
			Mockito.verify(pet, Mockito.times(1)).getId();
			Mockito.verify(owner, Mockito.times(0)).addPet(any(Pet.class));
			Mockito.verify(pets, Mockito.times(0)).save(any(Pet.class));
		}
	}

	//	Valid Inputs

	@Test
	public void testFindOwnerValidInput() {
		when(ownerRepository.findById(anyInt())).thenReturn(owner);
		Owner check = null;
		try {
			check = petService.findOwner(1);
		}catch (Exception e) {
			System.out.println(e);
			fail("FAILED");
		}
		//Behavior
		Mockito.verify(ownerRepository, Mockito.times(1)).findById(anyInt());
		//
		ArgumentCaptor<Integer> ownerId = ArgumentCaptor.forClass(Integer.class);
		Mockito.verify(ownerRepository).findById(ownerId.capture());
		Assert.assertEquals(java.util.Optional.of(1), java.util.Optional.ofNullable(ownerId.getValue()));

		//State
		assertEquals(check, owner);	//	????
	}

	@Test
	public void testNewPetValidInput() {
		try {
			petService.newPet(owner);
		}catch (Exception e) {
			System.out.println(e);
			fail("FAILED");
		}
		//Behavior
		Mockito.verify(owner, Mockito.times(1)).getId();
		Mockito.verify(owner, Mockito.times(1)).addPet(any(Pet.class));
		//
		ArgumentCaptor<Pet> p = ArgumentCaptor.forClass(Pet.class);
		Mockito.verify(owner).addPet(p.capture());
		Assert.assertEquals(Pet.class, p.getValue());
		//State
//		assertEquals(p, Pet.class);
	}

	@Test
	public void testFindPetValidInput() {
		when(pets.get(anyInt())).thenReturn(pet);
		Pet p = null;
		try {
			p = petService.findPet(1);
		}catch (Exception e) {
			System.out.println(e);
			fail("FAILED");
		}
		//Behavior
		Mockito.verify(pets, Mockito.times(1)).get(anyInt());
		//
		ArgumentCaptor<Integer> petId = ArgumentCaptor.forClass(Integer.class);
		Mockito.verify(pets).get(petId.capture());
		Assert.assertEquals(java.util.Optional.of(1), java.util.Optional.ofNullable(petId.getValue()));
		//	State
		assertEquals(p, pet);
	}

	@Test
	public void testSavePetValidInput() {
		Integer ownerPetSize = owner.getPets().size();
		try {
			petService.savePet(pet, owner);
		}catch (Exception e) {
			System.out.println(e);
			fail("FAILED");
		}
		//Behavior
		Mockito.verify(pet, Mockito.times(1)).getId();
		Mockito.verify(owner, Mockito.times(1)).addPet(any(Pet.class));
		Mockito.verify(pets, Mockito.times(1)).save(any(Pet.class));
		//
		ArgumentCaptor<Pet> repoArgumentCaptor = ArgumentCaptor.forClass(Pet.class);
		Mockito.verify(pets).save(repoArgumentCaptor.capture());
		Assert.assertEquals(pet, repoArgumentCaptor.getValue());
		//State
//		assertEquals(owner.getPets().size() - ownerPetSize, 1);

	}
}
