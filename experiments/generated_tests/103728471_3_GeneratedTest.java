java
package org.agoncal.fascicle.jpa.integrating.spring;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddressEndpointTest {

  @Mock
  private AddressRepository addressRepository;

  @InjectMocks
  private AddressEndpoint addressEndpoint;

  @Test
  void getAddressesLikeZip() {
    // Given
    String zip = "123%";
    List<Address> expectedAddresses = new ArrayList<>();
    Address address1 = new Address("1 Main St", "Anytown", "CA", "12345");
    Address address2 = new Address("2 Oak Ave", "Somecity", "NY", "12367");
    expectedAddresses.add(address1);
    expectedAddresses.add(address2);

    // When
    when(addressRepository.findAllLikeZip(zip)).thenReturn(expectedAddresses);
    List<Address> actualAddresses = addressEndpoint.getAddressesLikeZip(zip);

    // Then
    assertEquals(expectedAddresses.size(), actualAddresses.size());
    assertEquals(expectedAddresses.get(0).getStreet(), actualAddresses.get(0).getStreet());
    assertEquals(expectedAddresses.get(1).getZip(), actualAddresses.get(1).getZip());
  }

  @Test
  void getAddressesLikeZip_emptyResult() {
    String zip = "999%";
    List<Address> expectedAddresses = new ArrayList<>();

    when(addressRepository.findAllLikeZip(zip)).thenReturn(expectedAddresses);
    List<Address> actualAddresses = addressEndpoint.getAddressesLikeZip(zip);

    assertEquals(expectedAddresses.size(), actualAddresses.size());
  }

  @Test
  void getAddressesLikeZip_nullZip() {
    String zip = null;
    List<Address> expectedAddresses = new ArrayList<>();

    when(addressRepository.findAllLikeZip(zip)).thenReturn(expectedAddresses);
    List<Address> actualAddresses = addressEndpoint.getAddressesLikeZip(zip);

    assertEquals(expectedAddresses.size(), actualAddresses.size());
  }
}