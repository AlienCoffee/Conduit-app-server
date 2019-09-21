package ru.shemplo.conduit.appserver.services;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.shemplo.conduit.appserver.entities.UserEntity;
import ru.shemplo.conduit.appserver.entities.VerificationEntity;
import ru.shemplo.conduit.appserver.entities.repositories.VerificationEntityRepository;

@Service
@RequiredArgsConstructor
public class VerificationService {
    
    private final VerificationEntityRepository verificationEntityRepository;
    private final Clock clock;
    
    private final Lock lock = new ReentrantLock ();
    private MessageDigest MD;
    
    {
        try {
            MD = MessageDigest.getInstance ("SHA-256");
        } catch (NoSuchAlgorithmException nsae) {
            String message = "Failed initialize SHA-265 algorithm";
            throw new IllegalStateException (message);
        }
    }
    
    public boolean isLoginPending_ss (String login) {
        return verificationEntityRepository.findByLogin (login) != null;
    }
    
    public boolean isPhonePending_ss (String phone) {
        return verificationEntityRepository.findByPhone (phone) != null;
    }
    
    public VerificationEntity createCode_ss (String login, String phone, String password) {
        VerificationEntity entity = verificationEntityRepository
                                  . findByPhone (phone);
        LocalDateTime now = LocalDateTime.now (clock);
        
        if (entity == null) {
            String secret = password; // TODO: change password to sending via SMS code
            
            String hash = generateHash (phone, login, password, secret);
            entity = new VerificationEntity (login, phone, hash);
        
            entity.setAuthor (UserEntity.getAdminEntity ());
            entity.setIssued (now);
        }
        
        entity.setCommitter (UserEntity.getAdminEntity ());
        entity.setChanged (now);
        
        return verificationEntityRepository.save (entity);
    }
    
    public boolean checkCodeAndDelete_ss (String login, String phone, String password, String secret) {
        VerificationEntity entity = verificationEntityRepository
                                  . findByPhone (phone);
        if (entity == null) {
            String message = "Verification code entity doesn't exist";
            throw new EntityNotFoundException (message);
        }
        
        String hash = generateHash (phone, login, password, secret);
        if (hash.equals (entity.getChecksum ())) {
            verificationEntityRepository.delete (entity);
            return true;
        }
        
        return false;
    }
    
    private String generateHash (String phone, String login, String password, String secret) {
        lock.lock (); // MD is not thread-safe ... protection needed
        
        MD.update (phone.getBytes    (StandardCharsets.UTF_8));
        MD.update (login.getBytes    (StandardCharsets.UTF_8));
        MD.update (password.getBytes (StandardCharsets.UTF_8));
        MD.update (secret.getBytes   (StandardCharsets.UTF_8));
        final byte [] bytes = MD.digest ();
        
        StringBuilder sb = new StringBuilder (bytes.length);
        for (byte b : bytes) {
            sb.append (Integer.toHexString (0xff & b));
        }
        
        lock.unlock ();
        return sb.toString ();
    }
    
}
