package pl.zzpj.spacer.service;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.zzpj.spacer.exception.AccountException;
import pl.zzpj.spacer.exception.AppBaseException;
import pl.zzpj.spacer.exception.PictureException;
import pl.zzpj.spacer.model.Account;
import pl.zzpj.spacer.model.Picture;
import pl.zzpj.spacer.repositories.AccountRepository;
import pl.zzpj.spacer.repositories.PictureRepository;
import pl.zzpj.spacer.service.interfaces.AccountService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    private final PictureRepository pictureRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public void addAccount(Account account) throws AccountException {
        if (accountRepository.findByUsername(account.getUsername()).isEmpty()) {
            account.setPassword(passwordEncoder.encode(account.getPassword()));
            accountRepository.save(account);
        } else {
            throw AccountException.accountExistsException();
        }
    }

    @Override
    public Account getAccount(String username) throws AccountException {
        return accountRepository.findByUsername(username).orElseThrow(AccountException::noSuchAccountException);
    }

    @Override
    public List<Account> getAll() {
        return accountRepository.findAll();
    }

    @Override
    public void editAccount(String username, Account account) throws AccountException {
        Optional<Account> queryAccount = accountRepository.findByUsername(username);
        if (queryAccount.isPresent()) {
            Account temp = queryAccount.get();
            if (username.equals(account.getUsername()) && account.getPassword() != null) {
                temp.setPassword(passwordEncoder.encode(account.getPassword()));
            } else {
                throw AccountException.usernameMismatch();
            }
            temp.setFirstName(account.getFirstName());
            temp.setLastName(account.getLastName());
            accountRepository.save(temp);
        } else {
            throw AccountException.noSuchAccountException();
        }
    }

    @Override
    public void deleteAccount(String username) throws AppBaseException {
        Optional<Account> queryAccount = accountRepository.findByUsername(username);
        if (queryAccount.isPresent()) {
            Account temp = queryAccount.get();
            accountRepository.delete(temp);
        } else {
            throw AccountException.noSuchAccountException();
        }
    }

    @Override
    public void addLikedPicture(String username, String pictureId) throws AccountException, PictureException {
        Optional<Account> queryAccount = accountRepository.findByUsername(username);
        if (queryAccount.isPresent()) {
            Account temp = queryAccount.get();
            Optional<Picture> queryPicture = pictureRepository.findById(pictureId);
            if (queryPicture.isPresent()) {
                temp.getLikedPictures().add(pictureId);
                accountRepository.save(temp);
            } else {
                throw PictureException.noSuchPictureException();
            }
        } else {
            throw AccountException.noSuchAccountException();
        }
    }

    @Override
    public void removeLikedPicture(String username, String pictureId) throws AccountException {
        Optional<Account> queryAccount = accountRepository.findByUsername(username);
        if (queryAccount.isPresent()) {
            Account temp = queryAccount.get();
            if (temp.getLikedPictures().contains(pictureId)) {
                temp.getLikedPictures().remove(pictureId);
                accountRepository.save(temp);
            } else {
                throw AccountException.noSuchLikedPictureException();
            }
        } else {
            throw AccountException.noSuchAccountException();
        }
    }

    @Override
    public List<String> getLikedPicturesByUsername(String username){
        Optional<Account> queryAccount = accountRepository.findByUsername(username);
        if (queryAccount.isPresent()) {
            Account temp = queryAccount.get();
            return new ArrayList<>(temp.getLikedPictures());
        }
        else {
            return new ArrayList<>();
        }
    }
}
