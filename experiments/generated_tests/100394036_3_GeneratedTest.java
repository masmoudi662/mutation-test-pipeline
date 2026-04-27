java
package com.iitp.njack.iitp_connect.core.profile;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iitp.njack.iitp_connect.data.user.User;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProfileRepositoryTest {

    private ProfileRepository profileRepository;

    @Mock
    private DatabaseReference databaseReference;

    @Mock
    private FirebaseDatabase firebaseDatabase;

    @Mock
    private DataSnapshot dataSnapshot;

    @Captor
    private ArgumentCaptor<ValueEventListener> valueEventListenerArgumentCaptor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        profileRepository = new ProfileRepository(databaseReference);
    }

    @Test
    public void setUserTest() {
        User user = new User("testName", "testEmail");
        profileRepository.setUser(user);
        verify(databaseReference).setValue(user);
    }

    @Test
    public void loadUserTest() {
        profileRepository.loadUser();
        verify(databaseReference).addValueEventListener(any(ValueEventListener.class));
    }
}