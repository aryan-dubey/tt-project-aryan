package com.auriga_tt.repository;

import com.auriga_tt.model.User;
import com.auriga_tt.model.UserContactMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserContactMethodRepository extends JpaRepository<UserContactMethod, Long> {
    List<UserContactMethod> findByUser(User user);
    Optional<UserContactMethod> findByUserAndContactTypeAndContactValue(User user, UserContactMethod.ContactType contactType, String contactValue);
    Optional<UserContactMethod> findByVerificationToken(String verificationToken);
    Optional<UserContactMethod> findByContactTypeAndContactValue(UserContactMethod.ContactType contactType, String contactValue);
}
